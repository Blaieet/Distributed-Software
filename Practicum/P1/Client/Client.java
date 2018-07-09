
import java.util.Scanner;
import java.io.*;
import java.net.*;
/**
 *
 * @author blair
 */
public class Client {

    /**
     * @param args the command line arguments
     */ 
    
    private static int balance;
    private static int id;
    private static Comunicacio comunicacio = new Comunicacio();
    private static String nomMaquina;
    private static int port;
    private static int mode = 0;
    // args -s maquina -p port [-i mode fun]
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        if (args.length > 6) {
            System.out.println("Si us plau, usa java Client -s <nom_maquina> -p <port> [-i 0|1|2]");
            System.exit(1);
        }
        if(args[0].equals("-s") && args[2].equals("-p")){
            nomMaquina = args[1];
            port = Integer.parseInt(args[3]);
            if(args.length == 6 && args [4].equals("-i")){
                int argument5 = Integer.parseInt(args[5]);
                if(argument5 >= 0 && argument5 <= 2){
                    mode = argument5;
                }else{
                    System.out.println("Mode no valid. Opcions: 0(manual), 1(automatic) o 2(IA)");
                }
                
            }
            comunicacio.connectar(nomMaquina, port);
            
            System.out.println("Escriu el seu id: ");
            comunicacio.iniciarPartida(sc.nextInt());
            (new Thread(new FilClient(comunicacio, mode))).start();
            
        } else {
            System.out.println("El format no es el correcte. Si us plau, usa java Client -s <nom_maquina> -p <port> [-i 0|1|2]");
        }
        
    }
    
}

