package reporte;

import modelo.Jugador;
import modelo.Partido;

import java.util.ArrayList;
import java.util.List;

// DECORATOR concreto: agrega al reporte el goleador del partido con su cantidad de goles.
public class ReporteConTopJugadores extends ReporteDecorator {

    private Partido partido;

    public ReporteConTopJugadores(Reporte reporte, Partido partido) {
        super(reporte);
        this.partido = partido;
    }

    @Override
    public String generar() {
        List<Jugador> jugadores = new ArrayList<>();
        jugadores.addAll(partido.getEquipoLocal().getListaJugadores());
        jugadores.addAll(partido.getEquipoVisitante().getListaJugadores());

        Jugador goleador = null;
        for (Jugador j : jugadores) {
            if (goleador == null || j.getGoles() > goleador.getGoles()) {
                goleador = j;
            }
        }

        String detalle = (goleador != null && goleador.getGoles() > 0)
                ? goleador.getNombreCompleto() + " (" + goleador.getGoles() + " goles)"
                : "sin goleadores";
        return reporte.generar() + "\n  + Goleador del partido: " + detalle;
    }
}
