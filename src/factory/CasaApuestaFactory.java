package factory;

import interfaces.IUsuario;
import modelo.CasaApuesta;

// FACTORY METHOD concreta: crea una CasaApuesta con nombre genérico por defecto.
public class CasaApuestaFactory extends UsuarioFactory{

    @Override
    public IUsuario crearUsuario() {
        return new CasaApuesta("BetSport");
    }

}
