import estrategia.CriterioPorGoles;
import estrategia.CriterioPorPuntos;
import factory.AficionadoFactory;
import factory.AnalistaDeportivoFactory;
import factory.CasaApuestaFactory;
import factory.PeriodistaFactory;
import factory.UsuarioFactory;
import fuentes.AdaptadorCsvLiga;
import fuentes.AdaptadorTxtMundial;
import interfaces.ProveedorDatosDeportivos;
import interfaces.IUsuario;
import interfaces.Repositorio;
import interfaces.VistaTiempoReal;
import modelo.Arbitro;
import modelo.Equipo;
import modelo.EventoPartido;
import modelo.Jugador;
import modelo.Partido;
import modelo.Ranking;
import modelo.TipoEvento;
import persistencia.RepositorioEnMemoria;
import servicio.PlataformaDeportiva;
import vista.PantallaPartido;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Main {

    private static final String RUTA_ARBITROS = "data/arbitros.csv";

    public static void main(String[] args) {

        // ---------- ADAPTER TXT ----------
        ProveedorDatosDeportivos fuenteMundial = new AdaptadorTxtMundial();
        PlataformaDeportiva plataforma = new PlataformaDeportiva(fuenteMundial);
        List<Equipo> equipos = plataforma.obtenerEquipos();
        List<Partido> partidos = fuenteMundial.obtenerPartidosEnVivo();

        System.out.println("=== Equipos cargados desde TXT Mundial (ADAPTER) ===");
        imprimirEquipos(equipos);

        // ---------- ADAPTER CSV ----------
        ProveedorDatosDeportivos fuenteLiga = new AdaptadorCsvLiga();
        System.out.println("\n=== Equipos cargados desde CSV Liga local (ADAPTER) ===");
        imprimirEquipos(fuenteLiga.obtenerEquipos());

        // ---------- REPOSITORIO ----------
        Repositorio<Equipo> repositorioEquipos = new RepositorioEnMemoria<>();
        Repositorio<Partido> repositorioPartidos = new RepositorioEnMemoria<>();

        for (Equipo equipo : equipos) {
            repositorioEquipos.guardar(equipo);
        }

        for (Partido partido : partidos) {
            repositorioPartidos.guardar(partido);
        }

        System.out.println("\n=== Persistencia en memoria (REPOSITORIO) ===");
        System.out.println("Equipos guardados: " + repositorioEquipos.obtenerTodos().size());
        System.out.println("Partidos guardados: " + repositorioPartidos.obtenerTodos().size());

        // ---------- FACTORY METHOD ----------
        UsuarioFactory[] factories = {
                new AficionadoFactory(),
                new AnalistaDeportivoFactory(),
                new CasaApuestaFactory(),
                new PeriodistaFactory()
        };

        // ---------- OBSERVER + VISTA EN TIEMPO REAL ----------
        Partido partido = partidos.get(0);
        Equipo local = partido.getEquipoLocal();
        Equipo visitante = partido.getEquipoVisitante();
        VistaTiempoReal pantalla = new PantallaPartido("Pantalla principal");

        for (UsuarioFactory factory : factories) {
            IUsuario usuario = factory.crearUsuario();
            partido.suscribir(usuario);
        }

        System.out.println("\n=== Eventos del partido " + local.getNombre()
                + " vs " + visitante.getNombre() + " (OBSERVER + VISTA) ===");
        pantalla.mostrarMarcador(partido.getResultado());

        Jugador autorLocal = local.getListaJugadores().get(0);
        Jugador autorVisitante = visitante.getListaJugadores().get(0);

        System.out.println("\n>> Minuto 23 - GOL");
        registrarEvento(partido, pantalla, new EventoPartido(23, TipoEvento.GOL, autorLocal));

        System.out.println("\n>> Minuto 50 - TARJETA AMARILLA");
        registrarEvento(partido, pantalla, new EventoPartido(50, TipoEvento.TARJETA_AMARILLA, autorVisitante));

        System.out.println("\n>> Minuto 78 - TARJETA ROJA");
        registrarEvento(partido, pantalla, new EventoPartido(78, TipoEvento.TARJETA_ROJA, autorVisitante));

        // ---------- STRATEGY ----------
        System.out.println("\n=== Ranking por PUNTOS (STRATEGY) ===");
        plataforma.setCriterioRanking(new CriterioPorPuntos());
        imprimirRanking(plataforma.generarRanking(equipos));

        System.out.println("\n=== Ranking por GOLES (STRATEGY) ===");
        plataforma.setCriterioRanking(new CriterioPorGoles());
        imprimirRanking(plataforma.generarRanking(equipos));

        // ---------- ARBITROS + PARTIDOS DESDE ARCHIVOS ----------
        System.out.println("\n=== Arbitros asignados a partidos del Mundial ===");
        imprimirArbitrosYPartidos(partidos);
    }

    private static void imprimirEquipos(List<Equipo> equipos) {
        for (Equipo e : equipos) {
            System.out.println(" - " + e.getNombre() + " (" + e.getCantidadJugadores() + " jugadores)");
        }
    }

    private static void registrarEvento(Partido partido, VistaTiempoReal pantalla, EventoPartido evento) {
        partido.agregarEvento(evento);
        pantalla.mostrarEvento(evento);
    }

    private static void imprimirRanking(List<Ranking> tabla) {
        for (Ranking r : tabla) {
            System.out.println(" " + r);
        }
    }

    private static void imprimirArbitrosYPartidos(List<Partido> partidos) {
        List<Arbitro> arbitros = cargarArbitros();

        int cantidadAsignaciones = Math.min(arbitros.size(), partidos.size());
        for (int i = 0; i < cantidadAsignaciones; i++) {
            Partido partido = partidos.get(i);
            Arbitro arbitro = arbitros.get(i);
            partido.setArbitro(arbitro);

            System.out.println(" - " + partido.getArbitro().getNombreCompleto()
                    + " | Partido: " + partido.getEquipoLocal().getNombre()
                    + " vs " + partido.getEquipoVisitante().getNombre());
        }
    }

    private static List<Arbitro> cargarArbitros() {
        try {
            List<Arbitro> arbitros = new ArrayList<>();
            List<String> lineas = Files.readAllLines(Paths.get(RUTA_ARBITROS), StandardCharsets.UTF_8);

            for (int i = 1; i < lineas.size(); i++) {
                String[] columnas = lineas.get(i).split(",", -1);
                if (columnas.length >= 4) {
                    arbitros.add(new Arbitro(columnas[0], columnas[1], columnas[2], columnas[3]));
                }
            }

            return arbitros;
        } catch (IOException e) {
            throw new RuntimeException("No se pudo leer el archivo de arbitros: " + RUTA_ARBITROS, e);
        }
    }
}
