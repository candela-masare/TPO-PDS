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
import java.util.stream.Collectors;

/**
 * ADAPTER: adapta tarjetas en formato CSV a la interfaz comun
 * ProveedorDatosDeportivos y expone las tarjetas como dato especifico.
 */
public class AdaptadorCsvTarjetas implements ProveedorDatosDeportivos {

    private static final String RUTA_CSV = "data/tarjetas_mundial.csv";

    private String rutaArchivo;

    public AdaptadorCsvTarjetas() {
        this.rutaArchivo = RUTA_CSV;
    }

    public AdaptadorCsvTarjetas(String rutaArchivo) {
        this.rutaArchivo = rutaArchivo;
    }

    @Override
    public List<Equipo> obtenerEquipos() {
        Map<String, Equipo> equipos = new LinkedHashMap<>();

        for (TarjetaPartido tarjeta : obtenerTarjetas()) {
            obtenerOCrearEquipo(equipos, tarjeta.getEquipoLocal());
            obtenerOCrearEquipo(equipos, tarjeta.getEquipoVisitante());
        }

        return new ArrayList<>(equipos.values());
    }

    @Override
    public List<Partido> obtenerPartidosEnVivo() {
        Map<String, Equipo> equipos = new LinkedHashMap<>();
        Map<Integer, Partido> partidos = new LinkedHashMap<>();

        for (TarjetaPartido tarjeta : obtenerTarjetas()) {
            Equipo local = obtenerOCrearEquipo(equipos, tarjeta.getEquipoLocal());
            Equipo visitante = obtenerOCrearEquipo(equipos, tarjeta.getEquipoVisitante());

            Partido partido = partidos.get(tarjeta.getPartidoId());
            if (partido == null) {
                partido = new Partido(local, visitante, "Tarjetas");
                partidos.put(tarjeta.getPartidoId(), partido);
            }

            Equipo equipoTarjeta = obtenerOCrearEquipo(equipos, tarjeta.getEquipo());
            Jugador jugador = buscarOCrearJugador(equipoTarjeta, tarjeta.getJugador());
            partido.agregarEvento(new EventoPartido(
                    tarjeta.getMinuto(),
                    convertirTipoEvento(tarjeta.getTipoTarjeta()),
                    jugador));
        }

        return new ArrayList<>(partidos.values());
    }

    public List<TarjetaPartido> obtenerTarjetas() {
        return leerFilasCsv().stream()
                .map(columnas -> new TarjetaPartido(
                        Integer.parseInt(columnas[0]),
                        columnas[1],
                        columnas[2],
                        columnas[3],
                        Integer.parseInt(columnas[4]),
                        columnas[5],
                        columnas[6],
                        columnas[7]))
                .collect(Collectors.toList());
    }

    public List<TarjetaPartido> buscarTarjetasPorPartido(String local, String visitante) {
        return obtenerTarjetas().stream()
                .filter(tarjeta -> clave(tarjeta.getEquipoLocal()).equals(clave(local))
                        && clave(tarjeta.getEquipoVisitante()).equals(clave(visitante)))
                .collect(Collectors.toList());
    }

    private List<String[]> leerFilasCsv() {
        try {
            return Files.readAllLines(Paths.get(rutaArchivo), StandardCharsets.UTF_8).stream()
                    .skip(1)
                    .filter(linea -> !linea.trim().isEmpty())
                    .map(linea -> linea.split(",", -1))
                    .filter(columnas -> columnas.length >= 8)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException("No se pudo leer el archivo CSV de tarjetas: " + rutaArchivo, e);
        }
    }

    private TipoEvento convertirTipoEvento(String tipoTarjeta) {
        if ("AMARILLA".equalsIgnoreCase(tipoTarjeta)) {
            return TipoEvento.TARJETA_AMARILLA;
        }
        if ("ROJA".equalsIgnoreCase(tipoTarjeta)) {
            return TipoEvento.TARJETA_ROJA;
        }
        throw new IllegalArgumentException("Tipo de tarjeta invalido: " + tipoTarjeta);
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

    private String clave(String valor) {
        return valor.toUpperCase(Locale.ROOT);
    }

    public static class TarjetaPartido {
        private int partidoId;
        private String equipoLocal;
        private String equipoVisitante;
        private String tipoTarjeta;
        private int minuto;
        private String equipo;
        private String jugador;
        private String periodo;

        public TarjetaPartido(int partidoId, String equipoLocal, String equipoVisitante, String tipoTarjeta,
                              int minuto, String equipo, String jugador, String periodo) {
            this.partidoId = partidoId;
            this.equipoLocal = equipoLocal;
            this.equipoVisitante = equipoVisitante;
            this.tipoTarjeta = tipoTarjeta;
            this.minuto = minuto;
            this.equipo = equipo;
            this.jugador = jugador;
            this.periodo = periodo;
        }

        public int getPartidoId() {
            return partidoId;
        }

        public String getEquipoLocal() {
            return equipoLocal;
        }

        public String getEquipoVisitante() {
            return equipoVisitante;
        }

        public String getTipoTarjeta() {
            return tipoTarjeta;
        }

        public int getMinuto() {
            return minuto;
        }

        public String getEquipo() {
            return equipo;
        }

        public String getJugador() {
            return jugador;
        }

        public String getPeriodo() {
            return periodo;
        }

        @Override
        public String toString() {
            return "Partido " + partidoId + " | " + tipoTarjeta + " | Min "
                    + minuto + " | " + jugador + " (" + equipo + ")";
        }
    }
}
