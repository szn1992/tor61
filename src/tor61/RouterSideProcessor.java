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
			Buffer receivedSocketBuffer = new Buffer(receivedSocket);
			Util.bufferTable.put(receivedSocket, receivedSocketBuffer);
			receivedSocketBuffer.start();
			InputStream in = receivedSocket.getInputStream();
			
			// ================= 此处应有 while 包裹 0v0 =============
			
			byte[] message = Util.readMessageCell(in);
			ByteBuffer messageBuffer = ByteBuffer.wrap(message);
			byte type = messageBuffer.get(2);
			String messageType = Util.CELL_TYPE_BYTE_MAP.get(type);
			if(messageType == "OPEN"){
				byte[] openedCell = Cell.opened(message);
				
			}else if(messageType == "CREATE"){
				
			}else if(messageType == "DESTORY"){
				
			}else if(messageType == "RELAY"){
				
			}
			
			// ==================================================
		   
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
