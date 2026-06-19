package interfaces;

import modelo.EventoPartido;

/**
 * OBSERVER — canal de distribución de eventos del partido.
 * <p>
 * Cada implementación representa un medio diferente (app móvil, diario, panel físico)
 * que recibe los eventos y los publica en su formato propio.
 * La {@link servicio.PlataformaDeportiva} actúa como intermediario y reenvía los eventos
 * a todos los canales registrados.
 * </p>
 */
public interface CanalDistribucion {

    /**
     * Publica el evento en este canal de distribución.
     *
     * @param evento el evento ocurrido en el partido (gol, tarjeta, etc.)
     */
    void publicar(EventoPartido evento);
}
