package tor61;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.concurrent.ConcurrentLinkedQueue;

<<<<<<< HEAD
public class ProxySideServerProcessor extends Thread{
	Socket serverSocket;
	Socket nodeSocket;
	short streamID;
	short circuitID;
	int bufferSize = 512 - 14;
	
	public ProxySideServerProcessor(Socket serverSocket, Socket nodeSocket){
		// initiations and data goes here...
		this.serverSocket = serverSocket;
		this.nodeSocket = nodeSocket;
=======
public class ProxySideServerProcessor {
	Socket serverSocket;
	short streamID;
	short circuitID;
	byte[] receiveBuffer;
	
	public ProxySideServerProcessor(Socket serverSocket){
		// initiations and data goes here...
		this.serverSocket = serverSocket;
>>>>>>> origin/master
	}
	
	public void run(){
		try {
<<<<<<< HEAD
			InputStream input = serverSocket.getInputStream();
			// create buffer reader for this server socket
			BufferReader br = new BufferReader(serverSocket);
			br.start();
			
			//reply with CONNECTED cell to confirm connection
			byte[] relayConnected = Cell.relayConnected(circuitID, streamID);
			Util.bufferTable.get(nodeSocket).add(relayConnected);
			
			while(!serverSocket.isClosed()){
				byte[] buffer = new byte[bufferSize];
				int read = 0;
				boolean shouldClose = false;
				try {
					while(read < bufferSize){
						int length = input.read(buffer, read, bufferSize - read);
						read += length;
						if(length == -1)
							shouldClose = true;
							break;
					}
					byte[] dataCell = Cell.relayData(circuitID, streamID, buffer);
					Util.bufferTable.get(nodeSocket).add(dataCell);
					
					if (shouldClose) {
						serverSocket.close();
						// Util.bufferTable.remove(serverSocket);
						byte[] endCell = Cell.relayEnd(circuitID, streamID);
						Util.bufferTable.get(nodeSocket).add(endCell);
						Util.streamIDtable.remove(Pair.of(Pair.of(circuitID, streamID), serverSocket));
						
					}
					
				} catch (IOException e) {
					System.out.println("Error when reading message from the input stream in server!.");
					e.printStackTrace();
				}
			}
			
			
//			
//			//=== ======== Extract information from the http request  =============
//			InputStream in;
//			in = serverSocket.getInputStream();
//			BufferedReader br = new BufferedReader(new InputStreamReader(in));
//
//			
//			// ===================== Stream Creation ============================== 
//			
//			// update stream id table
//			Util.streamIDtable.put(Pair.of(circuitID, streamID), serverSocket);
//	
//			// reply with CONNECTED cell to confirm connection
//			byte[] relayConnected = Cell.relayConnected(circuitID, streamID);
//			
//			// get adjacent socket
//			Socket adjNodeSocket = Util.serAdjNodeSocket;
//			
//			// get buffer of adjcent node from Tor side of this node
//			ConcurrentLinkedQueue<byte[]> adjNodeBuffer = Util.bufferTable.get(adjNodeSocket);
//			
//			adjNodeBuffer.add(relayConnected);	// add begin cell to the buffer
//			
//			// receive cell from adjacent node
//			ByteBuffer received = ByteBuffer.wrap(Util.readMessageCell(in));
//			
//			// get cmd type from the received cell
//			String cmdType = Util.RELAY_CMD_BYTE_MAP.get(received.get(2));
//			
//			if (cmdType.equals("DATA")){ // circuit to first node successfully created
//				
//				// create buffer reader thread which reads buffer and writes to browser
//				BufferReader bufferReader = new BufferReader(serverSocket);
//				bufferReader.start();
//			
//				// send data from server to adjacent node
//				sendData(br, adjNodeBuffer);
//				
//				// close the socket
//				serverSocket.close();		
//				
//			//	}
//						
//			} else {
//				System.err.println("FATAL ERROR");
//			}
//			
//			// ====================================================================
//					
//			
=======
			
			//=== ======== Extract information from the http request  =============
			InputStream in;
			in = serverSocket.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(in));

			
			// ===================== Stream Creation ============================== 
			
			// update stream id table
			Util.streamIDtable.put(streamID, serverSocket);
	
			// reply with CONNECTED cell to confirm connection
			byte[] relayConnected = Cell.relayConnected(circuitID, streamID);
			
			// get adjacent socket
			Socket adjNodeSocket = Util.serAdjNodeSocket;
			
			// get buffer of adjcent node from Tor side of this node
			ConcurrentLinkedQueue<byte[]> adjNodeBuffer = Util.bufferTable.get(adjNodeSocket);
			
			adjNodeBuffer.add(relayConnected);	// add begin cell to the buffer
			
			// receive cell from adjacent node
			ByteBuffer received = ByteBuffer.wrap(Util.readMessageCell(in));
			
			// get cmd type from the received cell
			String cmdType = Util.RELAY_CMD_BYTE_MAP.get(received.get(2));
			
			if (cmdType.equals("DATA")){ // circuit to first node successfully created
				
				// create buffer reader thread which reads buffer and writes to browser
				BufferReader bufferReader = new BufferReader(serverSocket);
				bufferReader.start();
			/*	
				if(requestType.equals("CONNECT")){ // connect
					try{
						sendHTTPResponse("HTTP/1.1 200 OK\r\n", adjNodeSocket);
						
						// this thread listen to the browser and forward to web site server
						TCPTunnel tunnel = new TCPTunnel(serverSocket, adjNodeSocket);
						tunnel.start();
						// the main thread listen to the server and forward to browser
						sendData(br, adjNodeBuffer);
						serverSocket.shutdownInput();
						try {
							tunnel.join();
						} catch (InterruptedException e) {
							System.out.println(e.getMessage());
						}
			
						serverSocket.close();
					}catch(IOException e){
						System.out.println("Connetion failed");
						sendHTTPResponse("HTTP/1.1 502 Bad Gateway\r\n", serverSocket);
					}			
				}else{ // if the request type is not "CONNECT"  */
				
				
				// send data from server to adjacent node
				sendData(br, adjNodeBuffer);
				
				// close the socket
				serverSocket.close();		
				
			//	}
						
			} else {
				System.err.println("FATAL ERROR");
			}
			
			// ====================================================================
					
			
>>>>>>> origin/master
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			System.out.println(e1.getMessage());
		}
<<<<<<< HEAD
		
=======
>>>>>>> origin/master
	}
		
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
<<<<<<< HEAD
}
=======
}
>>>>>>> origin/master
