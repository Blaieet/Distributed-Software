/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import java.net.*;
import java.io.*;

/**
 *
 * @author blaieet
 */
public class Comunicacio {
    
    InetAddress maquinaServidora;
    ComUtils comUtils;
    Socket socket = null;
    private final String[] commands = {"STRT", "BETT", "CHCK", "FOLD", "CALL", "RPLY", "BLNC", "DEAL", "TURN", "SHOW", "WINN"};
    
    public void connectar(String maquina, int port) {

        try {
            /* Obtenim la IP de la maquina servidora */
            maquinaServidora = InetAddress.getByName(maquina);

            /* Obrim una connexio amb el servidor */
            socket = new Socket(maquinaServidora, port);

            /* Obrim un flux d'entrada/sortida amb el servidor */
            comUtils = new ComUtils(socket);

        }catch(Exception ex){
            System.out.println("No s'ha pogut conectar\n"+ex.getMessage()); 
        }
    }

    public String rebreComanda() throws IOException{
        String comanda = null;
        comanda = comUtils.read_command();
        return comanda;
    }
    
    public String rebreParam() throws IOException{
        String comanda = null;
        comanda = comUtils.read_param(); 
        return comanda;
    }

    public String rebreEspai() {
        String espai = null;
        try {
            espai = comUtils.read_bs();
            
        } catch(Exception e){
            System.out.println("Error llegir comanda");
        }
        return espai;
    }
    
    

    public void desconnectar() {
        try {
            if (socket != null) {
                socket.close();
                System.out.println("Adeu TETE!\n");
            }
        } catch (IOException e) {
            System.out.println("Socket err");
        }
    }

    public void iniciarPartida(int id){
    	String ini = "STRT";
		try{
			comUtils.write_command(ini);
			comUtils.write_bs();
			comUtils.write_param(String.valueOf(id));
		}catch (IOException e){
			System.out.println("No s'ha pogut conectar\n"+e.getMessage()); 
		}	
    }

    public void bett(){
        String bet = "BETT";
        try{
            comUtils.write_command(bet);
        }catch (IOException e){
            System.out .println("No s'ha pogut conectar\n"+e.getMessage());
        }
    } 

    public void fold(){
        String fold = "FOLD";
        try{
            comUtils.write_command(fold);
        }catch (IOException e) {
            System.out.println("No s'ha pogut conectar\n"+e.getMessage());
        }
    }

    public void call(){
        String call = "CALL";
        try{
            comUtils.write_command(call);
        }catch (IOException e){
            System.out.println("No s'ha pogut conectar\n"+e.getMessage());
        }
    }

    public void check(){
        String check = "CHCK";
        try{
            comUtils.write_command(check);
        }catch (IOException c) {
            System.out.println("No s'ha pogut conectar\n"+c.getMessage());
        }
    }

    public void show(char carta){
        String show = "SHOW";
        try{
            String card = Character.toString(carta);
            comUtils.write_command(show);
            comUtils.write_bs();
            comUtils.write_param1(card);

        }catch (IOException e){
            System.out.println("No s'ha pogut conectar\n"+e.getMessage());
        }
    }

    public String rebreParam1() throws IOException{
        String comanda = null;

        try {
            comanda = comUtils.read_param1();
            
        } catch(IOException e){
            System.out.println("Error Server");
        }
        return comanda;
    }

    public void rply() {
        String rply = "RPLY";
        try {
            comUtils.write_command(rply);
        }catch (IOException e) {
            System.out.println("No s'ha pogut Rply\n"+e.getMessage());
        }
    }

    public void exit() {
        String exit = "EXIT";
        try {
            comUtils.write_command(exit);
        }catch (IOException e) {
            System.out.println("No s'ha pogut exit\n"+e.getMessage());
        }
    }
    
    public void error(String error){
        String erro = "ERRO";
        try{
            comUtils.write_command(erro);
            comUtils.write_bs();
            comUtils.write_param(error);
        }catch (IOException e){
            System.out.println("No s'ha pogut conectar\n"+e.getMessage()); 
        }   
    }
    public void showError(String error){

        switch(Integer.valueOf(error)){
            case 401:
                System.out.println("ERROR 401 - Malformed command");
                break;
            case 402:
                System.out.println("ERROR 402 - Unexpected command");
                break;
            case 403:
                System.out.println("ERROR 403 - Timeout");
                break;
            case 410:
                System.out.println("ERROR 410 - Undefined error");
                break;
            case 501:
                System.out.println("ERROR 501 - Malformed command");
                break;
            case 502:
                System.out.println("ERROR 502 - Unexpected command");
                break;
            case 503:
                System.out.println("ERROR 503 - Timeout");
                break;
            case 510:
                System.out.println("ERROR 510 - Undefined error");
                break;
        }
    }
    public boolean isCommand(String command){
        for (int i = 0; i < commands.length; i++){
            if(command.equals(commands[i])){
                return true;
            }
        }
        return false;
    }

}
