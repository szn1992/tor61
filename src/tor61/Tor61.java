package tor61;

public class Tor61 {

	static int PORT;
	static String NAME = "Tor61Router-";
	static int INSTANCE_NUM;
	static String GROUP;
	
	public static void main(String[] args) {
		if (args.length != 3) {
	    	System.err.println("Usage: java Tor61 <group number> <instance number> <HTTP Proxy port>");
	        System.exit(1);
	    }
		
		GROUP = args[0];
		INSTANCE_NUM  = Integer.valueOf(args[1]);
		PORT = Integer.valueOf(args[2]);
		
		NAME += GROUP;
		
		String instanceNum_s = Util.padding(INSTANCE_NUM, 4);
		
		int data = (Integer.valueOf(GROUP) << 16) | INSTANCE_NUM;
	   Util.register("" + PORT, NAME + instanceNum_s, "" + data);
	
    }

}
