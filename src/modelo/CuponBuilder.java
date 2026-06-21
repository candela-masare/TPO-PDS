package modelo;

import java.util.ArrayList;
import java.util.List;

public class CuponBuilder {

    private final List<Pronostico> pronosticos = new ArrayList<>();

    public CuponBuilder agregarPronostico(Pronostico pronostico) {
        if (pronostico == null) {
            throw new IllegalArgumentException("No se puede agregar un pronostico nulo al cupon");
        }
        pronosticos.add(pronostico);
        return this;
    }

    public int getCantidad() {
        return pronosticos.size();
    }

    public Cupon build() {
        if (pronosticos.isEmpty()) {
            throw new IllegalStateException("No se puede armar un cupon vacio");
        }
        double puntaje = calcularPuntaje();
        return new Cupon(new ArrayList<>(pronosticos), Volatilidad.desdePromedio(puntaje), puntaje);
    }

    private double calcularPuntaje() {
        int sumaPesos = 0;
        for (Pronostico pronostico : pronosticos) {
            sumaPesos += pronostico.getVolatilidad().getPeso();
        }
        return (double) sumaPesos / pronosticos.size();
    }
}
