package elsu.common;

import java.io.*;
import java.util.zip.*;

import org.apache.commons.io.*;

public class CompressionUtils {
	// http://stackoverflow.com/questions/16351668/compression-and-decompression-of-string-data-in-java
	// http://stackoverflow.com/questions/6717165/how-can-i-zip-and-unzip-a-string-using-gzipoutputstream-that-is-compatible-with
	// https://gist.github.com/yfnick/227e0c12957a329ad138
    public static byte[] compress(String str) throws Exception {
        if (str == null || str.length() == 0) {
            return null;
        }
        
        ByteArrayOutputStream obj=new ByteArrayOutputStream();
        GZIPOutputStream gzip = new GZIPOutputStream(obj);

        gzip.write(str.getBytes());
        gzip.close();

        byte[] compressed = obj.toByteArray();
        obj.close();
		
		return compressed;
     }

      public static String decompress(byte[] compressed) throws Exception {
        if (compressed == null || compressed.length == 0) {
            return null;
        }

        ByteArrayInputStream bis = new ByteArrayInputStream(compressed);
        GZIPInputStream gis = new GZIPInputStream(bis);
        
        byte[] bytes = IOUtils.toByteArray(gis);
        gis.close();
        bis.close();
        
        return new String(bytes, "UTF-8");
     }	
}
