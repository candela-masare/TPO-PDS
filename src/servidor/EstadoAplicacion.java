package servidor;

import estrategia.CriterioPorGoles;
import estrategia.CriterioPorPuntos;
import factory.*;
import fuentes.AdaptadorCsvLiga;
import fuentes.AdaptadorTxtMundial;
import interfaces.ProveedorDatosDeportivos;
import modelo.*;
import servicio.PlataformaDeportiva;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class EstadoAplicacion {

    private static EstadoAplicacion instancia;

    private ProveedorDatosDeportivos fuenteMundial;
    private ProveedorDatosDeportivos fuenteLiga;
    private PlataformaDeportiva plataforma;

    private final Map<Integer, Partido> partidos = new ConcurrentHashMap<>();
    private final Map<Integer, String> partidoFuente = new ConcurrentHashMap<>();
    private final AtomicInteger partidoCounter = new AtomicInteger(1);

    private EstadoAplicacion() {}

    public static synchronized EstadoAplicacion getInstance() {
        if (instancia == null) {
            instancia = new EstadoAplicacion();
        }
        return instancia;
    }

    public void inicializar() {
        fuenteMundial = new AdaptadorTxtMundial();
        fuenteLiga = new AdaptadorCsvLiga();
        plataforma = new PlataformaDeportiva(fuenteMundial);
    }

    public synchronized List<Equipo> getEquipos(String fuente) {
        configurarProveedor(fuente);
        return plataforma.obtenerEquipos();
    }

    public synchronized List<Partido> getPartidosEnVivo(String fuente) {
        configurarProveedor(fuente);
        return plataforma.obtenerPartidosEnVivo();
    }

    public synchronized int crearPartido(String fuente, int localIdx, int visitanteIdx) {
        List<Equipo> equipos = getEquipos(fuente);
        if (localIdx < 0 || localIdx >= equipos.size()
                || visitanteIdx < 0 || visitanteIdx >= equipos.size()) {
            throw new IllegalArgumentException("Indices de equipo invalidos");
        }
        Equipo local = equipos.get(localIdx);
        Equipo visitante = equipos.get(visitanteIdx);
        Partido partido = new Partido(local, visitante, "0-0");

        // Patrón Observer + Factory: suscriptores automáticos
        partido.suscribir(new AficionadoFactory().crearUsuario());
        partido.suscribir(new AnalistaDeportivoFactory().crearUsuario());
        partido.suscribir(new CasaApuestaFactory().crearUsuario());
        partido.suscribir(new PeriodistaFactory().crearUsuario());

        int id = partidoCounter.getAndIncrement();
        partidos.put(id, partido);
        partidoFuente.put(id, fuente);
        return id;
    }

    public Partido getPartido(int id) {
        return partidos.get(id);
    }

    public Map<Integer, Partido> getPartidos() {
        return partidos;
    }

    public String getFuentePartido(int id) {
        return partidoFuente.getOrDefault(id, "mundial");
    }

    public synchronized List<Ranking> getRanking(String fuente, String criterio) {
        List<Equipo> equipos = getEquipos(fuente);
        if ("goles".equalsIgnoreCase(criterio)) {
            plataforma.setCriterioRanking(new CriterioPorGoles());
        } else {
            plataforma.setCriterioRanking(new CriterioPorPuntos());
        }
        return plataforma.generarRanking(equipos);
    }

    public String getReporte(int partidoId) {
        Partido partido = partidos.get(partidoId);
        if (partido == null) return null;
        return plataforma.generarReporteAvanzado(partido);
    }

    private void configurarProveedor(String fuente) {
        if ("liga".equalsIgnoreCase(fuente)) {
            plataforma.setProveedor(fuenteLiga);
        } else {
            plataforma.setProveedor(fuenteMundial);
        }
    }
}
