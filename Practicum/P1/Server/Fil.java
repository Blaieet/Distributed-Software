import java.net.*;
import java.io.*;
import java.util.logging.*;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;



public class Fil implements Runnable {

	private Comunicacio comunicacio;
	private int numPartida;
	private Logger logger;
	private int balanceP2;
	private int balanceP1C;
	private ConcurrentHashMap<String,Integer> myMap;
	private String id;
	private int mode;

	
	public Fil(int numPartida, Comunicacio com, Logger logger, int balanceP2, int balanceP1C, ConcurrentHashMap<String,Integer> myMap, String id, int mode) {
		this.numPartida = numPartida;
		this.comunicacio = com;
		this.logger = logger;
		this.balanceP2 = balanceP2;
		this.balanceP1C = balanceP1C;
		this.myMap = myMap;
		this.id = id;
		this.mode = mode;
	}

	public void run() {
		Partida p = new Partida(comunicacio, logger, balanceP2, balanceP1C, myMap, id, mode);
		System.out.println("\nAdeu Thread " +numPartida+"\n");
	}

}
