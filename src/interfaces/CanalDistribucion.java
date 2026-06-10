package interfaces;

import modelo.EventoPartido;

public interface CanalDistribucion {
    void publicar(EventoPartido evento);
}
