package servicio;

import estrategia.CriterioRanking;
import interfaces.CanalDistribucion;
import interfaces.IUsuario;
import interfaces.ProveedorDatosDeportivos;
import modelo.Equipo;
import modelo.EventoPartido;
import modelo.Partido;
import modelo.Ranking;
import modelo.Torneo;
import reporte.Reporte;
import reporte.ReporteBasico;
import reporte.ReporteConPromedios;
import reporte.ReporteConTopJugadores;

import java.util.ArrayList;
import java.util.List;

public class PlataformaDeportiva implements IUsuario {

    private ProveedorDatosDeportivos proveedor;
    private CriterioRanking criterioRanking;
    private List<CanalDistribucion> canales;

    public PlataformaDeportiva(ProveedorDatosDeportivos proveedor) {
        this.proveedor = proveedor;
        this.canales = new ArrayList<>();
    }

    public void setProveedor(ProveedorDatosDeportivos proveedor) {
        this.proveedor = proveedor;
    }

    public void setCriterioRanking(CriterioRanking criterioRanking) {
        this.criterioRanking = criterioRanking;
    }

    public void agregarCanal(CanalDistribucion canal) {
        canales.add(canal);
    }

    public void quitarCanal(CanalDistribucion canal) {
        canales.remove(canal);
    }

    @Override
    public void actualizar(EventoPartido evento) {
        for (CanalDistribucion canal : canales) {
            canal.publicar(evento);
        }
    }

    public List<Equipo> obtenerEquipos() {
        return proveedor.obtenerEquipos();
    }

    public List<Partido> obtenerPartidosEnVivo() {
        return proveedor.obtenerPartidosEnVivo();
    }

    public Torneo crearTorneo(String nombre) {
        Torneo torneo = new Torneo(nombre);
        for (Equipo equipo : proveedor.obtenerEquipos()) {
            torneo.agregarEquipo(equipo);
        }
        return torneo;
    }

    public String generarReporteAvanzado(Partido partido) {
        Reporte reporte = new ReporteConTopJugadores(
                new ReporteConPromedios(new ReporteBasico(partido), partido), partido);
        return reporte.generar();
    }

    public List<Ranking> generarRanking(List<Equipo> equipos) {
        if (criterioRanking == null) {
            throw new IllegalStateException("No se configuro un CriterioRanking (Strategy).");
        }
        List<Equipo> ordenados = criterioRanking.ordenar(equipos);
        List<Ranking> tabla = new ArrayList<>();
        for (int i = 0; i < ordenados.size(); i++) {
            tabla.add(new Ranking(i + 1, ordenados.get(i)));
        }
        return tabla;
    }
}
