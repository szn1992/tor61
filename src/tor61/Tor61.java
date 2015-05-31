package tor61;

<<<<<<< HEAD
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
=======
import java.util.ArrayList;
>>>>>>> dd6bd500d208386c84ad5ade72724d30d700be56

public class Tor61 {

	static int PORT;
	static String NAME = "Tor61Router-";
	static int INSTANCE_NUM;
	static String GROUP;
	static String PREFIX = "Tor61";
	static HashMap<String, Byte> CELL_TYPE_MAP; 	// map cell command to byte version
	static HashMap<String, Byte> RELAY_CMD_MAP;		// map relay cell command to byte version
	
	public static void main(String[] args) throws NumberFormatException, UnknownHostException, IOException {
		if (args.length != 3) {
	    	System.err.println("Usage: java Tor61 <group number> <instance number> <HTTP Proxy port>");
	        System.exit(1);
	   }
		
		// get info from arguments
		GROUP = args[0];
		INSTANCE_NUM  = Integer.valueOf(args[1]);
		PORT = Integer.valueOf(args[2]);
		
		NAME += GROUP;
		
		String instanceNum_s = Util.padding(INSTANCE_NUM, 4);
		
<<<<<<< HEAD
		int data = (Integer.valueOf(GROUP) << 16) | INSTANCE_NUM;	// DATA OR agent id
		
		// register this router at registration service
		Util.register("" + PORT, NAME + instanceNum_s, "" + data);
		
		// fetch information of routers
		ArrayList<String> routersAll = Util.fetch(PREFIX);
		int routersNum = routersAll.size();
		
		ArrayList<String> routersSelected = new ArrayList<String>();
		Random rand = new Random();
		
		// randomly select members of circuit
		for (int i = 0; i < 3; i++)
			routersSelected.add(routersAll.get(rand.nextInt(routersNum)));
		
		String router;
		String[] rAttributes;
		CELL_TYPE_MAP = Util.getCellTypeMap();
		RELAY_CMD_MAP = Util.getRelayCmdMap();
		
		// this router's socket
		Socket myRouter = new Socket("localhost", PORT);
		
		// build circuit
		for (int i = 0; i < 3; i++) {
			router = routersSelected.get(i);
			rAttributes = router.split("\\s");	// ip port data
			Socket otherR = new Socket(rAttributes[0], Integer.valueOf(rAttributes[1]));
			
			// create circuit shit, haven't figured out
			
		}
=======
		// register and fetch
		int data = (Integer.valueOf(GROUP) << 16) | INSTANCE_NUM;
	   Util.register("" + PORT, NAME + instanceNum_s, "" + data);
	   ArrayList<String> availableRouter = Util.fetch(NAME + instanceNum_s);
>>>>>>> dd6bd500d208386c84ad5ade72724d30d700be56
    }

	class Cell {
		short circuitID;
		byte cmd;
		int openerID;
		int openedID;
		boolean isOpenType = false;
		boolean isRelayType = false;
		
		short steamID;
		short padding;
		int digest;
		short bodyLength;
		byte relayCmd;
		
		// OPEN, OPENED, OPEN FAILED cell constructor
		public Cell(short circuitID, String cmd, int openerID, int openedID) {
			this.circuitID = circuitID;
			this.cmd = CELL_TYPE_MAP.get(cmd);
			this.openerID = openerID;
			this.openedID = openedID;
			isOpenType = true;
		}
		
		// other than OPEN AND RELAY cell constructor
		public Cell(short circuitID, String cmd) {
			this.circuitID = circuitID;
			this.cmd = CELL_TYPE_MAP.get(cmd);
		}
		
		// RELAY cell constructor
		public Cell(short circuitID, String cmd, short streamID, short padding,
					int digest, short bodyLength, String relayCmd, short steamID) {
			this.circuitID = circuitID;
			this.cmd = CELL_TYPE_MAP.get(cmd);
			this.steamID = steamID;
			this.padding = padding;
			this.digest = digest;
			this.bodyLength = bodyLength;
			this.relayCmd = RELAY_CMD_MAP.get(relayCmd);
			isRelayType = true;
		}

		public byte[] getByteArray() {
			ByteBuffer bb = ByteBuffer.allocate(512);
			bb.putShort(circuitID);
			bb.put(cmd);
			
			if (isOpenType) {
				bb.putInt(openerID);
				bb.putInt(openedID);
			} else if (isRelayType) {
				bb.putShort(steamID);
				bb.putShort(padding);
				bb.putInt(digest);
				bb.putShort(bodyLength);
				bb.put(relayCmd);
			}
			
			return bb.array();
		}
	}
}
