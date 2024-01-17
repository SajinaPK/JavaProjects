import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.GZIPOutputStream;

public class JavaGZip {
	public static void main(String[] args) {
		if (args.length < 3) {
			System.out.println(" Parameters required:");
			System.out.println("    <Input  File Name (required)>   ");
			System.out.println("    <Output File Name (required)>   ");
			System.out.println("    <Buffer size to read whole input file (required)>   ");
			System.exit(1);
		}
		String fileInName = args[0];
		String fileOutName = args[1];
		int buffSize = Integer.parseInt(args[2]);
		byte bytes[] = new byte[buffSize];
		System.out.println("Buffer size set to "+Integer.toString(buffSize));
		long startTime = System.currentTimeMillis();
		try {
			FileInputStream fileIn = new FileInputStream(fileInName);
			FileOutputStream fileOut = new FileOutputStream(fileOutName);
			GZIPOutputStream gzOut = new GZIPOutputStream(fileOut);
			int offset;
			while ((offset = fileIn.read(bytes)) > 0) {
				gzOut.write(bytes, 0, offset);
			}
			gzOut.finish();
			gzOut.close();
			fileIn.close();
			long totalTime = System.currentTimeMillis()-startTime;
			System.out.println("JAVAGZIP Compression "+buffSize+" time take = "+totalTime+" ms");
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

}
