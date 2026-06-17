package fuentes;

import interfaces.ProveedorDatosDeportivos;
import modelo.Equipo;
import modelo.Jugador;
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
import java.util.regex.Pattern;

/**
 * ADAPTER: adapta una fuente de datos en formato TXT (Mundial) a la interfaz
 * comun ProveedorDatosDeportivos.
 */
public class AdaptadorTxtMundial implements ProveedorDatosDeportivos {

    private static final String RUTA_EQUIPOS = "data/convocados_octavos_26_reales.txt";
    private static final String RUTA_PARTIDOS = "data/mundial_formato_final_completo.txt";

    private String rutaEquipos;
    private String rutaPartidos;

    public AdaptadorTxtMundial() {
        this.rutaEquipos = RUTA_EQUIPOS;
        this.rutaPartidos = RUTA_PARTIDOS;
    }

    public AdaptadorTxtMundial(String rutaEquipos, String rutaPartidos) {
        this.rutaEquipos = rutaEquipos;
        this.rutaPartidos = rutaPartidos;
    }

    @Override
    public List<Equipo> obtenerEquipos() {
        Map<String, Equipo> equipos = cargarEquiposPorClave();
        cargarEstadisticasDesdePartidos(equipos);
        return new ArrayList<>(equipos.values());
    }

    @Override
    public List<Partido> obtenerPartidosEnVivo() {
        Map<String, Equipo> equipos = cargarEquiposPorClave();
        cargarEstadisticasDesdePartidos(equipos);
        return cargarPartidos(equipos);
    }

    private Map<String, Equipo> cargarEquiposPorClave() {
        Map<String, Equipo> equipos = new LinkedHashMap<>();
        Equipo equipoActual = null;
        String nombreJugadorActual = null;

        try (BufferedReader br = Files.newBufferedReader(Paths.get(rutaEquipos), StandardCharsets.UTF_8)) {
            String linea;
            while ((linea = br.readLine()) != null) {
                linea = linea.trim();

                if (linea.startsWith("PAIS:") || linea.startsWith("PAÍS:") || linea.startsWith("PAÃS:")) {
                    String nombrePais = valorDespuesDeDosPuntos(linea);
                    equipoActual = new Equipo(nombrePais, 0);
                    equipos.put(clave(nombrePais), equipoActual);
                } else if (linea.startsWith("Fundacion") || linea.startsWith("Fundación") || linea.startsWith("FundaciÃ³n")) {
                    int anioFundacion = Integer.parseInt(valorDespuesDeDosPuntos(linea));
                    equipoActual = reemplazarEquipoConFundacion(equipos, equipoActual, anioFundacion);
                } else if (linea.matches("\\d+\\. .+")) {
                    nombreJugadorActual = linea.substring(linea.indexOf(".") + 1).trim();
                } else if (linea.startsWith("Posicion:") || linea.startsWith("Posición:") || linea.startsWith("PosiciÃ³n:")) {
                    String posicion = valorDespuesDeDosPuntos(linea);
                    if (equipoActual != null && nombreJugadorActual != null) {
                        equipoActual.agregarJugador(new Jugador(nombreJugadorActual, posicion, equipoActual));
                        nombreJugadorActual = null;
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("No se pudo leer el archivo TXT de equipos: " + rutaEquipos, e);
        }

        return equipos;
    }

    private List<Partido> cargarPartidos(Map<String, Equipo> equipos) {
        List<Partido> partidos = new ArrayList<>();
        String local = null;
        String visitante = null;

        try (BufferedReader br = Files.newBufferedReader(Paths.get(rutaPartidos), StandardCharsets.UTF_8)) {
            String linea;
            while ((linea = br.readLine()) != null) {
                linea = linea.trim();

                if (linea.startsWith("PARTIDO:")) {
                    String[] nombres = valorDespuesDeDosPuntos(linea).split(" vs ");
                    local = nombres[0].trim();
                    visitante = nombres[1].trim();
                } else if (linea.startsWith("Resultado Final:") && local != null && visitante != null) {
                    partidos.add(new Partido(
                            buscarEquipo(equipos, local),
                            buscarEquipo(equipos, visitante),
                            valorDespuesDeDosPuntos(linea)));
                    local = null;
                    visitante = null;
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("No se pudo leer el archivo TXT de partidos: " + rutaPartidos, e);
        }

        return partidos;
    }

    private void cargarEstadisticasDesdePartidos(Map<String, Equipo> equipos) {
        String local = null;
        String visitante = null;

        try (BufferedReader br = Files.newBufferedReader(Paths.get(rutaPartidos), StandardCharsets.UTF_8)) {
            String linea;
            while ((linea = br.readLine()) != null) {
                linea = linea.trim();

                if (linea.startsWith("PARTIDO:")) {
                    String[] nombres = valorDespuesDeDosPuntos(linea).split(" vs ");
                    local = nombres[0].trim();
                    visitante = nombres[1].trim();
                } else if (linea.startsWith("Resultado Final:") && local != null && visitante != null) {
                    int[] goles = extraerGoles(valorDespuesDeDosPuntos(linea), local, visitante);
                    aplicarResultado(buscarEquipo(equipos, local), buscarEquipo(equipos, visitante), goles[0], goles[1]);
                    local = null;
                    visitante = null;
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("No se pudo leer el archivo TXT de partidos: " + rutaPartidos, e);
        }
    }

    private Equipo reemplazarEquipoConFundacion(Map<String, Equipo> equipos, Equipo equipoAnterior, int anioFundacion) {
        if (equipoAnterior == null) {
            return null;
        }

        Equipo equipo = new Equipo(equipoAnterior.getNombre(), anioFundacion);
        equipos.put(clave(equipo.getNombre()), equipo);
        return equipo;
    }

    private void aplicarResultado(Equipo local, Equipo visitante, int golesLocal, int golesVisitante) {
        local.setGolesAFavor(local.getGolesAFavor() + golesLocal);
        visitante.setGolesAFavor(visitante.getGolesAFavor() + golesVisitante);

        if (golesLocal > golesVisitante) {
            local.setPuntos(local.getPuntos() + 3);
        } else if (golesVisitante > golesLocal) {
            visitante.setPuntos(visitante.getPuntos() + 3);
        } else {
            local.setPuntos(local.getPuntos() + 1);
            visitante.setPuntos(visitante.getPuntos() + 1);
        }
    }

    private int[] extraerGoles(String resultado, String local, String visitante) {
        Pattern patronMarcador = Pattern.compile("(\\d+)\\s*(?:\\(\\d+\\))?\\s*-\\s*(?:\\(\\d+\\))?\\s*(\\d+)");
        java.util.regex.Matcher matcher = patronMarcador.matcher(resultado);
        if (!matcher.find()) {
            throw new IllegalArgumentException("No se pudo leer el resultado entre " + local + " y " + visitante + ": " + resultado);
        }
        return new int[] {
                Integer.parseInt(matcher.group(1)),
                Integer.parseInt(matcher.group(2))
        };
    }

    private Equipo buscarEquipo(Map<String, Equipo> equipos, String nombre) {
        Equipo equipo = equipos.get(clave(nombre));
        if (equipo == null) {
            throw new IllegalArgumentException("No existe el equipo en el TXT: " + nombre);
        }
        return equipo;
    }

    private String valorDespuesDeDosPuntos(String linea) {
        return linea.substring(linea.indexOf(":") + 1).trim();
    }

    private String clave(String valor) {
        return valor.toUpperCase(Locale.ROOT);
    }
}
