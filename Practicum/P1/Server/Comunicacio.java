import java.net.*;
import java.io.*;
import java.util.Random;

/**
 *
 * @author blaieet
 */
public class Comunicacio {
	private ComUtils comUtils;
	private Socket socket;
	private int numPartida;
	Random r = new Random();
	char[] cartes = {'J','Q','K'};
    private final String[] commands = {"STRT", "BETT", "CHCK", "FOLD", "CALL", "RPLY", "BLNC", "DEAL", "TURN", "SHOW", "WINN"};
	public Comunicacio(Socket socket, int numPartida) throws IOException {
		this.socket = socket;
		this.numPartida = numPartida;
		try {
			comUtils = new ComUtils(socket);
		} catch (IOException e){
			System.out.println("No comUtils server");
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
	
	public String rebreParam1() throws IOException{
		String comanda = null;
		comanda = comUtils.read_param1();
        return comanda;
	}
	
	

	public void balance(int balanceP1C) {
        String balanceClient = String.valueOf(balanceP1C);
		String balance = "BLNC";//BLNC
		try{
			comUtils.write_command(balance);
			comUtils.write_bs();
			comUtils.write_param(balanceClient);
		}catch (IOException e){
			System.out.println("No s'ha balance\n"+e.getMessage()); 
		}	
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

    public void bett(){
        String bet = "BETT";
        try{
            comUtils.write_command(bet);
        }catch (IOException e){
            System.out .println("No s'ha pogut bett\n"+e.getMessage());
        }
    }

    public char deal(){
    	String deal = "DEAL";
    	char cartaP1C = cartes[r.nextInt(cartes.length)];
    	char cartaP2 = cartes[r.nextInt(cartes.length)];
    	while (cartaP2 == cartaP1C) {
    		cartaP2 = cartes[r.nextInt(cartes.length)];
    	}
    	String enviar = Character.toString(cartaP1C);

    	try{
			comUtils.write_command(deal);
			comUtils.write_bs();
			comUtils.write_param1(enviar);
		}catch (IOException e){
			System.out.println("No s'ha pogut deal\n"+e.getMessage()); 
		}
		return cartaP2;


    }

    public char establirTorn(){
    	String t = "TURN";
    	int torn = r.nextInt(2);
    	System.out.println("T: "+torn);

    	try{
			comUtils.write_command(t);
			comUtils.write_bs();
			comUtils.write_param1(String.valueOf(torn));
		}catch (IOException e){
			System.out.println("No s'ha pogut torn\n"+e.getMessage()); 
		}

    	if (torn == 1) {
    		return '1';
    	}
    	return '0';
    }

    public void check(){
        String check = "CHCK";
        try{
            comUtils.write_command(check);
        }catch (IOException c) {
            System.out.println("No s'ha pogut check\n"+c.getMessage());
        }
    }

    public void fold(){
        String fold = "FOLD";
        try{
            comUtils.write_command(fold);
        }catch (IOException e) {
            System.out.println("No s'ha pogut fold\n"+e.getMessage());
        }
    }

    public void call(){
        String call = "CALL";
        try{
            comUtils.write_command(call);
        }catch (IOException e){
            System.out.println("No s'ha pogut call\n"+e.getMessage());
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
            System.out.println("No s'ha pogut enviar carta Server\n"+e.getMessage());
        }
    }
    public void winn(char win){
        String winn = "WINN";

        try{
            String winner = Character.toString(win);
            comUtils.write_command(winn);
            comUtils.write_bs();
            comUtils.write_param1(winner);

        }catch (IOException e){
            System.out.println("No s'ha pogut enviar carta Server\n"+e.getMessage());
        }
    }
    public void error(String error){
        String erro = "ERRO";
        try{
            comUtils.write_command(erro);
            comUtils.write_bs();
            comUtils.write_param(error);
        }catch (IOException e){
            System.out.println("No s'ha pogut fold\n"+e.getMessage()); 
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
