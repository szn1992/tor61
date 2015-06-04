package tor61;

import java.net.Socket;

public class ProxySideProcessor extends Thread{
	Socket socket;
	
	public ProxySideProcessor(Socket s){
		// initiations and data goes here...
		this.socket = s;
	}
	
	public void run(){
		
	}
}
