package fuentes;

import interfaces.ProveedorDatosDeportivos;
import modelo.Equipo;
import modelo.Partido;

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
 * ADAPTER: adapta datos periodisticos en formato JSON a la interfaz comun
 * ProveedorDatosDeportivos y expone informes listos para cronicas.
 */
public class AdaptadorJsonPeriodistico implements ProveedorDatosDeportivos {

    private static final String RUTA_JSON = "data/datos_periodisticos_mundial.json";

    private String rutaArchivo;

    public AdaptadorJsonPeriodistico() {
        this.rutaArchivo = RUTA_JSON;
    }

    public AdaptadorJsonPeriodistico(String rutaArchivo) {
        this.rutaArchivo = rutaArchivo;
    }

    @Override
    public List<Equipo> obtenerEquipos() {
        Map<String, Equipo> equipos = new LinkedHashMap<>();

        for (InformePeriodistico informe : obtenerInformes()) {
            obtenerOCrearEquipo(equipos, informe.getEquipoLocal());
            obtenerOCrearEquipo(equipos, informe.getEquipoVisitante());
        }

        return new ArrayList<>(equipos.values());
    }

    @Override
    public List<Partido> obtenerPartidosEnVivo() {
        Map<String, Equipo> equipos = new LinkedHashMap<>();
        List<Partido> partidos = new ArrayList<>();

        for (InformePeriodistico informe : obtenerInformes()) {
            Partido partido = new Partido(
                    obtenerOCrearEquipo(equipos, informe.getEquipoLocal()),
                    obtenerOCrearEquipo(equipos, informe.getEquipoVisitante()),
                    informe.getResultado());
            partidos.add(partido);
        }

        return partidos;
    }

    public List<InformePeriodistico> obtenerInformes() {
        String json = leerArchivo();
        List<InformePeriodistico> informes = new ArrayList<>();

        for (String objetoInforme : extraerObjetosDeArray(json, "informes")) {
            String partido = extraerObjeto(objetoInforme, "partido");
            String posesion = extraerObjeto(objetoInforme, "posesion");
            String claves = extraerObjeto(objetoInforme, "clavesPeriodisticas");
            List<EventoPeriodistico> goles = extraerEventos(objetoInforme, "goles");
            List<EventoPeriodistico> tarjetas = extraerEventos(objetoInforme, "tarjetas");

            informes.add(new InformePeriodistico(
                    extraerEntero(objetoInforme, "partidoId"),
                    extraerTexto(partido, "local"),
                    extraerTexto(partido, "visitante"),
                    extraerTexto(objetoInforme, "fecha"),
                    extraerTexto(objetoInforme, "estadio"),
                    extraerTexto(objetoInforme, "resultado"),
                    extraerEntero(posesion, "posesionLocal"),
                    extraerEntero(posesion, "posesionVisitante"),
                    extraerEntero(claves, "totalGoles"),
                    extraerEntero(claves, "totalTarjetas"),
                    extraerTexto(claves, "dominadorPosesion"),
                    extraerBooleano(claves, "huboRoja"),
                    extraerBooleano(claves, "huboTiempoExtra"),
                    goles,
                    tarjetas,
                    extraerTexto(objetoInforme, "resumenPeriodistico")));
        }

        return informes;
    }

    public InformePeriodistico buscarInformePorPartido(String local, String visitante) {
        for (InformePeriodistico informe : obtenerInformes()) {
            if (clave(informe.getEquipoLocal()).equals(clave(local))
                    && clave(informe.getEquipoVisitante()).equals(clave(visitante))) {
                return informe;
            }
        }

        throw new IllegalArgumentException("No existe informe para el partido: " + local + " vs " + visitante);
    }

    private List<EventoPeriodistico> extraerEventos(String json, String nombreArray) {
        List<EventoPeriodistico> eventos = new ArrayList<>();

        for (String eventoObjeto : extraerObjetosDeArray(json, nombreArray)) {
            eventos.add(new EventoPeriodistico(
                    extraerEntero(eventoObjeto, "minuto"),
                    extraerTexto(eventoObjeto, "tipo"),
                    extraerTexto(eventoObjeto, "equipo"),
                    extraerTexto(eventoObjeto, "jugador"),
                    extraerTexto(eventoObjeto, "periodo")));
        }

        return eventos;
    }

    private String leerArchivo() {
        try {
            return new String(Files.readAllBytes(Paths.get(rutaArchivo)), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("No se pudo leer el archivo JSON periodistico: " + rutaArchivo, e);
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

    private boolean extraerBooleano(String json, String campo) {
        Matcher matcher = Pattern.compile("\"" + campo + "\"\\s*:\\s*(true|false)").matcher(json);
        return matcher.find() && Boolean.parseBoolean(matcher.group(1));
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

    public static class InformePeriodistico {
        private int partidoId;
        private String equipoLocal;
        private String equipoVisitante;
        private String fecha;
        private String estadio;
        private String resultado;
        private int posesionLocal;
        private int posesionVisitante;
        private int totalGoles;
        private int totalTarjetas;
        private String dominadorPosesion;
        private boolean huboRoja;
        private boolean huboTiempoExtra;
        private List<EventoPeriodistico> goles;
        private List<EventoPeriodistico> tarjetas;
        private String resumenPeriodistico;

        public InformePeriodistico(int partidoId, String equipoLocal, String equipoVisitante, String fecha,
                                   String estadio, String resultado, int posesionLocal, int posesionVisitante,
                                   int totalGoles, int totalTarjetas, String dominadorPosesion,
                                   boolean huboRoja, boolean huboTiempoExtra, List<EventoPeriodistico> goles,
                                   List<EventoPeriodistico> tarjetas, String resumenPeriodistico) {
            this.partidoId = partidoId;
            this.equipoLocal = equipoLocal;
            this.equipoVisitante = equipoVisitante;
            this.fecha = fecha;
            this.estadio = estadio;
            this.resultado = resultado;
            this.posesionLocal = posesionLocal;
            this.posesionVisitante = posesionVisitante;
            this.totalGoles = totalGoles;
            this.totalTarjetas = totalTarjetas;
            this.dominadorPosesion = dominadorPosesion;
            this.huboRoja = huboRoja;
            this.huboTiempoExtra = huboTiempoExtra;
            this.goles = goles;
            this.tarjetas = tarjetas;
            this.resumenPeriodistico = resumenPeriodistico;
        }

        public int getPartidoId() { return partidoId; }
        public String getEquipoLocal() { return equipoLocal; }
        public String getEquipoVisitante() { return equipoVisitante; }
        public String getFecha() { return fecha; }
        public String getEstadio() { return estadio; }
        public String getResultado() { return resultado; }
        public int getPosesionLocal() { return posesionLocal; }
        public int getPosesionVisitante() { return posesionVisitante; }
        public int getTotalGoles() { return totalGoles; }
        public int getTotalTarjetas() { return totalTarjetas; }
        public String getDominadorPosesion() { return dominadorPosesion; }
        public boolean isHuboRoja() { return huboRoja; }
        public boolean isHuboTiempoExtra() { return huboTiempoExtra; }
        public List<EventoPeriodistico> getGoles() { return goles; }
        public List<EventoPeriodistico> getTarjetas() { return tarjetas; }
        public String getResumenPeriodistico() { return resumenPeriodistico; }

        @Override
        public String toString() {
            return resumenPeriodistico;
        }
    }

    public static class EventoPeriodistico {
        private int minuto;
        private String tipo;
        private String equipo;
        private String jugador;
        private String periodo;

        public EventoPeriodistico(int minuto, String tipo, String equipo, String jugador, String periodo) {
            this.minuto = minuto;
            this.tipo = tipo;
            this.equipo = equipo;
            this.jugador = jugador;
            this.periodo = periodo;
        }

        public int getMinuto() { return minuto; }
        public String getTipo() { return tipo; }
        public String getEquipo() { return equipo; }
        public String getJugador() { return jugador; }
        public String getPeriodo() { return periodo; }

        @Override
        public String toString() {
            String tipoTexto = tipo == null || tipo.isEmpty() ? "" : tipo + " | ";
            return "Min " + minuto + " | " + tipoTexto + jugador + " (" + equipo + ")";
        }
    }
}
