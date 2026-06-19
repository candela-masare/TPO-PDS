package menus;

import fuentes.AdaptadorJsonPeriodistico;
import modelo.Periodista;

import java.util.List;
import java.util.Scanner;

// Submenú de consola para el perfil Periodista: lista y detalle de informes periodísticos.
public class MenuPeriodista {

    private Periodista periodista;
    private Scanner scanner;
    private AdaptadorJsonPeriodistico fuentePeriodistica =
            new AdaptadorJsonPeriodistico();

    public MenuPeriodista(Periodista periodista) {
        this(periodista, new Scanner(System.in));
    }

    public MenuPeriodista(Periodista periodista, Scanner scanner) {
        this.periodista = periodista;
        this.scanner = scanner;
    }

    public void ejecutar() {

        int opcion;

        do {

            mostrarMenu();
            opcion = scanner.nextInt();

            switch (opcion) {

                case 1:
                    mostrarInformes();
                    break;

                case 2:
                    mostrarDetalleInforme();
                    break;
            }

        } while (opcion != 0);
    }

    private void mostrarMenu() {

        System.out.println("\n=== MENU PERIODISTA ===");
        System.out.println("Periodista: " + periodista.getNombre());
        System.out.println("1. Ver informes periodisticos");
        System.out.println("2. Ver detalle de un informe");
        System.out.println("0. Volver");
    }

    private void mostrarInformes() {

        List<AdaptadorJsonPeriodistico.InformePeriodistico> informes =
                fuentePeriodistica.obtenerInformes();

        System.out.println("\n=== INFORMES DISPONIBLES ===");

        for (int i = 0; i < informes.size(); i++) {

            AdaptadorJsonPeriodistico.InformePeriodistico informe =
                    informes.get(i);

            System.out.println((i + 1) + ". "
                    + informe.getEquipoLocal()
                    + " vs "
                    + informe.getEquipoVisitante()
                    + " | Resultado: "
                    + informe.getResultado()
                    + " | Estadio: "
                    + informe.getEstadio());
        }
    }

    private void mostrarDetalleInforme() {

        List<AdaptadorJsonPeriodistico.InformePeriodistico> informes =
                fuentePeriodistica.obtenerInformes();

        mostrarInformes();

        System.out.print("\nSeleccione informe: ");

        int indice = scanner.nextInt() - 1;

        if (indice < 0 || indice >= informes.size()) {
            System.out.println("Informe inexistente");
            return;
        }

        AdaptadorJsonPeriodistico.InformePeriodistico informe =
                informes.get(indice);

        System.out.println("\n=== DETALLE PERIODISTICO ===");
        System.out.println("Partido: "
                + informe.getEquipoLocal()
                + " vs "
                + informe.getEquipoVisitante());
        System.out.println("Fecha: " + informe.getFecha());
        System.out.println("Estadio: " + informe.getEstadio());
        System.out.println("Resultado: " + informe.getResultado());
        System.out.println("Posesion: "
                + informe.getEquipoLocal()
                + " "
                + informe.getPosesionLocal()
                + "% - "
                + informe.getEquipoVisitante()
                + " "
                + informe.getPosesionVisitante()
                + "%");
        System.out.println("Dominador posesion: "
                + informe.getDominadorPosesion());

        System.out.println("Goles:");
        if (informe.getGoles().isEmpty()) {
            System.out.println(" - Sin goles");
        } else {
            for (AdaptadorJsonPeriodistico.EventoPeriodistico gol :
                    informe.getGoles()) {
                System.out.println(" - " + gol);
            }
        }

        System.out.println("Tarjetas:");
        if (informe.getTarjetas().isEmpty()) {
            System.out.println(" - Sin tarjetas");
        } else {
            for (AdaptadorJsonPeriodistico.EventoPeriodistico tarjeta :
                    informe.getTarjetas()) {
                System.out.println(" - " + tarjeta);
            }
        }

        System.out.println("Resumen: "
                + informe.getResumenPeriodistico());
    }
}
