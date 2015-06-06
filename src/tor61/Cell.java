package tor61;

import java.nio.ByteBuffer;
import java.util.HashMap;

public class Cell {
	public final static byte OPEN = 0x05;
	public final static byte OPENED = 0x06;
	public final static byte OPEN_FAILED = 0x07;
	public final static byte CREATE = 0x01;
	public final static byte CREATED = 0x02;
	public final static byte CREATE_FAILED = 0x08;
	public final static byte DESTORY = 0x04;
	public final static byte RELAY = 0x03;

	public final static byte BEGIN = 0x01;
	public final static byte DATA = 0x02;
	public final static byte END = 0x03;
	public final static byte CONNECTION = 0x04;
	public final static byte EXTEND = 0x05;
	public final static byte END = 0x03;
	public final static byte END = 0x03;
	public final static byte END = 0x03;
	
	map.put("BEGIN", (byte) 0x01);
	  map.put("DATA", (byte) 0x02);
	  map.put("END", (byte) 0x03);
	  map.put("CONNECTED", (byte) 0x04);
	  map.put("EXTEND", (byte) 0x06);
	  map.put("EXTENDED", (byte) 0x07);
	  map.put("BEGIN FAILED", (byte) 0x0b);
	  map.put("EXTEND FAILED", (byte) 0x0c);
	  
	short circuitID;
	byte cmd;
	int openerID;
	int openedID;
	boolean isOpenType = false;
	boolean isRelayType = false;
	
	short streamID;
	short padding = 0;
	int digest = 0;
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
	public Cell(short circuitID, String cmd, short streamID, short bodyLength, String relayCmd, byte[] body) {
		this.circuitID = circuitID;
		this.cmd = CELL_TYPE_MAP.get(cmd);
		this.streamID = streamID;
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
