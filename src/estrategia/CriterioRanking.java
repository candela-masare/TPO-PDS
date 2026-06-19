package estrategia;

import modelo.Equipo;

import java.util.List;

// STRATEGY: interfaz que define cómo se ordena la lista de equipos para construir un ranking.
public interface CriterioRanking {
    List<Equipo> ordenar(List<Equipo> equipos);
}
