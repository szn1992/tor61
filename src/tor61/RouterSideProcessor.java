package tor61;

import java.net.Socket;

public class RouterSideProcessor extends Thread{
	Socket socket;
	
	public RouterSideProcessor(Socket s){
		// initiations and data goes here...
		this.socket = s;
	}
	
	public void run(){
		
	}
}
