package tor61;

public class Tor61 {

	static int PORT;
	static String NAME = "Tor61Router-";
	static int INSTANCE_NUM;
	static String GROUP;
	
	public static void main(String[] args) {
		GROUP = args[0];
		INSTANCE_NUM  = Integer.valueOf(args[1]);
		PORT = Integer.valueOf(args[2]);
		
		NAME += GROUP;
		
		int numOfZero = ("" + INSTANCE_NUM).length();
		for (int i = 0; i < numOfZero; i++)
			NAME += "0";
		
		int data = (Integer.valueOf(GROUP) << 16) | INSTANCE_NUM;
	    register("" + PORT, NAME + "-" + INSTANCE_NUM, "" + data);
		
	    
		
    }


    public static void register(String port, String name, String data) {
        try {
            ProcessBuilder pb = new ProcessBuilder("python","registration_client.py", port, name, data);
            pb.inheritIO();
            Process p = pb.start();
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }
}
