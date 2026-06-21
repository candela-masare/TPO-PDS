package modelo;

public class CuponBuilderTest {

    private static int fallos = 0;

    public static void main(String[] args) {

        check("bajo + bajo => BAJA",
                volatilidadDe(Volatilidad.BAJA, Volatilidad.BAJA) == Volatilidad.BAJA);
        check("medio + medio => MEDIA",
                volatilidadDe(Volatilidad.MEDIA, Volatilidad.MEDIA) == Volatilidad.MEDIA);
        check("alto + alto => ALTA",
                volatilidadDe(Volatilidad.ALTA, Volatilidad.ALTA) == Volatilidad.ALTA);
        check("bajo + alto => MEDIA (promedio 2.0)",
                volatilidadDe(Volatilidad.BAJA, Volatilidad.ALTA) == Volatilidad.MEDIA);
        check("bajo + medio => MEDIA (empate 1.5 sube)",
                volatilidadDe(Volatilidad.BAJA, Volatilidad.MEDIA) == Volatilidad.MEDIA);
        check("medio + alto => ALTA (empate 2.5 sube)",
                volatilidadDe(Volatilidad.MEDIA, Volatilidad.ALTA) == Volatilidad.ALTA);

        Cupon cupon = new CuponBuilder()
                .agregarPronostico(pron(Volatilidad.MEDIA))
                .agregarPronostico(pron(Volatilidad.ALTA))
                .build();
        check("cupon con 2 pronosticos => getCantidad() == 2", cupon.getCantidad() == 2);
        check("puntaje medio+alto == 2.5", Math.abs(cupon.getPuntaje() - 2.5) < 0.001);

        boolean lanzo = false;
        try {
            new CuponBuilder().build();
        } catch (IllegalStateException e) {
            lanzo = true;
        }
        check("build() vacio lanza IllegalStateException", lanzo);

        HistorialEquipo local = new HistorialEquipo("LOCAL");
        local.registrar(3, 1, 2);
        HistorialEquipo visitante = new HistorialEquipo("VISITANTE");
        visitante.registrar(0, 2, 2);

        Pronostico p = new PronosticoBuilder(9, "CUARTOS DE FINAL", "LOCAL", "VISITANTE", local, visitante)
                .calcularGoles().calcularProbabilidades().calcularTarjetas().calcularVolatilidad().generarJustificacion().build();

        check("goles est local == 2.5", Math.abs(p.getGolesEstLocal() - 2.5) < 0.001);
        check("goles est visitante == 0.5", Math.abs(p.getGolesEstVisitante() - 0.5) < 0.001);
        check("prob local == 83", p.getProbLocal() == 83);
        check("prob suma 100", p.getProbLocal() + p.getProbVisitante() == 100);
        check("favorito claro => volatilidad BAJA", p.getVolatilidad() == Volatilidad.BAJA);
        check("justificacion no vacia", p.getJustificacion() != null && !p.getJustificacion().isEmpty());

        System.out.println();
        if (fallos == 0) {
            System.out.println("TODOS LOS TESTS PASARON");
        } else {
            System.out.println(fallos + " TEST(S) FALLARON");
            System.exit(1);
        }
    }

    private static Volatilidad volatilidadDe(Volatilidad... niveles) {
        CuponBuilder builder = new CuponBuilder();
        for (Volatilidad nivel : niveles) {
            builder.agregarPronostico(pron(nivel));
        }
        return builder.build().getVolatilidadCombinada();
    }

    private static Pronostico pron(Volatilidad volatilidad) {
        return new Pronostico(0, "CUARTOS DE FINAL", "A", "B", 50, 50, 1.0, 1.0, 1.0, volatilidad, "test");
    }

    private static void check(String nombre, boolean condicion) {
        if (condicion) {
            System.out.println("[OK]    " + nombre);
        } else {
            System.out.println("[FALLO] " + nombre);
            fallos++;
        }
    }
}
