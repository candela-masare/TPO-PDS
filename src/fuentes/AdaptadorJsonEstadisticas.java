package fuentes;

import interfaces.ProveedorDatosDeportivos;
import modelo.Equipo;
import modelo.EventoPartido;
import modelo.Jugador;
import modelo.Partido;
import modelo.TipoEvento;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * ADAPTER: adapta estadisticas en formato JSON a la interfaz comun
 * ProveedorDatosDeportivos.
 */
public class AdaptadorJsonEstadisticas implements ProveedorDatosDeportivos {

    private static final String RUTA_JSON = "data/estadisticas_partidos_mundial.json";

    private String rutaArchivo;

    public AdaptadorJsonEstadisticas() {
        this.rutaArchivo = RUTA_JSON;
    }

    public AdaptadorJsonEstadisticas(String rutaArchivo) {
        this.rutaArchivo = rutaArchivo;
    }

    @Override
    public List<Equipo> obtenerEquipos() {
        return new ArrayList<>(cargarEquiposPorNombre().values());
    }

    @Override
    public List<Partido> obtenerPartidosEnVivo() {
        Map<String, Equipo> equipos = cargarEquiposPorNombre();
        List<Partido> partidos = new ArrayList<>();

        for (PartidoJson partidoJson : leerPartidosJson()) {
            Partido partido = new Partido(
                    buscarEquipo(equipos, partidoJson.local),
                    buscarEquipo(equipos, partidoJson.visitante),
                    "0-0");

            for (EventoJson eventoJson : partidoJson.eventos) {
                Equipo equipo = buscarEquipo(equipos, eventoJson.equipo);
                Jugador jugador = buscarOCrearJugador(equipo, eventoJson.jugador);
                partido.agregarEvento(new EventoPartido(
                        eventoJson.minuto,
                        eventoJson.tipo,
                        jugador));
            }

            partido.setResultado(partidoJson.resultadoTexto);
            partidos.add(partido);
        }

        return partidos;
    }

    private Map<String, Equipo> cargarEquiposPorNombre() {
        Map<String, Equipo> equipos = new LinkedHashMap<>();

        for (PartidoJson partido : leerPartidosJson()) {
            Equipo local = obtenerOCrearEquipo(equipos, partido.local);
            Equipo visitante = obtenerOCrearEquipo(equipos, partido.visitante);

            local.setGolesAFavor(local.getGolesAFavor() + partido.golesLocal);
            local.setGolesEnContra(local.getGolesEnContra() + partido.golesVisitante);
            visitante.setGolesAFavor(visitante.getGolesAFavor() + partido.golesVisitante);
            visitante.setGolesEnContra(visitante.getGolesEnContra() + partido.golesLocal);

            if (partido.golesLocal > partido.golesVisitante) {
                local.setPuntos(local.getPuntos() + 3);
            } else if (partido.golesVisitante > partido.golesLocal) {
                visitante.setPuntos(visitante.getPuntos() + 3);
            } else {
                local.setPuntos(local.getPuntos() + 1);
                visitante.setPuntos(visitante.getPuntos() + 1);
            }
        }

        return equipos;
    }

    private List<PartidoJson> leerPartidosJson() {
        String json = leerArchivo();
        List<PartidoJson> partidos = new ArrayList<>();

        for (String partidoObjeto : extraerObjetosDeArray(json, "partidos")) {
            String partido = extraerObjeto(partidoObjeto, "partido");
            String resultado = extraerObjeto(partidoObjeto, "resultado");
            String eventos = extraerObjeto(partidoObjeto, "eventosEstadisticos");

            PartidoJson partidoJson = new PartidoJson(
                    extraerTexto(partido, "local"),
                    extraerTexto(partido, "visitante"),
                    extraerTexto(resultado, "texto"),
                    extraerEntero(resultado, "golesLocal"),
                    extraerEntero(resultado, "golesVisitante"));

            cargarEventos(eventos, "goles", TipoEvento.GOL, partidoJson.eventos);
            cargarEventos(eventos, "tarjetasAmarillas", TipoEvento.TARJETA_AMARILLA, partidoJson.eventos);
            cargarEventos(eventos, "tarjetasRojas", TipoEvento.TARJETA_ROJA, partidoJson.eventos);
            cargarEventosTiempoExtra(eventos, partidoJson.eventos);

            partidos.add(partidoJson);
        }

        return partidos;
    }

    private void cargarEventos(String json, String nombreArray, TipoEvento tipo, List<EventoJson> eventos) {
        for (String eventoObjeto : extraerObjetosDeArray(json, nombreArray)) {
            eventos.add(new EventoJson(
                    extraerEntero(eventoObjeto, "minuto"),
                    tipo,
                    extraerTexto(eventoObjeto, "equipo"),
                    extraerTexto(eventoObjeto, "jugador")));
        }
    }

    private void cargarEventosTiempoExtra(String json, List<EventoJson> eventos) {
        for (String eventoObjeto : extraerObjetosDeArray(json, "tiempoExtra")) {
            String tipo = extraerTexto(eventoObjeto, "tipo");
            if ("gol".equalsIgnoreCase(tipo)) {
                eventos.add(new EventoJson(
                        extraerEntero(eventoObjeto, "minuto"),
                        TipoEvento.GOL,
                        extraerTexto(eventoObjeto, "equipo"),
                        extraerTexto(eventoObjeto, "jugador")));
            }
        }
    }

    private String leerArchivo() {
        try {
            return new String(Files.readAllBytes(Paths.get(rutaArchivo)), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("No se pudo leer el archivo JSON: " + rutaArchivo, e);
        }
    }

    private Equipo obtenerOCrearEquipo(Map<String, Equipo> equipos, String nombre) {
        String clave = clave(nombre);
        Equipo equipo = equipos.get(clave);
        if (equipo == null) {
            equipo = new Equipo(nombre, 0);
            equipos.put(clave, equipo);
        }
        return equipo;
    }

    private Equipo buscarEquipo(Map<String, Equipo> equipos, String nombre) {
        Equipo equipo = equipos.get(clave(nombre));
        if (equipo == null) {
            throw new IllegalArgumentException("No existe el equipo en el JSON: " + nombre);
        }
        return equipo;
    }

    private Jugador buscarOCrearJugador(Equipo equipo, String nombreJugador) {
        for (Jugador jugador : equipo.getListaJugadores()) {
            if (jugador.getNombreCompleto().equalsIgnoreCase(nombreJugador)) {
                return jugador;
            }
        }

        Jugador jugador = new Jugador(nombreJugador, "Sin posicion", equipo);
        equipo.agregarJugador(jugador);
        return jugador;
    }

    private List<String> extraerObjetosDeArray(String json, String nombreArray) {
        int posicionNombre = json.indexOf("\"" + nombreArray + "\"");
        if (posicionNombre == -1) {
            return new ArrayList<>();
        }

        int inicioArray = json.indexOf("[", posicionNombre);
        int finArray = encontrarCierre(json, inicioArray, '[', ']');
        String contenidoArray = json.substring(inicioArray + 1, finArray);
        List<String> objetos = new ArrayList<>();

        boolean dentroDeTexto = false;
        boolean escapado = false;
        int profundidad = 0;
        int inicioObjeto = -1;

        for (int i = 0; i < contenidoArray.length(); i++) {
            char caracter = contenidoArray.charAt(i);

            if (escapado) {
                escapado = false;
                continue;
            }
            if (caracter == '\\') {
                escapado = dentroDeTexto;
                continue;
            }
            if (caracter == '"') {
                dentroDeTexto = !dentroDeTexto;
                continue;
            }
            if (dentroDeTexto) {
                continue;
            }
            if (caracter == '{') {
                if (profundidad == 0) {
                    inicioObjeto = i;
                }
                profundidad++;
            } else if (caracter == '}') {
                profundidad--;
                if (profundidad == 0 && inicioObjeto != -1) {
                    objetos.add(contenidoArray.substring(inicioObjeto, i + 1));
                }
            }
        }

        return objetos;
    }

    private String extraerObjeto(String json, String nombreObjeto) {
        int posicionNombre = json.indexOf("\"" + nombreObjeto + "\"");
        if (posicionNombre == -1) {
            return "";
        }

        int inicioObjeto = json.indexOf("{", posicionNombre);
        int finObjeto = encontrarCierre(json, inicioObjeto, '{', '}');
        return json.substring(inicioObjeto, finObjeto + 1);
    }

    private int encontrarCierre(String texto, int inicio, char apertura, char cierre) {
        boolean dentroDeTexto = false;
        boolean escapado = false;
        int profundidad = 0;

        for (int i = inicio; i < texto.length(); i++) {
            char caracter = texto.charAt(i);

            if (escapado) {
                escapado = false;
                continue;
            }
            if (caracter == '\\') {
                escapado = dentroDeTexto;
                continue;
            }
            if (caracter == '"') {
                dentroDeTexto = !dentroDeTexto;
                continue;
            }
            if (dentroDeTexto) {
                continue;
            }
            if (caracter == apertura) {
                profundidad++;
            } else if (caracter == cierre) {
                profundidad--;
                if (profundidad == 0) {
                    return i;
                }
            }
        }

        throw new IllegalArgumentException("JSON invalido: no se encontro cierre para " + apertura);
    }

    private String extraerTexto(String json, String campo) {
        Matcher matcher = Pattern.compile("\"" + campo + "\"\\s*:\\s*\"((?:\\\\.|[^\"])*)\"").matcher(json);
        if (!matcher.find()) {
            return "";
        }
        return desescapar(matcher.group(1));
    }

    private int extraerEntero(String json, String campo) {
        Matcher matcher = Pattern.compile("\"" + campo + "\"\\s*:\\s*(\\d+)").matcher(json);
        if (!matcher.find()) {
            return 0;
        }
        return Integer.parseInt(matcher.group(1));
    }

    private String desescapar(String texto) {
        return texto
                .replace("\\u0027", "'")
                .replace("\\u0026", "&")
                .replace("\\\"", "\"")
                .replace("\\\\", "\\");
    }

    private String clave(String valor) {
        return valor.toUpperCase(Locale.ROOT);
    }

    private static class PartidoJson {
        private String local;
        private String visitante;
        private String resultadoTexto;
        private int golesLocal;
        private int golesVisitante;
        private List<EventoJson> eventos;

        private PartidoJson(String local, String visitante, String resultadoTexto, int golesLocal, int golesVisitante) {
            this.local = local;
            this.visitante = visitante;
            this.resultadoTexto = resultadoTexto;
            this.golesLocal = golesLocal;
            this.golesVisitante = golesVisitante;
            this.eventos = new ArrayList<>();
        }
    }

    private static class EventoJson {
        private int minuto;
        private TipoEvento tipo;
        private String equipo;
        private String jugador;

        private EventoJson(int minuto, TipoEvento tipo, String equipo, String jugador) {
            this.minuto = minuto;
            this.tipo = tipo;
            this.equipo = equipo;
            this.jugador = jugador;
        }
    }
}
