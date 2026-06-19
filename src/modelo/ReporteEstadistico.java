package modelo;

import interfaces.CanalDistribucion;

import java.util.ArrayList;
import java.util.List;

// OBSERVER canal: acumula todos los eventos del partido para generar un resumen estadístico al final.
public class ReporteEstadistico implements CanalDistribucion {

    private List<EventoPartido> eventos;

    public ReporteEstadistico() {
        this.eventos = new ArrayList<>();
    }

    @Override
    public void publicar(EventoPartido evento) {
        eventos.add(evento);
        System.out.println("[Reporte] Evento registrado: min " + evento.getMinuto()
                + " | " + evento.getTipo()
                + " — " + evento.getAutor().getNombreCompleto());
    }

    public void imprimirResumen() {
        System.out.println("[Reporte] === Resumen del partido ===");
        long goles     = eventos.stream().filter(e -> e.getTipo() == TipoEvento.GOL).count();
        long amarillas = eventos.stream().filter(e -> e.getTipo() == TipoEvento.TARJETA_AMARILLA).count();
        long rojas     = eventos.stream().filter(e -> e.getTipo() == TipoEvento.TARJETA_ROJA).count();
        System.out.println("[Reporte]  Goles: " + goles
                + " | Amarillas: " + amarillas
                + " | Rojas: " + rojas
                + " | Total eventos: " + eventos.size());
    }
}
