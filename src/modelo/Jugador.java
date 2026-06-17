package modelo;

public class Jugador {

    private String nombreCompleto;
    private String posicion;
    private Equipo equipo;
    private int partidosJugados;


    public Jugador(String nombreCompleto, String posicion, Equipo equipo) {
        this.nombreCompleto = nombreCompleto;
        this.posicion = posicion;
        this.equipo = equipo;
        this.partidosJugados = 0;
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

    public int getPartidosJugados() {
        return partidosJugados;
    }

    @Override
    public String toString() {
        return "Jugador{" +
                "nombreCompleto='" + nombreCompleto + '\'' +
                ", posicion='" + posicion + '\'' +
                ", equipo=" + (equipo != null ? equipo.getNombre() : "sin equipo") +
                '}';
    }


}
