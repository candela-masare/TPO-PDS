package modelo;

public class Estadistica {

    private Equipo equipo;
    private int goles;
    private int tarjetasAmarillas;
    private int tarjetasRojas;

    public Estadistica(Equipo equipo) {
        this.equipo = equipo;
        this.goles = 0;
        this.tarjetasAmarillas = 0;
        this.tarjetasRojas = 0;
    }

    public Equipo getEquipo() {
        return equipo;
    }

    public int getGoles() {
        return goles;
    }

    public int getTarjetasAmarillas() {
        return tarjetasAmarillas;
    }

    public int getTarjetasRojas() {
        return tarjetasRojas;
    }

    public void incrementarGoles() {
        this.goles++;
    }

    public void incrementarTarjetasAmarillas() {
        this.tarjetasAmarillas++;
    }

    public void incrementarTarjetasRojas() {
        this.tarjetasRojas++;
    }

    @Override
    public String toString() {
        return "Estadistica{" +
                "equipo=" + (equipo != null ? equipo.getNombre() : "?") +
                ", goles=" + goles +
                ", tarjetasAmarillas=" + tarjetasAmarillas +
                ", tarjetasRojas=" + tarjetasRojas +
                '}';
    }
}
