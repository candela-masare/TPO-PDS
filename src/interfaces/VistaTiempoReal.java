package interfaces;

import modelo.EventoPartido;

/**
 * OBSERVER — contrato para vistas que muestran información del partido en tiempo real.
 * <p>
 * Se diferencia de {@link IUsuario} en que representa una pantalla o display,
 * no un perfil de usuario. La implementación concreta es {@link vista.PantallaPartido}.
 * </p>
 */
public interface VistaTiempoReal {

    /**
     * Muestra un evento ocurrido en el partido (gol, tarjeta, etc.).
     *
     * @param evento el evento a mostrar
     */
    void mostrarEvento(EventoPartido evento);

    /**
     * Muestra el marcador actual del partido.
     *
     * @param marcador cadena con el resultado, por ejemplo {@code "2-1"}
     */
    void mostrarMarcador(String marcador);
}
