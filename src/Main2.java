public class Main2 {
    static void main() {

        System.out.println("Directorio actual:");

        System.out.println(System.getProperty("user.dir"));

        Menu menuPrincipal = new Menu();
        menuPrincipal.iniciar();

    }
}
