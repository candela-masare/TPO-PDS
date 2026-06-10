package interfaces;

import modelo.Equipo;
import modelo.Partido;

import java.util.List;

public interface ProveedorDatosDeportivos {
    List<Partido> obtenerPartidosEnVivo();
    List<Equipo> obtenerEquipos();
}
