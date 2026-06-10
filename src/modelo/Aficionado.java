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
        boolean esMiEquipo = equipo != null
                && evento.getAutor() != null
                && evento.getAutor().getEquipo() != null
                && evento.getAutor().getEquipo().getNombre().equals(equipo.getNombre());

        if (esMiEquipo) {
            System.out.println("[ALERTA HINCHA] " + nombre + " - Vamos " + equipo.getNombre()
                    + "! " + evento.getTipo() + " de " + evento.getAutor().getNombreCompleto()
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
