package fuentes;

import interfaces.ProveedorDatosDeportivos;
import modelo.Equipo;
import modelo.Jugador;
import modelo.Partido;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AdaptadorCsvLiga implements ProveedorDatosDeportivos {

    private static final String RUTA_CSV = "data/liga_local.csv";
    private String rutaArchivo;

    public AdaptadorCsvLiga() {
        this.rutaArchivo = RUTA_CSV;
    }

    public AdaptadorCsvLiga(String rutaArchivo) {
        this.rutaArchivo = rutaArchivo;
    }

    @Override
    public List<Equipo> obtenerEquipos() {
        return new ArrayList<>(cargarEquiposPorNombre().values());
    }

    @Override
    public List<Partido> obtenerPartidosEnVivo() {
        Map<String, Equipo> equipos = cargarEquiposPorNombre();

        return leerFilasCsv().stream()
                .filter(columnas -> columnas[0].equalsIgnoreCase("PARTIDO"))
                .map(columnas -> new Partido(
                        buscarEquipo(equipos, columnas[8]),
                        buscarEquipo(equipos, columnas[9]),
                        columnas[10]))
                .collect(Collectors.toList());
    }

    private Map<String, Equipo> cargarEquiposPorNombre() {
        Map<String, Equipo> equipos = new LinkedHashMap<>();
        List<String[]> filas = leerFilasCsv();

        for (String[] columnas : filas) {
            if (columnas[0].equalsIgnoreCase("EQUIPO")) {
                Equipo equipo = new Equipo(
                        columnas[1],
                        Integer.parseInt(columnas[2]),
                        Integer.parseInt(columnas[3]),
                        Integer.parseInt(columnas[4]));
                equipos.put(equipo.getNombre(), equipo);
            }
        }

        for (String[] columnas : filas) {
            if (columnas[0].equalsIgnoreCase("JUGADOR")) {
                Equipo equipo = buscarEquipo(equipos, columnas[5]);
                equipo.agregarJugador(new Jugador(columnas[6], columnas[7], equipo));
            }
        }

        return equipos;
    }

    private List<String[]> leerFilasCsv() {
        try {
            return Files.readAllLines(Paths.get(rutaArchivo), StandardCharsets.UTF_8).stream()
                    .skip(1)
                    .filter(linea -> !linea.trim().isEmpty())
                    .map(linea -> linea.split(",", -1))
                    .filter(columnas -> columnas.length >= 11)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException("No se pudo leer el archivo CSV: " + rutaArchivo, e);
        }
    }

    private Equipo buscarEquipo(Map<String, Equipo> equipos, String nombre) {
        Equipo equipo = equipos.get(nombre);
        if (equipo == null) {
            throw new IllegalArgumentException("No existe el equipo en el CSV: " + nombre);
        }
        return equipo;
    }
}
