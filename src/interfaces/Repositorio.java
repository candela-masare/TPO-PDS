package interfaces;

import java.util.List;

/**
 * REPOSITORY — contrato genérico para persistir y consultar entidades en memoria.
 * <p>
 * Desacopla la lógica de negocio del mecanismo de almacenamiento.
 * La implementación concreta es {@link persistencia.RepositorioEnMemoria}.
 * </p>
 *
 * @param <T> tipo de entidad que gestiona este repositorio
 */
public interface Repositorio<T> {

    /**
     * Persiste una entidad en el repositorio.
     *
     * @param entidad la entidad a guardar
     */
    void guardar(T entidad);

    /**
     * Devuelve todas las entidades almacenadas.
     *
     * @return lista con todas las entidades (puede estar vacía, nunca {@code null})
     */
    List<T> obtenerTodos();
}
