package elsu.common;

import java.util.*;

/**
 * This is a clone of StringBuilder/StringBuffer classes to provide byte array
 * handling for socket communications or any other type of byte level handling
 * to reduce the overhead memory allocation of char which is unicode and cause
 * overhead in coversion from char to byte and back. Thread-safe implementation.
 *
 * NOTE: This code was modified from original code from SUN java.lang
 * StringBuilder and StringBuffer classes; so the credit is all to the original
 * authors.
 *
 * All unicode conversions were removed, some appends, inserts, and replaces
 * were updated to reuse existing functionality from one method vice performing
 * multiple converts.
 *
 * Added replace() functionality from string class to allow byte level
 * replacement of specific bytes if required.
 *
 * @author dhaliwal-admin
 */
public class ASCIIByteBuffer {

    private Object _runtimeSync = new Object();
    private volatile byte[] _value;
    private volatile int _count;
    private volatile int _minGrowthSize = 64;

    public ASCIIByteBuffer(int capacity) {
        this._value = new byte[capacity];
    }

    public ASCIIByteBuffer(int capacity, int growthSize) {
        this._value = new byte[capacity];
        this._minGrowthSize = growthSize;
    }

    public int length() {
        int result = 0;

        synchronized (this._runtimeSync) {
            result = this._count;
        }

        return result;
    }

    public synchronized int size() {
        int result = 0;

        synchronized (this._runtimeSync) {
            result = this._value.length;
        }

        return result;
    }

    public void ensureCapacity(int minimumCapacity) {
        synchronized (this._runtimeSync) {
            if (minimumCapacity > this._value.length) {
                expandCapacity(minimumCapacity);
            }
        }
    }

    private void expandCapacity(int minimumCapacity) {
        int newCapacity = (this._value.length + 1 + this._minGrowthSize);

        if (newCapacity < 0) {
            newCapacity = Integer.MAX_VALUE;
        } else if (minimumCapacity > newCapacity) {
            newCapacity = minimumCapacity;
        }

        this._value = Arrays.copyOf(this._value, newCapacity);
    }

    public void trimToSize() {
        synchronized (this._runtimeSync) {
            if (this._count < this._value.length) {
                this._value = Arrays.copyOf(this._value, this._count);
            }
        }
    }

    public void trimToSize(int size) {
        setLength(size);
        trimToSize();
    }

    public void setLength(int newLength) {
        if (newLength < 0) {
            throw new StringIndexOutOfBoundsException(newLength);
        }

        synchronized (this._runtimeSync) {
            if (newLength > this._value.length) {
                expandCapacity(newLength);
            }

            if (this._count < newLength) {
                for (; this._count < newLength; this._count++) {
                    this._value[this._count] = '\0';
                }
            } else {
                this._count = newLength;
            }
        }
    }

    public byte byteAt(int index) {
        if ((index < 0) || (index >= this._count)) {
            throw new StringIndexOutOfBoundsException(index);
        }

        byte result = 0;

        synchronized (this._runtimeSync) {
            result = this._value[index];
        }

        return result;
    }

    public byte[] getByteBuffer() {
        byte[] result = null;

        synchronized (this._runtimeSync) {
            result = this._value;
        }

        return result;
    }

    public byte[] getBytes() {
        return subSequence(0);
    }

    public String getString() {
        return new String(getBytes());
    }

    public void getBytes(int srcBegin, int srcEnd, byte[] dst,
            int dstBegin) {
        if (srcBegin < 0) {
            throw new StringIndexOutOfBoundsException(srcBegin);
        }

        if ((srcEnd < 0) || (srcEnd > this._count)) {
            throw new StringIndexOutOfBoundsException(srcEnd);
        }

        if (srcBegin > srcEnd) {
            throw new StringIndexOutOfBoundsException("srcBegin > srcEnd");
        }

        synchronized (this._runtimeSync) {
            System.arraycopy(this._value, srcBegin, dst, dstBegin, srcEnd - srcBegin);
        }
    }

    public void setByteAt(int index, byte ch) {
        if ((index < 0) || (index >= this._count)) {
            throw new StringIndexOutOfBoundsException(index);
        }

        synchronized (this._runtimeSync) {
            this._value[index] = ch;
        }
    }

    public ASCIIByteBuffer append(String str) {
        return append(str.getBytes());
    }

    public ASCIIByteBuffer append(Object obj) {
        return append(String.valueOf(obj));
    }

    public ASCIIByteBuffer append(StringBuffer sb) {
        if (sb == null) {
            return this;
        }

        return append(sb.toString());
    }

    public ASCIIByteBuffer append(CharSequence s, int start, int end) {
        ASCIIByteBuffer result = null;

        synchronized (this._runtimeSync) {
            if (s == null) {
                result = this;
            } else {
                if ((start < 0) || (end < 0) || (start > end) || (end > s.length())) {
                    throw new IndexOutOfBoundsException(
                            "start " + start + ", end " + end + ", s.length() "
                            + s.length());
                }

                int len = end - start;
                if (len == 0) {
                    result = this;
                } else {
                    int newCount = this._count + len;
                    if (newCount > this._value.length) {
                        expandCapacity(newCount);
                    }

                    for (int i = start; i < end; i++) {
                        this._value[this._count++] = (byte) s.charAt(i);
                    }

                    this._count = newCount;
                    result = this;
                }
            }
        }

        return result;
    }

    public ASCIIByteBuffer append(CharSequence s) {
        if (s == null) {
            return this;
        }

        if (s instanceof String) {
            return this.append((String) s);
        }

        if (s instanceof StringBuffer) {
            return this.append((StringBuffer) s);
        }

        return this.append(s, 0, s.length());
    }

    public ASCIIByteBuffer append(char[] str) {
        return append(new String(str).getBytes());
    }

    public ASCIIByteBuffer append(byte[] bytes) {
        synchronized (this._runtimeSync) {
            int newCount = this._count + bytes.length;
            if (newCount > this._value.length) {
                expandCapacity(newCount);
            }

            System.arraycopy(bytes, 0, this._value, this._count, bytes.length);
            this._count = newCount;
        }

        return this;
    }

    public ASCIIByteBuffer append(char[] str, int offset, int len) {
        return append(new String(str).getBytes(), offset, len);
    }

    public ASCIIByteBuffer append(byte[] bytes, int offset, int len) {
        synchronized (this._runtimeSync) {
            int newCount = this._count + len;
            if (newCount > this._value.length) {
                expandCapacity(newCount);
            }

            System.arraycopy(bytes, offset, this._value, this._count, len);
            this._count = newCount;
        }

        return this;
    }

    public ASCIIByteBuffer append(byte b) {
        synchronized (this._runtimeSync) {
            int newCount = this._count + 1;
            if (newCount > this._value.length) {
                expandCapacity(newCount);
            }

            this._value[this._count++] = b;
        }

        return this;
    }

    public ASCIIByteBuffer append(char c) {
        synchronized (this._runtimeSync) {
            int newCount = this._count + 1;
            if (newCount > this._value.length) {
                expandCapacity(newCount);
            }

            this._value[this._count++] = (byte) c;
        }

        return this;
    }

    public ASCIIByteBuffer append(int i) {
        return append(Integer.toString(i));
    }

    public ASCIIByteBuffer append(long l) {
        return append(Long.toString(l));
    }

    public ASCIIByteBuffer append(float f) {
        return append(Float.toString(f));
    }

    public ASCIIByteBuffer append(double d) {
        return append(Double.toString(d));
    }

    public ASCIIByteBuffer delete(char c) {
        return delete((byte) c);
    }

    // to-do
    public ASCIIByteBuffer delete(byte b) {
        return null;
    }

    // to-do
    public ASCIIByteBuffer delete(String str) {
        return null;
    }

    public ASCIIByteBuffer delete(int start, int end) {
        if (start < 0) {
            throw new StringIndexOutOfBoundsException(start);
        }

        synchronized (this._runtimeSync) {
            if (end > this._count) {
                end = this._count;
            }

            if (start > end) {
                throw new StringIndexOutOfBoundsException();
            }

            int len = end - start;
            if (len > 0) {
                System.arraycopy(this._value, start + len, this._value, start,
                        this._count - end);
                this._count -= len;
            }
        }

        return this;
    }

    public ASCIIByteBuffer deleteByteAt(int index) {
        if ((index < 0) || (index >= this._count)) {
            throw new StringIndexOutOfBoundsException(index);
        }

        synchronized (this._runtimeSync) {
            System.arraycopy(this._value, index + 1, this._value, index, this._count
                    - index - 1);
            this._count--;
        }

        return this;
    }

    // to-do
    public byte[] replace(String oldstr, String newstr) {
        return null;
    }

    public byte[] replace(byte oldChar, byte newChar) {
        synchronized (this._runtimeSync) {
            if (oldChar != newChar) {
                int len = this._count;
                int i = -1;
                byte[] val = this._value;

                while (++i < len) {
                    if (val[i] == oldChar) {
                        val[i] = newChar;
                    }
                }
            }
        }

        return this._value;
    }

    public ASCIIByteBuffer replace(int start, int end, String str) {
        if (start < 0) {
            throw new StringIndexOutOfBoundsException(start);
        }

        if (start > this._count) {
            throw new StringIndexOutOfBoundsException("start > length()");
        }

        if (start > end) {
            throw new StringIndexOutOfBoundsException("start > end");
        }

        synchronized (this._runtimeSync) {
            if (end > this._count) {
                end = this._count;
            }

            int len = str.length();
            int newCount = this._count + len - (end - start);
            if (newCount > this._value.length) {
                expandCapacity(newCount);
            }

            System.arraycopy(this._value, end, this._value, start + len, this._count
                    - end);

            //str.getChars(this._value, start);
            System.arraycopy(str.getBytes(), 0, this._value, start, len);
            this._count = newCount;
        }

        return this;
    }

    public byte[] subSequence(int start, int end) {
        if (start < 0) {
            throw new StringIndexOutOfBoundsException(start);
        }
        if (end > this._count) {
            throw new StringIndexOutOfBoundsException(end);
        }
        if (start > end) {
            throw new StringIndexOutOfBoundsException(end - start);
        }

        byte[] bytes = null;
        synchronized (this._runtimeSync) {
            bytes = new byte[end - start];
            System.arraycopy(this._value, start, bytes, 0, end - start);
        }

        return bytes;
    }

    public byte[] subSequence(int start) {
        return subSequence(start, this._count);
    }

    public ASCIIByteBuffer insert(int index, byte[] bytes,
            int offset,
            int len) {
        if ((index < 0) || (index > this._count)) {
            throw new StringIndexOutOfBoundsException(index);
        }

        if ((offset < 0) || (len < 0) || (offset > bytes.length - len)) {
            throw new StringIndexOutOfBoundsException(
                    "offset " + offset + ", len " + len + ", str.length "
                    + bytes.length);
        }

        synchronized (this._runtimeSync) {
            int newCount = this._count + len;
            if (newCount > this._value.length) {
                expandCapacity(newCount);
            }

            System.arraycopy(this._value, index, this._value, index + len,
                    this._count - index);
            System.arraycopy(bytes, offset, this._value, index, len);
            this._count = newCount;
        }

        return this;
    }

    public ASCIIByteBuffer insert(int index, char[] str, int offset,
            int len) {
        return insert(index, new String(str).getBytes(), offset, len);
    }

    public ASCIIByteBuffer insert(int offset, Object obj) {
        return insert(offset, String.valueOf(obj));
    }

    public ASCIIByteBuffer insert(int offset, String str) {
        return insert(offset, str.getBytes());
    }

    public ASCIIByteBuffer insert(int offset, byte[] bytes) {
        if ((offset < 0) || (offset > this._count)) {
            throw new StringIndexOutOfBoundsException(offset);
        }

        synchronized (this._runtimeSync) {
            int len = bytes.length;
            int newCount = this._count + len;
            if (newCount > this._value.length) {
                expandCapacity(newCount);
            }

            System.arraycopy(this._value, offset, this._value, offset + len,
                    this._count - offset);
            System.arraycopy(bytes, 0, this._value, offset, len);
            this._count = newCount;
        }

        return this;
    }

    public ASCIIByteBuffer insert(int offset, char[] str) {
        return insert(offset, new String(str));
    }

    public ASCIIByteBuffer insert(int dstOffset, CharSequence s,
            int start, int end) {
        if (s == null) {
            return this;
        }

        if ((dstOffset < 0) || (dstOffset > this._count)) {
            throw new IndexOutOfBoundsException("dstOffset " + dstOffset);
        }

        if ((start < 0) || (end < 0) || (start > end) || (end > s.length())) {
            throw new IndexOutOfBoundsException(
                    "start " + start + ", end " + end + ", s.length() "
                    + s.length());
        }

        synchronized (this._runtimeSync) {
            int len = end - start;
            if (len == 0) {
                return this;
            }

            int newCount = this._count + len;
            if (newCount > this._value.length) {
                expandCapacity(newCount);
            }

            System.arraycopy(this._value, dstOffset, this._value, dstOffset + len,
                    this._count - dstOffset);
            for (int i = start; i < end; i++) {
                this._value[dstOffset++] = (byte) s.charAt(i);
            }

            this._count = newCount;
        }

        return this;
    }

    public ASCIIByteBuffer insert(int dstOffset, CharSequence s) {
        if (s == null) {
            return this;
        }

        if (s instanceof String) {
            return this.insert(dstOffset, (String) s);
        }

        return this.insert(dstOffset, s, 0, s.length());
    }

    public ASCIIByteBuffer insert(int offset, boolean b) {
        return insert(offset, String.valueOf(b));
    }

    public ASCIIByteBuffer insert(int offset, byte b) {
        synchronized (this._runtimeSync) {
            int newCount = this._count + 1;
            if (newCount > this._value.length) {
                expandCapacity(newCount);
            }

            System.arraycopy(this._value, offset, this._value, offset + 1,
                    this._count - offset);
            this._value[offset] = b;
            this._count = newCount;
        }

        return this;
    }

    public ASCIIByteBuffer insert(int offset, char c) {
        synchronized (this._runtimeSync) {
            int newCount = this._count + 1;
            if (newCount > this._value.length) {
                expandCapacity(newCount);
            }

            System.arraycopy(this._value, offset, this._value, offset + 1,
                    this._count - offset);
            this._value[offset] = (byte) c;
            this._count = newCount;
        }

        return this;
    }

    public ASCIIByteBuffer insert(int offset, int i) {
        return insert(offset, Integer.toString(i));
    }

    public ASCIIByteBuffer insert(int offset, long l) {
        return insert(offset, Long.toString(l));
    }

    public ASCIIByteBuffer insert(int offset, float f) {
        return insert(offset, Float.toString(f));
    }

    public ASCIIByteBuffer insert(int offset, double d) {
        return insert(offset, Double.toString(d));
    }

    public static int indexOf(byte[] source, int sourceOffset, int sourceCount,
            byte[] target, int targetOffset, int targetCount,
            int fromIndex) {
        if (fromIndex >= sourceCount) {
            return (targetCount == 0 ? sourceCount : -1);
        }

        if (fromIndex < 0) {
            fromIndex = 0;
        }

        if (targetCount == 0) {
            return fromIndex;
        }

        byte first = target[targetOffset];
        int max = sourceOffset + (sourceCount - targetCount);

        for (int i = sourceOffset + fromIndex; i <= max; i++) {
            /* Look for first character. */
            if (source[i] != first) {
                while (++i <= max && source[i] != first);
            }

            /* Found first character, now look at the rest of v2 */
            if (i <= max) {
                int j = i + 1;
                int end = j + targetCount - 1;
                for (int k = targetOffset + 1; j < end && source[j]
                        == target[k]; j++, k++);

                if (j == end) {
                    /* Found whole string. */
                    return i - sourceOffset;
                }
            }
        }

        return -1;
    }

    public int indexOf(String str, int fromIndex) {
        return indexOf(this._value, 0, this._count,
                str.getBytes(), 0, str.length(), fromIndex);
    }

    // to-do
    public int indexOf(byte b) {
        return indexOf(this._value, 0, this._count,
                new byte[]{b}, 0, 1, 0);
    }

    // to-do
    public int indexOf(char c) {
        return indexOf(this._value, 0, this._count,
                new byte[]{(byte) c}, 0, 1, 0);
    }

    public int indexOf(String str) {
        return indexOf(str, 0);
    }

    public static int lastIndexOf(byte[] source, int sourceOffset,
            int sourceCount,
            byte[] target, int targetOffset, int targetCount,
            int fromIndex) {
        /*
         * Check arguments; return immediately where possible. For
         * consistency, don't check for null str.
         */
        int rightIndex = sourceCount - targetCount;
        if (fromIndex < 0) {
            return -1;
        }
        if (fromIndex > rightIndex) {
            fromIndex = rightIndex;
        }
        /* Empty string always matches. */
        if (targetCount == 0) {
            return fromIndex;
        }

        int strLastIndex = targetOffset + targetCount - 1;
        byte strLastChar = target[strLastIndex];
        int min = sourceOffset + targetCount - 1;
        int i = min + fromIndex;

        startSearchForLastChar:
        while (true) {
            while (i >= min && source[i] != strLastChar) {
                i--;
            }
            if (i < min) {
                return -1;
            }
            int j = i - 1;
            int start = j - (targetCount - 1);
            int k = strLastIndex - 1;

            while (j > start) {
                if (source[j--] != target[k--]) {
                    i--;
                    continue startSearchForLastChar;
                }
            }
            return start - sourceOffset + 1;
        }
    }

    public int lastIndexOf(String str, int fromIndex) {
        return lastIndexOf(this._value, 0, this._count,
                str.getBytes(), 0, str.length(), fromIndex);
    }

    public int lastIndexOf(String str) {
        return lastIndexOf(str, this._count - 1);
    }

    public byte[] reverse() {
        synchronized (this._runtimeSync) {
            int n = this._count - 1;

            for (int j = (n - 1) >> 1; j >= 0; --j) {
                byte temp = this._value[j];
                byte temp2 = this._value[n - j];

                this._value[j] = temp2;
                this._value[n - j] = temp;
            }
        }

        return this._value;
    }
}
