package servidor;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import fuentes.AdaptadorJsonPeriodistico;
import fuentes.AdaptadorJsonPeriodistico.EventoPeriodistico;
import fuentes.AdaptadorJsonPeriodistico.InformePeriodistico;
import fuentes.AdaptadorTxtMundial;
import modelo.Equipo;
import modelo.Jugador;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ApiHandler implements HttpHandler {

    private static final Map<String, String> NOMBRES = new HashMap<>();

    static {
        NOMBRES.put("ARGENTINA", "Argentina");
        NOMBRES.put("MEXICO", "México");
        NOMBRES.put("BRASIL", "Brasil");
        NOMBRES.put("URUGUAY", "Uruguay");
        NOMBRES.put("FRANCIA", "Francia");
        NOMBRES.put("DINAMARCA", "Dinamarca");
        NOMBRES.put("ESPANA", "España");
        NOMBRES.put("CROACIA", "Croacia");
        NOMBRES.put("PORTUGAL", "Portugal");
        NOMBRES.put("BELGICA", "Bélgica");
        NOMBRES.put("INGLATERRA", "Inglaterra");
        NOMBRES.put("ESTADOS UNIDOS", "Estados Unidos");
        NOMBRES.put("ALEMANIA", "Alemania");
        NOMBRES.put("JAPON", "Japón");
        NOMBRES.put("PAISES BAJOS", "Países Bajos");
        NOMBRES.put("ITALIA", "Italia");
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, OPTIONS");
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");

        if ("OPTIONS".equals(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(204, -1);
            return;
        }

        String path = exchange.getRequestURI().getPath();
        String ruta = path.startsWith("/api") ? path.substring(4) : path;
        if (ruta.endsWith("/") && ruta.length() > 1) {
            ruta = ruta.substring(0, ruta.length() - 1);
        }

        try {
            String respuesta;
            int codigo = 200;

            if (ruta.equals("/partidos")) {
                respuesta = getPartidos();
            } else if (ruta.startsWith("/partidos/")) {
                int id = Integer.parseInt(ruta.substring("/partidos/".length()));
                respuesta = getPartidoDetalle(id);
            } else if (ruta.equals("/equipos")) {
                respuesta = getEquipos();
            } else if (ruta.startsWith("/equipos/")) {
                String clave = URLDecoder.decode(ruta.substring("/equipos/".length()), "UTF-8");
                respuesta = getEquipoDetalle(clave);
            } else if (ruta.equals("/rankings")) {
                respuesta = getRankings();
            } else if (ruta.equals("/goleadores")) {
                respuesta = getGoleadores();
            } else {
                respuesta = "{\"error\":\"Ruta no encontrada\"}";
                codigo = 404;
            }

            byte[] bytes = respuesta.getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(codigo, bytes.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(bytes);
            }

        } catch (Exception e) {
            String error = "{\"error\":\"" + escJson(e.getMessage()) + "\"}";
            byte[] bytes = error.getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(500, bytes.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(bytes);
            }
        }
    }

    private String getPartidos() {
        List<InformePeriodistico> informes = new AdaptadorJsonPeriodistico().obtenerInformes();
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < informes.size(); i++) {
            InformePeriodistico inf = informes.get(i);
            if (i > 0) sb.append(",");
            sb.append("{")
              .append("\"id\":").append(inf.getPartidoId()).append(",")
              .append("\"local\":\"").append(escJson(nombre(inf.getEquipoLocal()))).append("\",")
              .append("\"visitante\":\"").append(escJson(nombre(inf.getEquipoVisitante()))).append("\",")
              .append("\"resultado\":\"").append(escJson(inf.getResultado())).append("\",")
              .append("\"golesLocal\":").append(contarGoles(inf.getGoles(), inf.getEquipoLocal())).append(",")
              .append("\"golesVisitante\":").append(contarGoles(inf.getGoles(), inf.getEquipoVisitante())).append(",")
              .append("\"fecha\":\"").append(escJson(inf.getFecha())).append("\",")
              .append("\"estadio\":\"").append(escJson(inf.getEstadio())).append("\",")
              .append("\"fase\":\"").append(fase(inf.getPartidoId())).append("\",")
              .append("\"huboTiempoExtra\":").append(inf.isHuboTiempoExtra()).append(",")
              .append("\"huboRoja\":").append(inf.isHuboRoja())
              .append("}");
        }
        sb.append("]");
        return sb.toString();
    }

    private String getPartidoDetalle(int id) {
        List<InformePeriodistico> informes = new AdaptadorJsonPeriodistico().obtenerInformes();
        InformePeriodistico inf = null;
        for (InformePeriodistico item : informes) {
            if (item.getPartidoId() == id) { inf = item; break; }
        }
        if (inf == null) return "{\"error\":\"Partido no encontrado\"}";

        StringBuilder sb = new StringBuilder("{");
        sb.append("\"id\":").append(inf.getPartidoId()).append(",")
          .append("\"local\":\"").append(escJson(nombre(inf.getEquipoLocal()))).append("\",")
          .append("\"visitante\":\"").append(escJson(nombre(inf.getEquipoVisitante()))).append("\",")
          .append("\"resultado\":\"").append(escJson(inf.getResultado())).append("\",")
          .append("\"golesLocal\":").append(contarGoles(inf.getGoles(), inf.getEquipoLocal())).append(",")
          .append("\"golesVisitante\":").append(contarGoles(inf.getGoles(), inf.getEquipoVisitante())).append(",")
          .append("\"fecha\":\"").append(escJson(inf.getFecha())).append("\",")
          .append("\"estadio\":\"").append(escJson(inf.getEstadio())).append("\",")
          .append("\"fase\":\"").append(fase(inf.getPartidoId())).append("\",")
          .append("\"posesionLocal\":").append(inf.getPosesionLocal()).append(",")
          .append("\"posesionVisitante\":").append(inf.getPosesionVisitante()).append(",")
          .append("\"huboTiempoExtra\":").append(inf.isHuboTiempoExtra()).append(",")
          .append("\"huboRoja\":").append(inf.isHuboRoja()).append(",")
          .append("\"totalGoles\":").append(inf.getTotalGoles()).append(",")
          .append("\"totalTarjetas\":").append(inf.getTotalTarjetas()).append(",")
          .append("\"resumen\":\"").append(escJson(inf.getResumenPeriodistico())).append("\",");

        sb.append("\"goles\":[");
        List<EventoPeriodistico> goles = inf.getGoles();
        for (int i = 0; i < goles.size(); i++) {
            EventoPeriodistico g = goles.get(i);
            if (i > 0) sb.append(",");
            sb.append("{\"minuto\":").append(g.getMinuto())
              .append(",\"equipo\":\"").append(escJson(nombre(g.getEquipo()))).append("\"")
              .append(",\"jugador\":\"").append(escJson(g.getJugador())).append("\"")
              .append(",\"periodo\":\"").append(escJson(g.getPeriodo())).append("\"")
              .append("}");
        }
        sb.append("],\"tarjetas\":[");

        List<EventoPeriodistico> tarjetas = inf.getTarjetas();
        for (int i = 0; i < tarjetas.size(); i++) {
            EventoPeriodistico t = tarjetas.get(i);
            if (i > 0) sb.append(",");
            sb.append("{\"minuto\":").append(t.getMinuto())
              .append(",\"tipo\":\"").append(escJson(t.getTipo())).append("\"")
              .append(",\"equipo\":\"").append(escJson(nombre(t.getEquipo()))).append("\"")
              .append(",\"jugador\":\"").append(escJson(t.getJugador())).append("\"")
              .append("}");
        }
        sb.append("]}");
        return sb.toString();
    }

    private String getEquipos() {
        List<Equipo> equipos = new AdaptadorTxtMundial().obtenerEquipos();
        Collections.sort(equipos, new Comparator<Equipo>() {
            public int compare(Equipo a, Equipo b) {
                if (b.getPuntos() != a.getPuntos()) return b.getPuntos() - a.getPuntos();
                return b.getGolesAFavor() - a.getGolesAFavor();
            }
        });
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < equipos.size(); i++) {
            Equipo e = equipos.get(i);
            if (i > 0) sb.append(",");
            sb.append("{")
              .append("\"nombre\":\"").append(escJson(nombre(e.getNombre()))).append("\",")
              .append("\"clave\":\"").append(escJson(norm(e.getNombre()))).append("\",")
              .append("\"anioFundacion\":").append(e.getAnioFundacion()).append(",")
              .append("\"puntos\":").append(e.getPuntos()).append(",")
              .append("\"golesAFavor\":").append(e.getGolesAFavor()).append(",")
              .append("\"golesEnContra\":").append(e.getGolesEnContra()).append(",")
              .append("\"diferencia\":").append(e.getDiferenciaGoles()).append(",")
              .append("\"cantidadJugadores\":").append(e.getCantidadJugadores())
              .append("}");
        }
        sb.append("]");
        return sb.toString();
    }

    private String getEquipoDetalle(String clave) {
        List<Equipo> equipos = new AdaptadorTxtMundial().obtenerEquipos();
        String busqueda = norm(clave);
        Equipo encontrado = null;
        for (Equipo e : equipos) {
            if (norm(e.getNombre()).equals(busqueda)) { encontrado = e; break; }
        }
        if (encontrado == null) return "{\"error\":\"Equipo no encontrado\"}";

        StringBuilder sb = new StringBuilder("{");
        sb.append("\"nombre\":\"").append(escJson(nombre(encontrado.getNombre()))).append("\",")
          .append("\"anioFundacion\":").append(encontrado.getAnioFundacion()).append(",")
          .append("\"puntos\":").append(encontrado.getPuntos()).append(",")
          .append("\"golesAFavor\":").append(encontrado.getGolesAFavor()).append(",")
          .append("\"golesEnContra\":").append(encontrado.getGolesEnContra()).append(",")
          .append("\"diferencia\":").append(encontrado.getDiferenciaGoles()).append(",")
          .append("\"jugadores\":[");

        List<Jugador> jugadores = encontrado.getListaJugadores();
        for (int i = 0; i < jugadores.size(); i++) {
            Jugador j = jugadores.get(i);
            if (i > 0) sb.append(",");
            sb.append("{\"nombre\":\"").append(escJson(j.getNombreCompleto())).append("\"")
              .append(",\"posicion\":\"").append(escJson(j.getPosicion())).append("\"")
              .append(",\"goles\":").append(j.getGoles())
              .append(",\"tarjetasAmarillas\":").append(j.getTarjetasAmarillas())
              .append(",\"tarjetasRojas\":").append(j.getTarjetasRojas())
              .append("}");
        }
        sb.append("]}");
        return sb.toString();
    }

    private String getRankings() {
        List<Equipo> equipos = new AdaptadorTxtMundial().obtenerEquipos();
        Collections.sort(equipos, new Comparator<Equipo>() {
            public int compare(Equipo a, Equipo b) {
                if (b.getPuntos() != a.getPuntos()) return b.getPuntos() - a.getPuntos();
                if (b.getDiferenciaGoles() != a.getDiferenciaGoles()) return b.getDiferenciaGoles() - a.getDiferenciaGoles();
                return b.getGolesAFavor() - a.getGolesAFavor();
            }
        });
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < equipos.size(); i++) {
            Equipo e = equipos.get(i);
            if (i > 0) sb.append(",");
            sb.append("{")
              .append("\"posicion\":").append(i + 1).append(",")
              .append("\"nombre\":\"").append(escJson(nombre(e.getNombre()))).append("\",")
              .append("\"clave\":\"").append(escJson(norm(e.getNombre()))).append("\",")
              .append("\"puntos\":").append(e.getPuntos()).append(",")
              .append("\"golesAFavor\":").append(e.getGolesAFavor()).append(",")
              .append("\"golesEnContra\":").append(e.getGolesEnContra()).append(",")
              .append("\"diferencia\":").append(e.getDiferenciaGoles())
              .append("}");
        }
        sb.append("]");
        return sb.toString();
    }

    private String getGoleadores() {
        List<InformePeriodistico> informes = new AdaptadorJsonPeriodistico().obtenerInformes();
        Map<String, Integer> conteo = new LinkedHashMap<>();
        Map<String, String> equipoPorJugador = new HashMap<>();

        for (InformePeriodistico inf : informes) {
            for (EventoPeriodistico g : inf.getGoles()) {
                String jugador = g.getJugador();
                if (jugador == null || jugador.isEmpty()) continue;
                conteo.put(jugador, conteo.getOrDefault(jugador, 0) + 1);
                equipoPorJugador.put(jugador, nombre(g.getEquipo()));
            }
        }

        List<Map.Entry<String, Integer>> lista = new ArrayList<>(conteo.entrySet());
        Collections.sort(lista, new Comparator<Map.Entry<String, Integer>>() {
            public int compare(Map.Entry<String, Integer> a, Map.Entry<String, Integer> b) {
                return b.getValue() - a.getValue();
            }
        });

        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < lista.size(); i++) {
            Map.Entry<String, Integer> e = lista.get(i);
            if (i > 0) sb.append(",");
            sb.append("{")
              .append("\"posicion\":").append(i + 1).append(",")
              .append("\"jugador\":\"").append(escJson(e.getKey())).append("\",")
              .append("\"equipo\":\"").append(escJson(equipoPorJugador.get(e.getKey()))).append("\",")
              .append("\"goles\":").append(e.getValue())
              .append("}");
        }
        sb.append("]");
        return sb.toString();
    }

    private int contarGoles(List<EventoPeriodistico> goles, String equipo) {
        int count = 0;
        String clave = norm(equipo);
        for (EventoPeriodistico g : goles) {
            if (norm(g.getEquipo()).equals(clave)) count++;
        }
        return count;
    }

    private String nombre(String clave) {
        if (clave == null) return "";
        String resultado = NOMBRES.get(norm(clave));
        return resultado != null ? resultado : clave;
    }

    private String norm(String s) {
        if (s == null) return "";
        return s.toUpperCase(Locale.ROOT)
                .replace("É", "E").replace("Á", "A").replace("Ó", "O")
                .replace("Í", "I").replace("Ú", "U").replace("Ñ", "N")
                .trim();
    }

    private String fase(int id) {
        if (id <= 8)  return "OCTAVOS DE FINAL";
        if (id <= 12) return "CUARTOS DE FINAL";
        if (id <= 14) return "SEMIFINALES";
        return "FINAL";
    }

    private static String escJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}
