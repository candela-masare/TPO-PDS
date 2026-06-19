package servidor;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import estrategia.CriterioPorGoles;
import estrategia.CriterioPorPuntos;
import fuentes.AdaptadorJsonApuestas;
import fuentes.AdaptadorJsonApuestas.MercadoApuestas;
import fuentes.AdaptadorJsonPeriodistico;
import fuentes.AdaptadorJsonPeriodistico.EventoPeriodistico;
import fuentes.AdaptadorJsonPeriodistico.InformePeriodistico;
import fuentes.AdaptadorTxtMundial;
import modelo.Equipo;
import modelo.Jugador;
import modelo.Ranking;
import servicio.PlataformaDeportiva;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
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
        NOMBRES.put("ARGENTINA",      "Argentina");
        NOMBRES.put("MEXICO",         "México");
        NOMBRES.put("BRASIL",         "Brasil");
        NOMBRES.put("URUGUAY",        "Uruguay");
        NOMBRES.put("FRANCIA",        "Francia");
        NOMBRES.put("DINAMARCA",      "Dinamarca");
        NOMBRES.put("ESPANA",         "España");
        NOMBRES.put("CROACIA",        "Croacia");
        NOMBRES.put("PORTUGAL",       "Portugal");
        NOMBRES.put("BELGICA",        "Bélgica");
        NOMBRES.put("INGLATERRA",     "Inglaterra");
        NOMBRES.put("ESTADOS UNIDOS", "Estados Unidos");
        NOMBRES.put("ALEMANIA",       "Alemania");
        NOMBRES.put("JAPON",          "Japón");
        NOMBRES.put("PAISES BAJOS",   "Países Bajos");
        NOMBRES.put("ITALIA",         "Italia");
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
                respuesta = getPartidoDetalle(Integer.parseInt(ruta.substring("/partidos/".length())));
            } else if (ruta.equals("/equipos")) {
                respuesta = getEquipos();
            } else if (ruta.startsWith("/equipos/")) {
                respuesta = getEquipoDetalle(URLDecoder.decode(ruta.substring("/equipos/".length()), "UTF-8"));
            } else if (ruta.equals("/rankings")) {
                respuesta = getRankings("puntos");
            } else if (ruta.equals("/rankings/goles")) {
                respuesta = getRankings("goles");
            } else if (ruta.equals("/goleadores")) {
                respuesta = getGoleadores();
            } else if (ruta.equals("/mercados")) {
                respuesta = getMercados();
            } else if (ruta.startsWith("/mercados/")) {
                respuesta = getMercadoDetalle(Integer.parseInt(ruta.substring("/mercados/".length())));
            } else if (ruta.startsWith("/reporte/")) {
                respuesta = getReporte(Integer.parseInt(ruta.substring("/reporte/".length())));
            } else if (ruta.equals("/arbitros")) {
                respuesta = getArbitros();
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

    // ── PARTIDOS ──────────────────────────────────────────────────────────────

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
              .append("\"arbitro\":\"").append(escJson(nombreArbitro(inf.getPartidoId()))).append("\",")
              .append("\"arbitroNacionalidad\":\"").append(escJson(nacionalidadArbitro(inf.getPartidoId()))).append("\",")
              .append("\"fase\":\"").append(fase(inf.getPartidoId())).append("\",")
              .append("\"huboTiempoExtra\":").append(inf.isHuboTiempoExtra()).append(",")
              .append("\"huboRoja\":").append(inf.isHuboRoja())
              .append("}");
        }
        return sb.append("]").toString();
    }

    private String getPartidoDetalle(int id) {
        InformePeriodistico inf = buscarInforme(id);
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
          .append("\"arbitro\":\"").append(escJson(nombreArbitro(inf.getPartidoId()))).append("\",")
          .append("\"arbitroNacionalidad\":\"").append(escJson(nacionalidadArbitro(inf.getPartidoId()))).append("\",")
          .append("\"fase\":\"").append(fase(inf.getPartidoId())).append("\",")
          .append("\"posesionLocal\":").append(inf.getPosesionLocal()).append(",")
          .append("\"posesionVisitante\":").append(inf.getPosesionVisitante()).append(",")
          .append("\"dominadorPosesion\":\"").append(escJson(nombre(inf.getDominadorPosesion()))).append("\",")
          .append("\"huboTiempoExtra\":").append(inf.isHuboTiempoExtra()).append(",")
          .append("\"huboRoja\":").append(inf.isHuboRoja()).append(",")
          .append("\"totalGoles\":").append(inf.getTotalGoles()).append(",")
          .append("\"totalTarjetas\":").append(inf.getTotalTarjetas()).append(",")
          .append("\"resumen\":\"").append(escJson(inf.getResumenPeriodistico())).append("\",");

        sb.append("\"goles\":[");
        appendEventos(sb, inf.getGoles());
        sb.append("],\"tarjetas\":[");
        appendEventos(sb, inf.getTarjetas());
        sb.append("]}");
        return sb.toString();
    }

    // ── EQUIPOS ───────────────────────────────────────────────────────────────

    private String getEquipos() {
        List<Equipo> equipos = ordenarEquiposPorPuntos(new AdaptadorTxtMundial().obtenerEquipos());
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < equipos.size(); i++) {
            Equipo e = equipos.get(i);
            if (i > 0) sb.append(",");
            appendEquipoBasico(sb, e);
        }
        return sb.append("]").toString();
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
        return sb.append("]}").toString();
    }

    // ── RANKINGS ──────────────────────────────────────────────────────────────

    private String getRankings(String criterio) {
        AdaptadorTxtMundial adaptador = new AdaptadorTxtMundial();
        PlataformaDeportiva plataforma = new PlataformaDeportiva(adaptador);

        if ("goles".equals(criterio)) {
            plataforma.setCriterioRanking(new CriterioPorGoles());
        } else {
            plataforma.setCriterioRanking(new CriterioPorPuntos());
        }

        List<Ranking> rankings = plataforma.generarRanking(adaptador.obtenerEquipos());

        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < rankings.size(); i++) {
            Ranking r = rankings.get(i);
            Equipo e = r.getEquipo();
            if (i > 0) sb.append(",");
            sb.append("{")
              .append("\"posicion\":").append(r.getPosicion()).append(",")
              .append("\"nombre\":\"").append(escJson(nombre(e.getNombre()))).append("\",")
              .append("\"clave\":\"").append(escJson(norm(e.getNombre()))).append("\",")
              .append("\"puntos\":").append(e.getPuntos()).append(",")
              .append("\"golesAFavor\":").append(e.getGolesAFavor()).append(",")
              .append("\"golesEnContra\":").append(e.getGolesEnContra()).append(",")
              .append("\"diferencia\":").append(e.getDiferenciaGoles())
              .append("}");
        }
        return sb.append("]").toString();
    }

    // ── GOLEADORES ────────────────────────────────────────────────────────────

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
        return sb.append("]").toString();
    }

    // ── MERCADOS (CASA DE APUESTAS) ───────────────────────────────────────────

    private String getMercados() {
        List<MercadoApuestas> mercados = new AdaptadorJsonApuestas().obtenerMercados();
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < mercados.size(); i++) {
            MercadoApuestas m = mercados.get(i);
            if (i > 0) sb.append(",");
            sb.append("{")
              .append("\"id\":").append(m.getPartidoId()).append(",")
              .append("\"local\":\"").append(escJson(nombre(m.getEquipoLocal()))).append("\",")
              .append("\"visitante\":\"").append(escJson(nombre(m.getEquipoVisitante()))).append("\",")
              .append("\"resultado\":\"").append(escJson(m.getResultado())).append("\",")
              .append("\"mercadoDestacado\":\"").append(escJson(m.getMercadoDestacado())).append("\",")
              .append("\"riesgo\":\"").append(escJson(m.getRiesgoParaCuota())).append("\",")
              .append("\"totalGoles\":").append(m.getTotalGoles()).append(",")
              .append("\"fase\":\"").append(fase(m.getPartidoId())).append("\"")
              .append("}");
        }
        return sb.append("]").toString();
    }

    private String getMercadoDetalle(int id) {
        List<MercadoApuestas> mercados = new AdaptadorJsonApuestas().obtenerMercados();
        MercadoApuestas m = null;
        for (MercadoApuestas item : mercados) {
            if (item.getPartidoId() == id) { m = item; break; }
        }
        if (m == null) return "{\"error\":\"Mercado no encontrado\"}";

        StringBuilder sb = new StringBuilder("{");
        sb.append("\"id\":").append(m.getPartidoId()).append(",")
          .append("\"local\":\"").append(escJson(nombre(m.getEquipoLocal()))).append("\",")
          .append("\"visitante\":\"").append(escJson(nombre(m.getEquipoVisitante()))).append("\",")
          .append("\"resultado\":\"").append(escJson(m.getResultado())).append("\",")
          .append("\"fase\":\"").append(fase(m.getPartidoId())).append("\",")
          .append("\"golesLocal\":").append(m.getGolesLocal()).append(",")
          .append("\"golesVisitante\":").append(m.getGolesVisitante()).append(",")
          .append("\"totalGoles\":").append(m.getTotalGoles()).append(",")
          .append("\"ambosAnotaron\":").append(m.isAmbosEquiposAnotaron()).append(",")
          .append("\"tarjetasAmarillas\":").append(m.getTarjetasAmarillas()).append(",")
          .append("\"tarjetasRojas\":").append(m.getTarjetasRojas()).append(",")
          .append("\"totalTarjetas\":").append(m.getTotalTarjetas()).append(",")
          .append("\"posesionLocal\":").append(m.getPosesionLocal()).append(",")
          .append("\"posesionVisitante\":").append(m.getPosesionVisitante()).append(",")
          .append("\"dominadorPosesion\":\"").append(escJson(nombre(m.getDominadorPosesion()))).append("\",")
          .append("\"huboRoja\":").append(m.isHuboRoja()).append(",")
          .append("\"huboTiempoExtra\":").append(m.isHuboTiempoExtra()).append(",")
          .append("\"mercadoDestacado\":\"").append(escJson(m.getMercadoDestacado())).append("\",")
          .append("\"riesgo\":\"").append(escJson(m.getRiesgoParaCuota())).append("\"")
          .append("}");
        return sb.toString();
    }

    // ── REPORTE AVANZADO (ANALISTA) ───────────────────────────────────────────

    private String getReporte(int id) {
        InformePeriodistico inf = buscarInforme(id);
        if (inf == null) return "{\"error\":\"Partido no encontrado\"}";

        int golesLocal    = contarGoles(inf.getGoles(), inf.getEquipoLocal());
        int golesVisitante = contarGoles(inf.getGoles(), inf.getEquipoVisitante());
        double promedio   = inf.getTotalGoles() / 2.0;

        String goleador = "Sin goleadores";
        int maxGoles = 0;
        Map<String, Integer> golesPorJugador = new LinkedHashMap<>();
        Map<String, String> equipoPorJugador = new HashMap<>();
        for (EventoPeriodistico g : inf.getGoles()) {
            String jug = g.getJugador();
            if (jug == null || jug.isEmpty()) continue;
            golesPorJugador.put(jug, golesPorJugador.getOrDefault(jug, 0) + 1);
            equipoPorJugador.put(jug, nombre(g.getEquipo()));
        }
        for (Map.Entry<String, Integer> e : golesPorJugador.entrySet()) {
            if (e.getValue() > maxGoles) { maxGoles = e.getValue(); goleador = e.getKey(); }
        }
        String equipoGoleador = equipoPorJugador.getOrDefault(goleador, "");

        StringBuilder sb = new StringBuilder("{");
        sb.append("\"id\":").append(inf.getPartidoId()).append(",")
          .append("\"local\":\"").append(escJson(nombre(inf.getEquipoLocal()))).append("\",")
          .append("\"visitante\":\"").append(escJson(nombre(inf.getEquipoVisitante()))).append("\",")
          .append("\"resultado\":\"").append(escJson(inf.getResultado())).append("\",")
          .append("\"golesLocal\":").append(golesLocal).append(",")
          .append("\"golesVisitante\":").append(golesVisitante).append(",")
          .append("\"fecha\":\"").append(escJson(inf.getFecha())).append("\",")
          .append("\"estadio\":\"").append(escJson(inf.getEstadio())).append("\",")
          .append("\"fase\":\"").append(fase(id)).append("\",")
          .append("\"totalEventos\":").append(inf.getTotalGoles() + inf.getTotalTarjetas()).append(",")
          .append("\"promedioGoles\":").append(String.format(Locale.US, "%.1f", promedio)).append(",")
          .append("\"goleadorPartido\":\"").append(escJson(goleador)).append("\",")
          .append("\"equipoGoleador\":\"").append(escJson(equipoGoleador)).append("\",")
          .append("\"golesGoleador\":").append(maxGoles).append(",")
          .append("\"posesionLocal\":").append(inf.getPosesionLocal()).append(",")
          .append("\"posesionVisitante\":").append(inf.getPosesionVisitante()).append(",")
          .append("\"dominadorPosesion\":\"").append(escJson(nombre(inf.getDominadorPosesion()))).append("\",")
          .append("\"totalTarjetas\":").append(inf.getTotalTarjetas()).append(",")
          .append("\"huboTiempoExtra\":").append(inf.isHuboTiempoExtra()).append(",")
          .append("\"huboRoja\":").append(inf.isHuboRoja())
          .append("}");
        return sb.toString();
    }

    // ── ÁRBITROS ──────────────────────────────────────────────────────────────

    private String getArbitros() throws IOException {
        List<String> lineas = Files.readAllLines(Paths.get("data/arbitros.csv"), StandardCharsets.UTF_8);
        StringBuilder sb = new StringBuilder("[");
        boolean primero = true;
        for (int i = 1; i < lineas.size(); i++) {
            String[] col = lineas.get(i).split(",", -1);
            if (col.length < 4) continue;
            if (!primero) sb.append(",");
            primero = false;
            sb.append("{")
              .append("\"id\":\"").append(escJson(col[0].trim())).append("\",")
              .append("\"nombre\":\"").append(escJson(col[1].trim())).append("\",")
              .append("\"apellido\":\"").append(escJson(col[2].trim())).append("\",")
              .append("\"nacionalidad\":\"").append(escJson(col[3].trim())).append("\"")
              .append("}");
        }
        return sb.append("]").toString();
    }

    // ── HELPERS ───────────────────────────────────────────────────────────────

    private String nombreArbitro(int partidoId) {
        String[] arbitro = arbitroPorPartido(partidoId);
        if (arbitro == null) return "Sin arbitro asignado";
        return arbitro[1] + " " + arbitro[2];
    }

    private String nacionalidadArbitro(int partidoId) {
        String[] arbitro = arbitroPorPartido(partidoId);
        if (arbitro == null) return "";
        return arbitro[3];
    }

    private String[] arbitroPorPartido(int partidoId) {
        try {
            List<String> lineas = Files.readAllLines(Paths.get("data/arbitros.csv"), StandardCharsets.UTF_8);
            int indice = partidoId;
            if (indice <= 0 || indice >= lineas.size()) return null;
            String[] col = lineas.get(indice).split(",", -1);
            if (col.length < 4) return null;
            return new String[] { col[0].trim(), col[1].trim(), col[2].trim(), col[3].trim() };
        } catch (IOException e) {
            return null;
        }
    }

    private InformePeriodistico buscarInforme(int id) {
        for (InformePeriodistico inf : new AdaptadorJsonPeriodistico().obtenerInformes()) {
            if (inf.getPartidoId() == id) return inf;
        }
        return null;
    }

    private void appendEventos(StringBuilder sb, List<EventoPeriodistico> eventos) {
        for (int i = 0; i < eventos.size(); i++) {
            EventoPeriodistico ev = eventos.get(i);
            if (i > 0) sb.append(",");
            sb.append("{\"minuto\":").append(ev.getMinuto())
              .append(",\"tipo\":\"").append(escJson(ev.getTipo())).append("\"")
              .append(",\"equipo\":\"").append(escJson(nombre(ev.getEquipo()))).append("\"")
              .append(",\"jugador\":\"").append(escJson(ev.getJugador())).append("\"")
              .append(",\"periodo\":\"").append(escJson(ev.getPeriodo())).append("\"")
              .append("}");
        }
    }

    private void appendEquipoBasico(StringBuilder sb, Equipo e) {
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

    private List<Equipo> ordenarEquiposPorPuntos(List<Equipo> equipos) {
        Collections.sort(equipos, new Comparator<Equipo>() {
            public int compare(Equipo a, Equipo b) {
                if (b.getPuntos() != a.getPuntos()) return b.getPuntos() - a.getPuntos();
                return b.getGolesAFavor() - a.getGolesAFavor();
            }
        });
        return equipos;
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
