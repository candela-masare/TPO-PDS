package modelo;

import java.util.ArrayList;
import java.util.List;

/**
 * Representa un torneo que agrupa equipos y partidos.
 */
public class Torneo {

    private String nombre;
    private List<Equipo> equipos;
    private List<Partido> partidos;

    public Torneo(String nombre) {
        this.nombre = nombre;
        this.equipos = new ArrayList<>();
        this.partidos = new ArrayList<>();
    }

    public void agregarEquipo(Equipo equipo) {
        equipos.add(equipo);
    }

    public void agregarPartido(Partido partido) {
        partidos.add(partido);
    }

    public String getNombre() { return nombre; }
    public List<Equipo> getEquipos() { return equipos; }
    public List<Partido> getPartidos() { return partidos; }

    @Override
    public String toString() {
        return "Torneo{" +
                "nombre='" + nombre + '\'' +
                ", equipos=" + equipos.size() +
                ", partidos=" + partidos.size() +
                '}';
    }
}
