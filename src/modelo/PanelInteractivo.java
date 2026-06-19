package modelo;

import interfaces.CanalDistribucion;

// OBSERVER canal: publica eventos del partido en un panel físico identificado por su ubicación.
public class PanelInteractivo implements CanalDistribucion {

    private String ubicacion;

    public PanelInteractivo(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    @Override
    public void publicar(EventoPartido evento) {
        System.out.println("[Panel " + ubicacion + "] ACTUALIZACION | Min "
                + evento.getMinuto() + ": " + evento.getTipo()
                + " — " + evento.getAutor().getNombreCompleto());
    }
}
