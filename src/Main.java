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
import modelo.Aficionado;
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
        ProveedorDatosDeportivos fuenteLiga = new AdaptadorCsvLiga();

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

        plataforma.setProveedor(fuenteMundial);

        Torneo torneo = plataforma.crearTorneo("Copa de Campeones");
        List<Equipo> equiposMundial = torneo.getEquipos();

        System.out.println("\n=== Torneo registrado ===");
        System.out.println(" " + torneo.getNombre() + " | equipos: " + torneo.getEquipos().size());

        List<Partido> partidosEnVivo = plataforma.obtenerPartidosEnVivo();
        System.out.println("\n=== Partidos en vivo reportados por la fuente ===");
        for (Partido p : partidosEnVivo) {
            System.out.println(" - " + p);
        }

        Equipo local = equiposMundial.get(0);
        Equipo visitante = equiposMundial.get(1);
        Jugador autorLocal = local.getListaJugadores().get(0);
        Jugador autorVisitante = visitante.getListaJugadores().get(0);

        Partido partido = new Partido(local, visitante, "0-0");
        torneo.agregarPartido(partido);

        ReporteEstadistico reporteCanal = new ReporteEstadistico();
        plataforma.agregarCanal(new DiarioOle());
        plataforma.agregarCanal(new AppMovil("iOS"));
        plataforma.agregarCanal(new PanelInteractivo("Estadio Monumental"));
        plataforma.agregarCanal(reporteCanal);

        UsuarioFactory[] factories = {
                new AficionadoFactory(),
                new AnalistaDeportivoFactory(),
                new CasaApuestaFactory(),
                new PeriodistaFactory()
        };
        for (UsuarioFactory factory : factories) {
            partido.suscribir(factory.crearUsuario());
        }

        Aficionado hinchaPersonalizado = new Aficionado("Candela", local, autorLocal);
        partido.suscribir(hinchaPersonalizado);
        partido.suscribir(plataforma);

        System.out.println("\n=== Eventos del partido "
                + local.getNombre() + " vs " + visitante.getNombre() + " ===");

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
        reporteCanal.imprimirResumen();

        System.out.println("\n=== Reporte avanzado (DECORATOR) ===");
        System.out.println(plataforma.generarReporteAvanzado(partido));

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
