package tor61;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.ByteBuffer;

public class RouterSideProcessor extends Thread{
	public final int CELL_SIZE = 512;
	Socket receivedSocket;
	
	public RouterSideProcessor(Socket s){
		// initiations and data goes here...
		this.receivedSocket = s;
	}
	
	public void run(){
		try {
			InputStream in = receivedSocket.getInputStream();
			byte[] message = Util.readMessageCell(in);
			ByteBuffer messageBuffer = ByteBuffer.wrap(message);
			byte type = messageBuffer.get(2);
			String messageType = Util.CELL_TYPE_BYTE_MAP.get(type);
			if(messageType == "OPEN"){
				
			}
		   
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
