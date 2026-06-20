package fuentes;

import interfaces.ProveedorArbitros;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import modelo.Arbitro;

public class AdaptadorCsvArbitros implements ProveedorArbitros{

     private static final String RUTA_CSV = "data/arbitros.csv";
    private String rutaArchivo;


    public AdaptadorCsvArbitros() {
        this.rutaArchivo = RUTA_CSV;
    }


    public AdaptadorCsvArbitros(String rutaArchivo) {
        this.rutaArchivo = rutaArchivo;
    }

    @Override
    public List<Arbitro> obtenerArbitros() {
        List<Arbitro> arbitros = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(rutaArchivo))) {

            String linea;

            // Saltar encabezado si el CSV tiene títulos
            br.readLine();

            while ((linea = br.readLine()) != null) {

                String[] columnas = linea.split(",");


                Arbitro arbitro = new Arbitro(
                        Integer.parseInt(columnas[0]),
                        columnas[1],
                        columnas[2],
                        columnas[3]
                );

                arbitros.add(arbitro);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return arbitros;

    }

}
