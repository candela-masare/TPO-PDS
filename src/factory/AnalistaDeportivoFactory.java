package factory;

import interfaces.IUsuario;
import modelo.AnalistaDeportivo;

public class AnalistaDeportivoFactory extends UsuarioFactory{

    @Override
    public IUsuario crearUsuario() {
        return new AnalistaDeportivo("Analista Pro");
    }

}
