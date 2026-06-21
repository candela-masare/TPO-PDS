package modelo;

public class PronosticoBuilder {

    private final int partidoId;
    private final String fase;
    private final String equipoLocal;
    private final String equipoVisitante;
    private final HistorialEquipo histLocal;
    private final HistorialEquipo histVisitante;

    private double golesEstLocal;
    private double golesEstVisitante;
    private int probLocal;
    private int probVisitante;
    private double tarjetasEst;
    private Volatilidad volatilidad;
    private String justificacion;

    public PronosticoBuilder(int partidoId, String fase, String equipoLocal, String equipoVisitante,
                             HistorialEquipo histLocal, HistorialEquipo histVisitante) {
        this.partidoId = partidoId;
        this.fase = fase;
        this.equipoLocal = equipoLocal;
        this.equipoVisitante = equipoVisitante;
        this.histLocal = histLocal;
        this.histVisitante = histVisitante;
    }

    public PronosticoBuilder calcularGoles() {
        golesEstLocal = redondear1((histLocal.promedioGolesFavor() + histVisitante.promedioGolesContra()) / 2);
        golesEstVisitante = redondear1((histVisitante.promedioGolesFavor() + histLocal.promedioGolesContra()) / 2);
        return this;
    }

    public PronosticoBuilder calcularProbabilidades() {
        double total = golesEstLocal + golesEstVisitante;
        if (total <= 0) {
            probLocal = 50;
        } else {
            probLocal = (int) Math.round(golesEstLocal / total * 100);
        }
        probVisitante = 100 - probLocal;
        return this;
    }

    public PronosticoBuilder calcularTarjetas() {
        tarjetasEst = redondear1((histLocal.promedioTarjetas() + histVisitante.promedioTarjetas()) / 2);
        return this;
    }

    public PronosticoBuilder calcularVolatilidad() {
        int margen = Math.abs(probLocal - probVisitante);
        if (margen < 20) {
            volatilidad = Volatilidad.ALTA;
        } else if (margen < 50) {
            volatilidad = Volatilidad.MEDIA;
        } else {
            volatilidad = Volatilidad.BAJA;
        }
        return this;
    }

    public PronosticoBuilder generarJustificacion() {
        justificacion = equipoLocal + " llega con " + histLocal.getGolesFavor() + " GF, "
                + histLocal.getGolesContra() + " GC en " + histLocal.getPartidos() + " partido(s); "
                + equipoVisitante + " con " + histVisitante.getGolesFavor() + " GF, "
                + histVisitante.getGolesContra() + " GC en " + histVisitante.getPartidos() + " partido(s).";
        return this;
    }

    public Pronostico build() {
        if (volatilidad == null) {
            throw new IllegalStateException("Pronostico incompleto: falta calcular la volatilidad");
        }
        return new Pronostico(partidoId, fase, equipoLocal, equipoVisitante,
                probLocal, probVisitante, golesEstLocal, golesEstVisitante,
                tarjetasEst, volatilidad, justificacion);
    }

    private double redondear1(double valor) {
        return Math.round(valor * 10.0) / 10.0;
    }
}
