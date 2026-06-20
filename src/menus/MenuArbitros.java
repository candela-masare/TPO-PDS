package menus;

import java.util.List;
import java.util.Scanner;

import fuentes.AdaptadorCsvArbitros;
import fuentes.AdaptadorTxtMundial;
import interfaces.ProveedorArbitros;
import interfaces.ProveedorDatosDeportivos;
import modelo.Arbitro;
import modelo.Partido;
import utils.UtilsConsola;

// Submenú de consola para consultar árbitros y asignaciones (opciones pendientes de implementar).
public class MenuArbitros {

    private Scanner scanner;

    public MenuArbitros() {
        this(new Scanner(System.in));
    }

    public MenuArbitros(Scanner scanner) {
        this.scanner = scanner;
    }

    public void ejecutar() {

        int opcion;

        do {

            mostrarMenu();
            opcion = scanner.nextInt();

            switch (opcion) {

                case 1:
                    mostrarArbitros();

                    break;

                case 2:
                    mostrarPartidosAsignados();

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

    private Arbitro seleccionarArbitro() {

        ProveedorArbitros proveedor =
                new AdaptadorCsvArbitros();

        List<Arbitro> arbitros =
                proveedor.obtenerArbitros();

        mostrarArbitros();

        System.out.print("Seleccione arbitro: ");

        int opcion =
                UtilsConsola.leerEntero(scanner);

        if (opcion < 1 ||
                opcion > arbitros.size()) {

            return null;
        }

        return arbitros.get(opcion - 1);
    }

    private void mostrarArbitros() {

        ProveedorArbitros proveedor =
                new AdaptadorCsvArbitros();

        List<Arbitro> arbitros =
                proveedor.obtenerArbitros();

        for (int i = 0; i < arbitros.size(); i++) {

            Arbitro arbitro =
                    arbitros.get(i);

            System.out.println(
                    (i + 1) + ". "
                            + arbitro.getNombre()
                            + " "
                            + arbitro.getApellido()
            );
        }
    }




    private void mostrarPartidosAsignados() {

        Arbitro arbitro =
                seleccionarArbitro();

        if (arbitro == null) {

            System.out.println(
                    "Arbitro inexistente"
            );

            return;
        }

        ProveedorDatosDeportivos proveedor =
                new AdaptadorTxtMundial();

        List<Partido> partidos =
                proveedor.obtenerPartidosEnVivo();

        System.out.println(
                "\nPartidos asignados a "
                        + arbitro.getNombre()
                        + " "
                        + arbitro.getApellido()
        );

        for (Partido partido : partidos) {

            if (arbitro.getId()==(partido.getIdArbitro())) {

                System.out.println(
                        partido.getEquipoLocal()
                                .getNombre()
                                + " vs "
                                + partido.getEquipoVisitante()
                                .getNombre()
                );
            }
        }
    }

}
