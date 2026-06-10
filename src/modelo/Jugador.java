package modelo;

public class Jugador {

    private String nombreCompleto;
    private String posicion;
    private Equipo equipo;
    private int goles;
    private int tarjetasAmarillas;
    private int tarjetasRojas;


    public Jugador(String nombreCompleto, String posicion, Equipo equipo) {
        this.nombreCompleto = nombreCompleto;
        this.posicion = posicion;
        this.equipo = equipo;
        this.goles = 0;
        this.tarjetasAmarillas = 0;
        this.tarjetasRojas = 0;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public String getPosicion() {
        return posicion;
    }

    public Equipo getEquipo() {
        return equipo;
    }

    public int getGoles() { return goles; }
    public int getTarjetasAmarillas() { return tarjetasAmarillas; }
    public int getTarjetasRojas() { return tarjetasRojas; }

    public void registrarGol() { this.goles++; }
    public void registrarTarjetaAmarilla() { this.tarjetasAmarillas++; }
    public void registrarTarjetaRoja() { this.tarjetasRojas++; }

    @Override
    public String toString() {
        return "Jugador{" +
                "nombreCompleto='" + nombreCompleto + '\'' +
                ", posicion='" + posicion + '\'' +
                ", equipo=" + (equipo != null ? equipo.getNombre() : "sin equipo") +
                ", goles=" + goles +
                ", tarjetasAmarillas=" + tarjetasAmarillas +
                ", tarjetasRojas=" + tarjetasRojas +
                '}';
    }


}
