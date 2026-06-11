package persistencia;

import java.util.List;

public interface Repositorio<T> {
    void guardar(T entidad);
    List<T> obtenerTodos();
}
