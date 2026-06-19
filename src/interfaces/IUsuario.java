package interfaces;

import modelo.EventoPartido;

/**
 * OBSERVER — suscriptor de eventos del partido.
 * <p>
 * Cada implementación reacciona de forma distinta según el perfil del usuario:
 * {@link modelo.Aficionado}, {@link modelo.Periodista}, {@link modelo.CasaApuesta}
 * y {@link modelo.AnalistaDeportivo}.
 * El sujeto observable es {@link modelo.Partido}, que llama a {@code actualizar()}
 * en todos los suscriptores registrados cada vez que ocurre un evento.
 * </p>
 */
public interface IUsuario {

    /**
     * Recibe y procesa un evento ocurrido en el partido.
     *
     * @param evento el evento a procesar (gol, tarjeta, penal, etc.)
     */
    void actualizar(EventoPartido evento);
}
