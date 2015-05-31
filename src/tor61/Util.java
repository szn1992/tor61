package tor61;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class Util {
  public static ArrayList<String> fetch(String name) {
	  List<String> list = new ArrayList<String>();
	  try {
          ProcessBuilder pb = new ProcessBuilder("python","fetch.py", name);
          //pb.inheritIO();
          Process p = pb.start();
          // read output, line by line
          BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
          String line;
          while ((line = in.readLine()) != null ) {
            System.out.println("Read: " + line);
            list.add(line);
          }
          //p.waitFor();
      }
      catch(Exception e) {
          e.printStackTrace();
      }
      return (ArrayList<String>) list;
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
  
  public static String padding(int num, int length){
	  String num_s = Integer.toString(num);
	  for(int i = 0; i < length - num_s.length(); i++){
		  num_s = "0" + num_s;
	  }
	  return num_s;
  }
}
