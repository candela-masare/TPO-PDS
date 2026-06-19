package servidor;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ArchivoHandler implements HttpHandler {

    private static final String DIRECTORIO = "frontend";

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String ruta = exchange.getRequestURI().getPath();

        if (ruta.equals("/") || ruta.isEmpty()) {
            ruta = "/index.html";
        }

        Path archivo = Paths.get(DIRECTORIO + ruta);

        if (!Files.exists(archivo) || Files.isDirectory(archivo)) {
            archivo = Paths.get(DIRECTORIO + "/index.html");
        }

        if (!Files.exists(archivo)) {
            String cuerpo = "404 - Archivo no encontrado";
            byte[] bytes = cuerpo.getBytes();
            exchange.sendResponseHeaders(404, bytes.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(bytes);
            }
            return;
        }

        byte[] contenido = Files.readAllBytes(archivo);
        exchange.getResponseHeaders().set("Content-Type", tipoContenido(archivo.toString()));
        exchange.getResponseHeaders().set("Cache-Control", "no-store, no-cache, must-revalidate");
        exchange.getResponseHeaders().set("Pragma", "no-cache");
        exchange.getResponseHeaders().set("Expires", "0");
        exchange.sendResponseHeaders(200, contenido.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(contenido);
        }
    }

    private String tipoContenido(String ruta) {
        if (ruta.endsWith(".html")) return "text/html; charset=UTF-8";
        if (ruta.endsWith(".css"))  return "text/css; charset=UTF-8";
        if (ruta.endsWith(".js"))   return "application/javascript; charset=UTF-8";
        if (ruta.endsWith(".json")) return "application/json; charset=UTF-8";
        if (ruta.endsWith(".png"))  return "image/png";
        if (ruta.endsWith(".jpg") || ruta.endsWith(".jpeg")) return "image/jpeg";
        if (ruta.endsWith(".ico"))  return "image/x-icon";
        return "text/plain; charset=UTF-8";
    }
}
