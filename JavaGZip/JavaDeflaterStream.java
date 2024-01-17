import java.util.zip.DeflaterOutputStream;
import java.io.FileOutputStream;
import java.io.FileInputStream;

class JavaDeflaterStream {
	public static void main(String args[]) {
		if(args.length < 3) {
			System.out.println(" Parameters required:");
			System.out.println("      <Input File Name (required)>    ");
			System.out.println("      <Output File Name (required)>    ");
			System.out.println("      <Buffer size to read whole input file (required)>    ");
			System.exit(1);
		}
		String fileInName = args[0];
		String fileOutName = args[1];
		int buffSize = Integer.parseInt(args[2]);
		byte bytes[] = new byte[buffSize];
		System.out.printf("Buffer size set to %d\n", buffSize);
		long startTime = System.currentTimeMillis();
		try {
			FileInputStream fileIn = new FileInputStream(fileInName);
			FileOutputStream fileOut = new FileOutputStream(fileOutName);
			DeflaterOutputStream defOut = new DeflaterOutputStream(fileOut);
			int offset;
			while((offset = fileIn.read(bytes)) > 0) {
				defOut.write(bytes, 0, offset);
			}
			defOut.finish();
			defOut.close();
			fileIn.close();
			long totalTime = System.currentTimeMillis()-startTime;
			System.out.println("JavaDeflateStream "+buffSize+" time take = "+totalTime+" ms");
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
