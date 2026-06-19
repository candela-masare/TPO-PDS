package menus;

import estrategia.CriterioPorGoles;
import estrategia.CriterioPorPuntos;
import fuentes.AdaptadorTxtMundial;
import modelo.AnalistaDeportivo;
import modelo.Equipo;
import modelo.Partido;
import modelo.Ranking;
import servicio.PlataformaDeportiva;

import java.util.List;
import java.util.Scanner;

// Submenú de consola para el perfil Analista Deportivo: rankings por Strategy y reporte avanzado por Decorator.
public class MenuAnalista {

    private AnalistaDeportivo analista;
    private Scanner scanner;
    private PlataformaDeportiva plataforma =
            new PlataformaDeportiva(new AdaptadorTxtMundial());

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
            }

        } while (opcion != 0);
    }

    private void mostrarMenu() {

        System.out.println("\n=== MENU ANALISTA DEPORTIVO ===");
        System.out.println("Analista: " + analista.getNombre());
        System.out.println("1. Ranking por puntos");
        System.out.println("2. Ranking por goles");
        System.out.println("3. Reporte avanzado de un partido");
        System.out.println("0. Volver");
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
}
