package servidor;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import modelo.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ApiHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");

        if ("OPTIONS".equals(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(204, -1);
            return;
        }

        String fullPath = exchange.getRequestURI().getPath();
        String apiPath = fullPath.startsWith("/api") ? fullPath.substring(4) : fullPath;
        if (apiPath.isEmpty()) apiPath = "/";

        String method = exchange.getRequestMethod();
        String[] seg = apiPath.split("/");
        // seg[0]="" seg[1]=recurso seg[2]=id seg[3]=sub-recurso
        String recurso = seg.length > 1 ? seg[1] : "";

        try {
            switch (recurso) {
                case "equipos":
                    handleEquipos(exchange);
                    break;
                case "partidos-en-vivo":
                    handlePartidosEnVivo(exchange);
                    break;
                case "partidos":
                    routePartidos(exchange, method, seg);
                    break;
                case "ranking":
                    handleRanking(exchange);
                    break;
                case "reporte":
                    if (seg.length >= 3) {
                        handleReporte(exchange, Integer.parseInt(seg[2]));
                    } else {
                        sendError(exchange, 400, "Falta id del partido");
                    }
                    break;
                default:
                    sendError(exchange, 404, "Recurso no encontrado: " + recurso);
            }
        } catch (NumberFormatException e) {
            sendError(exchange, 400, "ID invalido");
        } catch (IllegalArgumentException e) {
            sendError(exchange, 400, e.getMessage());
        } catch (Exception e) {
            sendError(exchange, 500, "Error interno: " + e.getMessage());
        }
    }

    private void routePartidos(HttpExchange exchange, String method, String[] seg) throws IOException {
        if (seg.length == 2) {
            if ("GET".equals(method)) handleListarPartidos(exchange);
            else if ("POST".equals(method)) handleCrearPartido(exchange);
            else sendError(exchange, 405, "Metodo no permitido");
        } else if (seg.length == 3) {
            int id = Integer.parseInt(seg[2]);
            if ("GET".equals(method)) handlePartidoDetalle(exchange, id);
            else sendError(exchange, 405, "Metodo no permitido");
        } else if (seg.length == 4 && "eventos".equals(seg[3])) {
            int id = Integer.parseInt(seg[2]);
            if ("POST".equals(method)) handleAgregarEvento(exchange, id);
            else sendError(exchange, 405, "Metodo no permitido");
        } else {
            sendError(exchange, 404, "Ruta no encontrada");
        }
    }

    // ===== Handlers =====

    private void handleEquipos(HttpExchange exchange) throws IOException {
        String fuente = queryParam(exchange, "fuente", "mundial");
        List<Equipo> equipos = EstadoAplicacion.getInstance().getEquipos(fuente);

        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < equipos.size(); i++) {
            if (i > 0) sb.append(",");
            sb.append(equipoJson(equipos.get(i), i));
        }
        sb.append("]");
        sendJson(exchange, obj("fuente", fuente, "equipos", sb.toString()), 200);
    }

    private void handlePartidosEnVivo(HttpExchange exchange) throws IOException {
        String fuente = queryParam(exchange, "fuente", "mundial");
        List<Partido> partidos = EstadoAplicacion.getInstance().getPartidosEnVivo(fuente);

        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < partidos.size(); i++) {
            if (i > 0) sb.append(",");
            Partido p = partidos.get(i);
            sb.append(partidoResumenJson(-1, p, fuente));
        }
        sb.append("]");
        sendJson(exchange, "{\"fuente\":" + jstr(fuente) + ",\"partidos\":" + sb + "}", 200);
    }

    private void handleListarPartidos(HttpExchange exchange) throws IOException {
        Map<Integer, Partido> partidos = EstadoAplicacion.getInstance().getPartidos();

        StringBuilder sb = new StringBuilder("[");
        boolean first = true;
        for (Map.Entry<Integer, Partido> entry : partidos.entrySet()) {
            if (!first) sb.append(",");
            first = false;
            int id = entry.getKey();
            String fuente = EstadoAplicacion.getInstance().getFuentePartido(id);
            sb.append(partidoResumenJson(id, entry.getValue(), fuente));
        }
        sb.append("]");
        sendJson(exchange, "{\"partidos\":" + sb + "}", 200);
    }

    private void handleCrearPartido(HttpExchange exchange) throws IOException {
        Map<String, String> body = parseBody(exchange);
        String fuente = body.getOrDefault("fuente", "mundial");
        int localIdx = Integer.parseInt(body.getOrDefault("localIdx", "0"));
        int visitanteIdx = Integer.parseInt(body.getOrDefault("visitanteIdx", "1"));

        int id = EstadoAplicacion.getInstance().crearPartido(fuente, localIdx, visitanteIdx);
        Partido partido = EstadoAplicacion.getInstance().getPartido(id);
        sendJson(exchange, partidoResumenJson(id, partido, fuente), 201);
    }

    private void handlePartidoDetalle(HttpExchange exchange, int id) throws IOException {
        Partido partido = EstadoAplicacion.getInstance().getPartido(id);
        if (partido == null) { sendError(exchange, 404, "Partido no encontrado"); return; }
        String fuente = EstadoAplicacion.getInstance().getFuentePartido(id);
        sendJson(exchange, partidoDetalleJson(id, partido, fuente), 200);
    }

    private void handleAgregarEvento(HttpExchange exchange, int id) throws IOException {
        Partido partido = EstadoAplicacion.getInstance().getPartido(id);
        if (partido == null) { sendError(exchange, 404, "Partido no encontrado"); return; }

        Map<String, String> body = parseBody(exchange);
        int minuto = Integer.parseInt(body.getOrDefault("minuto", "1"));
        TipoEvento tipo = TipoEvento.valueOf(body.getOrDefault("tipo", "GOL"));
        String jugadorNombre = body.getOrDefault("jugadorNombre", "");

        Jugador jugador = buscarJugador(partido, jugadorNombre);
        if (jugador == null && !jugadorNombre.isEmpty()) {
            sendError(exchange, 404, "Jugador no encontrado: " + jugadorNombre);
            return;
        }

        partido.agregarEvento(new EventoPartido(minuto, tipo, jugador));

        String fuente = EstadoAplicacion.getInstance().getFuentePartido(id);
        sendJson(exchange, partidoDetalleJson(id, partido, fuente), 200);
    }

    private void handleRanking(HttpExchange exchange) throws IOException {
        String fuente = queryParam(exchange, "fuente", "mundial");
        String criterio = queryParam(exchange, "criterio", "puntos");
        List<Ranking> ranking = EstadoAplicacion.getInstance().getRanking(fuente, criterio);

        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < ranking.size(); i++) {
            if (i > 0) sb.append(",");
            Ranking r = ranking.get(i);
            sb.append("{")
              .append("\"posicion\":").append(r.getPosicion()).append(",")
              .append("\"equipo\":").append(jstr(r.getEquipo().getNombre())).append(",")
              .append("\"puntos\":").append(r.getPuntos()).append(",")
              .append("\"gf\":").append(r.getGolesAFavor()).append(",")
              .append("\"gc\":").append(r.getGolesEnContra()).append(",")
              .append("\"dg\":").append(r.getDiferenciaGoles())
              .append("}");
        }
        sb.append("]");
        sendJson(exchange,
            "{\"fuente\":" + jstr(fuente) + ",\"criterio\":" + jstr(criterio) + ",\"ranking\":" + sb + "}",
            200);
    }

    private void handleReporte(HttpExchange exchange, int id) throws IOException {
        String reporte = EstadoAplicacion.getInstance().getReporte(id);
        if (reporte == null) { sendError(exchange, 404, "Partido no encontrado"); return; }
        sendJson(exchange, "{\"id\":" + id + ",\"reporte\":" + jstr(reporte) + "}", 200);
    }

    // ===== JSON builders =====

    private String equipoJson(Equipo e, int idx) {
        StringBuilder sb = new StringBuilder("{");
        sb.append("\"idx\":").append(idx).append(",");
        sb.append("\"nombre\":").append(jstr(e.getNombre())).append(",");
        sb.append("\"anioFundacion\":").append(e.getAnioFundacion()).append(",");
        sb.append("\"puntos\":").append(e.getPuntos()).append(",");
        sb.append("\"gf\":").append(e.getGolesAFavor()).append(",");
        sb.append("\"gc\":").append(e.getGolesEnContra()).append(",");
        sb.append("\"dg\":").append(e.getDiferenciaGoles()).append(",");
        sb.append("\"jugadores\":[");
        List<Jugador> jugadores = e.getListaJugadores();
        for (int i = 0; i < jugadores.size(); i++) {
            if (i > 0) sb.append(",");
            Jugador j = jugadores.get(i);
            sb.append("{")
              .append("\"nombre\":").append(jstr(j.getNombreCompleto())).append(",")
              .append("\"posicion\":").append(jstr(j.getPosicion())).append(",")
              .append("\"goles\":").append(j.getGoles()).append(",")
              .append("\"amarillas\":").append(j.getTarjetasAmarillas()).append(",")
              .append("\"rojas\":").append(j.getTarjetasRojas())
              .append("}");
        }
        sb.append("]}");
        return sb.toString();
    }

    private String partidoResumenJson(int id, Partido p, String fuente) {
        return "{" +
            "\"id\":" + id + "," +
            "\"local\":" + jstr(p.getEquipoLocal().getNombre()) + "," +
            "\"visitante\":" + jstr(p.getEquipoVisitante().getNombre()) + "," +
            "\"resultado\":" + jstr(p.getResultado()) + "," +
            "\"totalEventos\":" + p.getEventos().size() + "," +
            "\"fuente\":" + jstr(fuente) +
            "}";
    }

    private String partidoDetalleJson(int id, Partido p, String fuente) {
        StringBuilder sb = new StringBuilder("{");
        sb.append("\"id\":").append(id).append(",");
        sb.append("\"local\":").append(jstr(p.getEquipoLocal().getNombre())).append(",");
        sb.append("\"visitante\":").append(jstr(p.getEquipoVisitante().getNombre())).append(",");
        sb.append("\"resultado\":").append(jstr(p.getResultado())).append(",");
        sb.append("\"fuente\":").append(jstr(fuente)).append(",");

        Estadistica el = p.getEstadisticaLocal();
        Estadistica ev = p.getEstadisticaVisitante();
        sb.append("\"estadisticaLocal\":{")
          .append("\"goles\":").append(el.getGoles()).append(",")
          .append("\"amarillas\":").append(el.getTarjetasAmarillas()).append(",")
          .append("\"rojas\":").append(el.getTarjetasRojas())
          .append("},");
        sb.append("\"estadisticaVisitante\":{")
          .append("\"goles\":").append(ev.getGoles()).append(",")
          .append("\"amarillas\":").append(ev.getTarjetasAmarillas()).append(",")
          .append("\"rojas\":").append(ev.getTarjetasRojas())
          .append("},");

        sb.append("\"eventos\":[");
        List<EventoPartido> eventos = p.getEventos();
        for (int i = 0; i < eventos.size(); i++) {
            if (i > 0) sb.append(",");
            EventoPartido e = eventos.get(i);
            sb.append("{")
              .append("\"minuto\":").append(e.getMinuto()).append(",")
              .append("\"tipo\":").append(jstr(e.getTipo().name())).append(",")
              .append("\"autor\":").append(e.getAutor() != null ? jstr(e.getAutor().getNombreCompleto()) : "null").append(",")
              .append("\"equipo\":").append(e.getAutor() != null && e.getAutor().getEquipo() != null
                  ? jstr(e.getAutor().getEquipo().getNombre()) : "null")
              .append("}");
        }
        sb.append("],");

        sb.append("\"jugadoresLocal\":").append(jugadoresJson(p.getEquipoLocal().getListaJugadores())).append(",");
        sb.append("\"jugadoresVisitante\":").append(jugadoresJson(p.getEquipoVisitante().getListaJugadores())).append(",");

        sb.append("\"suscriptores\":[")
          .append("\"Aficionado\",\"AnalistaDeportivo\",\"CasaApuesta\",\"Periodista\"")
          .append("]");
        sb.append("}");
        return sb.toString();
    }

    private String jugadoresJson(List<Jugador> jugadores) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < jugadores.size(); i++) {
            if (i > 0) sb.append(",");
            Jugador j = jugadores.get(i);
            sb.append("{")
              .append("\"nombre\":").append(jstr(j.getNombreCompleto())).append(",")
              .append("\"posicion\":").append(jstr(j.getPosicion())).append(",")
              .append("\"goles\":").append(j.getGoles()).append(",")
              .append("\"amarillas\":").append(j.getTarjetasAmarillas()).append(",")
              .append("\"rojas\":").append(j.getTarjetasRojas())
              .append("}");
        }
        sb.append("]");
        return sb.toString();
    }

    // ===== Utilidades =====

    private Jugador buscarJugador(Partido partido, String nombre) {
        for (Jugador j : partido.getEquipoLocal().getListaJugadores()) {
            if (j.getNombreCompleto().equalsIgnoreCase(nombre)) return j;
        }
        for (Jugador j : partido.getEquipoVisitante().getListaJugadores()) {
            if (j.getNombreCompleto().equalsIgnoreCase(nombre)) return j;
        }
        return null;
    }

    private String queryParam(HttpExchange exchange, String key, String def) {
        String query = exchange.getRequestURI().getQuery();
        if (query == null) return def;
        for (String par : query.split("&")) {
            String[] kv = par.split("=", 2);
            if (kv.length == 2 && kv[0].equals(key)) return kv[1];
        }
        return def;
    }

    private Map<String, String> parseBody(HttpExchange exchange) throws IOException {
        Map<String, String> result = new LinkedHashMap<>();
        String body;
        try (InputStream is = exchange.getRequestBody();
             ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[1024];
            int n;
            while ((n = is.read(buffer)) != -1) bos.write(buffer, 0, n);
            body = bos.toString("UTF-8");
        }
        Pattern p = Pattern.compile("\"(\\w+)\"\\s*:\\s*(?:\"([^\"]*)\"|(-?\\d+))");
        Matcher m = p.matcher(body);
        while (m.find()) {
            result.put(m.group(1), m.group(2) != null ? m.group(2) : m.group(3));
        }
        return result;
    }

    private String jstr(String s) {
        if (s == null) return "null";
        return "\"" + s.replace("\\", "\\\\")
                       .replace("\"", "\\\"")
                       .replace("\n", "\\n")
                       .replace("\r", "") + "\"";
    }

    private String obj(String k1, String v1, String k2, String v2Raw) {
        return "{" + jstr(k1) + ":" + jstr(v1) + "," + jstr(k2) + ":" + v2Raw + "}";
    }

    private void sendJson(HttpExchange exchange, String json, int status) throws IOException {
        byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8");
        exchange.sendResponseHeaders(status, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    private void sendError(HttpExchange exchange, int status, String msg) throws IOException {
        sendJson(exchange, "{\"error\":" + jstr(msg) + "}", status);
    }
}
