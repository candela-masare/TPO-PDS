package fuentes;

import interfaces.ProveedorDatosDeportivos;
import modelo.Equipo;
import modelo.Jugador;
import modelo.Partido;

import java.util.ArrayList;
import java.util.List;

public class AdaptadorApiOle implements ProveedorDatosDeportivos {

    @Override
    public List<Equipo> obtenerEquipos() {
        List<Equipo> equipos = new ArrayList<>();

        Equipo boca = new Equipo("Boca Juniors", 1905, 28, 22);
        boca.agregarJugador(new Jugador("Edinson Cavani", "Delantero", boca));

        Equipo river = new Equipo("River Plate", 1901, 32, 26);
        river.agregarJugador(new Jugador("Miguel Borja", "Delantero", river));

        Equipo sanLorenzo = new Equipo("San Lorenzo", 1908, 20, 18);
        sanLorenzo.agregarJugador(new Jugador("Adam Bareiro", "Delantero", sanLorenzo));

        equipos.add(boca);
        equipos.add(river);
        equipos.add(sanLorenzo);
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
