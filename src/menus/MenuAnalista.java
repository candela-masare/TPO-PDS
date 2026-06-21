package menus;

import estrategia.CriterioPorGoles;
import estrategia.CriterioPorPuntos;
import fuentes.AdaptadorCsvLiga;
import fuentes.AdaptadorTxtMundial;
import modelo.AnalistaDeportivo;
import modelo.Equipo;
import modelo.Jugador;
import modelo.Partido;
import modelo.Ranking;
import servicio.PlataformaDeportiva;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

// Submenú de consola para el perfil Analista Deportivo: rankings por Strategy 
public class MenuAnalista {

    private AnalistaDeportivo analista;
    private Scanner scanner;
    private PlataformaDeportiva plataforma =
            new PlataformaDeportiva(new AdaptadorTxtMundial());

    private PlataformaDeportiva plataformaLiga =
        new PlataformaDeportiva(new AdaptadorCsvLiga());

    public MenuAnalista(AnalistaDeportivo analista) {
        this(analista, new Scanner(System.in));
    }

    public MenuAnalista(AnalistaDeportivo analista, Scanner scanner) {
        this.analista = analista;
        this.scanner = scanner;
    }

    public void ejecutar() {

        int opcion;

        do {

            mostrarMenu();
            opcion = scanner.nextInt();

            switch (opcion) {

                case 1:
                    mostrarRankingPorPuntos();
                    break;

                case 2:
                    mostrarRankingPorGoles();
                    break;

                case 3:
                    mostrarReportePartido();
                    break;
                case 4:
                    mostrarInformacionEquipo();
                    break;
                case 5:
                    mostrarInformacionJugador();
                    break;


            }

        } while (opcion != 0);
    }

    private void mostrarMenu() {

        System.out.println("\n=== MENU ANALISTA DEPORTIVO ===");
        System.out.println("Analista: " + analista.getNombre());
        System.out.println("1. Ranking por puntos");
        System.out.println("2. Ranking por goles");
        System.out.println("3. Reporte avanzado de un partido");
        System.out.println("4. Consultar informacion de un equipo");
        System.out.println("5. Consultar informacion de un jugador");
        System.out.println("0. Volver");
        System.out.println("\n");
        System.out.println("Elige una opcion: ");
    }

    private void mostrarRankingPorPuntos() {

        plataforma.setCriterioRanking(new CriterioPorPuntos());
        mostrarRanking(plataforma.generarRanking(obtenerEquipos()));
    }

    private void mostrarRankingPorGoles() {

        plataforma.setCriterioRanking(new CriterioPorGoles());
        mostrarRanking(plataforma.generarRanking(obtenerEquipos()));
    }

    private void mostrarRanking(List<Ranking> rankings) {

        System.out.println("\n=== RANKING ===");

        for (Ranking ranking : rankings) {
            System.out.println(ranking);
        }
    }

    private void mostrarReportePartido() {

        List<Partido> partidos = plataforma.obtenerPartidosEnVivo();

        mostrarPartidos(partidos);

        System.out.print("\nSeleccione partido: ");

        int indice = scanner.nextInt() - 1;

        if (indice < 0 || indice >= partidos.size()) {
            System.out.println("Partido inexistente");
            return;
        }

        Partido partido = partidos.get(indice);

        System.out.println("\n=== REPORTE AVANZADO ===");
        System.out.println(plataforma.generarReporteAvanzado(partido));
    }

    private void mostrarPartidos(List<Partido> partidos) {

        System.out.println("\n=== PARTIDOS DISPONIBLES ===");

        for (int i = 0; i < partidos.size(); i++) {

            Partido partido = partidos.get(i);

            System.out.println((i + 1) + ". "
                    + partido.getEquipoLocal().getNombre()
                    + " vs "
                    + partido.getEquipoVisitante().getNombre()
                    + " | Resultado: "
                    + partido.getResultado());
        }
    }

    private List<Equipo> obtenerEquipos() {
        return plataforma.obtenerEquipos();
    }

    private void mostrarInformacionEquipo() {

        

        List<Equipo> equipos =
            plataformaLiga.obtenerEquipos();

        System.out.println("\n=== EQUIPOS DISPONIBLES ===");

        for (int i = 0; i < equipos.size(); i++) {

        System.out.println(
                (i + 1) + ". "
                        + equipos.get(i).getNombre()
        );

        }

        System.out.print("\nSeleccione equipo: ");

        int indice = scanner.nextInt() - 1;

        if (indice < 0 || indice >= equipos.size()) {

        System.out.println("Equipo inexistente");
        return;

        }

        Equipo equipo = equipos.get(indice);

        System.out.println("\n=== INFORMACION DEL EQUIPO ===");
        System.out.println("Nombre: " + equipo.getNombre());
        System.out.println("Puntos: " + equipo.getPuntos());
        System.out.println("Goles a favor: " + equipo.getGolesAFavor());
        System.out.println("Goles en contra: " + equipo.getGolesEnContra());


    }

    private void mostrarInformacionJugador() {


        List<Equipo> equipos = plataformaLiga.obtenerEquipos();


        // Selección de equipo

        System.out.println("\n=== EQUIPOS DISPONIBLES ===");

        for (int i = 0; i < equipos.size(); i++) {

        System.out.println(
                (i + 1) + ". "
                + equipos.get(i).getNombre()
        );
        }


        System.out.print("\nSeleccione equipo: ");

        int indiceEquipo = scanner.nextInt() - 1;


        if (indiceEquipo < 0 || indiceEquipo >= equipos.size()) {

        System.out.println("Equipo inexistente");
        return;
        }


        Equipo equipoSeleccionado = equipos.get(indiceEquipo);



        // Obtener jugadores del equipo elegido

        List<Jugador> jugadores =
            equipoSeleccionado.getListaJugadores();



        System.out.println("\n=== JUGADORES DE "
            + equipoSeleccionado.getNombre()
            + " ===");


        for (int i = 0; i < jugadores.size(); i++) {

        System.out.println(
                (i + 1) + ". "
                + jugadores.get(i).getNombreCompleto()
        );
        }



        System.out.print("\nSeleccione jugador: ");

        int indiceJugador = scanner.nextInt() - 1;


        if (indiceJugador < 0 || indiceJugador >= jugadores.size()) {

        System.out.println("Jugador inexistente");
        return;
        }



        Jugador jugador = jugadores.get(indiceJugador);



        // Mostrar información

        System.out.println("\n=== INFORMACION DEL JUGADOR ===");

        System.out.println("Nombre: "
            + jugador.getNombreCompleto());

        System.out.println("Equipo: "
            + equipoSeleccionado.getNombre());

        System.out.println("Posicion: "
            + jugador.getPosicion());

        System.out.println("Goles: "
            + jugador.getGoles());

        System.out.println("Tarjetas amarillas: "
            + jugador.getTarjetasAmarillas());

        System.out.println("Tarjetas rojas: "
            + jugador.getTarjetasRojas());
        

    }

    



}
