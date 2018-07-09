
import java.net.*;
import java.io.*;
import java.util.Random;
import java.util.logging.Logger;
import java.util.concurrent.ConcurrentHashMap;

public class Partida {

	private LogicaPartida logica;
	private Comunicacio com;
	private int balanceP1C;
	private int balanceP2;
    private char cartaP2;
    private char cartaEnemic;
	private int pot;
	private int fold;
	private char win;
    private char torn;
    private boolean case2prime;
    private boolean ultim;
    private Random r = new Random();
    private Logger logger;
    private ConcurrentHashMap<String,Integer> myMap;
    private String id;
    private int mode;


	public Partida(Comunicacio com, Logger logger, int balanceP2, int balanceP1C, ConcurrentHashMap<String,Integer> myMap, String id, int mode) {
		logica = new LogicaPartida();
		this.com = com;
        this.logger = logger;
        this.balanceP2 = balanceP2;
        this.balanceP1C = balanceP1C;
        this.myMap = myMap;
        this.id = id;
        this.mode = mode;
		jugant();
	}

    public char knowWinner() {
        if (cartaP2 == 'K') {
            balanceP2 += pot;
            return '1';
        } else if (cartaEnemic == 'K'){
            balanceP1C += pot;
            return '0';
        } else {
            if (cartaP2 > cartaEnemic){
                balanceP2 += pot;
                return '1';
            } else if (cartaP2 < cartaEnemic){
                balanceP1C += pot;
                return '0';
            }
        }

        return '\u0000';
    }

	public void jugant() {

		while (logica.getPartida()) {
            System.out.println("\nBALANCE: "+balanceP1C+" BALANCE S: "+balanceP2+" POT TOTAL: "+pot+"\n");

            String command = null;
            
            switch(logica.getEstat()) {
                case ANTE:
                    pot = 0;
                    case2prime = true;
                    fold = -1;
                    try {
                        command = com.rebreComanda();
                    } catch (IOException e) {
                        com.error("503");
                        com.showError("503");
                        logica.setPartida(false);
                    }   
                    if (command.equals("BETT")) {
                        logger.info("C: BETT");
                    	balanceP1C -=1;
                    	balanceP2 -=1;
                    	pot = 2;
                    	com.bett();
                        logger.info("S: BETT");
                    	logica.setEstat(LogicaPartida.EstatPartida.DEAL);
                    } else if (command.equals("ERRO")){
                        com.rebreEspai();
                        try{
                            com.showError(com.rebreParam());
                        }catch (IOException e){
                            com.error("503");
                            com.showError("503");
                            logger.severe("ERRO 503");
                            logica.setPartida(false);
                        }
                        logica.setPartida(false);
                    } else {
                        if(com.isCommand(command)){
                            com.error("502");
                            com.showError("502");
                            logger.severe("ERRO 502");
                            logica.setPartida(false);
                        }else{
                            com.error("501");
                            com.showError("501");
                            logger.severe("ERRO 501");
                            logica.setPartida(false);
                        }
                    }
                    break;
                case DEAL:
                    cartaP2 = com.deal();
                    logger.info("S: DEAL"); //??!
                    System.out.println("CHAR SERVER: "+cartaP2);

                    torn = com.establirTorn();
                    logger.info("S: TURN "+torn);

                    logica.setEstat(LogicaPartida.EstatPartida.UN); //BET o check
                    break;

                case UN:
                    if (torn == '1') {
                        if (balanceP2 == 0) {
                            com.check();
                            logger.info("S: CHECK");
                            logica.setEstat(LogicaPartida.EstatPartida.TRES);
                        } else {
                            int action = -1;
                            if(mode == 1){
                                action = r.nextInt(2);
                            }else{
                                if(cartaP2 == 'J'){
                                    action = 2;
                                }else {
                                    action = 1;
                                    action = 1;
                                }
                            }
                            if (action == 0) { 
                                balanceP2 -= 1;
                                pot  = pot + 1;
                                com.bett();
                                logger.info("S: BETT");
                                System.out.println("BET");
                                logica.setEstat(LogicaPartida.EstatPartida.DOS);
                            } else {
                                com.check();
                                logger.info("S: CHECK");
                                logica.setEstat(LogicaPartida.EstatPartida.TRES);
                            }
                        }
                    } else {
                        command = null;
                        try {
                            command = com.rebreComanda();
                        } catch (IOException e) {
                            com.error("503");
                            com.showError("503");
                            logger.severe("ERRO 503");
                            logica.setPartida(false);
                        }
                        if (command.equals("BETT")) { //Client ha fet raise
                            logger.info("C: BETT");
                            balanceP1C -= 1;
                            pot  = pot + 1;
                            logica.setEstat(LogicaPartida.EstatPartida.DOS);
                        } else if (command.equals("CHCK")){ //Check
                            logger.info("C: CHCK");
                            logica.setEstat(LogicaPartida.EstatPartida.TRES);
                        } else if (command.equals("ERRO")){
                            com.rebreEspai();
                            try{
                                com.showError(com.rebreParam());
                            }catch (IOException e){
                                com.error("503");
                                com.showError("503");
                                logger.severe("ERRO 503");
                                logica.setPartida(false);
                            }
                            logica.setPartida(false);
                        } else {
                            if(com.isCommand(command)){
                                com.error("502");
                                com.showError("502");
                                logger.severe("ERRO 502");
                                logica.setPartida(false);
                            }else{
                                com.error("501");
                                com.showError("501");
                                logger.severe("ERRO 501");
                                logica.setPartida(false);
                            }
                        }                   
                    }
                    
                    break;

                case DOS:
                    
                    if (torn == '1' || !case2prime) {
                        command = null;
                        try {
                            command = com.rebreComanda();
                        } catch (IOException e) {
                            com.error("503");
                            com.showError("503");
                            logger.severe("ERRO 503");
                            logica.setPartida(false);
                        }
                        if (command.equals("CALL")) {
                            logger.info("C: CALL");
                            balanceP1C -= 1;
                            pot = pot +1;
                            logica.setEstat(LogicaPartida.EstatPartida.SHOW);
                            ultim = false;
                        } else if (command.equals("FOLD")) {
                            logger.info("C: FOLD");
                            logica.setEstat(LogicaPartida.EstatPartida.END);
                            fold = 0;
                        } else if (command.equals("ERRO")){
                            com.rebreEspai();
                            try{
                                com.showError(com.rebreParam());
                            }catch (IOException e){
                                com.error("503");
                                com.showError("503");
                                logger.severe("ERRO 503");
                                logica.setPartida(false);
                            }
                            logica.setPartida(false);
                        } else {
                            if(com.isCommand(command)){
                                com.error("502");
                                com.showError("502");
                                logger.severe("ERRO 502");
                                logica.setPartida(false);
                            }else{
                                com.error("501");
                                com.showError("501");
                                logger.severe("ERRO 501");
                                logica.setPartida(false);
                            }
                        }         
                    } else {
                        int action = -1;
                        if(mode == 1){
                            action = r.nextInt(2);
                        }else{
                            if(cartaP2 == 'J'){
                                action = 0;
                            }else if (cartaP2 == 'Q'){
                                action = 0;
                            }else{
                                action = 1;
                            }
                        }
                        if (action == 0) {
                            balanceP2 -=1;
                            pot  = pot + 1;
                            com.call();
                            logger.info("S: CALL");
                            logica.setEstat(LogicaPartida.EstatPartida.SHOW);
                            ultim = true;
                        } else { 
                            System.out.println("FOLDEJO");
                            com.fold();
                            logger.info("S: FOLD");
                            logica.setEstat(LogicaPartida.EstatPartida.END);
                            fold = 1;
                        }
                    }

                    break;

                case TRES:
                    if (torn == '0') {
                        if (balanceP1C == 0) {
                            com.check();
                            logger.info("S: CHCK");
                            logica.setEstat(LogicaPartida.EstatPartida.SHOW);
                        } else {
                            int action = -1;
                            if(mode == 1){
                                action = r.nextInt(2);
                            }else{
                                if(cartaP2 == 'J'){
                                    action = 1;
                                }else if (cartaP2 == 'Q'){
                                    action = 1;
                                }else{
                                    action = 0;
                                }
                            }
                            if (action == 0) { 
                                balanceP2 -=1;
                                pot  = pot + 1;
                                com.bett();
                                logger.info("S: BETT");
                                logica.setEstat(LogicaPartida.EstatPartida.DOS);
                            } else {
                                com.check();
                                logger.info("S: CHCK");
                                logica.setEstat(LogicaPartida.EstatPartida.SHOW);
                            }
                        }
                        ultim = true;
                        case2prime = false;
                    } else {
                        command = null;
                        try {
                            command = com.rebreComanda();
                        } catch (IOException e) {
                            com.error("503");
                            com.showError("503");
                            logger.severe("ERRO 503");
                            logica.setPartida(false);
                        }

                        if (command.equals("BETT")) {
                            logger.info("C: BETT");
                            balanceP1C -= 1;
                            pot += 1;
                            logica.setEstat(LogicaPartida.EstatPartida.DOS);
                        } else if (command.equals("CHCK")) {
                            logger.info("C: CHCK");
                            logica.setEstat(LogicaPartida.EstatPartida.SHOW);
                            ultim = false;
                        } else if (command.equals("ERRO")){
                            com.rebreEspai();
                            try{
                                com.showError(com.rebreParam());
                            }catch (IOException e){
                                com.error("503");
                                com.showError("503");
                                logger.severe("ERRO 503");
                                logica.setPartida(false);
                            }
                            logica.setPartida(false);
                        } else {
                            if(com.isCommand(command)){
                                com.error("502");
                                com.showError("502");
                                logger.severe("ERRO 502");
                                logica.setPartida(false);
                            }else{
                                com.error("501");
                                com.showError("501");
                                logger.severe("ERRO 501");
                                logica.setPartida(false);
                            }
                        }  
                        torn = '0';
                        case2prime = true;
                    }
                    
                    break;
                    
                case SHOW:
                    if (!ultim) {
                        logger.info("S: SHOW "+cartaP2);
                        com.show(cartaP2);
                        command = null;
                        try {
                            command = com.rebreComanda();
                        } catch (IOException e) {
                            com.error("503");
                            com.showError("503");
                            logger.severe("ERRO 503");
                            logica.setPartida(false);
                        }
                        if (command.equals("SHOW")) {
                            com.rebreEspai();
                            try{
                                cartaEnemic = com.rebreParam1().charAt(0);
                                System.out.println("CARTA ENEMIC : "+cartaEnemic);
                            }catch (IOException e){
                                com.error("503");
                                com.showError("503");
                                logger.severe("ERRO 503");
                                logica.setPartida(false);
                            }
                            logger.info("C: SHOW "+cartaEnemic);
                        } else if (command.equals("ERRO")){
                            com.rebreEspai();
                            try{
                                com.showError(com.rebreParam());
                            }catch (IOException e){
                                com.error("503");
                                com.showError("503");
                                logger.severe("ERRO 503");
                                logica.setPartida(false);
                            }
                            logica.setPartida(false);
                        } else {
                            if(com.isCommand(command)){
                                com.error("502");
                                com.showError("502");
                                logger.severe("ERRO 502");
                                logica.setPartida(false);
                            }else{
                                com.error("501");
                                com.showError("501");
                                logger.severe("ERRO 501");
                                logica.setPartida(false);
                            }
                        }  
                    } else {
                        command = null;
                        try {
                            command = com.rebreComanda();
                        } catch (IOException e) {
                            com.error("503");
                            com.showError("503");
                            logger.severe("ERRO 503");
                            logica.setPartida(false);
                        }
                        if (command.equals("SHOW")) {
                            com.rebreEspai();
                            try{
                                cartaEnemic = com.rebreParam1().charAt(0);
                                System.out.println("CARTA ENEMIC : "+cartaEnemic);
                            }catch (IOException e){
                                System.out.println("No s'ha Rebut carta a Server\n"+e.getMessage()); 
                            }
                            logger.info("C: SHOW "+cartaEnemic);
                            logger.info("S: SHOW "+cartaP2);
                            com.show(cartaP2);
                        } else if (command.equals("ERRO")){
                            com.rebreEspai();
                            try{
                                com.showError(com.rebreParam());
                            }catch (IOException e){
                                com.error("503");
                                com.showError("503");
                                logger.severe("ERRO 503");
                                logica.setPartida(false);
                            }
                            logica.setPartida(false);
                        } else {
                            if(com.isCommand(command)){
                                com.error("502");
                                com.showError("502");
                                logger.severe("ERRO 502");
                                logica.setPartida(false);
                            }else{
                                com.error("501");
                                com.showError("501");
                                logger.severe("ERRO 501");
                                logica.setPartida(false);
                            }
                        }  
                    }
                    logica.setEstat(LogicaPartida.EstatPartida.END);
                    break;
                case END:
                    win = '\u0000';
                    if(fold == -1){
                        win = knowWinner();
                    }else{
                        if(fold == 1){
                            win = '0';
                            balanceP1C += pot;
                        }else if(fold == 0){
                            win = '1';
                            balanceP2 += pot;
                        }else{
                            System.out.println("ERROR GUANYADOR");
                        }
                    }
                    myMap.put(id,balanceP1C);
                    com.winn(win);
                    logger.info("S: WINN "+win);
                    
                    command = null;
                    try {
                        command = com.rebreComanda();
                    } catch (IOException e) {
                        com.error("503");
                        com.showError("503");
                        logger.severe("ERRO 503");
                        logica.setPartida(false);
                    }
                    if (command.equals("RPLY")) {
                        logger.info("C: RPLY");
                        logica.setEstat(LogicaPartida.EstatPartida.ANTE);
                    }else if (command.equals("EXIT")){
                        logica.setPartida(false);
                        logger.info("C: EXIT");
                    } else if (command.equals("ERRO")){
                        com.rebreEspai();
                        try{
                            com.showError(com.rebreParam());
                        }catch (IOException e){
                            com.error("503");
                            com.showError("503");
                            logger.severe("ERRO 503");
                            logica.setPartida(false);
                        }
                        logica.setPartida(false);
                    } else {
                        if(com.isCommand(command)){
                            com.error("502");
                            com.showError("502");
                            logger.severe("ERRO 502");
                            logica.setPartida(false);
                        }else{
                            com.error("501");
                            com.showError("501");
                            logger.severe("ERRO 501");
                            logica.setPartida(false);
                        }
                    }  
                    break;
                default:
                    System.out.println("default");
                    break;
            }

        }

	}
}

