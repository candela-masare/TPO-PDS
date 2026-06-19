package persistencia;

import interfaces.Repositorio;

import java.util.ArrayList;
import java.util.List;

// Implementacion en memoria de Repositorio.
// Simula una capa de persistencia que almacena entidades durante la ejecucion.
public class RepositorioEnMemoria<T> implements Repositorio<T> {

    private List<T> almacenamiento;

    public RepositorioEnMemoria() {
        this.almacenamiento = new ArrayList<>();
    }

    @Override
    public void guardar(T entidad) {
        almacenamiento.add(entidad);
    }

    @Override
    public List<T> obtenerTodos() {
        return new ArrayList<>(almacenamiento);
    }
}
