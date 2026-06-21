package menus;

import modelo.CasaApuesta;
import modelo.Cupon;
import modelo.CuponBuilder;
import modelo.Pronostico;
import servicio.MotorPronosticos;
import utils.UtilsConsola;

import java.util.List;
import java.util.Locale;
import java.util.Scanner;

public class MenuCasaApuesta {

    private CasaApuesta casa;
    private Scanner scanner;
    private MotorPronosticos motor = new MotorPronosticos();

    public MenuCasaApuesta(CasaApuesta casa) {
        this(casa, new Scanner(System.in));
    }

    public MenuCasaApuesta(CasaApuesta casa, Scanner scanner) {
        this.casa = casa;
        this.scanner = scanner;
    }

    public void ejecutar() {

        int opcion;

        do {

            mostrarMenu();
            opcion = scanner.nextInt();

            switch (opcion) {

                case 1:
                    mostrarProximos();
                    break;

                case 2:
                    mostrarDetalle();
                    break;

                case 3:
                    armarCupon();
                    break;
            }

        } while (opcion != 0);
    }

    private void mostrarMenu() {

        System.out.println("\n=== MENU CASA DE APUESTAS ===");
        System.out.println("Casa: " + casa.getNombre());
        System.out.println("1. Ver proximos partidos");
        System.out.println("2. Ver pronostico de un partido");
        System.out.println("3. Armar cupon combinado");
        System.out.println("0. Volver");
        System.out.println("\n");
        System.out.println("Elige una opcion: ");
    }

    private void mostrarProximos() {

        List<Pronostico> proximos = motor.obtenerProximos();

        System.out.println("\n=== PROXIMOS PARTIDOS (mercados abiertos) ===");
        System.out.println("(Octavos ya se jugaron: son el historial para estimar)\n");

        for (int i = 0; i < proximos.size(); i++) {
            Pronostico p = proximos.get(i);
            System.out.println((i + 1) + ". [" + p.getFase() + "] "
                    + p.getEquipoLocal() + " vs " + p.getEquipoVisitante()
                    + " | Prob: " + p.getProbLocal() + "%-" + p.getProbVisitante() + "%"
                    + " | Goles est.: " + fmt(p.getGolesEstLocal()) + "-" + fmt(p.getGolesEstVisitante())
                    + " | Volatilidad: " + p.getVolatilidad().getEtiqueta());
        }
    }

    private void mostrarDetalle() {

        List<Pronostico> proximos = motor.obtenerProximos();

        mostrarProximos();

        System.out.print("\nSeleccione partido: ");

        int indice = UtilsConsola.leerEntero(scanner) - 1;

        if (indice < 0 || indice >= proximos.size()) {
            System.out.println("Partido inexistente");
            return;
        }

        Pronostico p = proximos.get(indice);

        System.out.println("\n=== PRONOSTICO DEL PARTIDO ===");
        System.out.println(p.getFase() + ": " + p.getEquipoLocal() + " vs " + p.getEquipoVisitante());
        System.out.println("Probabilidad: " + p.getEquipoLocal() + " " + p.getProbLocal()
                + "% - " + p.getEquipoVisitante() + " " + p.getProbVisitante() + "%");
        System.out.println("Goles estimados: " + p.getEquipoLocal() + " " + fmt(p.getGolesEstLocal())
                + " - " + p.getEquipoVisitante() + " " + fmt(p.getGolesEstVisitante()));
        System.out.println("Tarjetas estimadas: " + fmt(p.getTarjetasEst()));
        System.out.println("Volatilidad estimada: " + p.getVolatilidad().getEtiqueta());
        System.out.println("Por que: " + p.getJustificacion());
    }

    private void armarCupon() {

        List<Pronostico> proximos = motor.obtenerProximos();

        CuponBuilder builder = new CuponBuilder();

        System.out.println("\n=== ARMAR CUPON COMBINADO ===");

        while (true) {

            mostrarProximos();
            System.out.print("\nAgregue un partido al cupon (0 para terminar): ");

            int indice = UtilsConsola.leerEntero(scanner) - 1;

            if (indice == -1) {
                break;
            }

            if (indice < 0 || indice >= proximos.size()) {
                System.out.println("Partido inexistente");
                continue;
            }

            Pronostico p = proximos.get(indice);
            builder.agregarPronostico(p);
            System.out.println("Agregado: " + p.getEquipoLocal() + " vs " + p.getEquipoVisitante()
                    + " (partidos en el cupon: " + builder.getCantidad() + ")");
        }

        if (builder.getCantidad() == 0) {
            System.out.println("\nNo agregaste partidos. Cupon vacio.");
            return;
        }

        Cupon cupon = builder.build();

        System.out.println("\n=== MI CUPON ===");
        for (Pronostico p : cupon.getPronosticos()) {
            System.out.println("- [" + p.getFase() + "] "
                    + p.getEquipoLocal() + " vs " + p.getEquipoVisitante()
                    + "   (Volatilidad: " + p.getVolatilidad().getEtiqueta() + ")");
        }
        System.out.println("Partidos: " + cupon.getCantidad()
                + " | Riesgo del cupon: "
                + cupon.getVolatilidadCombinada().getEtiqueta().toUpperCase()
                + " - "
                + String.format(Locale.US, "%.2f", cupon.getPuntaje())
                + " / 3.00");
    }

    private String fmt(double valor) {
        return String.format(Locale.US, "%.1f", valor);
    }
}
