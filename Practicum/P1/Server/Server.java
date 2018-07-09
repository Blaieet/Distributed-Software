
import java.io.*;
import java.net.*;
import java.util.logging.*;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;



/**
 *
 * @author blair
 */
public class Server{
    private static final int MAXBALANCE = 10;

    public static void main(String[] args) {
        
        ServerSocket serverSocket = null;
        Socket socket = null;
        int numPartida = 0;
        Comunicacio comunicacio;
        String command = null;
        String id;
        int balanceP2;
        int balanceP1C;
        int serverPort;
        Logger logger = Logger.getLogger("MyLog");
        FileHandler file;
        ConcurrentHashMap<String,Integer> myMap = new ConcurrentHashMap<String,Integer>();
        int mode = 1;
        
        if (args.length > 4) {
            System.out.println("Si us plau, fes java Server -p [<numport>] [-1 1|2]");
            System.exit(1);
        }else{
            if(args[0].equals("-p")){
                if(args.length == 4){
                    if(args[2].equals("-i")){
                        int argument3 = Integer.parseInt(args[3]);
                        if(argument3 >= 0 && argument3 <= 2){
                            mode = argument3;
                        }else{
                            System.out.println("Mode no valid. Opcions: 1(automatic) o 2(IA)");
                        }
                    }
                }
                try {
                    serverPort = Integer.parseInt(args[1]); 
                    serverSocket = new ServerSocket(serverPort);
                    System.out.println("Server preparat al port: "+serverPort);

                    while (true) {
                        System.out.println("Esperem");
                        socket = serverSocket.accept();
                        comunicacio = new Comunicacio(socket, numPartida);
                        file = new FileHandler("ServerGame-"+numPartida+".log");
                        SimpleFormatter format = new SimpleFormatter() {
                            private static final String format = "[%1$tF %1$tT] [%2$-7s] %3$s %n";

                            @Override
                            public synchronized String format(LogRecord lr){
                                return String.format(format, new Date(lr.getMillis()),
                                    lr.getLevel().getLocalizedName(), lr.getMessage()
                                    );
                            }
                        };
                        file.setFormatter(format);
                        logger.addHandler(file);
                        numPartida++;
                        System.out.println("Un client s'ha connectat!\n");
                        command = comunicacio.rebreComanda();
                        if (command.equals("STRT")){
                            comunicacio.rebreEspai();
                            id = comunicacio.rebreParam();
                            if (myMap.containsKey(id)) {
                                balanceP1C = myMap.get(id);
                            } else {
                                balanceP1C = MAXBALANCE;
                                myMap.put(id,balanceP1C);
                            }
                            System.out.println("ID de l'usuari "+id);
                            comunicacio.balance(balanceP1C);

                            balanceP2 = MAXBALANCE;
                            logger.info("C: STRT "+id);
                            crearFil(numPartida, comunicacio, logger, balanceP2, balanceP1C, myMap, id, mode);
                        }
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    
                    try {
                        if(serverSocket != null) { serverSocket.close();}
                        
                    } catch (IOException e) {
                        System.out.println("Error Server");
                    }
                    
                }
            }
            
        }

    }

    public static void crearFil(int num, Comunicacio comunicacio, Logger logger, int balanceP2, int balanceP1C, ConcurrentHashMap<String,Integer> myMap, String id, int mode) {
        System.out.println("Thread num: "+num);
        Thread t = (new Thread(new Fil(num, comunicacio, logger,balanceP2, balanceP1C, myMap, id, mode)));
        t.start();
        System.out.println("Thread num: "+num+" ha comensat");
    }
    
}


