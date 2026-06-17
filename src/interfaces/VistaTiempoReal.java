package interfaces;

import modelo.EventoPartido;

// Contrato para vistas que muestran informacion del partido en tiempo real.
public interface VistaTiempoReal {

    void mostrarEvento(EventoPartido evento);

    void mostrarMarcador(String marcador);
}
