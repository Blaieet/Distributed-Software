/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 *
 * @author blair
 */
public class ComUtils {
    /* Mida d'una cadena de caracters */
    private final int STRSIZE = 4;
    /* Objectes per escriure i llegir dades */
    private DataInputStream dis;
    private DataOutputStream dos;

    public ComUtils(Socket socket) throws IOException {
        dis = new DataInputStream(socket.getInputStream());
        dos = new DataOutputStream(socket.getOutputStream());
    }

    /* Llegir un enter de 32 bits */
    public int read_int32() throws IOException
    {
        byte bytes[] = new byte[4];
        bytes  = read_bytes(4);

        return bytesToInt32(bytes,"be");
    }

    /* Escriure un enter de 32 bits */
    public void write_int32(int number) throws IOException
    {
        byte bytes[]=new byte[4];

        int32ToBytes(number,bytes,"be");
        dos.write(bytes, 0, 4);
    }

    /* Llegir un string de mida STRSIZE */
    public String read_string() throws IOException
    {
        String str;
        byte bStr[] = new byte[STRSIZE];
        char cStr[] = new char[STRSIZE];

        bStr = read_bytes(STRSIZE);

        for(int i = 0; i < STRSIZE;i++)
            cStr[i]= (char) bStr[i];

        str = String.valueOf(cStr);

        return str.trim(); 
    }
    
    

    /* Llegir un string de mida STRSIZE */
    public String read_command() throws IOException
    {
        String str;
        byte bStr[] = new byte[STRSIZE];
        char cStr[] = new char[STRSIZE];

        bStr = read_bytes(STRSIZE);

        for(int i = 0; i < STRSIZE;i++)
            cStr[i]= (char) bStr[i];

        str = String.valueOf(cStr);

        return str.trim(); 
    }
    
    public String read_param() throws IOException
    {
        String str;
        byte bStr[] = new byte[3];
        char cStr[] = new char[3];

        bStr = read_bytes(3);

        for(int i = 0; i < 3;i++)
            cStr[i]= (char) bStr[i];

        str = String.valueOf(cStr);

        return str.trim(); 
    }

    /* Escriure un string */
    public void write_command(String str) throws IOException
    { 
        int numBytes, lenStr; 
        byte bStr[] = new byte[STRSIZE];

        lenStr = str.length();

        if (lenStr > STRSIZE)
            numBytes = STRSIZE;
        else
            numBytes = lenStr;

        for(int i = 0; i < numBytes; i++)
            bStr[i] = (byte) str.charAt(i);

        for(int i = numBytes; i < STRSIZE; i++)
            bStr[i] = (byte) ' ';

        dos.write(bStr, 0,STRSIZE);
    }
    
    public void write_param(String str) throws IOException
    { 
        int numBytes, lenStr; 
        byte bStr[] = new byte[3];

        lenStr = str.length();

        if (lenStr > 3)
            numBytes = 3;
        else
            numBytes = lenStr;

        for(int i = 0; i < numBytes; i++)
            bStr[i] = (byte) str.charAt(i);

        for(int i = numBytes; i < 3; i++)
            bStr[i] = (byte) ' ';

        dos.write(bStr, 0,3);
    }

    public void write_bs() throws IOException
    { 
        byte bStr[] = new byte[1];
        bStr[0] = (byte) ' ';
        dos.write(bStr, 0, 1);
    }
    public String read_bs() throws IOException
    {
        String str;
        byte bStr[] = new byte[1];
        char cStr[] = new char[1];

        bStr = read_bytes(1);

        for(int i = 0; i < 1;i++)
            cStr[i]= (char) bStr[i];

        str = String.valueOf(cStr);

        return str; 
    }

    /* Passar d'enters a bytes */
    private int int32ToBytes(int number,byte bytes[], String endianess)
    {
        if("be".equals(endianess.toLowerCase()))
        {
            bytes[0] = (byte)((number >> 24) & 0xFF);
            bytes[1] = (byte)((number >> 16) & 0xFF);
            bytes[2] = (byte)((number >> 8) & 0xFF);
            bytes[3] = (byte)(number & 0xFF);
        }
        else
        {
            bytes[0] = (byte)(number & 0xFF);
            bytes[1] = (byte)((number >> 8) & 0xFF);
            bytes[2] = (byte)((number >> 16) & 0xFF);
            bytes[3] = (byte)((number >> 24) & 0xFF);
        }
        return 4;
    }

    /* Passar de bytes a enters */
    private int bytesToInt32(byte bytes[], String endianess)
    {
        int number;

        if("be".equals(endianess.toLowerCase()))
        {
            number=((bytes[0] & 0xFF) << 24) | ((bytes[1] & 0xFF) << 16) |
              ((bytes[2] & 0xFF) << 8) | (bytes[3] & 0xFF);
        }
        else
        {
            number=(bytes[0] & 0xFF) | ((bytes[1] & 0xFF) << 8) |
              ((bytes[2] & 0xFF) << 16) | ((bytes[3] & 0xFF) << 24);
        }
        return number;
    }
    
    //llegir bytes.
    private byte[] read_bytes(int numBytes) throws IOException{
        int len=0 ;
        byte bStr[] = new byte[numBytes];
        int bytesread=0; 
        do {
            bytesread= dis.read(bStr, len, numBytes-len);
            if (bytesread == -1)
                throw new IOException("Broken Pipe");
                len += bytesread;
            } while (len < numBytes);
        return bStr;
    }

    /* Llegir un string  mida variable size = nombre de bytes especifica la longitud*/
    public  String read_string_variable(int size) throws IOException
    {
        byte bHeader[]=new byte[size];
        char cHeader[]=new char[size];
        int numBytes=0;

        // Llegim els bytes que indiquen la mida de l'string
        bHeader = read_bytes(size);
        // La mida de l'string ve en format text, per tant creem un string i el parsejem
        for(int i=0;i<size;i++){
                cHeader[i]=(char)bHeader[i]; }
        numBytes=Integer.parseInt(new String(cHeader));

        // Llegim l'string
        byte bStr[]=new byte[numBytes];
        char cStr[]=new char[numBytes];
        bStr = read_bytes(numBytes);
        for(int i=0;i<numBytes;i++)
                cStr[i]=(char)bStr[i];
        return String.valueOf(cStr);
    }

    /* Escriure un string mida variable, size = nombre de bytes especifica la longitud  */
    /* String str = string a escriure.*/
    public  void write_string_variable(int size,String str) throws IOException
    {

        // Creem una seqüència amb la mida
        byte bHeader[]=new byte[size];
        String strHeader;
        int numBytes=0;

        // Creem la capçalera amb el nombre de bytes que codifiquen la mida
        numBytes=str.length();

        strHeader=String.valueOf(numBytes);
        int len;
        if ((len=strHeader.length()) < size)
        for (int i =len; i< size;i++){
                strHeader= "0"+strHeader;}
        for(int i=0;i<size;i++)
                bHeader[i]=(byte)strHeader.charAt(i);
        // Enviem la capçalera
        dos.write(bHeader, 0, size);
        // Enviem l'string writeBytes de DataOutputStrem no envia el byte més alt dels chars.
        dos.writeBytes(str);
    }

    public String read_param1() throws IOException
    {
        String str;
        byte bStr[] = new byte[1];
        char cStr[] = new char[1];

        bStr = read_bytes(1);

        for(int i = 0; i < 1;i++)
            cStr[i]= (char) bStr[i];

        str = String.valueOf(cStr);

        return str.trim(); 
    }

    public void write_param1(String str) throws IOException
    { 
        int numBytes, lenStr; 
        byte bStr[] = new byte[1];

        lenStr = str.length();

        if (lenStr > 1)
            numBytes = 1;
        else
            numBytes = lenStr;

        for(int i = 0; i < numBytes; i++)
            bStr[i] = (byte) str.charAt(i);

        for(int i = numBytes; i < 1; i++)
            bStr[i] = (byte) ' ';

        dos.write(bStr, 0,1);
    }

}
