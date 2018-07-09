/**
 *
 * @author blair
 */
public class LogicaPartida {


	
	public static enum EstatPartida {ANTE, DEAL, UN, DOS, TRES, SHOW, END };
	private boolean part = false;
	private EstatPartida estat;

	
	public LogicaPartida() {
		part = true;
		estat = EstatPartida.ANTE;
	}
	
	    //ANTE -> DEAL
	    //DEAL -> 1 

	    //1 Player1((raise)BETT-> 2 / CHCK -> 3)
	    
	    //2 Player2(FOLD -> Res(guanya player1) / CALL -> Show)
	    //3 Player2(CHCK -> Show /(raise)BETT -> 1)

	    // SHOW -> END
	    //END

	public void setPartida(boolean part){
		this.part = part;
	}

	public boolean getPartida(){
		return part;
	}

	public void setEstat(EstatPartida estat){
		this.estat = estat;
	}

	public EstatPartida getEstat(){
		return estat;
	}
	
}