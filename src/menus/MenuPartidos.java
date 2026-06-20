package menus;

import fuentes.AdaptadorTxtMundial;
import interfaces.ProveedorDatosDeportivos;
import modelo.Partido;
import utils.UtilsConsola;

import java.util.List;
import java.util.Scanner;

// Submenú de consola que lista los partidos del Mundial y muestra el detalle de uno seleccionado.
public class MenuPartidos {

    private Scanner scanner;

    public MenuPartidos() {
        this(new Scanner(System.in));
    }

    public MenuPartidos(Scanner scanner) {
        this.scanner = scanner;
    }

    public void ejecutar() {

        int opcion;

        do {

            mostrarMenu();
            opcion = scanner.nextInt();

            switch (opcion) {

                case 1:
                    mostrarPartidos();
                    break;

                case 2:
                    mostrarDetallePartido();
                    break;
            }

        } while (opcion != 0);
    }

    private void mostrarMenu() {

        System.out.println("\n=== MENU PARTIDOS ===");
        System.out.println("1. Ver partidos");
        System.out.println("2. Ver detalle de un partido");
        System.out.println("0. Volver");
    }

    private void mostrarPartidos() {

        ProveedorDatosDeportivos fuente =
                new AdaptadorTxtMundial();

        List<Partido> partidos =
                fuente.obtenerPartidosEnVivo();

        System.out.println("\n=== PARTIDOS DISPONIBLES ===");

        for (int i = 0; i < partidos.size(); i++) {

            Partido partido = partidos.get(i);

            System.out.println(
                    (i + 1) + ". "
                            + partido.getEquipoLocal().getNombre()
                            + " vs "
                            + partido.getEquipoVisitante().getNombre()
                            + " | Resultado: "
                            + partido.getResultado()
            );
        }
    }

    private void mostrarDetallePartido() {

        ProveedorDatosDeportivos fuente =
                new AdaptadorTxtMundial();

        List<Partido> partidos =
                fuente.obtenerPartidosEnVivo();

        mostrarPartidos();

        System.out.print("\nSeleccione partido: ");

        int indice =UtilsConsola.leerEntero(scanner) - 1;

        if (indice < 0 || indice >= partidos.size()) {

            System.out.println("Partido inexistente");
            return;
        }

        Partido partido = partidos.get(indice);

        System.out.println("\n=== DETALLE ===");
        System.out.println("Local: "
                + partido.getEquipoLocal().getNombre());

        System.out.println("Visitante: "
                + partido.getEquipoVisitante().getNombre());

        System.out.println("Resultado: "
                + partido.getResultado());
    }



}
