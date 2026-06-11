package fuentes;

import interfaces.ProveedorDatosDeportivos;
import modelo.Equipo;
import modelo.Jugador;
import modelo.Partido;

import java.util.ArrayList;
import java.util.List;

public class AdaptadorCsvLiga implements ProveedorDatosDeportivos {

    private final String[] lineasArchivoCsv = {
            "Boca Juniors,1905,30,25,12,Edinson Cavani:Delantero;Sergio Romero:Arquero",
            "River Plate,1901,34,28,10,Miguel Borja:Delantero;Franco Armani:Arquero",
            "Racing Club,1903,28,20,15,Maximiliano Salas:Delantero"
    };

    @Override
    public List<Equipo> obtenerEquipos() {
        List<Equipo> equipos = new ArrayList<>();
        for (String linea : lineasArchivoCsv) {
            String[] campos = linea.split(",");
            String nombre = campos[0];
            int anio = Integer.parseInt(campos[1]);
            int puntos = Integer.parseInt(campos[2]);
            int golesAFavor = Integer.parseInt(campos[3]);
            int golesEnContra = Integer.parseInt(campos[4]);
            Equipo equipo = new Equipo(nombre, anio, puntos, golesAFavor);
            equipo.setGolesEnContra(golesEnContra);
            if (campos.length > 5) {
                for (String j : campos[5].split(";")) {
                    String[] datos = j.split(":");
                    equipo.agregarJugador(new Jugador(datos[0], datos[1], equipo));
                }
            }
            equipos.add(equipo);
        }
        return equipos;
    }

    @Override
    public List<Partido> obtenerPartidosEnVivo() {
        List<Equipo> equipos = obtenerEquipos();
        List<Partido> partidos = new ArrayList<>();
        partidos.add(new Partido(equipos.get(0), equipos.get(1), "0-0"));
        return partidos;
    }
}
