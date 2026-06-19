package estrategia;

import modelo.Equipo;

import java.util.List;

public interface CriterioRanking {
    List<Equipo> ordenar(List<Equipo> equipos);
}
