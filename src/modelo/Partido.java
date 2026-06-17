package modelo;

import interfaces.IUsuario;

import java.util.ArrayList;
import java.util.List;

public class Partido {

    private Equipo equipoLocal;
    private Equipo equipoVisitante;
    private String resultado;
    private int idArbitro;
    private List<EventoPartido> eventos;
    private List<IUsuario> suscriptores;
    private Estadistica estadisticaLocal;
    private Estadistica estadisticaVisitante;

    public Partido(Equipo equipoLocal, Equipo equipoVisitante, String resultado,int idArbitro) {
        this.equipoLocal = equipoLocal;
        this.equipoVisitante = equipoVisitante;
        this.resultado = resultado;
        this.idArbitro=idArbitro;
        this.eventos = new ArrayList<>();
        this.suscriptores = new ArrayList<>();
        this.estadisticaLocal = new Estadistica(equipoLocal);
        this.estadisticaVisitante = new Estadistica(equipoVisitante);
    }

    public void suscribir(IUsuario usuario) {
        suscriptores.add(usuario);
    }

    public void desuscribir(IUsuario usuario) {
        suscriptores.remove(usuario);
    }

    public void agregarEvento(EventoPartido evento) {
        eventos.add(evento);
        procesarEstadisticas(evento);
        notificar(evento);
    }

    private void procesarEstadisticas(EventoPartido evento) {
        Jugador autor = evento.getAutor();
        if (autor == null) return;

        boolean esLocal = autor.getEquipo() != null
                && autor.getEquipo().getNombre().equals(equipoLocal.getNombre());
        Estadistica estadistica = esLocal ? estadisticaLocal : estadisticaVisitante;

        switch (evento.getTipo()) {
            case GOL:
                estadistica.incrementarGoles();
                autor.registrarGol();
                autor.getEquipo().setGolesAFavor(autor.getEquipo().getGolesAFavor() + 1);
                actualizarResultado();
                break;
            case TARJETA_AMARILLA:
                estadistica.incrementarTarjetasAmarillas();
                autor.registrarTarjetaAmarilla();
                break;
            case TARJETA_ROJA:
                estadistica.incrementarTarjetasRojas();
                autor.registrarTarjetaRoja();
                break;
            case PENAL:
                estadistica.incrementarGoles();
                autor.registrarGol();
                autor.getEquipo().setGolesAFavor(autor.getEquipo().getGolesAFavor() + 1);
                actualizarResultado();
                break;
            case SUSTITUCION:
                break;
        }
    }

    private void actualizarResultado() {
        this.resultado = estadisticaLocal.getGoles() + "-" + estadisticaVisitante.getGoles();
    }

    private void notificar(EventoPartido evento) {
        for (IUsuario suscriptor : suscriptores) {
            suscriptor.actualizar(evento);
        }
    }

    public Equipo getEquipoLocal() { return equipoLocal; }
    public Equipo getEquipoVisitante() { return equipoVisitante; }
    public String getResultado() { return resultado; }
    public void setResultado(String resultado) { this.resultado = resultado; }
    public List<EventoPartido> getEventos() { return eventos; }
    public Estadistica getEstadisticaLocal() { return estadisticaLocal; }
    public Estadistica getEstadisticaVisitante() { return estadisticaVisitante; }

    @Override
    public String toString() {
        return "Partido{" +
                "equipoLocal=" + (equipoLocal != null ? equipoLocal.getNombre() : "?") +
                ", equipoVisitante=" + (equipoVisitante != null ? equipoVisitante.getNombre() : "?") +
                ", resultado='" + resultado + '\'' +
                ", eventos=" + eventos.size() +
                '}';
    }
}
