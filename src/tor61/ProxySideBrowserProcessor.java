package tor61;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ProxySideBrowserProcessor extends Thread{
	Socket browserSocket;
	short streamID;
	byte[] receiveBuffer;
<<<<<<< HEAD
	int bufferSize = 512 - 14;
=======
>>>>>>> origin/master
	
	public ProxySideBrowserProcessor(Socket browserSocket){
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
			
			// update stream id table
<<<<<<< HEAD
=======
			//Util.streamTable.put(streamID, );
>>>>>>> origin/master
			
			String host_ip = java.net.InetAddress.getByName(host).getHostName();
			byte[] relayBegin = Cell.relayBegin((short) 1, streamID, host_ip + ":" + port);
			
			Socket adjNodeSocket = Util.broAdjNodeSocket;
			
<<<<<<< HEAD
			Util.bufferTable.get(adjNodeSocket).add(relayBegin);	// add begin cell to the buffer
=======
			// get buffer of adjcent node from Tor side of this node
			ConcurrentLinkedQueue<byte[]> adjNodeBuffer = Util.bufferTable.get(adjNodeSocket);
			
			adjNodeBuffer.add(relayBegin);	// add begin cell to the buffer
>>>>>>> origin/master
			
			// receive cell from adjacent node
			ByteBuffer received = ByteBuffer.wrap(Util.readMessageCell(in));
			
			// get cmd type from the received cell
			String cmdType = Util.RELAY_CMD_BYTE_MAP.get(received.get(2));
			
<<<<<<< HEAD
			if (cmdType.equals("CONNECTED")){ // stream created!
				
				// create buffer reader thread which reads buffer and writes to browser
				BufferReader bufferReader = new BufferReader(browserSocket);
				Util.bufferTable.put(browserSocket, bufferReader.queue);
				bufferReader.start();
				
				Util.streamIDtable.put(Pair.of(Pair.of((short) 0, streamID), Util.broAdjNodeSocket), browserSocket);
				
				// send request to web site
				request += "\r\n";
				while(request.length() > bufferSize){
					String data = request.substring(0, bufferSize + 1);
					byte[] relayData = Cell.relayData((short) 1, streamID, data.getBytes());
					Util.bufferTable.get(adjNodeSocket).add(relayData);
					request = request.substring(bufferSize);
				}
				byte[] relayData = Cell.relayData((short) 1, streamID, request.getBytes());
				Util.bufferTable.get(adjNodeSocket).add(relayData);
				
				while(!browserSocket.isClosed()){
					byte[] buffer = new byte[bufferSize];
					int read = 0;
					boolean shouldClose = false;
					while(read < bufferSize){
						int length = in.read(buffer, read, bufferSize - read);
						read += length;
						if(length == -1)
							shouldClose = true;
							break;
					}
					byte[] dataCell = Cell.relayData((short) 1, streamID, buffer);
					Util.bufferTable.get(Util.broAdjNodeSocket).add(dataCell);
					
					if (shouldClose) {
						browserSocket.close();
						// Util.bufferTable.remove(serverSocket);
						byte[] endCell = Cell.relayEnd((short) 1, streamID);
						Util.bufferTable.get(Util.broAdjNodeSocket).add(endCell);
						Util.streamIDtable.remove(Pair.of(Pair.of((short) 1, streamID), browserSocket));
					}
				}
=======
			if (cmdType.equals("CONNECTED")){ // circuit to first node successfully created
				
				// create buffer reader thread which reads buffer and writes to browser
				BufferReader bufferReader = new BufferReader(browserSocket);
				bufferReader.start();
			/*	
				if(requestType.equals("CONNECT")){ // connect
					try{
						sendHTTPResponse("HTTP/1.1 200 OK\r\n", adjNodeSocket);
						
						// this thread listen to the browser and forward to web site server
						TCPTunnel tunnel = new TCPTunnel(browserSocket, adjNodeSocket);
						tunnel.start();
						// the main thread listen to the server and forward to browser
						sendData(br, adjNodeBuffer);
						browserSocket.shutdownInput();
						try {
							tunnel.join();
						} catch (InterruptedException e) {
							System.out.println(e.getMessage());
						}
			
						browserSocket.close();
					}catch(IOException e){
						System.out.println("Connetion failed");
						sendHTTPResponse("HTTP/1.1 502 Bad Gateway\r\n", browserSocket);
					}			
				}else{ // if the request type is not "CONNECT"  */
				
				
				// send request to web site
				byte[] relayData = Cell.relayData((short) 1, streamID, (request + "\r\n").getBytes());
				adjNodeBuffer.add(relayData);
				
				// read the reply from the web site and forward it to the browser
				sendData(br, adjNodeBuffer);
				
				// close the socket
				browserSocket.close();
				
				
				
			//	}
>>>>>>> origin/master
						
			} else if (cmdType.equals("BEGIN FAILED")) {
				System.err.println("********BEGIN FAILED*********");
			} else {
				System.err.println("FATAL ERROR");
			}
			
			// ====================================================================
					
			
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			System.out.println(e1.getMessage());
		}
	}
		
<<<<<<< HEAD
//	public void sendData(BufferedReader br, ConcurrentLinkedQueue<byte[]> targetBuffer){
//		byte[] relayData;
//		String line = "";
//		try {
//			while((line = br.readLine()) != null && !line.equals("")){
//				relayData = Cell.relayData((short) 1, streamID, line.getBytes());
//				targetBuffer.add(relayData);
//			}
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			System.out.println("Error when copying stream");
//			e.printStackTrace();
//		}
//	}
=======
	public void sendData(BufferedReader br, ConcurrentLinkedQueue<byte[]> targetBuffer){
		byte[] relayData;
		String line = "";
		try {
			while((line = br.readLine()) != null && !line.equals("")){
				relayData = Cell.relayData((short) 1, streamID, line.getBytes());
				targetBuffer.add(relayData);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Error when copying stream");
			e.printStackTrace();
		}
	}
	
	private static void sendHTTPResponse(String response, Socket s){
		PrintWriter s_out;
		try {
			s_out = new PrintWriter(s.getOutputStream(), true);
			s_out.println(response);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Error when sending response");
		}
	}
>>>>>>> origin/master
	
	private static int getPortFromURL(String line){
		String[] parts = line.split(" ");
		if(parts.length >= 2){
			String[] URLParts = parts[1].split(":");
			if(URLParts.length >= 2){
				try{
					int port = Integer.valueOf(URLParts[URLParts.length-1]).intValue();
					return port;
				}catch(NumberFormatException e){
					return -1;
				}
			}
		}
		return -1;			
	}
<<<<<<< HEAD
}
=======
	
	/*
	class TCPTunnel extends Thread{
		Socket sender = null;
		Socket receiver = null;
	
		public TCPTunnel(Socket sender, Socket recevier){
			this.sender = sender;
			this.receiver = recevier;
		}
		
		public void run(){
			try {
				ProxySideProcessor.sendData(br, adjNodeBuffer);
				sender.shutdownInput();
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.out.println("TCPTunnel Error");
			}
		}
	}
	
	*/

}
>>>>>>> origin/master
