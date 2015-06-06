package tor61;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ProxySideProcessor extends Thread{
	Socket browserSocket;
	int streamID;
	
	public ProxySideProcessor(Socket browserSocket){
		// initiations and data goes here...
		this.browserSocket = browserSocket;
	}
	
	public void run(){
		try {
			
			//=== ======== Extract information from the http request  =============
			InputStream in;
			in = browserSocket.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			int lineCount = 0;
			int port = 80; // default
			String requestType = "";
			String host = "";
			String line = "";
			String request = "";
			String firstLine = "";
			while((line = br.readLine()) != null && !line.equals("")){
				lineCount++;
				String lineWithoutSpace = line.replace(" ", "").toLowerCase();
				if(lineCount == 1) {
					firstLine = line;
					request += line.replace("HTTP/1.1", "HTTP/1.0") + "\r\n";
					System.out.println(">>> " + line);
					requestType = line.split(" ")[0];
					String url = line.split(" ")[1];
					if(url.toLowerCase().startsWith("https//")){
						port = 443;
					}
				}else if(lineWithoutSpace.startsWith("host:")){ // host line
					request += line + "\r\n";
					String[] parts = lineWithoutSpace.substring(5).split(":");
					if(parts.length > 1) {
						port = Integer.valueOf(parts[1]).intValue();
					}else{
						int port_temp = Util.getPortFromURL(firstLine);
						if(port_temp >= 0){
							port = port_temp;
						}
					}
					host = parts[0];
				}else if(lineWithoutSpace.startsWith("connection:")){
					request += line.replace("keep-alive", "close") + "\r\n";
				}else if(lineWithoutSpace.startsWith("proxy-connection:")){
					request += line.replace("keep-alive", "close") + "\r\n";
				}else{
					request += line + "\r\n";
				
				}
			}
			
			if (host == "")	// if host is not found, returns
				return;
			// ====================================================================
			
			
			// ===================== Stream Creation ============================== 
					byte[] relayBegin = Cell.relayBegin((short) 1, streamID, address);
					
					
			// ====================================================================
					
			
			if(requestType.equals("CONNECT")){ // connect
				try{
					Socket sender = new Socket(host, port);
					sendHTTPResponse("HTTP/1.1 200 OK\r\n", s);
					
					// this thread listen to the browser and forward to web site server
					TCPTunnel tunnel = new TCPTunnel(sender,s);
					tunnel.start();
					// the main thread listen to the server and forward to browser
					copyStream(sender.getInputStream(), s.getOutputStream());
					browserSocket.shutdownInput();
					try {
						tunnel.join();
					} catch (InterruptedException e) {
						System.out.println(e.getMessage());
					}
					sender.close();
					browserSocket.close();
				}catch(IOException e){
					System.out.println("Connetion failed");
					sendHTTPResponse("HTTP/1.1 502 Bad Gateway\r\n", browserSocket);
				}			
			}else{ // if the request type is not "CONNECT"
				// System.out.println(host + " " + port);
				Socket sender = new Socket(host, port);
				// send request to web site
				PrintWriter s_out = new PrintWriter(sender.getOutputStream(), true);
				s_out.println(request + "\r\n");
				// read the reply from the web site and forward it to the browser
				copyStream(sender.getInputStream(), browserSocket.getOutputStream());
				
				// close the socket
				sender.close();
				browserSocket.close();
			}
			
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			System.out.println(e1.getMessage());
		}
	}
}
