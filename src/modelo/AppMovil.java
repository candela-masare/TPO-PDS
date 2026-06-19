package modelo;

import interfaces.CanalDistribucion;

// OBSERVER canal: publica eventos del partido como notificaciones push de una aplicación móvil.
public class AppMovil implements CanalDistribucion {

    private String plataforma;

    public AppMovil(String plataforma) {
        this.plataforma = plataforma;
    }

    @Override
    public void publicar(EventoPartido evento) {
        System.out.println("[App Movil - " + plataforma + "] PUSH | Min "
                + evento.getMinuto() + ": " + evento.getTipo()
                + " de " + evento.getAutor().getNombreCompleto());
    }
}
