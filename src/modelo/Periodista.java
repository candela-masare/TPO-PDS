package modelo;

import interfaces.IUsuario;

public class Periodista implements IUsuario {

    private String nombre;
    private int edad;

    public Periodista(String nombre) {
        this.nombre = nombre;
    }

    public Periodista(String nombre, int edad) {
        this.nombre = nombre;
        this.edad = edad;
    }

    @Override
    public void actualizar(EventoPartido evento) {
        String autor = evento.getAutor() != null ? evento.getAutor().getNombreCompleto() : "jugador";
        System.out.println("[PERIODISTA] " + nombre + " anota para la cronica: '"
                + evento.getTipo() + " de " + autor + " (min " + evento.getMinuto() + ")'.");
    }

    public String getNombre() { return nombre; }
    public int getEdad() { return edad; }
}
