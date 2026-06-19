package reporte;

import modelo.Partido;

public class ReporteBasico implements Reporte {

    private Partido partido;

    public ReporteBasico(Partido partido) {
        this.partido = partido;
    }

    @Override
    public String generar() {
        return "Reporte de " + partido.getEquipoLocal().getNombre()
                + " vs " + partido.getEquipoVisitante().getNombre()
                + " | Resultado: " + partido.getResultado()
                + " | Eventos: " + partido.getEventos().size();
    }
}
