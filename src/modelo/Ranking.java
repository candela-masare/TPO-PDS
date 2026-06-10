package modelo;

/**
 * Representa una entrada en la tabla de posiciones: combina la posición
 * asignada con los datos del equipo en ese momento del torneo.
 */
public class Ranking {

    private int posicion;
    private Equipo equipo;
    private int puntos;
    private int golesAFavor;
    private int golesEnContra;

    public Ranking(int posicion, Equipo equipo) {
        this.posicion     = posicion;
        this.equipo       = equipo;
        this.puntos       = equipo.getPuntos();
        this.golesAFavor  = equipo.getGolesAFavor();
        this.golesEnContra = equipo.getGolesEnContra();
    }

    public int getPosicion() { return posicion; }
    public Equipo getEquipo() { return equipo; }
    public int getPuntos() { return puntos; }
    public int getGolesAFavor() { return golesAFavor; }
    public int getGolesEnContra() { return golesEnContra; }
    public int getDiferenciaGoles() { return golesAFavor - golesEnContra; }

    @Override
    public String toString() {
        return posicion + ". " + equipo.getNombre()
                + " | pts=" + puntos
                + " | GF=" + golesAFavor
                + " | GC=" + golesEnContra
                + " | DG=" + getDiferenciaGoles();
    }
}
