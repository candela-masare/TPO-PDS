package modelo;

import java.util.Collections;
import java.util.List;

public class Cupon {

    private final List<Pronostico> pronosticos;
    private final Volatilidad volatilidadCombinada;
    private final double puntaje;

    Cupon(List<Pronostico> pronosticos, Volatilidad volatilidadCombinada, double puntaje) {
        this.pronosticos = Collections.unmodifiableList(pronosticos);
        this.volatilidadCombinada = volatilidadCombinada;
        this.puntaje = puntaje;
    }

    public List<Pronostico> getPronosticos() {
        return pronosticos;
    }

    public int getCantidad() {
        return pronosticos.size();
    }

    public Volatilidad getVolatilidadCombinada() {
        return volatilidadCombinada;
    }

    public double getPuntaje() {
        return puntaje;
    }
}
