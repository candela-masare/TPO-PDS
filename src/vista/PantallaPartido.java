package vista;

import interfaces.VistaTiempoReal;
import modelo.EventoPartido;

// Implementacion concreta de VistaTiempoReal.
// Muestra los eventos del partido y el marcador actualizado en pantalla.
public class PantallaPartido implements VistaTiempoReal {

    private String nombre;

    public PantallaPartido(String nombre) {
        this.nombre = nombre;
    }

    @Override
    public void mostrarEvento(EventoPartido evento) {
        System.out.println("[" + nombre + "] Min " + evento.getMinuto()
                + " | " + evento.getTipo()
                + " - " + evento.getAutor().getNombreCompleto());
    }

    @Override
    public void mostrarMarcador(String marcador) {
        System.out.println("[" + nombre + "] Marcador actual: " + marcador);
    }
}
