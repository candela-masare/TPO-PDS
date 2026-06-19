package estrategia;

import modelo.Equipo;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

// STRATEGY concreta: ordena equipos de mayor a menor según puntos acumulados.
public class CriterioPorPuntos implements CriterioRanking {

    @Override
    public List<Equipo> ordenar(List<Equipo> equipos) {
        List<Equipo> ordenados = new ArrayList<>(equipos);
        ordenados.sort(Comparator.comparingInt(Equipo::getPuntos).reversed());
        return ordenados;
    }
}
