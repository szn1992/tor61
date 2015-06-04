package tor61;

import java.nio.ByteBuffer;

public class Testing {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Cell cell = new Cell((short) 1, "CREATE");
		byte[] b = cell.getByteArray();
		ByteBuffer c = ByteBuffer.wrap(b);
		System.out.print(c.getShort(0));
		System.out.print(c.get(2));
	}

}
