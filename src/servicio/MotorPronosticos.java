package servicio;

import fuentes.AdaptadorJsonApuestas;
import fuentes.AdaptadorJsonApuestas.MercadoApuestas;
import modelo.HistorialEquipo;
import modelo.Pronostico;
import modelo.PronosticoBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MotorPronosticos {

    private final AdaptadorJsonApuestas fuente;

    public MotorPronosticos() {
        this.fuente = new AdaptadorJsonApuestas();
    }

    public MotorPronosticos(AdaptadorJsonApuestas fuente) {
        this.fuente = fuente;
    }

    public List<Pronostico> obtenerProximos() {
        List<MercadoApuestas> mercados = fuente.obtenerMercados();
        List<Pronostico> proximos = new ArrayList<>();

        for (MercadoApuestas mercado : mercados) {
            int ronda = ronda(mercado.getPartidoId());
            if (ronda <= 1) {
                continue;
            }

            HistorialEquipo histLocal = construirHistorial(mercado.getEquipoLocal(), ronda, mercados);
            HistorialEquipo histVisitante = construirHistorial(mercado.getEquipoVisitante(), ronda, mercados);

            Pronostico pronostico = new PronosticoBuilder(
                    mercado.getPartidoId(), fase(mercado.getPartidoId()),
                    mercado.getEquipoLocal(), mercado.getEquipoVisitante(),
                    histLocal, histVisitante)
                    .calcularGoles()
                    .calcularProbabilidades()
                    .calcularTarjetas()
                    .calcularVolatilidad()
                    .generarJustificacion()
                    .build();

            proximos.add(pronostico);
        }

        return proximos;
    }

    public Pronostico obtenerPorId(int partidoId) {
        for (Pronostico pronostico : obtenerProximos()) {
            if (pronostico.getPartidoId() == partidoId) {
                return pronostico;
            }
        }
        return null;
    }

    private HistorialEquipo construirHistorial(String equipo, int rondaActual, List<MercadoApuestas> mercados) {
        HistorialEquipo historial = new HistorialEquipo(equipo);
        String clave = clave(equipo);

        for (MercadoApuestas mercado : mercados) {
            if (ronda(mercado.getPartidoId()) >= rondaActual) {
                continue;
            }
            if (clave(mercado.getEquipoLocal()).equals(clave)) {
                historial.registrar(mercado.getGolesLocal(), mercado.getGolesVisitante(), mercado.getTotalTarjetas());
            } else if (clave(mercado.getEquipoVisitante()).equals(clave)) {
                historial.registrar(mercado.getGolesVisitante(), mercado.getGolesLocal(), mercado.getTotalTarjetas());
            }
        }

        return historial;
    }

    private int ronda(int id) {
        if (id <= 8) return 1;
        if (id <= 12) return 2;
        if (id <= 14) return 3;
        return 4;
    }

    private String fase(int id) {
        if (id <= 8) return "OCTAVOS DE FINAL";
        if (id <= 12) return "CUARTOS DE FINAL";
        if (id <= 14) return "SEMIFINAL";
        return "FINAL";
    }

    private String clave(String valor) {
        if (valor == null) return "";
        return valor.toUpperCase(Locale.ROOT)
                .replace("Á", "A").replace("É", "E").replace("Í", "I")
                .replace("Ó", "O").replace("Ú", "U").replace("Ñ", "N").trim();
    }
}
