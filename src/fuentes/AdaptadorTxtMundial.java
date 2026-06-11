package fuentes;

import interfaces.ProveedorDatosDeportivos;
import modelo.Equipo;
import modelo.Jugador;
import modelo.Partido;

import java.util.ArrayList;
import java.util.List;

public class AdaptadorTxtMundial implements ProveedorDatosDeportivos {

    private final String[] lineasArchivoTxt = {
            "Argentina|1893|9|8|3|Lionel Messi/Delantero;Emiliano Martinez/Arquero",
            "Francia|1919|6|20|9|Kylian Mbappe/Delantero;Antoine Griezmann/Mediocampista",
            "Croacia|1912|4|14|11|Luka Modric/Mediocampista"
    };

    @Override
    public List<Equipo> obtenerEquipos() {
        List<Equipo> equipos = new ArrayList<>();
        for (String linea : lineasArchivoTxt) {
            String[] campos = linea.split("\\|");
            String nombre = campos[0];
            int anio = Integer.parseInt(campos[1]);
            int puntos = Integer.parseInt(campos[2]);
            int golesAFavor = Integer.parseInt(campos[3]);
            int golesEnContra = Integer.parseInt(campos[4]);
            Equipo equipo = new Equipo(nombre, anio, puntos, golesAFavor);
            equipo.setGolesEnContra(golesEnContra);
            if (campos.length > 5) {
                for (String j : campos[5].split(";")) {
                    String[] datos = j.split("/");
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
