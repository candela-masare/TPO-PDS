package estrategia;

import modelo.Equipo;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * STRATEGY concreta — ordena los equipos de mayor a menor según puntos acumulados.
 * <p>
 * Usada por {@link servicio.PlataformaDeportiva#generarRanking(java.util.List)}
 * para construir la tabla de posiciones estándar del torneo.
 * </p>
 */
public class CriterioPorPuntos implements CriterioRanking {

    /**
     * @param equipos lista de equipos a ordenar
     * @return nueva lista ordenada por puntos de mayor a menor
     */
    @Override
    public List<Equipo> ordenar(List<Equipo> equipos) {
        List<Equipo> ordenados = new ArrayList<>(equipos);
        ordenados.sort(Comparator.comparingInt(Equipo::getPuntos).reversed());
        return ordenados;
    }
}
