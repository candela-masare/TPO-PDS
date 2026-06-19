package interfaces;

import modelo.Equipo;
import modelo.Partido;

import java.util.List;

/**
 * ADAPTER — interfaz objetivo que unifica distintas fuentes de datos bajo un contrato común.
 * <p>
 * Cada adaptador traduce su formato de origen (TXT, CSV, JSON) a esta interfaz,
 * permitiendo que el resto del sistema trabaje con cualquier fuente sin conocer su formato.
 * Implementaciones: {@link fuentes.AdaptadorTxtMundial}, {@link fuentes.AdaptadorCsvLiga},
 * {@link fuentes.AdaptadorJsonEstadisticas}, {@link fuentes.AdaptadorJsonPeriodistico},
 * {@link fuentes.AdaptadorJsonApuestas}, {@link fuentes.AdaptadorTxtPosesion},
 * {@link fuentes.AdaptadorCsvTarjetas}.
 * </p>
 */
public interface ProveedorDatosDeportivos {

    /**
     * Devuelve la lista de partidos disponibles en la fuente de datos.
     *
     * @return lista de partidos (puede estar vacía, nunca {@code null})
     */
    List<Partido> obtenerPartidosEnVivo();

    /**
     * Devuelve la lista de equipos disponibles en la fuente de datos.
     *
     * @return lista de equipos (puede estar vacía, nunca {@code null})
     */
    List<Equipo> obtenerEquipos();
}
