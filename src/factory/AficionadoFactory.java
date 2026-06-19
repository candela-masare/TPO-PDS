package factory;

import interfaces.IUsuario;
import modelo.Aficionado;

// FACTORY METHOD concreta: crea un Aficionado con nombre genérico por defecto.
public class AficionadoFactory extends UsuarioFactory{

    @Override
    public IUsuario crearUsuario() {
        return new Aficionado("Hincha Anónimo");
    }

}
