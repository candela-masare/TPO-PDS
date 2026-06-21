package servidor;

import com.sun.net.httpserver.HttpServer;

import java.net.InetSocketAddress;


public class ServidorHttp {

    private static final int PUERTO = 8080;

    public static void iniciar() throws Exception {
        crearYArrancar();
        System.out.println("Presiona Ctrl+C para detener.");
        Thread.currentThread().join();
    }

    public static void iniciarEnSegundoPlano() {
        try {
            crearYArrancar();
        } catch (Exception e) {
            System.err.println("No se pudo iniciar el servidor web: " + e.getMessage());
        }
    }

    private static void crearYArrancar() throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(PUERTO), 0);
        server.createContext("/api", new ApiHandler());
        server.createContext("/", new ArchivoHandler());
        server.setExecutor(null);
        server.start();
        System.out.println("Servidor web disponible en http://localhost:" + PUERTO);
    }
}
