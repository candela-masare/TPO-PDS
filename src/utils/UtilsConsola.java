package utils;

import java.util.InputMismatchException;
import java.util.Scanner;

public class UtilsConsola {
    

    private UtilsConsola(){

    }

    public static int leerEntero(Scanner scanner){

        while(true){
            try{
                return scanner.nextInt();
            }

            catch (InputMismatchException e){
                System.out.println("-----------------------------");
                System.out.println("Error: Debe ingresar un numero");
                System.out.println("-----------------------------");
                System.out.println("\n");
                System.out.println("Seleccione una opcion: ");
                scanner.next();
            }

        }

    }

    
}
