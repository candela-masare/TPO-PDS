package modelo;

import interfaces.IUsuario;

// OBSERVER concreto: recibe eventos del partido y evalúa su impacto táctico en profundidad.
public class AnalistaDeportivo implements IUsuario {

    private String nombre;

    public AnalistaDeportivo(String nombre) {
        this.nombre = nombre;
    }

    @Override
    public void actualizar(EventoPartido evento) {
        String autor = evento.getAutor() != null ? evento.getAutor().getNombreCompleto() : "desconocido";
        System.out.println("[ANALISIS] " + nombre + " evalua el impacto del evento "
                + evento.getTipo() + " (" + autor + ", min " + evento.getMinuto()
                + ") en el desarrollo tactico del partido.");
    }

    public String getNombre() { return nombre; }
}
