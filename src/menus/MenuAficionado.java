package menus;

import modelo.Aficionado;

import java.util.Scanner;

public class MenuAficionado {

    private Aficionado usuario;
    private Scanner scanner;

    public MenuAficionado(Aficionado usuario) {
        this(usuario, new Scanner(System.in));
    }

    public MenuAficionado(Aficionado usuario, Scanner scanner) {
        this.usuario = usuario;
        this.scanner = scanner;
    }

    public void ejecutar() {

        int opcion;

        do {

            mostrarMenu();
            opcion = scanner.nextInt();

            switch (opcion) {

                case 1:

                    break;

                case 2:
                    new MenuRankings(scanner).ejecutar();
                    break;

                case 3:
                    new MenuPartidos(scanner).ejecutar();
                    break;

                case 4:
                    new MenuArbitros(scanner).ejecutar();
                    break;
            }

        } while (opcion != 0);
    }


    private void mostrarMenu() {

        System.out.println("\n=== MENU AFICIONADO ===");
        System.out.println("1. Ver equipos");
        System.out.println("2. Rankings");
        System.out.println("3. Partidos");
        System.out.println("4. Árbitros");
        System.out.println("0. Volver");
    }


}
