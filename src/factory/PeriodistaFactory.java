package factory;

import interfaces.IUsuario;
import modelo.Periodista;

public class PeriodistaFactory extends UsuarioFactory {

    @Override
    public IUsuario crearUsuario() {
        return new Periodista("Reportero", 35);
    }

}
