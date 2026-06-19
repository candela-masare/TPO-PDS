package modelo;

public class EventoPartido {

    private int minuto;
    private TipoEvento tipo;
    private Jugador autor;

    public EventoPartido(int minuto, TipoEvento tipo, Jugador autor) {
        this.minuto = minuto;
        this.tipo = tipo;
        this.autor = autor;
    }

    public int getMinuto() {
        return minuto;
    }

    public TipoEvento getTipo() {
        return tipo;
    }

    public Jugador getAutor() {
        return autor;
    }

    @Override
    public String toString() {
        return "EventoPartido{" +
                "minuto=" + minuto +
                ", tipo=" + tipo +
                ", autor=" + autor +
                '}';
    }
}
