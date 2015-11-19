package elsu.common;

import elsu.common.*;
import junit.framework.*;

/**
 *
 * @author ssd.administrator
 */
public class ByteBufferTest extends TestCase {

    public ByteBufferTest(String testName) {
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

    /**
     * Test of length method, of class ASCIIByteBuffer.
     */
    public void testLength() {
        System.out.println("\nlength");
        ASCIIByteBuffer instance = new ASCIIByteBuffer(25);

        System.out.println("-> array length: " + instance.length() + " .. size: " + instance.size());
        if (instance.length() != 0) {
            fail(".. length != 0.");
        }
        if (instance.size() != 25) {
            fail(".. size != 25.");
        }
    }

    /**
     * Test of ensureCapacity method, of class ASCIIByteBuffer.
     */
    public void testEnsureCapacity() {
        System.out.println("\nensureCapacity");
        int minimumCapacity = 20;
        ASCIIByteBuffer instance = new ASCIIByteBuffer(25);
        instance.ensureCapacity(minimumCapacity);

        System.out.println("-> array length: " + instance.length() + " .. size: " + instance.size());
    }

    /**
     * Test of trimToSize method, of class ASCIIByteBuffer.
     */
    public void testTrimToSize_0args() {
        System.out.println("\ntrimToSize");
        String array = "This is a test string 1!! This is a test string 2!!";
        ASCIIByteBuffer instance = new ASCIIByteBuffer(15);
        instance.append(array);
        instance.trimToSize();

        System.out.println("-> array length: " + instance.length() + " .. size: " + instance.size());
        System.out.println(array);
        System.out.println(instance.getString());

        if (instance.length() < array.length()) {
            fail("internal buffer size mis-match.");
        }
        if (!array.equals(instance.getString())) {
            fail("internal buffer data mis-match.");
        }
    }

    /**
     * Test of trimToSize method, of class ASCIIByteBuffer.
     */
    public void testTrimToSize_int() {
        System.out.println("\ntrimToSize");
        String array = "This is a test string 1!! This is a test string 2!!";
        int size = 15;
        ASCIIByteBuffer instance = new ASCIIByteBuffer(25);
        instance.append(array);
        instance.trimToSize(size);

        System.out.println("-> array length: " + instance.length() + " .. size: " + instance.size());
        System.out.println(instance.getString());
        if (instance.length() < 15) {
            fail("internal buffer mis-match.");
        }
    }

    /**
     * Test of setLength method, of class ASCIIByteBuffer.
     */
    public void testSetLength() {
        System.out.println("\nsetLength");
        String array = "This is a test string 1!! This is a test string 2!!";
        int newLength = 15;
        ASCIIByteBuffer instance = new ASCIIByteBuffer(25);
        instance.append(array);
        instance.setLength(newLength);
        instance.trimToSize();

        System.out.println("-> array length: " + instance.length() + " .. size: " + instance.size());
        System.out.println(instance.getString());
        if (instance.length() < 15) {
            fail("internal buffer mis-match.");
        }
    }

    /**
     * Test of byteAt method, of class ASCIIByteBuffer.
     */
    public void testByteAt() {
        System.out.println("\nbyteAt");
        int index = 13;
        ASCIIByteBuffer instance = new ASCIIByteBuffer(25);
        instance.append("This is a test string 1!! This is a test string 2!!");
        byte result = instance.byteAt(index);

        System.out.println("-> array length: " + instance.length() + " .. size: " + instance.size());
        System.out.println(instance.getString() + ", @" + index + ", " + result);
    }

    /**
     * Test of getBytes method, of class ASCIIByteBuffer.
     */
    public void testGetBytes_0args() {
        System.out.println("\ngetBytes");
        ASCIIByteBuffer instance = new ASCIIByteBuffer(25);
        instance.append("This is a test string 1!! This is a test string 2!!");
        byte[] result = instance.getBytes();

        System.out.println("-> array length: " + instance.length() + " .. size: " + instance.size());
        System.out.println(instance.getString());
        System.out.println(new String(result));
    }

    /**
     * Test of getBytes method, of class ASCIIByteBuffer.
     */
    public void testGetBytes_4args() {
        System.out.println("\ngetBytes");
        int srcBegin = 0;
        int srcEnd = 8;
        byte[] result = new byte[]{'x', 'o', 'x', 'o', 'x', 'o', 'x', 'o', 'x', 'o', 'x', 'o', 'x', 'o', 'x', 'o', 'x', 'o', 'x', 'o', 'x', 'o'};
        int dstBegin = 5;
        ASCIIByteBuffer instance = new ASCIIByteBuffer(25);
        instance.append("This is a test string 1!! This is a test string 2!!");

        instance.getBytes(srcBegin, srcEnd, result, dstBegin);

        System.out.println("-? src start");
        System.out.println("-> array length: " + instance.length() + " .. size: " + instance.size());
        System.out.println(instance.getString());
        System.out.println(new String(result));

        instance = new ASCIIByteBuffer(25);
        instance.append("This is a test string 1!! This is a test string 2!!");

        srcBegin = 13;
        srcEnd = 18;
        dstBegin = 5;

        result = new byte[]{'x', 'o', 'x', 'o', 'x', 'o', 'x', 'o', 'x', 'o', 'x', 'o', 'x', 'o', 'x', 'o', 'x', 'o', 'x', 'o', 'x', 'o'};
        instance.getBytes(srcBegin, srcEnd, result, dstBegin);

        System.out.println("-? src start");
        System.out.println("-> array length: " + instance.length() + " .. size: " + instance.size());
        System.out.println(instance.getString());
        System.out.println(new String(result));
    }

    /**
     * Test of setByteAt method, of class ASCIIByteBuffer.
     */
    public void testSetByteAt() {
        System.out.println("\nsetByteAt");
        byte ch = 0;
        ASCIIByteBuffer instance = new ASCIIByteBuffer(25);
        instance.append("This is a test string 1!! This is a test string 2!!");

        System.out.println("-> array length: " + instance.length() + " .. size: " + instance.size());
        System.out.println(instance.getString());

        int index = 13;
        instance.setByteAt(index, ch);
        System.out.println(instance.getString() + ", @" + index);

        index = 0;
        instance.setByteAt(index, ch);
        System.out.println(instance.getString() + ", @" + index);

        index = instance.length() - 1;
        instance.setByteAt(index, ch);
        System.out.println(instance.getString() + ", @" + index);
    }

    /**
     * Test of append method, of class ASCIIByteBuffer.
     */
    public void testAppend_3args_1() {
        System.out.println("\nappend");
        String str = "This is a test ";
        CharSequence s = new String("string 1!!");
        int offset = 0;
        int len = s.length();
        ASCIIByteBuffer instance = new ASCIIByteBuffer(25);
        instance.append(str);

        System.out.println("\n-> array length: " + instance.length() + " .. size: " + instance.size());
        System.out.println(instance.getString());

        instance.append(s, offset, len);
        System.out.println("-> array length: " + instance.length() + " .. size: " + instance.size());
        System.out.println(instance.getString());
    }

    /**
     * Test of append method, of class ASCIIByteBuffer.
     */
    public void testAppend_byteArr() {
        System.out.println("\nappend");
        String str = "This is a test ";
        byte[] cStr = new byte[]{' ', '1', '!', '!'};
        ASCIIByteBuffer instance = new ASCIIByteBuffer(25);
        instance.append(str);

        System.out.println("-> array length: " + instance.length() + " .. size: " + instance.size());
        System.out.println(instance.getString());

        instance.append(cStr);
        System.out.println("-> array length: " + instance.length() + " .. size: " + instance.size());
        System.out.println(instance.getString());
    }

    /**
     * Test of append method, of class ASCIIByteBuffer.
     */
    public void testAppend_3args_3() {
        System.out.println("\nappend");
        String str = "This is a test";
        byte[] bytes = new byte[]{' ', 's', 't', 'r', 'i', 'n', 'g', ' ', '1', '!', '!'};
        int offset = 0;
        int len = bytes.length;
        ASCIIByteBuffer instance = new ASCIIByteBuffer(25);
        instance.append(str);

        System.out.println("-> array length: " + instance.length() + " .. size: " + instance.size());
        System.out.println(instance.getString());

        instance.append(bytes, offset, len);
        System.out.println("-> array length: " + instance.length() + " .. size: " + instance.size());
        System.out.println(instance.getString());
    }

    /**
     * Test of append method, of class ASCIIByteBuffer.
     */
    public void testAppend_byte() {
        System.out.println("\nappend");
        byte b = 40;
        ASCIIByteBuffer instance = new ASCIIByteBuffer(25);
        instance.append("This is a test string 1!! This is a test string 2!!");

        System.out.println("-> array length: " + instance.length() + " .. size: " + instance.size());
        System.out.println(instance.getString());

        instance.append(b);
        System.out.println("-> array length: " + instance.length() + " .. size: " + instance.size());
        System.out.println(instance.getString());
    }

    /**
     * Test of append method, of class ASCIIByteBuffer.
     */
    public void testAppend_char() {
        System.out.println("\nappend");
        char c = 'R';
        ASCIIByteBuffer instance = new ASCIIByteBuffer(25);
        instance.append("This is a test string 1!! This is a test string 2!!");
        instance.append("This is a test string 1!! This is a test string 2!!");

        System.out.println("-> array length: " + instance.length() + " .. size: " + instance.size());
        System.out.println(instance.getString());

        instance.append(c);
        System.out.println("-> array length: " + instance.length() + " .. size: " + instance.size());
        System.out.println(instance.getString());
    }

    /**
     * Test of delete method, of class ASCIIByteBuffer.
     */
    public void testDelete() {
        System.out.println("\ndelete");
        int start = 25;
        int end = 0;
        ASCIIByteBuffer instance = new ASCIIByteBuffer(25);
        instance.append("This is a test string 1!! This is a test string 2!!");

        System.out.println("-? delete to end");
        System.out.println("-> array length: " + instance.length() + " .. size: " + instance.size());
        System.out.println(instance.getString());

        end = instance.length();
        instance.delete(start, end);
        System.out.println("-> array length: " + instance.length() + " .. size: " + instance.size());
        System.out.println(instance.getString());

        System.out.println("-? delete middle");
        instance = new ASCIIByteBuffer(25);
        instance.append("This is a test string 1!! This is a test string 2!!");

        System.out.println("-> array length: " + instance.length() + " .. size: " + instance.size());
        System.out.println(instance.getString());

        end = 25 + 5;
        instance.delete(start, end);
        System.out.println("-> array length: " + instance.length() + " .. size: " + instance.size());
        System.out.println(instance.getString());
    }

    /**
     * Test of deleteByteAt method, of class ASCIIByteBuffer.
     */
    public void testDeleteByteAt() {
        System.out.println("\ndeleteByteAt");
        int index = 22; // 23 or 24 should result in same string :)
        ASCIIByteBuffer instance = new ASCIIByteBuffer(25);
        instance.append("This is a test string 1!!");

        System.out.println("-> array length: " + instance.length() + " .. size: " + instance.size());
        System.out.println(instance.getString());

        instance.deleteByteAt(index);
        System.out.println(instance.getString() + ", @" + index);
    }

    /**
     * Test of insert method, of class ASCIIByteBuffer.
     */
    public void testInsert_4args_1() {
        System.out.println("\ninsert");
        int index = 0;
        byte[] bytes = {'1', '2', '3', '4'};

        // insert all
        int offset = 0;
        int len = bytes.length;

        ASCIIByteBuffer instance = new ASCIIByteBuffer(25);
        instance.append("This is a test string 1!!");

        System.out.println("-? insert all");
        instance.insert(index, bytes, offset, len);
        System.out.println("-> array length: " + instance.length() + " .. size: " + instance.size());
        System.out.println(instance.getString());

        // insert partial
        System.out.println("-? insert partial");
        offset = 3;
        len = bytes.length - offset;
        instance = new ASCIIByteBuffer(25);
        instance.append("This is a test string 1!!");

        instance.insert(index, bytes, offset, len);
        System.out.println("-> array length: " + instance.length() + " .. size: " + instance.size());
        System.out.println(instance.getString());
    }

    /**
     * Test of insert method, of class ASCIIByteBuffer.
     */
    public void testInsert_int_byteArr() {
        System.out.println("\ninsert");
        int offset = 4;
        byte[] bytes = new byte[]{'5', '7', 'R'};
        ASCIIByteBuffer instance = new ASCIIByteBuffer(25);
        instance.append("This is a test string 1!!");

        instance.insert(offset, bytes);
        System.out.println("-> array length: " + instance.length() + " .. size: " + instance.size());
        System.out.println(instance.getString() + ", @ " + offset);
    }

    /**
     * Test of insert method, of class ASCIIByteBuffer.
     */
    public void testInsert_4args_3() {
        System.out.println("\ninsert");
        int dstOffset = 0;
        CharSequence s = new String("00!!00");
        int start = 2;
        int end = 5;
        ASCIIByteBuffer instance = new ASCIIByteBuffer(25);
        instance.append("This is a test string 1!!");

        System.out.println("-> array length: " + instance.length() + " .. size: " + instance.size());
        System.out.println(instance.getString() + ", @ " + start + ", for " + end);
        instance.insert(dstOffset, s, start, end);
        System.out.println(instance.getString() + ", @ " + start + ", for " + end);
    }

    /**
     * Test of insert method, of class ASCIIByteBuffer.
     */
    public void testInsert_int_byte() {
        System.out.println("\ninsert");
        int offset = 15;
        byte b = 0;
        ASCIIByteBuffer instance = new ASCIIByteBuffer(25);
        instance.append("This is a test string 1!!");

        System.out.println("-> array length: " + instance.length() + " .. size: " + instance.size());
        System.out.println(instance.getString() + ", @ " + offset);
        instance.insert(offset, b);
        System.out.println(instance.getString() + ", @ " + offset + ", with " + b);
    }

    /**
     * Test of insert method, of class ASCIIByteBuffer.
     */
    public void testInsert_int_char() {
        System.out.println("\ninsert");
        int offset = 15;
        char c = ' ';
        ASCIIByteBuffer instance = new ASCIIByteBuffer(25);
        instance.append("This is a test string 1!!");

        instance.insert(offset, c);
        System.out.println("-> array length: " + instance.length() + " .. size: " + instance.size());
        System.out.println(instance.getString() + ", @ " + offset);
    }

    /**
     * Test of indexOf method, of class ASCIIByteBuffer.
     */
    public void testIndexOf_7args() {
        System.out.println("\nindexOf");
        byte[] source = new byte[]{'x', '1', 'x', 'o', 'x', 'a', 'x', '2', 'x', 'o', 'x', 'b', 'x', '3', 'x', 'o', 'x', 'c', 'x', '4', 'x', 'o'};
        int sourceOffset = 0;
        int sourceCount = source.length;
        
        byte[] target = new byte[]{'x', 'o', 'x'};
        int targetOffset = 0;
        int targetCount = target.length;
        int fromIndex = 9;
        
        int result = ASCIIByteBuffer.indexOf(source, sourceOffset, sourceCount, target, targetOffset, targetCount, fromIndex);

        System.out.println("-> array length: " + source.length + " .. size: " + source.length);
        System.out.println(new String(source));
        System.out.println("-> array length: " + target.length + " .. size: " + target.length);
        System.out.println(new String(target));
        System.out.println("-> found @ " + result);
    }

    /**
     * Test of lastIndexOf method, of class ASCIIByteBuffer.
     */
    public void testLastIndexOf_7args() {
        System.out.println("\nlastIndexOf");
        byte[] source = new byte[]{'x', '1', 'x', 'o', 'x', 'a', 'x', '2', 'x', 'o', 'x', 'b', 'x', '3', 'x', 'o', 'x', 'c', 'x', '4', 'x', 'o'};
        int sourceOffset = 0;
        int sourceCount = source.length;
        
        byte[] target = new byte[]{'x', 'o', 'x'};
        int targetOffset = 0;
        int targetCount = target.length;
        int fromIndex = source.length;
        
        int result = ASCIIByteBuffer.lastIndexOf(source, sourceOffset, sourceCount, target, targetOffset, targetCount, fromIndex);

        System.out.println("-> array length: " + source.length + " .. size: " + source.length);
        System.out.println(new String(source));
        System.out.println("-> array length: " + target.length + " .. size: " + target.length);
        System.out.println(new String(target));
        System.out.println("-> found @ " + result);
    }

    /**
     * Test of replace method, of class ASCIIByteBuffer.
     */
    public void testReplace_byte_byte() {
        System.out.println("\nreplace");
        byte oldChar = 't';
        byte newChar = '2';
        ASCIIByteBuffer instance = new ASCIIByteBuffer(25);
        instance.append("This is a test 1!!");
        
        System.out.println("-> array length: " + instance.length() + " .. size: " + instance.size());
        System.out.println(instance.getString());
        instance.replace(oldChar, newChar);
        System.out.println(instance.getString());
    }

    /**
     * Test of replace method, of class ASCIIByteBuffer.
     */
    public void testReplace_3args() {
        System.out.println("\nreplace");
        int start = 15;
        String str = "seraj";
        int end = str.length() + start + 1; // offset length by 1 for next byte
        ASCIIByteBuffer instance = new ASCIIByteBuffer(25);
        instance.append("This is a test string 1!!");

        System.out.println("-> array length: " + instance.length() + " .. size: " + instance.size());
        System.out.println(instance.getString());
        instance.replace(start, end, str);
        System.out.println(instance.getString());
    }

    /**
     * Test of subSequence method, of class ASCIIByteBuffer.
     */
    public void testSubSequence_int_int() {
        System.out.println("\nsubSequence");
        int start = 5;
        int end = 14;
        ASCIIByteBuffer instance = new ASCIIByteBuffer(25);
        instance.append("This is a test string 1!!");
    
        System.out.println("-> array length: " + instance.length() + " .. size: " + instance.size());
        System.out.println(instance.getString());
        byte[] result = instance.subSequence(start, end);
        System.out.println(new String(result));
    }

    /**
     * Test of reverse method, of class ASCIIByteBuffer.
     */
    public void testReverse() {
        System.out.println("\nreverse");
        ASCIIByteBuffer instance = new ASCIIByteBuffer(25);
        instance.append("This is a test string 1!!");

        System.out.println("-> array length: " + instance.length() + " .. size: " + instance.size());
        System.out.println(instance.getString());
        instance.reverse();
        System.out.println(instance.getString());
    }

}
