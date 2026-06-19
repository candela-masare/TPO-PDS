package reporte;

import modelo.Partido;

// DECORATOR concreto: agrega al reporte el promedio de goles por equipo en el partido.
public class ReporteConPromedios extends ReporteDecorator {

    private Partido partido;

    public ReporteConPromedios(Reporte reporte, Partido partido) {
        super(reporte);
        this.partido = partido;
    }

    @Override
    public String generar() {
        int totalGoles = partido.getEstadisticaLocal().getGoles()
                + partido.getEstadisticaVisitante().getGoles();
        double promedio = totalGoles / 2.0;
        return reporte.generar()
                + "\n  + Promedio de goles por equipo: " + String.format("%.1f", promedio);
    }
}
