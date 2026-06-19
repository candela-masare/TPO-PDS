package modelo;

// Jugador de un equipo. El constructor simple (3 args) se usa al cargar desde TXT/JSON;
// el constructor completo (7 args) se usa al cargar desde CSV con estadísticas ya calculadas.
public class Jugador {

    private String nombreCompleto;
    private String posicion;
    private Equipo equipo;
    private int partidosJugados;
    private int goles;
    private int tarjetasAmarillas;
    private int tarjetasRojas;

    public Jugador(String nombreCompleto, String posicion, Equipo equipo) {
        this(nombreCompleto, posicion, equipo, 0, 0, 0, 0);
    }

    public Jugador(String nombreCompleto, String posicion, Equipo equipo, int partidosJugados, int goles,
                   int tarjetasAmarillas, int tarjetasRojas) {
        this.nombreCompleto = nombreCompleto;
        this.posicion = posicion;
        this.equipo = equipo;
        this.partidosJugados = partidosJugados;
        this.goles = goles;
        this.tarjetasAmarillas = tarjetasAmarillas;
        this.tarjetasRojas = tarjetasRojas;
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

    public int getGoles() {
        return goles;
    }

    public int getTarjetasAmarillas() {
        return tarjetasAmarillas;
    }

    public int getTarjetasRojas() {
        return tarjetasRojas;
    }

    public void registrarGol() {
        this.goles++;
    }

    public void registrarTarjetaAmarilla() {
        this.tarjetasAmarillas++;
    }

    public void registrarTarjetaRoja() {
        this.tarjetasRojas++;
    }

    @Override
    public String toString() {
        return "Jugador{" +
                "nombreCompleto='" + nombreCompleto + '\'' +
                ", posicion='" + posicion + '\'' +
                ", equipo=" + (equipo != null ? equipo.getNombre() : "sin equipo") +
                ", partidosJugados=" + partidosJugados +
                ", goles=" + goles +
                ", tarjetasAmarillas=" + tarjetasAmarillas +
                ", tarjetasRojas=" + tarjetasRojas +
                '}';
    }
}
