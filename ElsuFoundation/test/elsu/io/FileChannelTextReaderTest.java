package elsu.io;

import elsu.io.*;
import junit.framework.*;
/**
 *
 * @author ssd.administrator
 */
public class FileChannelTextReaderTest extends TestCase {
//
//    public FileChannelTextReaderTest(String testName) {
//        super(testName);
//    }
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

    /**
     * Test of readline method, of class FileChannelTextReader.
     */
//    public void testReadline() throws Exception {
//        System.out.println("readline");
//        FileChannelTextReader instance = new FileChannelTextReader("201_2014061019_MSG.txt", "M:\\Temp\\dataLogger\\outgoing", FileProcessingType.STANDARD);
//        String expResult = "";
//        while (true) {
//            String result = instance.readline();
//
//            if (instance.isReaderValid()) {
//                System.out.println(result);
//            } else {
//                break;
//            }
//        }
//    }
//
//    /**
//     * Test of read method, of class FileChannelTextReader.
//     */
//    public void testRead() throws Exception {
//        System.out.println("read");
//        int maxBuffer = 55;
//        FileChannelTextReader instance = new FileChannelTextReader("201_2014061019_MSG.txt", "M:\\Temp\\dataLogger\\outgoing", FileProcessingType.STANDARD);
//        byte[] expResult = null;
//        while (true) {
//            byte[] result = instance.read(maxBuffer);
//
//            if (instance.isReaderValid()) {
//                System.out.println(new String(result));
//            } else {
//                break;
//            }
//        }
//    }
//
//    /**
//     * Test of readline method, of class FileChannelTextReader.
//     */
//    public void testReadlineLive() throws Exception {
//        System.out.println("readline");
//        final int threads = 25;
//        final int records = 50000;
//
//        for (int i = 0; i < threads; i++) {
//            Thread tWriter = new Thread(new Runnable() {
//                public void run() {
//                    String threadId = UUID.randomUUID().toString();
//                    FileChannelTextWriter instance = null;
//
//                    try {
//                        // note file name needs %s for date field substitution
//                        instance = new FileChannelTextWriter("Data_Output2_%s.txt", "M:\\Temp\\dataLogger\\outgoing",
//                                FileRolloverPeriodicity.HOUR, 5);
//
//                        int count = 0;
//                        while (count++ < records) {
//                            try {
//                                int result = instance.writeline(threadId + " -> dtg " + 
//                                        DateStack.convertDate2String(Calendar.getInstance().getTime(), "yyyyMMdd_HHmmss.S") + " record # " + count);
//                            } catch (Exception ex){
//                            }
//
//                            try {
//                                Thread.sleep(10000);
//                            } catch (Exception ex){
//                            }
//                        }
//                    } catch (Exception ex){
//                    } finally {
//                        instance.close();
//                    }
//                }
//            });
//
//            // start the thread to create connection for the service.
//            tWriter.start();
//        }
//        
//        FileChannelTextReader instance = new FileChannelTextReader("Data_Output2_%s.txt", "M:\\Temp\\dataLogger\\outgoing", FileProcessingType.LIVE);
//        String expResult = "";
//        while (true) {
//            String result = instance.readline();
//
//            if (instance.isReaderValid()) {
//                System.out.println(result);
//            } else {
//                break;
//            }
//        }
//    }

    /**
     * Test of readline method, of class FileChannelTextReader.
     */
    public void testReadlineArchive() throws Exception {
        System.out.println("readline");
        // FileChannelTextReader instance = new FileChannelTextReader("Data_Output2_%s.txt", "M:\\Temp\\dataLogger\\outgoing", 
        //         FileProcessingType.ARCHIVE);
        FileChannelTextReader instance = new FileChannelTextReader("import_msg.txt", "M:\\Temp\\dataLogger\\outgoing", 
                 FileProcessingType.STANDARD);
        String expResult = "";
        while (true) {
            String result = instance.readline();

            if (instance.isReaderValid()) {
                System.out.println(result);
            } else {
                System.out.println("\n** EXIT **");
                break;
            }
        }
    }
}
