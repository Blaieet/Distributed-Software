
import java.net.*;
import java.io.*;
import java.util.Random;
import java.util.Scanner;

public class Partida {

	private LogicaPartida logica;
	private Comunicacio com;
	private int balanceP1C;
    private int balanceP2;
    private char cartaP1C;
    private char cartaEnemic;
	private int pot;
    private char torn;
    private boolean case2prime;
    private Random r = new Random();
    private Scanner sc;
    private boolean canviat;
    private boolean ultim;
    private char winner;
    private int rply;
    private int mode;
    private int nPartides;

	public Partida(Comunicacio com, int mode) {
		logica = new LogicaPartida();
		this.com = com;
        sc = new Scanner(System.in);
        balanceP2 = 10;
        this.mode = mode;
        if(mode != 0){
            System.out.println("Quantes partides vols jugar automaticament?");
            nPartides = sc.nextInt();
        }
		jugant();
	}

    public char rebreChar() {
        char ch = '\u0000';
        try{
            ch = com.rebreParam1().charAt(0);
        }catch (IOException e){
            System.out.println("No s'ha Rebut el char\n"+e.getMessage()); 
        }
        return ch;
    }

	public void jugant() {

		while (logica.getPartida()) {
            

            String command = null;
            
            switch(logica.getEstat()) {

                case BLNC:
                    
                    try{
                        command = com.rebreComanda();
                    }catch(IOException e){
                        com.error("403");
                        com.showError("403");
                        System.exit(1);
                    }
                    if (command.equals("BLNC")) {
                        com.rebreEspai();
                        try{
                            balanceP1C = Integer.parseInt(com.rebreParam());
                        }catch (IOException e){
                            com.error("403");
                            com.showError("403");
                            System.exit(1);
                        }
                        System.out.println("EL BALANCE ES: "+balanceP1C);
                        logica.setEstat(LogicaPartida.EstatPartida.ANTE);
                    }else if (command.equals("ERRO")){
                        com.rebreEspai();
                        try{
                            com.showError(com.rebreParam());
                        }catch (IOException e){
                            System.out.println(e.getMessage()); 
                        }
                        System.exit(1);
                    } else {
                        if(com.isCommand(command)){
                            com.error("402");
                            com.showError("402");
                            System.exit(1);
                        }else{
                            com.error("401");
                            com.showError("401");
                            System.exit(1);
                        }
                    }

                case ANTE:
                    pot = 0;
                    com.bett();
                    case2prime = false;
                    canviat = false;
                    command = null;
                    try {
                        command = com.rebreComanda();
                    } catch (IOException e) {
                        com.error("403");
                        com.showError("403");
                        System.exit(1);
                    }
                    if (command.equals("BETT")) {
                        balanceP1C -=1;
                        balanceP2 -=1;
                        pot = 2;
                        logica.setEstat(LogicaPartida.EstatPartida.DEAL);
                    }else if (command.equals("ERRO")){
                        com.rebreEspai();
                        try{
                            com.showError(com.rebreParam());
                        }catch (IOException e){
                            System.out.println(e.getMessage()); 
                        }
                        System.exit(1);
                    } else {
                        if(com.isCommand(command)){
                            com.error("402");
                            com.showError("402");
                            System.exit(1);
                        }else{
                            com.error("401");
                            com.showError("401");
                            System.exit(1);
                        }
                    }
                    break;

                case DEAL:
                    command = null;
                    try {
                        command = com.rebreComanda();
                    } catch (IOException e) {
                        com.error("403");
                        com.showError("403");
                        System.exit(1);
                    }
                    if(command.equals("DEAL")){
                        com.rebreEspai();
                        cartaP1C = rebreChar();
                        System.out.println("Has rebut la carta: "+cartaP1C);
                    }else if (command.equals("ERRO")){
                        com.rebreEspai();
                        try{
                            com.showError(com.rebreParam());
                        }catch (IOException e){
                            com.error("403");
                            com.showError("403");
                            System.exit(1);
                        }
                        System.exit(1);
                    } else {
                        if(com.isCommand(command)){
                            com.error("402");
                            com.showError("402");
                            System.exit(1);
                        }else{
                            com.error("401");
                            com.showError("401");
                            System.exit(1);
                        }
                    }

                    try {
                        command = com.rebreComanda();
                    } catch (IOException e) {
                        com.error("403");
                        com.showError("403");
                        System.exit(1);
                    }
                    if(command.equals("TURN")){
                        com.rebreEspai();
                        try{
                            torn = com.rebreParam1().charAt(0);
                            if (torn == '0') {
                                System.out.println("Comenses tu!");
                            } else {
                                System.out.println("Comensa el contrincant!");
                            }
                        }catch (IOException e){
                            com.error("403");
                            com.showError("403");
                            System.exit(1);
                        }
                    }else if (command.equals("ERRO")){
                        com.rebreEspai();
                        try{
                            com.showError(com.rebreParam());
                        }catch (IOException e){
                            com.error("403");
                            com.showError("403");
                            System.exit(1);
                        }
                        logica.setPartida(false);
                    } else {
                        if(com.isCommand(command)){
                            com.error("402");
                            com.showError("402");
                            System.exit(1);
                        }else{
                            com.error("401");
                            com.showError("401");
                            System.exit(1);
                        }
                    }
                    logica.setEstat(LogicaPartida.EstatPartida.UN);
                    break;

                case UN:
                    if (torn == '1') {
                        command = null;
                        try {
                            command = com.rebreComanda();
                        } catch (IOException e) {
                            com.error("403");
                            com.showError("403");
                            System.exit(1);
                        }
                        if (command.equals("BETT")) {
                            balanceP2 -= 1;
                            pot  = pot + 1;
                            logica.setEstat(LogicaPartida.EstatPartida.DOS);
                            case2prime = true;
                        }else if (command.equals("CHCK")){
                            logica.setEstat(LogicaPartida.EstatPartida.TRES);
                        }else if (command.equals("ERRO")){
                            com.rebreEspai();
                            try{
                                com.showError(com.rebreParam());
                            }catch (IOException e){
                                com.error("403");
                                com.showError("403");
                                System.exit(1);
                            }
                            System.exit(1);
                        } else {
                            if(com.isCommand(command)){
                                com.error("402");
                                com.showError("402");
                                System.exit(1);
                            }else{
                                com.error("401");
                                com.showError("401");
                                System.exit(1);
                            }
                        }
                    }else{
                        if (balanceP1C == 0) {
                            System.out.println("No tenim diners, aixi que fem Check!");
                            com.check();
                            logica.setEstat(LogicaPartida.EstatPartida.TRES);
                        } else {
                            int action = -1;
                            if(mode == 0){
                                System.out.println("Que vols fer? 1 per BETT o 2 per CHECK");
                                action = sc.nextInt();
                            }else if(mode == 1){
                                action = r.nextInt(2) + 1;
                            }else{
                                if(cartaP1C == 'J'){
                                    action = 2;
                                }else {
                                    action = 1;
                                    action = 1;
                                }
                            }

                            if (action == 1) {
                                balanceP1C -= 1;
                                pot  = pot + 1;
                                com.bett();
                                logica.setEstat(LogicaPartida.EstatPartida.DOS);
                            } else if (action == 2){
                                com.check();
                                logica.setEstat(LogicaPartida.EstatPartida.TRES);
                            }else  {
                                System.out.println("Les opcions son 1 o 2! Si us plau, torna a seleccionar una opcio!");
                            }
                        }
                    }
                    break;

                case DOS: 
                    if(torn == '1' && case2prime){
                        int action = -1;
                        if(mode == 0){
                            System.out.println("Que vols fer? 1 per FOLD o 2 per CALL");
                            action = sc.nextInt();
                        }else if(mode == 1){
                            action = r.nextInt(2) + 1;
                        }else{
                            if(cartaP1C == 'J'){
                                action = 1;
                            }else if (cartaP1C == 'Q'){
                                action = 1;
                            }else{
                                action = 2;
                            }
                        }
                        if (action == 2) {
                            balanceP1C -=1;
                            pot  = pot + 1;
                            com.call();
                            logica.setEstat(LogicaPartida.EstatPartida.SHOW);
                        } else if(action == 1) { 
                            com.fold();
                            logica.setEstat(LogicaPartida.EstatPartida.END);
                        }else{
                            System.out.println("Les opcions son 1 o 2! Si us plau, torna a seleccionar una opcio!");
                        }
                        ultim = true;
                    }else{
                        command = null;
                        try {
                            command = com.rebreComanda();
                        } catch (IOException e) {
                            com.error("403");
                            com.showError("403");
                            System.exit(1);
                        }
                        if (command.equals("CALL")) {
                            balanceP2 -= 1;
                            pot  = pot + 1; 
                            logica.setEstat(LogicaPartida.EstatPartida.SHOW);
                        } else if (command.equals("FOLD")) {
                            logica.setEstat(LogicaPartida.EstatPartida.END);
                        }else if (command.equals("ERRO")){
                            com.rebreEspai();
                            try{
                                com.showError(com.rebreParam());
                            }catch (IOException e){
                                com.error("403");
                                com.showError("403");
                                System.exit(1);
                            }
                            System.exit(1);
                        } else {
                            if(com.isCommand(command)){
                                com.error("402");
                                com.showError("402");
                                System.exit(1);
                            }else{
                                com.error("401");
                                com.showError("401");
                                System.exit(1);
                            }
                        }
                        ultim = false;
                    }

                    break;

                case TRES:
                    if(torn == '0'){
                        command = null;
                        try {
                            command = com.rebreComanda();
                        } catch (IOException e) {
                            com.error("403");
                            com.showError("403");
                            System.exit(1);
                        }
                        if (command.equals("BETT")) {
                            balanceP2 -= 1;
                            pot += 1;
                            logica.setEstat(LogicaPartida.EstatPartida.DOS);
                        }else if (command.equals("CHCK")) {
                            logica.setEstat(LogicaPartida.EstatPartida.SHOW);
                        }else if (command.equals("ERRO")){
                            com.rebreEspai();
                            try{
                                com.showError(com.rebreParam());
                            }catch (IOException e){
                                com.error("403");
                                com.showError("403");
                                System.exit(1);
                            }
                            System.exit(1);
                        } else {
                            if(com.isCommand(command)){
                                com.error("402");
                                com.showError("402");
                                System.exit(1);
                            }else{
                                com.error("401");
                                com.showError("401");
                                System.exit(1);
                            }
                        }
                        torn = '1';
                        canviat = true;
                        case2prime = true;
                        ultim = false;
                        
                    }else{
                        if (balanceP1C == 0) {
                            System.out.println("No tenim diners, aixi que fem Check!");
                            com.check();
                            logica.setEstat(LogicaPartida.EstatPartida.SHOW);
                        } else {
                            if (balanceP2 == 0) {
                                System.out.println("El teu contrincant no te prous diners. Fem Show!");
                                com.check();
                                logica.setEstat(LogicaPartida.EstatPartida.SHOW);
                            } else {
                                int action = -1;
                                if(mode == 0){
                                    System.out.println("Que vols fer? 1 for BET o 2 per CHECK");
                                    action = sc.nextInt();
                                }else if(mode == 1){
                                    action = r.nextInt(2) + 1;
                                }else{
                                    if(cartaP1C == 'J'){
                                        action = 2;
                                    }else if (cartaP1C == 'Q'){
                                        action = 2;
                                    }else{
                                        action = 1;
                                    }
                                }
                                if (action == 1) { //Fa BET
                                    balanceP1C -=1;
                                    pot  = pot + 1;
                                    com.bett();
                                    logica.setEstat(LogicaPartida.EstatPartida.DOS);
                                    
                                } else if(action == 2) {//CHECK
                                    com.check();
                                    logica.setEstat(LogicaPartida.EstatPartida.SHOW);
                                }else{
                                    System.out.println("Les opcions son 1 o 2! Si us plau, torna a seleccionar una opcio!");
                                }
                            }
                        }
                        ultim = true;
                        case2prime = false;
                        
                    }

                    break;
                    
                case SHOW:
                    if (!ultim) {
                        com.show(cartaP1C);
                        command = null;
                        try {
                            command = com.rebreComanda();
                        } catch (IOException e) {
                            com.error("403");
                            com.showError("403");
                            System.exit(1);
                        }
                        if (command.equals("SHOW")) {
                            com.rebreEspai();
                            cartaEnemic = rebreChar();
                        }else if (command.equals("ERRO")){
                            com.rebreEspai();
                            try{
                                com.showError(com.rebreParam());
                            }catch (IOException e){
                                com.error("403");
                                com.showError("403");
                                System.exit(1);
                            }
                            System.exit(1);
                        } else {
                            if(com.isCommand(command)){
                                com.error("402");
                                com.showError("402");
                                System.exit(1);
                            }else{
                                com.error("401");
                                com.showError("401");
                                System.exit(1);
                            }
                        }
                    } else {
                        command = null;
                        try {
                            command = com.rebreComanda();
                        } catch (IOException e) {
                            System.out.println("Error xDDDDD");
                        }
                        if (command.equals("SHOW")) {
                            com.rebreEspai();
                            cartaEnemic = rebreChar();
                        }else if (command.equals("ERRO")){
                            com.rebreEspai();
                            try{
                                com.showError(com.rebreParam());
                            }catch (IOException e){
                                com.error("403");
                                com.showError("403");
                                System.exit(1);
                            }
                            System.exit(1);
                        } else {
                            if(com.isCommand(command)){
                                com.error("402");
                                com.showError("402");
                                System.exit(1);
                            }else{
                                com.error("401");
                                com.showError("401");
                                System.exit(1);
                            }
                        }
                        com.show(cartaP1C);
                    }
                    logica.setEstat(LogicaPartida.EstatPartida.END);
                    break;
                case END:
                    System.out.println("Aquesta es la ma del contrincant: "+cartaEnemic);
                    command = null;
                    try {
                        command = com.rebreComanda();
                    } catch (IOException e) {
                        com.error("403");
                        com.showError("403");
                        System.exit(1);
                    }
                    if (command.equals("WINN")) {
                        com.rebreEspai();
                        winner = rebreChar();
                    }else if (command.equals("ERRO")){
                        com.rebreEspai();
                        try{
                            com.showError(com.rebreParam());
                        }catch (IOException e){
                            com.error("403");
                            com.showError("403");
                            System.exit(1); 
                        }
                        System.exit(1);
                    } else {
                        if(com.isCommand(command)){
                            com.error("402");
                            com.showError("402");
                            System.exit(1);
                        }else{
                            com.error("401");
                            com.showError("401");
                            System.exit(1);
                        }
                    }
                    if (winner == '1') {
                        balanceP2 += pot;
                        System.out.println("Partida perduda!\n");
                    } else if (winner == '0') {
                        balanceP1C += pot;
                        System.out.println("Enhorabona, has guanyat!");
                    }
                    System.out.println("\nBALANCE: "+balanceP1C+" BALANCE S: "+balanceP2+" POT TOTAL: "+pot+"\n");
                    if (balanceP1C <= 0) {
                        System.out.println("No tenim diners! A reveure");
                        com.exit();
                        logica.setPartida(false);
                    } else if (balanceP2 <= 0) {
                        System.out.println("Enhorabona, el teu contrincant no te diners. Fins aviat!");
                        com.exit();
                        logica.setPartida(false);
                    } else {
                        if(nPartides <= 0){
                            System.out.println("Vol tornar a jugar? 0 (No) 1 (Si)");
                            rply = sc.nextInt();
                            if (rply == 1) {
                                com.rply();
                                logica.setEstat(LogicaPartida.EstatPartida.ANTE);
                            } else if (rply == 0){
                                com.exit();
                                logica.setPartida(false);
                            } else {
                                System.out.println("Les opcions son 1 o 2! Si us plau, torna a seleccionar una opcio!");
                            }
                        }else{
                            nPartides -= 1;
                            com.rply();
                            logica.setEstat(LogicaPartida.EstatPartida.ANTE);
                        }
                    }
                    break;
                default:
                    System.out.println("def");
                    break;
            }

        }

	}


}

