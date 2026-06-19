package factory;

import interfaces.IUsuario;
import modelo.CasaApuesta;

public class CasaApuestaFactory extends UsuarioFactory{

    @Override
    public IUsuario crearUsuario() {
        return new CasaApuesta("BetSport");
    }

}
