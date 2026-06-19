package interfaces;

import java.util.List;

// Contrato generico para guardar y consultar entidades.
public interface Repositorio<T> {

    void guardar(T entidad);

    List<T> obtenerTodos();
}
