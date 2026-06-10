import estrategia.CriterioPorGoles;
import estrategia.CriterioPorPuntos;
import factory.AficionadoFactory;
import factory.AnalistaDeportivoFactory;
import factory.CasaApuestaFactory;
import factory.PeriodistaFactory;
import factory.UsuarioFactory;
import fuentes.AdaptadorApiOle;
import fuentes.AdaptadorCsvLiga;
import fuentes.AdaptadorTxtMundial;
import interfaces.IUsuario;
import interfaces.ProveedorDatosDeportivos;
import modelo.AppMovil;
import modelo.DiarioOle;
import modelo.Equipo;
import modelo.EventoPartido;
import modelo.Jugador;
import modelo.PanelInteractivo;
import modelo.Partido;
import modelo.Ranking;
import modelo.ReporteEstadistico;
import modelo.TipoEvento;
import modelo.Torneo;
import servicio.PlataformaDeportiva;

import java.util.List;

public class Main {

    public static void main(String[] args) {

        ProveedorDatosDeportivos fuenteMundial = new AdaptadorTxtMundial();
        ProveedorDatosDeportivos fuenteLiga    = new AdaptadorCsvLiga();
        ProveedorDatosDeportivos fuenteApiOle  = new AdaptadorApiOle();

        PlataformaDeportiva plataforma = new PlataformaDeportiva(fuenteMundial);

        System.out.println("=== Equipos desde TXT Mundial (ADAPTER) ===");
        for (Equipo e : plataforma.obtenerEquipos()) {
            System.out.println(" - " + e.getNombre() + " (" + e.getCantidadJugadores() + " jugadores)");
        }

        plataforma.setProveedor(fuenteLiga);
        System.out.println("\n=== Equipos desde CSV Liga (ADAPTER) ===");
        for (Equipo e : plataforma.obtenerEquipos()) {
            System.out.println(" - " + e.getNombre() + " (" + e.getCantidadJugadores() + " jugadores)");
        }

        plataforma.setProveedor(fuenteApiOle);
        System.out.println("\n=== Equipos desde API Ole (ADAPTER) ===");
        for (Equipo e : plataforma.obtenerEquipos()) {
            System.out.println(" - " + e.getNombre() + " (" + e.getCantidadJugadores() + " jugadores)");
        }

        plataforma.setProveedor(fuenteMundial);
        List<Equipo> equiposMundial = plataforma.obtenerEquipos();

        Torneo torneo = new Torneo("Copa de Campeones");
        for (Equipo e : equiposMundial) torneo.agregarEquipo(e);

        System.out.println("\n=== Torneo registrado ===");
        System.out.println(" " + torneo.getNombre() + " | equipos: " + torneo.getEquipos().size());

        ReporteEstadistico reporte = new ReporteEstadistico();
        plataforma.agregarCanal(new DiarioOle());
        plataforma.agregarCanal(new AppMovil("iOS"));
        plataforma.agregarCanal(new PanelInteractivo("Estadio Monumental"));
        plataforma.agregarCanal(reporte);

        UsuarioFactory[] factories = {
                new AficionadoFactory(),
                new AnalistaDeportivoFactory(),
                new CasaApuestaFactory(),
                new PeriodistaFactory()
        };

        Equipo local     = equiposMundial.get(0);
        Equipo visitante = equiposMundial.get(1);
        Partido partido  = new Partido(local, visitante, "0-0");
        torneo.agregarPartido(partido);

        for (UsuarioFactory factory : factories) {
            IUsuario usuario = factory.crearUsuario();
            partido.suscribir(usuario);
        }
        partido.suscribir(plataforma);

        System.out.println("\n=== Eventos del partido "
                + local.getNombre() + " vs " + visitante.getNombre() + " ===");

        Jugador autorLocal     = local.getListaJugadores().get(0);
        Jugador autorVisitante = visitante.getListaJugadores().get(0);

        System.out.println("\n>> Minuto 23 - GOL de " + autorLocal.getNombreCompleto());
        partido.agregarEvento(new EventoPartido(23, TipoEvento.GOL, autorLocal));
        System.out.println("   Resultado: " + partido.getResultado());

        System.out.println("\n>> Minuto 50 - TARJETA AMARILLA a " + autorVisitante.getNombreCompleto());
        partido.agregarEvento(new EventoPartido(50, TipoEvento.TARJETA_AMARILLA, autorVisitante));

        System.out.println("\n>> Minuto 78 - GOL de " + autorVisitante.getNombreCompleto());
        partido.agregarEvento(new EventoPartido(78, TipoEvento.GOL, autorVisitante));
        System.out.println("   Resultado: " + partido.getResultado());

        System.out.println("\n>> Minuto 90 - TARJETA ROJA a " + autorVisitante.getNombreCompleto());
        partido.agregarEvento(new EventoPartido(90, TipoEvento.TARJETA_ROJA, autorVisitante));

        System.out.println("\n=== Estadisticas del partido ===");
        System.out.println(" " + local.getNombre() + ": " + partido.getEstadisticaLocal());
        System.out.println(" " + visitante.getNombre() + ": " + partido.getEstadisticaVisitante());

        System.out.println("\n=== Estadisticas individuales de jugadores ===");
        for (Jugador j : local.getListaJugadores()) {
            System.out.println(" " + j.getNombreCompleto()
                    + " | goles=" + j.getGoles()
                    + " | amarillas=" + j.getTarjetasAmarillas()
                    + " | rojas=" + j.getTarjetasRojas());
        }
        for (Jugador j : visitante.getListaJugadores()) {
            System.out.println(" " + j.getNombreCompleto()
                    + " | goles=" + j.getGoles()
                    + " | amarillas=" + j.getTarjetasAmarillas()
                    + " | rojas=" + j.getTarjetasRojas());
        }

        System.out.println();
        reporte.imprimirResumen();

        System.out.println("\n=== Ranking por PUNTOS (STRATEGY) ===");
        plataforma.setCriterioRanking(new CriterioPorPuntos());
        imprimirRanking(plataforma.generarRanking(equiposMundial));

        System.out.println("\n=== Ranking por GOLES (STRATEGY) ===");
        plataforma.setCriterioRanking(new CriterioPorGoles());
        imprimirRanking(plataforma.generarRanking(equiposMundial));
    }

    private static void imprimirRanking(List<Ranking> tabla) {
        for (Ranking r : tabla) {
            System.out.println(" " + r);
        }
    }
}
