package servidor;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ArchivoHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String requestPath = exchange.getRequestURI().getPath();

        if (requestPath.equals("/") || requestPath.equals("/index.html")) {
            servirArchivo(exchange, "frontend/index.html", "text/html; charset=utf-8");
        } else {
            enviarError(exchange, 404, "Pagina no encontrada");
        }
    }

    private void servirArchivo(HttpExchange exchange, String rutaRelativa, String contentType) throws IOException {
        Path ruta = Paths.get(System.getProperty("user.dir"), rutaRelativa);

        if (!Files.exists(ruta)) {
            enviarError(exchange, 404, "Archivo no encontrado: " + ruta.toAbsolutePath());
            return;
        }

        byte[] bytes = Files.readAllBytes(ruta);
        exchange.getResponseHeaders().set("Content-Type", contentType);
        exchange.sendResponseHeaders(200, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    private void enviarError(HttpExchange exchange, int status, String mensaje) throws IOException {
        byte[] bytes = mensaje.getBytes("UTF-8");
        exchange.getResponseHeaders().set("Content-Type", "text/plain; charset=utf-8");
        exchange.sendResponseHeaders(status, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }
}
