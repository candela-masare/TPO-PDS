import factory.AficionadoFactory;
import factory.AnalistaDeportivoFactory;
import factory.CasaApuestaFactory;
import factory.PeriodistaFactory;
import menus.MenuAnalista;
import menus.MenuAficionado;
import menus.MenuCasaApuesta;
import menus.MenuPeriodista;
import modelo.Aficionado;
import modelo.AnalistaDeportivo;
import modelo.CasaApuesta;
import modelo.Periodista;

import java.util.Scanner;

public class Menu {

    private Scanner scanner = new Scanner(System.in);

    public Menu() {
    }

    public Menu(Scanner scanner) {
        this.scanner = scanner;
    }

    public void iniciar() {

        int opcion;

        do {

            mostrarMenu();
            opcion = scanner.nextInt();

            switch (opcion) {

                case 1:
                    Aficionado aficionado =
                            (Aficionado) new AficionadoFactory().crearUsuario();

                    new MenuAficionado(aficionado, scanner).ejecutar();
                    break;

                case 2:
                    Periodista periodista =
                            (Periodista) new PeriodistaFactory().crearUsuario();

                    new MenuPeriodista(periodista, scanner).ejecutar();
                    break;

                case 3:
                    CasaApuesta casa =
                            (CasaApuesta) new CasaApuestaFactory().crearUsuario();

                    new MenuCasaApuesta(casa, scanner).ejecutar();
                    break;

                case 4:
                    AnalistaDeportivo analista =
                            (AnalistaDeportivo) new AnalistaDeportivoFactory().crearUsuario();

                    new MenuAnalista(analista, scanner).ejecutar();
                    break;
            }

        } while (opcion != 0);
    }

    private void mostrarMenu() {

        System.out.println("\n=== PLATAFORMA DEPORTIVA ===");
        System.out.println("1. Aficionado");
        System.out.println("2. Periodista");
        System.out.println("3. Casa de Apuestas");
        System.out.println("4. Analista Deportivo");
        System.out.println("0. Salir");
    }


}
