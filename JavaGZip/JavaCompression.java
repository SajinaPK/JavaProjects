import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

public class JavaCompression {
	public static ByteArrayOutputStream compress(byte[] input, int bufferSize) throws IOException {
		long startTime = System.currentTimeMillis();
		Deflater deflater = new Deflater();
		deflater.setInput(input);
		deflater.finish();
		ByteArrayOutputStream deflatedData = new ByteArrayOutputStream(input.length);
		byte[] buffer = new byte[bufferSize];
		while (!deflater.finished()) {
			int offset = deflater.deflate(buffer);
			deflatedData.write(buffer, 0, offset);
		}
		deflatedData.close();
		long totalTime = System.currentTimeMillis()-startTime;
		System.out.println("Time taken to deflate "+input.length+" bytes with "+bufferSize+" buffer size took "+totalTime+" ms, compressed data size =" + deflatedData.size());
		return deflatedData;
	}
	
	public static ByteArrayOutputStream decompress(byte[] input, int bufferSize) throws IOException, DataFormatException{
		long startTime = System.currentTimeMillis();
		Inflater inflater = new Inflater();
		inflater.setInput(input);
		
		ByteArrayOutputStream inflatedData = new ByteArrayOutputStream(input.length);
		
		byte[] buffer = new byte[bufferSize];
		while (!inflater.finished()) {
			int offset = inflater.inflate(buffer);
			inflatedData.write(buffer, 0, offset);
		}
		inflatedData.close();
		long totalTime = System.currentTimeMillis()-startTime;
		System.out.println("Time taken to inflate "+input.length+" bytes with "+bufferSize+" buffer size took "+totalTime+" ms, inflated data size =" + inflatedData.size());
		return inflatedData;
	}
	
	
	public static void main(String[] args) throws IOException, DataFormatException {
		if (args.length < 2) {
			System.out.println(" Parameters required:");
			System.out.println("    <Input File Name (required)>   ");
			System.out.println("    <Buffer size to set the input buffer>:   ");
			System.exit(1);
		}
		String fileInName = args[0];
		int bufferSize = Integer.parseInt(args[1]);
		byte bytes[] = new byte[1024];
		@SuppressWarnings("resource")
		FileInputStream fileIn = new FileInputStream(fileInName);
		ByteArrayOutputStream inputByteArrayBuffer = new ByteArrayOutputStream(bufferSize);
		int offset;
		while ((offset = fileIn.read(bytes)) > 0) {
			inputByteArrayBuffer.write(bytes, 0, offset);			
		}
		inputByteArrayBuffer.close();
		ByteArrayOutputStream deflatedData = compress(inputByteArrayBuffer.toByteArray(), bufferSize);
		
		ByteArrayOutputStream inflatedData = decompress(deflatedData.toByteArray(), bufferSize);
		
		if (Arrays.equals(inputByteArrayBuffer.toByteArray(), inflatedData.toByteArray())) {
			System.out.println("Test Passed");
		} else {
			System.out.println("Test Failed");
		}
		
		
	}

}
