package menus;

import java.util.Scanner;

public class MenuRankings {

    private Scanner scanner = new Scanner(System.in);

    public void ejecutar() {

        int opcion;

        do {

            mostrarMenu();
            opcion = scanner.nextInt();

            switch (opcion) {

                case 1:

                    break;

                case 2:

                    break;

                case 3:

                    break;
            }

        } while (opcion != 0);
    }


    private void mostrarMenu() {

        System.out.println("\n=== RANKINGS ===");
        System.out.println("1. Ranking por puntos");
        System.out.println("0. Volver");
    }


}
