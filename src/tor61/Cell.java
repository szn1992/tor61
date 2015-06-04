package tor61;

import java.nio.ByteBuffer;
import java.util.HashMap;

public class Cell {
	short circuitID;
	byte cmd;
	int openerID;
	int openedID;
	boolean isOpenType = false;
	boolean isRelayType = false;
	
	short streamID;
	short padding;
	int digest;
	short bodyLength;
	byte relayCmd;
	byte[] body;
	
	static HashMap<String, Byte> CELL_TYPE_MAP = Util.getCellTypeMap();
	static HashMap<String, Byte> RELAY_CMD_MAP = Util.getRelayCmdMap();
	
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
				int digest, short bodyLength, String relayCmd, byte[] body) {
		this.circuitID = circuitID;
		this.cmd = CELL_TYPE_MAP.get(cmd);
		this.streamID = streamID;
		this.padding = padding;
		this.digest = digest;
		this.bodyLength = bodyLength;
		this.relayCmd = RELAY_CMD_MAP.get(relayCmd);
		this.body = body;
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
			bb.putShort(streamID);
			bb.putShort(padding);
			bb.putInt(digest);
			bb.putShort(bodyLength);
			bb.put(relayCmd);
			bb.put(body);
		}
		
		return bb.array();
	}
}
