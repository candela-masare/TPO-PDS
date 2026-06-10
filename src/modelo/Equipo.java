package modelo;

import java.util.ArrayList;
import java.util.List;

public class Equipo {

    private String nombre;
    private int anioFundacion;
    private ArrayList<Jugador> listaJugadores;
    private int puntos;
    private int golesAFavor;
    private int golesEnContra;


    public Equipo(String nombre, int anioFundacion) {
        this.nombre = nombre;
        this.anioFundacion = anioFundacion;
        this.listaJugadores = new ArrayList<>();
        this.puntos = 0;
        this.golesAFavor = 0;
        this.golesEnContra = 0;
    }

    public Equipo(String nombre, int anioFundacion, int puntos, int golesAFavor) {
        this(nombre, anioFundacion);
        this.puntos = puntos;
        this.golesAFavor = golesAFavor;
    }

    public void agregarJugador(Jugador jugador) {
        listaJugadores.add(jugador);
    }

    public int getCantidadJugadores() {
        return listaJugadores.size();
    }

    public String getNombre() {
        return nombre;
    }

    public int getAnioFundacion() {
        return anioFundacion;
    }

    public List<Jugador> getListaJugadores() {
        return listaJugadores;
    }

    public int getPuntos() {
        return puntos;
    }

    public void setPuntos(int puntos) {
        this.puntos = puntos;
    }

    public int getGolesAFavor() {
        return golesAFavor;
    }

    public void setGolesAFavor(int golesAFavor) {
        this.golesAFavor = golesAFavor;
    }

    public int getGolesEnContra() { return golesEnContra; }
    public void setGolesEnContra(int golesEnContra) { this.golesEnContra = golesEnContra; }

    public int getDiferenciaGoles() { return golesAFavor - golesEnContra; }


    @Override
    public String toString() {
        return "Equipo{" +
                "nombre='" + nombre + '\'' +
                ", anioFundacion=" + anioFundacion +
                ", puntos=" + puntos +
                ", golesAFavor=" + golesAFavor +
                ", cantidadJugadores=" + getCantidadJugadores() +
                '}';
    }


}
