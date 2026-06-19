package reporte;

// DECORATOR: clase base abstracta que envuelve un Reporte existente para agregarle capas de información.
public abstract class ReporteDecorator implements Reporte {

    protected Reporte reporte;

    public ReporteDecorator(Reporte reporte) {
        this.reporte = reporte;
    }
}
