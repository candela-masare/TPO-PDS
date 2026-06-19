package factory;

import interfaces.IUsuario;
import modelo.Aficionado;

/**
 * FACTORY METHOD concreta — crea un {@link modelo.Aficionado} con nombre genérico por defecto.
 */
public class AficionadoFactory extends UsuarioFactory {

    /**
     * @return nuevo {@link modelo.Aficionado} con nombre "Hincha Anónimo"
     */
    @Override
    public IUsuario crearUsuario() {
        return new Aficionado("Hincha Anónimo");
    }

}
