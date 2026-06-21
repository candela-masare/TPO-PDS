package modelo;

public class HistorialEquipo {

    private final String equipo;
    private int partidos;
    private int golesFavor;
    private int golesContra;
    private int tarjetas;

    public HistorialEquipo(String equipo) {
        this.equipo = equipo;
    }

    public void registrar(int golesFavor, int golesContra, int tarjetas) {
        this.partidos++;
        this.golesFavor += golesFavor;
        this.golesContra += golesContra;
        this.tarjetas += tarjetas;
    }

    public String getEquipo() { return equipo; }
    public int getPartidos() { return partidos; }
    public int getGolesFavor() { return golesFavor; }
    public int getGolesContra() { return golesContra; }
    public int getTarjetas() { return tarjetas; }

    public double promedioGolesFavor() { return partidos == 0 ? 0 : (double) golesFavor / partidos; }
    public double promedioGolesContra() { return partidos == 0 ? 0 : (double) golesContra / partidos; }
    public double promedioTarjetas() { return partidos == 0 ? 0 : (double) tarjetas / partidos; }
}
