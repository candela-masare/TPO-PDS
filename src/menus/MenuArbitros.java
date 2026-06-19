package menus;

import java.util.Scanner;

public class MenuArbitros {

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
            }

        } while (opcion != 0);
    }


    private void mostrarMenu() {

        System.out.println("\n=== ARBITROS ===");
        System.out.println("1. Listar árbitros");
        System.out.println("2. Ver asignaciones");
        System.out.println("0. Volver");
    }

}
