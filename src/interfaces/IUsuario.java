package interfaces;

import modelo.EventoPartido;

// OBSERVER: suscriptor que reacciona a los eventos del partido según su perfil (Aficionado, Periodista, etc.).
public interface IUsuario {
    void actualizar(EventoPartido evento);
}
