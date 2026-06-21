package menus;

import estrategia.CriterioPorGoles;
import estrategia.CriterioPorPuntos;
import fuentes.AdaptadorTxtMundial;
import modelo.Ranking;
import servicio.PlataformaDeportiva;

import java.util.List;
import java.util.Scanner;

// Submenú de consola para consultar rankings de equipos 
public class MenuRankings {

    private Scanner scanner;
    private PlataformaDeportiva plataforma =
            new PlataformaDeportiva(new AdaptadorTxtMundial());

    public MenuRankings() {
        this(new Scanner(System.in));
    }

    public MenuRankings(Scanner scanner) {
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

            }

        } while (opcion != 0);
    }


    private void mostrarMenu() {

        System.out.println("\n=== RANKINGS ===");
        System.out.println("1. Ranking por puntos");
        System.out.println("0. Volver");
        System.out.println("0. Volver");
        System.out.println("Elige una opcion: ");
    }

    private void mostrarRankingPorPuntos() {

        plataforma.setCriterioRanking(new CriterioPorPuntos());
        mostrarRanking(plataforma.generarRanking(plataforma.obtenerEquipos()));
    }

    private void mostrarRanking(List<Ranking> rankings) {

        System.out.println("\n=== RANKING ===");

        for (Ranking ranking : rankings) {
            System.out.println(ranking);
        }
    }

     private void mostrarRankingPorGoles() {

        plataforma.setCriterioRanking(new CriterioPorGoles());

        mostrarRanking(
                plataforma.generarRanking(
                        plataforma.obtenerEquipos()
                )
        );
    }

}
