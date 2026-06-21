package modelo;

import java.util.Locale;

public enum Volatilidad {

    BAJA(1),
    MEDIA(2),
    ALTA(3);

    private final int peso;

    Volatilidad(int peso) {
        this.peso = peso;
    }

    public int getPeso() {
        return peso;
    }

    public String getEtiqueta() {
        return name().toLowerCase(Locale.ROOT);
    }

    public static Volatilidad desdeTexto(String texto) {
        if (texto == null) {
            throw new IllegalArgumentException("La volatilidad no puede ser nula");
        }
        switch (texto.trim().toLowerCase(Locale.ROOT)) {
            case "bajo":
            case "baja":
                return BAJA;
            case "medio":
            case "media":
                return MEDIA;
            case "alto":
            case "alta":
                return ALTA;
            default:
                throw new IllegalArgumentException("Volatilidad desconocida: " + texto);
        }
    }

    public static Volatilidad desdePromedio(double promedio) {
        if (promedio < 1.5) {
            return BAJA;
        }
        if (promedio < 2.5) {
            return MEDIA;
        }
        return ALTA;
    }
}
