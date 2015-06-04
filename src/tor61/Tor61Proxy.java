package tor61;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

// This is the proxy side of the Tor61 router
public class Tor61Proxy extends Thread{
	int port;
	
	public Tor61Proxy(int port){
		// initiations and data goes here...
		this.port = port;
		
	}
	
	@Override
	public void run(){
		//TODO: Question: Where should I close serverSocket?
		try {
			ServerSocket serverSocket = new ServerSocket(port);
			while (true) {
				Socket s = serverSocket.accept();
				ProxySideProcessor processor = new ProxySideProcessor(s);
				processor.start();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Error when creating server socket in proxy side");
			System.out.println(e.getMessage());
		}
	}
}
