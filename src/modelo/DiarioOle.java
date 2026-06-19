package modelo;

import interfaces.CanalDistribucion;

// OBSERVER canal: publica eventos del partido con el formato editorial del Diario Olé.
public class DiarioOle implements CanalDistribucion {

    @Override
    public void publicar(EventoPartido evento) {
        System.out.println("[Diario Ole] Min " + evento.getMinuto()
                + " | " + evento.getTipo()
                + " — " + evento.getAutor().getNombreCompleto()
                + " (" + evento.getAutor().getEquipo().getNombre() + ")");
    }
}
