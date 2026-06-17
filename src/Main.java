import estrategia.CriterioPorGoles;
import estrategia.CriterioPorPuntos;
import factory.AficionadoFactory;
import factory.AnalistaDeportivoFactory;
import factory.CasaApuestaFactory;
import factory.PeriodistaFactory;
import factory.UsuarioFactory;
import fuentes.AdaptadorCsvLiga;
import fuentes.AdaptadorTxtMundial;
import interfaces.IUsuario;
import interfaces.ProveedorDatosDeportivos;
import interfaces.Repositorio;
import interfaces.VistaTiempoReal;
import modelo.Equipo;
import modelo.EventoPartido;
import modelo.Jugador;
import modelo.Partido;
import modelo.TipoEvento;
import persistencia.RepositorioEnMemoria;
import servicio.PlataformaDeportiva;
import vista.PantallaPartido;

import java.util.List;

public class Main {

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

    private static void imprimirRanking(List<Equipo> equipos) {
        int posicion = 1;
        for (Equipo e : equipos) {
            System.out.println(" " + posicion++ + ". " + e.getNombre()
                    + " | puntos=" + e.getPuntos() + " | goles=" + e.getGolesAFavor());
        }
    }
}
