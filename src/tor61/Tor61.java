package tor61;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;


public class Tor61 {

	static int PORT;
	static String NAME = "Tor61Router-";
	static int INSTANCE_NUM;
	static String GROUP;
	static String PREFIX = "Tor61";
	//static HashMap<String, Byte> CELL_TYPE_MAP; 	// map cell command to byte version
	//static HashMap<String, Byte> RELAY_CMD_MAP;		// map relay cell command to byte version
	static HashMap<Byte, String> CELL_TYPE_BYTE_MAP = Util.getByteCellTypeMap();
	static HashMap<Byte, String> RELAY_CMD_BYTE_MAP = Util.getByteRelayCmdMap();
	
	public static void main(String[] args) throws NumberFormatException, UnknownHostException, IOException {
		if (args.length != 3) {
	    	System.err.println("Usage: java Tor61 <group number> <instance number> <HTTP Proxy port>");
	        System.exit(1);
	   }
		
		// get info from arguments
		GROUP = args[0];
		INSTANCE_NUM  = Integer.valueOf(args[1]);
		PORT = Integer.valueOf(args[2]);
		
		String instanceNum_s = Util.padding(INSTANCE_NUM, 4);
		
		int data = (Integer.valueOf(GROUP) << 16) | INSTANCE_NUM;	// DATA OR agent id
		
		// register this router at registration service
		Util.register("" + PORT, NAME + GROUP + "-" + instanceNum_s, "" + data);
		
		// fetch information of routers
		ArrayList<String> routersAll = Util.fetch(NAME + GROUP);
		int routersNum = routersAll.size();
		
		ArrayList<String> routersSelected = new ArrayList<String>();
		Random rand = new Random();
		
		// ======================== circuit creation =======================
		
		System.out.println("Check point1, first node information: " + routersSelected.get(0));
		String[] info = routersAll.get(rand.nextInt(routersNum)).split(" "); //randomly pick one and extract info
		String ip = info[0];
		int port = Integer.valueOf(info[1]);
		int registrationData = Integer.valueOf(info[2]);
		
		// Here, I try to connect to the first node without checking if there is a 
		// TCP connection, fine when create circuit for the first time, but need to 
		// change when circuit creation need to be done multiple times
		Socket s = new Socket(ip, port);
		Cell openCell = new Cell((short) 0,"OPEN", data, registrationData); // hard code 1 as circuit id
		s.getOutputStream().write(openCell.getByteArray()); // send the open cell
		
		InputStream in = s.getInputStream();
		ByteBuffer received = ByteBuffer.wrap(Util.readMessageCell(in));
		if(CELL_TYPE_BYTE_MAP.get(received.get(2)).equals("OPENED")){ // first node successfully opened
			// create a circuit
			short circuitID = 1; // hard code the first circuit id to be 1
			Cell createCell = new Cell(circuitID, "CREATE");
			s.getOutputStream().write(createCell.getByteArray());
			received = ByteBuffer.wrap(Util.readMessageCell(in));
			if(CELL_TYPE_BYTE_MAP.get(received.get(2)).equals("CREATED")){ // circuit to first node successfully created
				int count = 0;
				while(count < 2){
					String[] infoNext = routersAll.get(rand.nextInt(routersNum)).split(" ");
					String ipNext = infoNext[0];
					int portNext = Integer.valueOf(infoNext[1]);
					int registrationDataNext = Integer.valueOf(infoNext[2]);
					String body = ipNext + ":" + portNext + "\0" + registrationDataNext;
					byte[] bodyByte = body.getBytes();
					
					Cell extend = new Cell((short) 0, "RELAY", (short) 0,(short) 0, (short) 0, (short) bodyByte.length, "EXTEND", bodyByte);
					s.getOutputStream().write(extend.getByteArray());
					received = ByteBuffer.wrap(Util.readMessageCell(in));
					if(CELL_TYPE_BYTE_MAP.get(received.get(2)).equals("EXTENDED")){
						count++;
					}
				}			
			}else{
				// do something
			}
		}else{
			// do something?
			s.close();
			// and loop back again
		}
		
		
		// ===============================================================
		
		
//		String router;
//		String[] rAttributes;
//		
//		// this router's socket
//		Socket myRouter = new Socket("localhost", PORT);
//		
//		// build circuit
//		for (int i = 0; i < 0; i++) {
//			router = routersSelected.get(i);
//			rAttributes = router.split("\\s");	// ip port data
//			Socket otherR = new Socket(rAttributes[0], Integer.valueOf(rAttributes[1]));
//			
//			// create circuit shit, haven't figured out
//			
//		}
	   
    }	
}
