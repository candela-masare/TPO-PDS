package factory;

import interfaces.IUsuario;

// FACTORY METHOD: creador abstracto que delega la construcción del usuario concreto a cada subclase.
public abstract class UsuarioFactory {

    public abstract IUsuario crearUsuario();

}
