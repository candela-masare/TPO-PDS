package menus;

import fuentes.AdaptadorJsonApuestas;
import modelo.CasaApuesta;
import utils.UtilsConsola;

import java.util.List;
import java.util.Scanner;

// Submenú de consola para el perfil Casa de Apuestas: lista y detalle de mercados con riesgo y cuotas.
public class MenuCasaApuesta {

    private CasaApuesta casa;
    private Scanner scanner;
    private AdaptadorJsonApuestas fuenteApuestas = new AdaptadorJsonApuestas();

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
                    mostrarMercados();
                    break;

                case 2:
                    mostrarDetalleMercado();
                    break;
            }

        } while (opcion != 0);
    }

    private void mostrarMenu() {

        System.out.println("\n=== MENU CASA DE APUESTAS ===");
        System.out.println("Casa: " + casa.getNombre());
        System.out.println("1. Ver mercados disponibles");
        System.out.println("2. Ver detalle de un mercado");
        System.out.println("0. Volver");
        System.out.println("\n");
        System.out.println("Elige una opcion: ");
    }

    private void mostrarMercados() {

        List<AdaptadorJsonApuestas.MercadoApuestas> mercados =
                fuenteApuestas.obtenerMercados();

        System.out.println("\n=== MERCADOS DISPONIBLES ===");

        for (int i = 0; i < mercados.size(); i++) {

            AdaptadorJsonApuestas.MercadoApuestas mercado =
                    mercados.get(i);

            System.out.println((i + 1) + ". "
                    + mercado.getEquipoLocal()
                    + " vs "
                    + mercado.getEquipoVisitante()
                    + " | Mercado: "
                    + mercado.getMercadoDestacado()
                    + " | Riesgo: "
                    + mercado.getRiesgoParaCuota());
        }
    }

    private void mostrarDetalleMercado() {

        List<AdaptadorJsonApuestas.MercadoApuestas> mercados =
                fuenteApuestas.obtenerMercados();

        mostrarMercados();

        System.out.print("\nSeleccione mercado: ");

        int indice =UtilsConsola.leerEntero(scanner) - 1;

        if (indice < 0 || indice >= mercados.size()) {
            System.out.println("Mercado inexistente");
            return;
        }

        AdaptadorJsonApuestas.MercadoApuestas mercado =
                mercados.get(indice);

        System.out.println("\n=== DETALLE DEL MERCADO ===");
        System.out.println("Partido: "
                + mercado.getEquipoLocal()
                + " vs "
                + mercado.getEquipoVisitante());
        System.out.println("Resultado: " + mercado.getResultado());
        System.out.println("Goles local: " + mercado.getGolesLocal());
        System.out.println("Goles visitante: " + mercado.getGolesVisitante());
        System.out.println("Total goles: " + mercado.getTotalGoles());
        System.out.println("Ambos equipos anotaron: "
                + (mercado.isAmbosEquiposAnotaron() ? "SI" : "NO"));
        System.out.println("Tarjetas amarillas: "
                + mercado.getTarjetasAmarillas());
        System.out.println("Tarjetas rojas: " + mercado.getTarjetasRojas());
        System.out.println("Total tarjetas: " + mercado.getTotalTarjetas());
        System.out.println("Posesion: "
                + mercado.getEquipoLocal()
                + " "
                + mercado.getPosesionLocal()
                + "% - "
                + mercado.getEquipoVisitante()
                + " "
                + mercado.getPosesionVisitante()
                + "%");
        System.out.println("Dominador posesion: "
                + mercado.getDominadorPosesion());
        System.out.println("Hubo roja: "
                + (mercado.isHuboRoja() ? "SI" : "NO"));
        System.out.println("Hubo tiempo extra: "
                + (mercado.isHuboTiempoExtra() ? "SI" : "NO"));
        System.out.println("Mercado destacado: "
                + mercado.getMercadoDestacado());
        System.out.println("Riesgo para cuota: "
                + mercado.getRiesgoParaCuota());
    }
}
