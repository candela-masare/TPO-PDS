package servidor;

import com.sun.net.httpserver.HttpServer;

import java.net.InetSocketAddress;


public class ServidorHttp {

    private static final int PUERTO = 8080;

    public static void iniciar() throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(PUERTO), 0);
        server.createContext("/api", new ApiHandler());
        server.createContext("/", new ArchivoHandler());
        server.setExecutor(null);
        server.start();
        System.out.println("Servidor iniciado en http://localhost:" + PUERTO);
        System.out.println("Presiona Ctrl+C para detener.");
        Thread.currentThread().join();
    }
}
