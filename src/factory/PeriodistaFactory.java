package factory;

import interfaces.IUsuario;
import modelo.Periodista;

// FACTORY METHOD concreta: crea un Periodista con nombre y edad genéricos por defecto.
public class PeriodistaFactory extends UsuarioFactory {

    @Override
    public IUsuario crearUsuario() {
        return new Periodista("Reportero", 35);
    }

}
