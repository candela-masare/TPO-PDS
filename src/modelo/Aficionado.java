package modelo;

import interfaces.IUsuario;

public class Aficionado implements IUsuario {

    private String nombre;
    private Equipo equipo;
    private Jugador jugadorPreferido;

    public Aficionado(String nombre) {
        this.nombre = nombre;
    }

    public Aficionado(String nombre, Equipo equipo, Jugador jugadorPreferido) {
        this.nombre = nombre;
        this.equipo = equipo;
        this.jugadorPreferido = jugadorPreferido;
    }

    @Override
    public void actualizar(EventoPartido evento) {
        Jugador autor = evento.getAutor();

        boolean esMiJugador = jugadorPreferido != null && autor != null
                && autor.getNombreCompleto().equals(jugadorPreferido.getNombreCompleto());

        boolean esMiEquipo = equipo != null && autor != null && autor.getEquipo() != null
                && autor.getEquipo().getNombre().equals(equipo.getNombre());

        if (esMiJugador) {
            System.out.println("[ALERTA HINCHA] " + nombre + " - Tu jugador preferido "
                    + autor.getNombreCompleto() + ": " + evento.getTipo()
                    + " al minuto " + evento.getMinuto() + "!");
        } else if (esMiEquipo) {
            System.out.println("[ALERTA HINCHA] " + nombre + " - Vamos " + equipo.getNombre()
                    + "! " + evento.getTipo() + " de " + autor.getNombreCompleto()
                    + " al minuto " + evento.getMinuto());
        } else {
            System.out.println("[Hincha] " + nombre + " sigue el partido: " + evento.getTipo()
                    + " al minuto " + evento.getMinuto());
        }
    }

    public String getNombre() { return nombre; }
    public Equipo getEquipo() { return equipo; }
    public void setEquipo(Equipo equipo) { this.equipo = equipo; }
    public Jugador getJugadorPreferido() { return jugadorPreferido; }
    public void setJugadorPreferido(Jugador jugadorPreferido) { this.jugadorPreferido = jugadorPreferido; }
}
