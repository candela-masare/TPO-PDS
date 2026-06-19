package servidor;

import com.sun.net.httpserver.HttpServer;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

public class ServidorHttp {

    private static final int PUERTO = 8080;

    public static void main(String[] args) throws Exception {
        EstadoAplicacion.getInstance().inicializar();

        HttpServer server = HttpServer.create(new InetSocketAddress(PUERTO), 0);
        server.createContext("/api", new ApiHandler());
        server.createContext("/", new ArchivoHandler());
        server.setExecutor(Executors.newFixedThreadPool(4));
        server.start();

        System.out.println("╔══════════════════════════════════════╗");
        System.out.println("║      PLATAFORMA DEPORTIVA - WEB      ║");
        System.out.println("╠══════════════════════════════════════╣");
        System.out.println("║  http://localhost:" + PUERTO + "               ║");
        System.out.println("║  Ctrl+C para detener                 ║");
        System.out.println("╚══════════════════════════════════════╝");
    }
}
