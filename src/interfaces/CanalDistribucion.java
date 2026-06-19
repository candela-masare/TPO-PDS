package interfaces;

import modelo.EventoPartido;

// OBSERVER: canal de distribución que recibe eventos del partido y los difunde a su medio (app, diario, panel).
public interface CanalDistribucion {
    void publicar(EventoPartido evento);
}
