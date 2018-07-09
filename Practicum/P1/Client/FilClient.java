import java.net.*;
import java.io.*;

public class FilClient implements Runnable {
	private Comunicacio comunicacio;
	private int mode;
	public FilClient(Comunicacio com, int mode) {
		this.comunicacio = com;
		this.mode = mode;
	}
	public void run(){
		Partida p = new Partida(comunicacio, mode);
		System.out.println("FINS AVIAT!");
	}
}
