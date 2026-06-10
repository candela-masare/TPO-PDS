package estrategia;

import modelo.Equipo;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class CriterioPorGoles implements CriterioRanking {

    @Override
    public List<Equipo> ordenar(List<Equipo> equipos) {
        List<Equipo> ordenados = new ArrayList<>(equipos);
        ordenados.sort(Comparator.comparingInt(Equipo::getGolesAFavor).reversed());
        return ordenados;
    }
}
