import estrategia.CriterioPorGoles;
import estrategia.CriterioPorPuntos;
import factory.AficionadoFactory;
import factory.AnalistaDeportivoFactory;
import factory.CasaApuestaFactory;
import factory.PeriodistaFactory;
import factory.UsuarioFactory;
import fuentes.AdaptadorCsvLiga;
import fuentes.AdaptadorCsvTarjetas;
import fuentes.AdaptadorJsonApuestas;
import fuentes.AdaptadorJsonEstadisticas;
import fuentes.AdaptadorJsonPeriodistico;
import fuentes.AdaptadorTxtMundial;
import fuentes.AdaptadorTxtPosesion;
import interfaces.IUsuario;
import interfaces.ProveedorDatosDeportivos;
import interfaces.Repositorio;
import interfaces.VistaTiempoReal;
import modelo.Arbitro;
import modelo.Equipo;
import modelo.EventoPartido;
import modelo.Jugador;
import modelo.Partido;
import modelo.Ranking;
import modelo.TipoEvento;
import persistencia.RepositorioEnMemoria;
import servicio.PlataformaDeportiva;
import vista.PantallaPartido;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {

    private static final String RUTA_ARBITROS = "data/arbitros.csv";

    public static void main(String[] args) {
        if (args.length > 0 && args[0].equalsIgnoreCase("periodista")) {
            imprimirDatosParaPeriodista();
            return;
        }
        if (args.length > 0 && args[0].equalsIgnoreCase("apuestas")) {
            imprimirDatosParaCasaApuestas(args);
            return;
        }

        mostrarInicio();
    }

    private static void mostrarInicio() {

        Scanner scanner = new Scanner(System.in);
        int opcion;

        do {

            System.out.println("\n=== INICIO ===");
            System.out.println("1. Abrir menu interactivo");
            System.out.println("2. Ejecutar demo completa");
            System.out.println("0. Salir");

            opcion = scanner.nextInt();

            switch (opcion) {

                case 1:
                    new Menu(scanner).iniciar();
                    break;

                case 2:
                    ejecutarDemoCompleta();
                    break;
            }

        } while (opcion != 0);
    }

    private static void ejecutarDemoCompleta() {

        // ---------- ADAPTER TXT ----------
        ProveedorDatosDeportivos fuenteMundial = new AdaptadorTxtMundial();
        PlataformaDeportiva plataforma = new PlataformaDeportiva(fuenteMundial);
        List<Equipo> equipos = plataforma.obtenerEquipos();
        List<Partido> partidos = fuenteMundial.obtenerPartidosEnVivo();

        System.out.println("=== Equipos cargados desde TXT Mundial (ADAPTER) ===");
        imprimirEquipos(equipos);

        // ---------- ADAPTER CSV ----------
        ProveedorDatosDeportivos fuenteLiga = new AdaptadorCsvLiga();
        System.out.println("\n=== Equipos cargados desde CSV Liga local (ADAPTER) ===");
        imprimirEquipos(fuenteLiga.obtenerEquipos());

        // ---------- NUEVOS ADAPTERS DE DATOS DEL MUNDIAL ----------
        AdaptadorJsonEstadisticas fuenteEstadisticas = new AdaptadorJsonEstadisticas();
        AdaptadorTxtPosesion fuentePosesion = new AdaptadorTxtPosesion();
        AdaptadorCsvTarjetas fuenteTarjetas = new AdaptadorCsvTarjetas();
        AdaptadorJsonPeriodistico fuentePeriodistica = new AdaptadorJsonPeriodistico();

        System.out.println("\n=== Datos complementarios del Mundial (ADAPTERS) ===");
        System.out.println("Partidos desde JSON estadistico: "
                + fuenteEstadisticas.obtenerPartidosEnVivo().size());
        System.out.println("Registros de posesion desde TXT: "
                + fuentePosesion.obtenerPosesiones().size());
        System.out.println("Tarjetas desde CSV: "
                + fuenteTarjetas.obtenerTarjetas().size());
        System.out.println("Informes periodisticos desde JSON: "
                + fuentePeriodistica.obtenerInformes().size());

        System.out.println("\nEjemplo posesion ARGENTINA vs FRANCIA:");
        System.out.println(" - " + fuentePosesion.buscarPosesion("ARGENTINA", "FRANCIA"));

        System.out.println("\nEjemplo tarjetas ARGENTINA vs BRASIL:");
        for (AdaptadorCsvTarjetas.TarjetaPartido tarjeta :
                fuenteTarjetas.buscarTarjetasPorPartido("ARGENTINA", "BRASIL")) {
            System.out.println(" - " + tarjeta);
        }

        System.out.println("\nEjemplo informe periodistico ARGENTINA vs BRASIL:");
        imprimirInformePeriodistico(
                fuentePeriodistica.buscarInformePorPartido("ARGENTINA", "BRASIL"));

        // ---------- REPOSITORIO ----------
        Repositorio<Equipo> repositorioEquipos = new RepositorioEnMemoria<>();
        Repositorio<Partido> repositorioPartidos = new RepositorioEnMemoria<>();

        for (Equipo equipo : equipos) {
            repositorioEquipos.guardar(equipo);
        }

        for (Partido partido : partidos) {
            repositorioPartidos.guardar(partido);
        }

        System.out.println("\n=== Persistencia en memoria (REPOSITORIO) ===");
        System.out.println("Equipos guardados: " + repositorioEquipos.obtenerTodos().size());
        System.out.println("Partidos guardados: " + repositorioPartidos.obtenerTodos().size());

        // ---------- FACTORY METHOD ----------
        UsuarioFactory[] factories = {
                new AficionadoFactory(),
                new AnalistaDeportivoFactory(),
                new CasaApuestaFactory(),
                new PeriodistaFactory()
        };

        // ---------- OBSERVER + VISTA EN TIEMPO REAL ----------
        Partido partidoBase = partidos.get(0);
        Equipo local = partidoBase.getEquipoLocal();
        Equipo visitante = partidoBase.getEquipoVisitante();
        Partido partido = new Partido(local, visitante, "0-0");
        VistaTiempoReal pantalla = new PantallaPartido("Pantalla principal");

        for (UsuarioFactory factory : factories) {
            IUsuario usuario = factory.crearUsuario();
            partido.suscribir(usuario);
        }

        System.out.println("\n=== Eventos del partido " + local.getNombre()
                + " vs " + visitante.getNombre() + " (OBSERVER + VISTA) ===");
        pantalla.mostrarMarcador(partido.getResultado());

        Jugador autorLocal = local.getListaJugadores().get(0);
        Jugador autorVisitante = visitante.getListaJugadores().get(0);

        System.out.println("\n>> Minuto 23 - GOL");
        registrarEvento(partido, pantalla, new EventoPartido(23, TipoEvento.GOL, autorLocal));

        System.out.println("\n>> Minuto 50 - TARJETA AMARILLA");
        registrarEvento(partido, pantalla, new EventoPartido(50, TipoEvento.TARJETA_AMARILLA, autorVisitante));

        System.out.println("\n>> Minuto 78 - TARJETA ROJA");
        registrarEvento(partido, pantalla, new EventoPartido(78, TipoEvento.TARJETA_ROJA, autorVisitante));

        // ---------- STRATEGY ----------
        System.out.println("\n=== Ranking por PUNTOS (STRATEGY) ===");
        plataforma.setCriterioRanking(new CriterioPorPuntos());
        imprimirRanking(plataforma.generarRanking(equipos));

        System.out.println("\n=== Ranking por GOLES (STRATEGY) ===");
        plataforma.setCriterioRanking(new CriterioPorGoles());
        imprimirRanking(plataforma.generarRanking(equipos));

        // ---------- ARBITROS + PARTIDOS DESDE ARCHIVOS ----------
        System.out.println("\n=== Arbitros asignados a partidos del Mundial ===");
        imprimirArbitrosYPartidos(partidos);
    }

    private static void imprimirEquipos(List<Equipo> equipos) {
        for (Equipo e : equipos) {
            System.out.println(" - " + e.getNombre() + " (" + e.getCantidadJugadores() + " jugadores)");
        }
    }

    private static void registrarEvento(Partido partido, VistaTiempoReal pantalla, EventoPartido evento) {
        partido.agregarEvento(evento);
        pantalla.mostrarEvento(evento);
    }

    private static void imprimirRanking(List<Ranking> tabla) {
        for (Ranking r : tabla) {
            System.out.println(" " + r);
        }
    }

    private static void imprimirInformePeriodistico(
            AdaptadorJsonPeriodistico.InformePeriodistico informe) {
        System.out.println("Resultado: " + informe.getResultado());
        System.out.println("Posesion: " + informe.getEquipoLocal() + " "
                + informe.getPosesionLocal() + "% - " + informe.getEquipoVisitante()
                + " " + informe.getPosesionVisitante() + "%");

        System.out.println("Goles:");
        for (AdaptadorJsonPeriodistico.EventoPeriodistico gol : informe.getGoles()) {
            System.out.println(" - Min " + gol.getMinuto() + " | "
                    + gol.getJugador() + " (" + gol.getEquipo() + ")");
        }

        System.out.println("Tarjetas:");
        if (informe.getTarjetas().isEmpty()) {
            System.out.println(" - Sin tarjetas");
        } else {
            for (AdaptadorJsonPeriodistico.EventoPeriodistico tarjeta : informe.getTarjetas()) {
                System.out.println(" - Min " + tarjeta.getMinuto() + " | "
                        + tarjeta.getTipo() + " | " + tarjeta.getJugador()
                        + " (" + tarjeta.getEquipo() + ")");
            }
        }

        System.out.println("Resumen: " + informe.getResumenPeriodistico());
    }

    private static void imprimirDatosParaPeriodista() {
        AdaptadorJsonPeriodistico fuentePeriodistica = new AdaptadorJsonPeriodistico();

        System.out.println("=== Datos para periodista ===");
        imprimirInformePeriodistico(
                fuentePeriodistica.buscarInformePorPartido("ARGENTINA", "BRASIL"));
    }

    private static void imprimirDatosParaCasaApuestas(String[] args) {
        AdaptadorJsonApuestas fuenteApuestas = new AdaptadorJsonApuestas();

        System.out.println("=== Datos para casa de apuestas ===");

        if (args.length == 1) {
            imprimirMercadoApuestas(
                    fuenteApuestas.buscarMercadoPorPartido("ARGENTINA", "BRASIL"));
            return;
        }

        if (args.length == 2) {
            int cantidad = Integer.parseInt(args[1]);
            List<AdaptadorJsonApuestas.MercadoApuestas> mercados = fuenteApuestas.obtenerMercados();

            for (int i = 0; i < cantidad && i < mercados.size(); i++) {
                imprimirMercadoApuestas(mercados.get(i));
                if (i < cantidad - 1 && i < mercados.size() - 1) {
                    System.out.println();
                }
            }
            return;
        }

        String local = args[1];
        String visitante = args[2];
        imprimirMercadoApuestas(fuenteApuestas.buscarMercadoPorPartido(local, visitante));
    }

    private static void imprimirMercadoApuestas(
            AdaptadorJsonApuestas.MercadoApuestas mercado) {
        System.out.println("Partido: " + mercado.getEquipoLocal()
                + " vs " + mercado.getEquipoVisitante());
        System.out.println("Goles local: " + mercado.getGolesLocal());
        System.out.println("Goles visitante: " + mercado.getGolesVisitante());
        System.out.println("Total goles: " + mercado.getTotalGoles());
        System.out.println("Ambos equipos anotaron: "
                + (mercado.isAmbosEquiposAnotaron() ? "SI" : "NO"));
        System.out.println("Tarjetas amarillas: " + mercado.getTarjetasAmarillas());
        System.out.println("Tarjetas rojas: " + mercado.getTarjetasRojas());
        System.out.println("Total tarjetas: " + mercado.getTotalTarjetas());
        System.out.println("Posesion: " + mercado.getEquipoLocal() + " "
                + mercado.getPosesionLocal() + "% - " + mercado.getEquipoVisitante()
                + " " + mercado.getPosesionVisitante() + "%");
        System.out.println("Diferencia posesion: "
                + Math.abs(mercado.getPosesionLocal() - mercado.getPosesionVisitante()));
        System.out.println("Hubo roja: " + (mercado.isHuboRoja() ? "SI" : "NO"));
        System.out.println("Hubo tiempo extra: " + (mercado.isHuboTiempoExtra() ? "SI" : "NO"));
    }

    private static void imprimirArbitrosYPartidos(List<Partido> partidos) {
        List<Arbitro> arbitros = cargarArbitros();

        int cantidadAsignaciones = Math.min(arbitros.size(), partidos.size());
        for (int i = 0; i < cantidadAsignaciones; i++) {
            Partido partido = partidos.get(i);
            Arbitro arbitro = arbitros.get(i);
            partido.setArbitro(arbitro);

            System.out.println(" - " + partido.getArbitro().getNombreCompleto()
                    + " | Partido: " + partido.getEquipoLocal().getNombre()
                    + " vs " + partido.getEquipoVisitante().getNombre()
                    + " | Resultado: " + partido.getResultado());
        }
    }

    private static List<Arbitro> cargarArbitros() {
        try {
            List<Arbitro> arbitros = new ArrayList<>();
            List<String> lineas = Files.readAllLines(Paths.get(RUTA_ARBITROS), StandardCharsets.UTF_8);

            for (int i = 1; i < lineas.size(); i++) {
                String[] columnas = lineas.get(i).split(",", -1);
                if (columnas.length >= 4) {
                    arbitros.add(new Arbitro(columnas[0], columnas[1], columnas[2], columnas[3]));
                }
            }

            return arbitros;
        } catch (IOException e) {
            throw new RuntimeException("No se pudo leer el archivo de arbitros: " + RUTA_ARBITROS, e);
        }
    }
}
