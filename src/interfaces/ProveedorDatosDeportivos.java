package interfaces;

import modelo.Equipo;
import modelo.Partido;

import java.util.List;

// ADAPTER: interfaz objetivo que unifica distintas fuentes de datos (TXT, CSV, JSON) bajo un contrato común.
public interface ProveedorDatosDeportivos {
    List<Partido> obtenerPartidosEnVivo();
    List<Equipo> obtenerEquipos();
}
