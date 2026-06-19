package factory;

import interfaces.IUsuario;
import modelo.Aficionado;

public class AficionadoFactory extends UsuarioFactory{

    @Override
    public IUsuario crearUsuario() {
        return new Aficionado("Hincha Anónimo");
    }

}
