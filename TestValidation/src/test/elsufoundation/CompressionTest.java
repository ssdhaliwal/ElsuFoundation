package test.elsufoundation;

import elsu.common.CompressionUtils.*;
import elsu.common.FileUtils;

public class CompressionTest {

	public static void main(String[] args) {

		try {
			String text = FileUtils.readFile("/home/development/temp/state/state_c38524e9-5ea0-41a5-abc6-d3c665791efe.kml");

			byte[] compressed = elsu.common.CompressionUtils.compress(text);
			//for (byte character : compressed) {
				System.out.println("test: " + text.length() + ", " + compressed.length);
			//}
			
			String decompressed = elsu.common.CompressionUtils.decompress(compressed);
			System.out.println("test: " + decompressed);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
