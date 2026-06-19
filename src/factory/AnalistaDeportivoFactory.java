package factory;

import interfaces.IUsuario;
import modelo.AnalistaDeportivo;

// FACTORY METHOD concreta: crea un AnalistaDeportivo con nombre genérico por defecto.
public class AnalistaDeportivoFactory extends UsuarioFactory{

    @Override
    public IUsuario crearUsuario() {
        return new AnalistaDeportivo("Analista Pro");
    }

}
