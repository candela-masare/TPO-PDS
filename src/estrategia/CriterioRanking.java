package estrategia;

import modelo.Equipo;

import java.util.List;

/**
 * STRATEGY — define el algoritmo de ordenamiento para construir la tabla de posiciones.
 * <p>
 * Permite intercambiar el criterio de ranking en tiempo de ejecución sin modificar
 * {@link servicio.PlataformaDeportiva}. Implementaciones disponibles:
 * {@link CriterioPorPuntos} y {@link CriterioPorGoles}.
 * </p>
 */
public interface CriterioRanking {

    /**
     * Ordena la lista de equipos según el criterio concreto de esta estrategia.
     *
     * @param equipos lista de equipos a ordenar
     * @return nueva lista ordenada (no modifica la original)
     */
    List<Equipo> ordenar(List<Equipo> equipos);
}
