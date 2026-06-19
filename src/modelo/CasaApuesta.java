package modelo;

import interfaces.IUsuario;

// OBSERVER concreto: recibe eventos del partido y simula un recálculo de cuotas de apuestas.
public class CasaApuesta implements IUsuario {

    private String nombre;

    public CasaApuesta(String nombre) {
        this.nombre = nombre;
    }

    @Override
    public void actualizar(EventoPartido evento) {
        System.out.println("[CASA DE APUESTAS] " + nombre + " recalcula cuotas tras "
                + evento.getTipo() + " al minuto " + evento.getMinuto() + ".");
    }

    public String getNombre() { return nombre; }
}
