package modelo;

public class Pronostico {

    private final int partidoId;
    private final String fase;
    private final String equipoLocal;
    private final String equipoVisitante;
    private final int probLocal;
    private final int probVisitante;
    private final double golesEstLocal;
    private final double golesEstVisitante;
    private final double tarjetasEst;
    private final Volatilidad volatilidad;
    private final String justificacion;

    Pronostico(int partidoId, String fase, String equipoLocal, String equipoVisitante,
               int probLocal, int probVisitante, double golesEstLocal, double golesEstVisitante,
               double tarjetasEst, Volatilidad volatilidad, String justificacion) {
        this.partidoId = partidoId;
        this.fase = fase;
        this.equipoLocal = equipoLocal;
        this.equipoVisitante = equipoVisitante;
        this.probLocal = probLocal;
        this.probVisitante = probVisitante;
        this.golesEstLocal = golesEstLocal;
        this.golesEstVisitante = golesEstVisitante;
        this.tarjetasEst = tarjetasEst;
        this.volatilidad = volatilidad;
        this.justificacion = justificacion;
    }

    public int getPartidoId() { return partidoId; }
    public String getFase() { return fase; }
    public String getEquipoLocal() { return equipoLocal; }
    public String getEquipoVisitante() { return equipoVisitante; }
    public int getProbLocal() { return probLocal; }
    public int getProbVisitante() { return probVisitante; }
    public double getGolesEstLocal() { return golesEstLocal; }
    public double getGolesEstVisitante() { return golesEstVisitante; }
    public double getTarjetasEst() { return tarjetasEst; }
    public Volatilidad getVolatilidad() { return volatilidad; }
    public String getJustificacion() { return justificacion; }
}
