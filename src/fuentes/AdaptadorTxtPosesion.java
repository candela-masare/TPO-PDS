package fuentes;

import interfaces.ProveedorDatosDeportivos;
import modelo.Equipo;
import modelo.Partido;

import java.io.BufferedReader;
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
 * ADAPTER: adapta una fuente TXT de posesion de balon a la interfaz comun
 * ProveedorDatosDeportivos y expone las posesiones como dato especifico.
 */
public class AdaptadorTxtPosesion implements ProveedorDatosDeportivos {

    private static final String RUTA_TXT = "data/posesion_balon_mundial.txt";
    private static final Pattern PATRON_POSESION = Pattern.compile("^(.+)\\s+(\\d+)%\\s+-\\s+(.+)\\s+(\\d+)%$");

    private String rutaArchivo;

    public AdaptadorTxtPosesion() {
        this.rutaArchivo = RUTA_TXT;
    }

    public AdaptadorTxtPosesion(String rutaArchivo) {
        this.rutaArchivo = rutaArchivo;
    }

    @Override
    public List<Equipo> obtenerEquipos() {
        Map<String, Equipo> equipos = new LinkedHashMap<>();

        for (PosesionPartido posesion : obtenerPosesiones()) {
            obtenerOCrearEquipo(equipos, posesion.getEquipoLocal());
            obtenerOCrearEquipo(equipos, posesion.getEquipoVisitante());
        }

        return new ArrayList<>(equipos.values());
    }

    @Override
    public List<Partido> obtenerPartidosEnVivo() {
        Map<String, Equipo> equipos = new LinkedHashMap<>();
        List<Partido> partidos = new ArrayList<>();

        for (PosesionPartido posesion : obtenerPosesiones()) {
            Equipo local = obtenerOCrearEquipo(equipos, posesion.getEquipoLocal());
            Equipo visitante = obtenerOCrearEquipo(equipos, posesion.getEquipoVisitante());
            String resultado = "Posesion: " + posesion.getEquipoLocal() + " " + posesion.getPosesionLocal()
                    + "% - " + posesion.getEquipoVisitante() + " " + posesion.getPosesionVisitante() + "%";

            partidos.add(new Partido(local, visitante, resultado));
        }

        return partidos;
    }

    public List<PosesionPartido> obtenerPosesiones() {
        List<PosesionPartido> posesiones = new ArrayList<>();
        String local = null;
        String visitante = null;

        try (BufferedReader br = Files.newBufferedReader(Paths.get(rutaArchivo), StandardCharsets.UTF_8)) {
            String linea;
            while ((linea = br.readLine()) != null) {
                linea = linea.trim();

                if (linea.isEmpty()) {
                    continue;
                }

                if (linea.startsWith("PARTIDO:")) {
                    String[] equipos = valorDespuesDeDosPuntos(linea).split(" vs ");
                    if (equipos.length != 2) {
                        throw new IllegalArgumentException("Formato de partido invalido: " + linea);
                    }
                    local = equipos[0].trim();
                    visitante = equipos[1].trim();
                } else if (linea.startsWith("POSESION:") && local != null && visitante != null) {
                    PosesionPartido posesion = leerPosesion(valorDespuesDeDosPuntos(linea), local, visitante);
                    validarPosesion(posesion);
                    posesiones.add(posesion);
                    local = null;
                    visitante = null;
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("No se pudo leer el archivo TXT de posesion: " + rutaArchivo, e);
        }

        return posesiones;
    }

    public PosesionPartido buscarPosesion(String local, String visitante) {
        for (PosesionPartido posesion : obtenerPosesiones()) {
            if (clave(posesion.getEquipoLocal()).equals(clave(local))
                    && clave(posesion.getEquipoVisitante()).equals(clave(visitante))) {
                return posesion;
            }
        }

        throw new IllegalArgumentException("No existe posesion para el partido: " + local + " vs " + visitante);
    }

    private PosesionPartido leerPosesion(String linea, String localEsperado, String visitanteEsperado) {
        Matcher matcher = PATRON_POSESION.matcher(linea);
        if (!matcher.find()) {
            throw new IllegalArgumentException("Formato de posesion invalido: " + linea);
        }

        String local = matcher.group(1).trim();
        int posesionLocal = Integer.parseInt(matcher.group(2));
        String visitante = matcher.group(3).trim();
        int posesionVisitante = Integer.parseInt(matcher.group(4));

        if (!clave(local).equals(clave(localEsperado)) || !clave(visitante).equals(clave(visitanteEsperado))) {
            throw new IllegalArgumentException("La posesion no coincide con el partido: " + linea);
        }

        return new PosesionPartido(local, visitante, posesionLocal, posesionVisitante);
    }

    private void validarPosesion(PosesionPartido posesion) {
        int total = posesion.getPosesionLocal() + posesion.getPosesionVisitante();
        if (total != 100) {
            throw new IllegalArgumentException("La posesion debe sumar 100 en "
                    + posesion.getEquipoLocal() + " vs " + posesion.getEquipoVisitante());
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

    private String valorDespuesDeDosPuntos(String linea) {
        return linea.substring(linea.indexOf(":") + 1).trim();
    }

    private String clave(String valor) {
        return valor.toUpperCase(Locale.ROOT);
    }

    public static class PosesionPartido {
        private String equipoLocal;
        private String equipoVisitante;
        private int posesionLocal;
        private int posesionVisitante;

        public PosesionPartido(String equipoLocal, String equipoVisitante, int posesionLocal, int posesionVisitante) {
            this.equipoLocal = equipoLocal;
            this.equipoVisitante = equipoVisitante;
            this.posesionLocal = posesionLocal;
            this.posesionVisitante = posesionVisitante;
        }

        public String getEquipoLocal() {
            return equipoLocal;
        }

        public String getEquipoVisitante() {
            return equipoVisitante;
        }

        public int getPosesionLocal() {
            return posesionLocal;
        }

        public int getPosesionVisitante() {
            return posesionVisitante;
        }

        @Override
        public String toString() {
            return equipoLocal + " " + posesionLocal + "% - "
                    + equipoVisitante + " " + posesionVisitante + "%";
        }
    }
}
