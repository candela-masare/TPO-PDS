package factory;

import interfaces.IUsuario;

/**
 * FACTORY METHOD — creador abstracto de usuarios.
 * <p>
 * Cada subclase concreta decide qué tipo de {@link IUsuario} instanciar,
 * desacoplando la creación del objeto de su uso en el sistema.
 * Subclases: {@link AficionadoFactory}, {@link PeriodistaFactory},
 * {@link CasaApuestaFactory}, {@link AnalistaDeportivoFactory}.
 * </p>
 */
public abstract class UsuarioFactory {

    /**
     * Crea y devuelve una instancia del usuario correspondiente a esta fábrica.
     *
     * @return nuevo usuario listo para suscribirse a eventos de un partido
     */
    public abstract IUsuario crearUsuario();

}
