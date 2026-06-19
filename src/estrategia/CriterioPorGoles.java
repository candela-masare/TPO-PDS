package estrategia;

import modelo.Equipo;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * STRATEGY concreta — ordena los equipos de mayor a menor según goles a favor.
 * <p>
 * Usada por {@link servicio.PlataformaDeportiva#generarRanking(java.util.List)}
 * como criterio alternativo al de puntos para el ranking del analista deportivo.
 * </p>
 */
public class CriterioPorGoles implements CriterioRanking {

    /**
     * @param equipos lista de equipos a ordenar
     * @return nueva lista ordenada por goles a favor de mayor a menor
     */
    @Override
    public List<Equipo> ordenar(List<Equipo> equipos) {
        List<Equipo> ordenados = new ArrayList<>(equipos);
        ordenados.sort(Comparator.comparingInt(Equipo::getGolesAFavor).reversed());
        return ordenados;
    }
}
