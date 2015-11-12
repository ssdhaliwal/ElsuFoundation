package elsu.io;

import elsu.io.*;
import junit.framework.*;

/**
 *
 * @author ssd.administrator
 */
public class FileChannelBinaryReaderTest extends TestCase {
    
    public FileChannelBinaryReaderTest(String testName) {
        super(testName);
    }
//    
//    @Override
//    protected void setUp() throws Exception {
//        super.setUp();
//    }
//    
//    @Override
//    protected void tearDown() throws Exception {
//        super.tearDown();
//    }
//
//    /**
//     * Test of readline method, of class FileChannelReader.
//     */
//    public void testReadline() throws Exception {
//        System.out.println("readline");
//        FileChannelReader instance = null;
//        String expResult = "";
//        String result = instance.readline();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of read method, of class FileChannelReader.
//     */
//    public void testRead() throws Exception {
//        System.out.println("read");
//        FileChannelReader instance = null;
//        byte[] expResult = null;
//        byte[] result = instance.read();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
    
    public void testReadArchive() throws Exception {
        System.out.println("read");
        // FileChannelTextReader instance = new FileChannelTextReader("Data_Output2_%s.txt", "M:\\Temp\\dataLogger\\outgoing", 
        //         FileProcessingType.ARCHIVE);
        FileChannelReader instance = new FileChannelReader("import_msg.txt", "M:\\Temp\\dataLogger\\outgoing", 
                 FileProcessingType.STANDARD);
        String expResult = "";
        while (true) {
            byte[] result = instance.read();

            if (instance.isReaderValid()) {
                for(int i = 0; i < result.length; i++) {
                    System.out.print((char)result[i]);
                }
            } else {
                System.out.println("\n** EXIT **");
                break;
            }
        }
    }    
}
