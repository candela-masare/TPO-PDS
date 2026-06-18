package fuentes;

import interfaces.ProveedorDatosDeportivos;
import modelo.Equipo;
import modelo.Partido;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * ADAPTER: adapta los informes combinados a datos utiles para una casa de apuestas.
 */
public class AdaptadorJsonApuestas implements ProveedorDatosDeportivos {

    private AdaptadorJsonPeriodistico fuentePeriodistica;

    public AdaptadorJsonApuestas() {
        this.fuentePeriodistica = new AdaptadorJsonPeriodistico();
    }

    public AdaptadorJsonApuestas(String rutaArchivoPeriodistico) {
        this.fuentePeriodistica = new AdaptadorJsonPeriodistico(rutaArchivoPeriodistico);
    }

    @Override
    public List<Equipo> obtenerEquipos() {
        Map<String, Equipo> equipos = new LinkedHashMap<>();

        for (MercadoApuestas mercado : obtenerMercados()) {
            obtenerOCrearEquipo(equipos, mercado.getEquipoLocal());
            obtenerOCrearEquipo(equipos, mercado.getEquipoVisitante());
        }

        return new ArrayList<>(equipos.values());
    }

    @Override
    public List<Partido> obtenerPartidosEnVivo() {
        Map<String, Equipo> equipos = new LinkedHashMap<>();
        List<Partido> partidos = new ArrayList<>();

        for (MercadoApuestas mercado : obtenerMercados()) {
            partidos.add(new Partido(
                    obtenerOCrearEquipo(equipos, mercado.getEquipoLocal()),
                    obtenerOCrearEquipo(equipos, mercado.getEquipoVisitante()),
                    mercado.getResultado()));
        }

        return partidos;
    }

    public List<MercadoApuestas> obtenerMercados() {
        List<MercadoApuestas> mercados = new ArrayList<>();

        for (AdaptadorJsonPeriodistico.InformePeriodistico informe : fuentePeriodistica.obtenerInformes()) {
            int golesLocal = contarGolesDeEquipo(informe, informe.getEquipoLocal());
            int golesVisitante = contarGolesDeEquipo(informe, informe.getEquipoVisitante());
            int tarjetasRojas = contarTarjetas(informe, "ROJA");
            int tarjetasAmarillas = contarTarjetas(informe, "AMARILLA");

            mercados.add(new MercadoApuestas(
                    informe.getPartidoId(),
                    informe.getEquipoLocal(),
                    informe.getEquipoVisitante(),
                    informe.getResultado(),
                    informe.getTotalGoles(),
                    golesLocal,
                    golesVisitante,
                    golesLocal > 0 && golesVisitante > 0,
                    informe.getTotalTarjetas(),
                    tarjetasAmarillas,
                    tarjetasRojas,
                    informe.isHuboRoja(),
                    informe.isHuboTiempoExtra(),
                    informe.getPosesionLocal(),
                    informe.getPosesionVisitante(),
                    informe.getDominadorPosesion(),
                    calcularMercadoDestacado(informe, golesLocal, golesVisitante),
                    calcularRiesgoParaCuota(informe)));
        }

        return mercados;
    }

    public MercadoApuestas buscarMercadoPorPartido(String local, String visitante) {
        for (MercadoApuestas mercado : obtenerMercados()) {
            if (clave(mercado.getEquipoLocal()).equals(clave(local))
                    && clave(mercado.getEquipoVisitante()).equals(clave(visitante))) {
                return mercado;
            }
        }

        throw new IllegalArgumentException("No existe mercado para el partido: " + local + " vs " + visitante);
    }

    private int contarGolesDeEquipo(AdaptadorJsonPeriodistico.InformePeriodistico informe, String equipo) {
        int total = 0;
        for (AdaptadorJsonPeriodistico.EventoPeriodistico gol : informe.getGoles()) {
            if (clave(gol.getEquipo()).equals(clave(equipo))) {
                total++;
            }
        }
        return total;
    }

    private int contarTarjetas(AdaptadorJsonPeriodistico.InformePeriodistico informe, String tipo) {
        int total = 0;
        for (AdaptadorJsonPeriodistico.EventoPeriodistico tarjeta : informe.getTarjetas()) {
            if (tarjeta.getTipo().equalsIgnoreCase(tipo)) {
                total++;
            }
        }
        return total;
    }

    private String calcularMercadoDestacado(AdaptadorJsonPeriodistico.InformePeriodistico informe,
                                            int golesLocal, int golesVisitante) {
        if (informe.isHuboTiempoExtra() && golesLocal > 0 && golesVisitante > 0) {
            return "empate_en_90_y_definicion_extendida";
        }
        if (informe.getTotalGoles() >= 4) {
            return "mas_de_3_5_goles";
        }
        if (informe.isHuboRoja()) {
            return "habra_tarjeta_roja";
        }
        if (informe.getTotalTarjetas() >= 2) {
            return "mas_de_1_5_tarjetas";
        }
        return "mercado_conservador";
    }

    private String calcularRiesgoParaCuota(AdaptadorJsonPeriodistico.InformePeriodistico informe) {
        if (informe.isHuboRoja() || informe.isHuboTiempoExtra()) {
            return "alto";
        }
        if (informe.getTotalGoles() >= 4 || informe.getTotalTarjetas() >= 2) {
            return "medio";
        }
        return "bajo";
    }

    private Equipo obtenerOCrearEquipo(Map<String, Equipo> equipos, String nombre) {
        String clave = clave(nombre);
        Equipo equipo = equipos.get(clave);
        if (equipo == null) {
            equipo = new Equipo(nombre, 0);
            equipos.put(clave, equipo);
        }
        return equipo;
    }

    private String clave(String valor) {
        return valor.toUpperCase(Locale.ROOT)
                .replace("Á", "A")
                .replace("É", "E")
                .replace("Í", "I")
                .replace("Ó", "O")
                .replace("Ú", "U")
                .replace("Ñ", "N");
    }

    public static class MercadoApuestas {
        private int partidoId;
        private String equipoLocal;
        private String equipoVisitante;
        private String resultado;
        private int totalGoles;
        private int golesLocal;
        private int golesVisitante;
        private boolean ambosEquiposAnotaron;
        private int totalTarjetas;
        private int tarjetasAmarillas;
        private int tarjetasRojas;
        private boolean huboRoja;
        private boolean huboTiempoExtra;
        private int posesionLocal;
        private int posesionVisitante;
        private String dominadorPosesion;
        private String mercadoDestacado;
        private String riesgoParaCuota;

        public MercadoApuestas(int partidoId, String equipoLocal, String equipoVisitante, String resultado,
                               int totalGoles, int golesLocal, int golesVisitante,
                               boolean ambosEquiposAnotaron, int totalTarjetas, int tarjetasAmarillas,
                               int tarjetasRojas, boolean huboRoja, boolean huboTiempoExtra,
                               int posesionLocal, int posesionVisitante, String dominadorPosesion,
                               String mercadoDestacado, String riesgoParaCuota) {
            this.partidoId = partidoId;
            this.equipoLocal = equipoLocal;
            this.equipoVisitante = equipoVisitante;
            this.resultado = resultado;
            this.totalGoles = totalGoles;
            this.golesLocal = golesLocal;
            this.golesVisitante = golesVisitante;
            this.ambosEquiposAnotaron = ambosEquiposAnotaron;
            this.totalTarjetas = totalTarjetas;
            this.tarjetasAmarillas = tarjetasAmarillas;
            this.tarjetasRojas = tarjetasRojas;
            this.huboRoja = huboRoja;
            this.huboTiempoExtra = huboTiempoExtra;
            this.posesionLocal = posesionLocal;
            this.posesionVisitante = posesionVisitante;
            this.dominadorPosesion = dominadorPosesion;
            this.mercadoDestacado = mercadoDestacado;
            this.riesgoParaCuota = riesgoParaCuota;
        }

        public int getPartidoId() { return partidoId; }
        public String getEquipoLocal() { return equipoLocal; }
        public String getEquipoVisitante() { return equipoVisitante; }
        public String getResultado() { return resultado; }
        public int getTotalGoles() { return totalGoles; }
        public int getGolesLocal() { return golesLocal; }
        public int getGolesVisitante() { return golesVisitante; }
        public boolean isAmbosEquiposAnotaron() { return ambosEquiposAnotaron; }
        public int getTotalTarjetas() { return totalTarjetas; }
        public int getTarjetasAmarillas() { return tarjetasAmarillas; }
        public int getTarjetasRojas() { return tarjetasRojas; }
        public boolean isHuboRoja() { return huboRoja; }
        public boolean isHuboTiempoExtra() { return huboTiempoExtra; }
        public int getPosesionLocal() { return posesionLocal; }
        public int getPosesionVisitante() { return posesionVisitante; }
        public String getDominadorPosesion() { return dominadorPosesion; }
        public String getMercadoDestacado() { return mercadoDestacado; }
        public String getRiesgoParaCuota() { return riesgoParaCuota; }

        @Override
        public String toString() {
            return equipoLocal + " vs " + equipoVisitante
                    + " | mercado=" + mercadoDestacado
                    + " | riesgo=" + riesgoParaCuota;
        }
    }
}
