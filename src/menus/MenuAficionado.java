package menus;

import modelo.Aficionado;
import modelo.Equipo;

import java.util.List;
import java.util.Scanner;

import fuentes.AdaptadorTxtMundial;
import interfaces.ProveedorDatosDeportivos;

// Submenú de consola para el perfil Aficionado: equipos, rankings, partidos y árbitros.
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
                    mostrarEquipos();

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
        System.out.println("\n");
        System.out.println("Elige una opcion: ");
    }

    private void mostrarEquipos() {

        ProveedorDatosDeportivos proveedor =
                new AdaptadorTxtMundial();

        List<Equipo> equipos =
                proveedor.obtenerEquipos();


        System.out.println("\n=== EQUIPOS PARTICIPANTES ===");

        for (Equipo equipo : equipos) {

            System.out.println(
                    "- " + equipo.getNombre()
            );
        }
    }



}
