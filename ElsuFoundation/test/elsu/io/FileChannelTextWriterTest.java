package elsu.io;

import elsu.io.*;
import junit.framework.*;
import java.util.*;
import elsu.common.*;

/**
 *
 * @author ssd.administrator
 */
public class FileChannelTextWriterTest extends TestCase {

    public FileChannelTextWriterTest(String testName) {
        super(testName);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }
//
//    /**
//     * Test of isIgnoreEmptyLines method, of class FileChannelTextWriter.
//     */
//    public void testIsIgnoreEmptyLines_0args() {
//        System.out.println("isIgnoreEmptyLines");
//        FileChannelTextWriter instance = null;
//        boolean expResult = false;
//        boolean result = instance.isIgnoreEmptyLines();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of isIgnoreEmptyLines method, of class FileChannelTextWriter.
//     */
//    public void testIsIgnoreEmptyLines_boolean() {
//        System.out.println("isIgnoreEmptyLines");
//        boolean ignoreEmptyLines = false;
//        FileChannelTextWriter instance = null;
//        boolean expResult = false;
//        boolean result = instance.isIgnoreEmptyLines(ignoreEmptyLines);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of writeline method, of class FileChannelTextWriter.
//     */
//    public void testWriteline() throws Exception {
//        System.out.println("writeline");
//        String data = "This is a write test.... !001";
//
//        FileChannelTextWriter instance = new FileChannelTextWriter("Data_Output.txt", "M:\\Temp\\dataLogger\\outgoing");
//        int expResult = 0;
//        int result = instance.writeline(data);
//        instance.close();
//
//        instance = new FileChannelTextWriter("Data_Output.txt", "M:\\Temp\\dataLogger\\outgoing", false);
//        result = instance.writeline(data);
//        instance.close();
//
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of write method, of class FileChannelTextWriter.
//     */
//    public void testWrite() throws Exception {
//        System.out.println("write");
//        byte[] buffer = "This is a write test.... !002".getBytes();
//        FileChannelTextWriter instance = new FileChannelTextWriter("Data_Output.txt", "M:\\Temp\\dataLogger\\outgoing");
//        int expResult = 0;
//        int result = instance.write(buffer);
//        instance.close();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    public void testWriteLive() throws Exception {
//        final int records = 1000;
//
//        String threadId = UUID.randomUUID().toString();
//        FileChannelTextWriter instance = null;
//
//        try {
//            instance = new FileChannelTextWriter("Data_Output2_%s.txt", "M:\\Temp\\dataLogger\\outgoing",
//                    FileRolloverPeriodicityType.HOUR, 5);
//
//            int count = 0;
//            while (count++ < records) {
//                try {
//                    int result = instance.writeline(threadId + " -> record # " + count);
//                } catch (Exception ex){
//                }
//
//                try {
//                    Thread.sleep(10000);
//                } catch (Exception ex){
//                }
//            }
//        } catch (Exception ex){
//        } finally {
//            instance.close();
//        }
//    }

    public void testWritelineThreadedStandard() throws Exception {
        final int threads = 25;
        final int records = 1000;

        for (int i = 0; i < threads; i++) {
            Thread tWriter = new Thread(new Runnable() {
                public void run() {
                    String threadId = UUID.randomUUID().toString();
                    FileChannelTextWriter instance = null;

                    try {
                        instance = new FileChannelTextWriter("Data_Output.txt", "M:\\Temp\\dataLogger\\outgoing", true);

                        int count = 0;
                        while (count++ < records) {
                            try {
                                int result = instance.writeline(threadId + " -> dtg " + 
                                        DateUtils.convertDate2String(Calendar.getInstance().getTime(), "yyyyMMdd_HHmmss.S") + " record # " + count);
                            } catch (Exception ex){
                            }

                            try {
                                Thread.sleep(50);
                            } catch (Exception ex){
                            }
                        }
                    } catch (Exception ex){
                    } finally {
                        instance.close();
                    }
                }
            });

            // start the thread to create connection for the service.
            tWriter.start();
        }
    }

    public void testWritelineThreadedLive() throws Exception {
        final int threads = 25;
        final int records = 50000;

        for (int i = 0; i < threads; i++) {
            Thread tWriter = new Thread(new Runnable() {
                public void run() {
                    String threadId = UUID.randomUUID().toString();
                    FileChannelTextWriter instance = null;

                    try {
                        // note file name needs %s for date field substitution
                        instance = new FileChannelTextWriter("Data_Output2_%s.txt", "M:\\Temp\\dataLogger\\outgoing",
                                FileRolloverPeriodicityType.HOUR);

                        int count = 0;
                        while (count++ < records) {
                            try {
                                int result = instance.writeline(threadId + " -> dtg " + 
                                        DateUtils.convertDate2String(Calendar.getInstance().getTime(), "yyyyMMdd_HHmmss.S") + " record # " + count);
                            } catch (Exception ex){
                            }

                            try {
                                Thread.sleep(10000);
                            } catch (Exception ex){
                            }
                        }
                    } catch (Exception ex){
                    } finally {
                        instance.close();
                    }
                }
            });

            // start the thread to create connection for the service.
            tWriter.start();
        }
    }
}
