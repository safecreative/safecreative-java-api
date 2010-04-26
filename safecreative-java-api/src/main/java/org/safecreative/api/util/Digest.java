/*
 Copyright (c) 2010 Safe Creative (http://www.safecreative.org)

 Permission is hereby granted, free of charge, to any person
 obtaining a copy of this software and associated documentation
 files (the "Software"), to deal in the Software without
 restriction, including without limitation the rights to use,
 copy, modify, merge, publish, distribute, sublicense, and/or sell
 copies of the Software, and to permit persons to whom the
 Software is furnished to do so, subject to the following
 conditions:

 The above copyright notice and this permission notice shall be
 included in all copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 OTHER DEALINGS IN THE SOFTWARE.
*/
package org.safecreative.api.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Digest {

    public static final String SHA1 = "SHA-1";
    public static final String MD5 = "MD5";

    public static String toHex(byte[] data) {
        StringBuilder buf = new StringBuilder();
        for (int i = 0; i < data.length; i++) {
            int halfbyte = (data[i] >>> 4) & 0x0F;
            int two_halfs = 0;
            do {
                if ((0 <= halfbyte) && (halfbyte <= 9)) {
                    buf.append((char) ('0' + halfbyte));
                } else {
                    buf.append((char) ('a' + (halfbyte - 10)));
                }
                halfbyte = data[i] & 0x0F;
            } while (two_halfs++ < 1);
        }
        return buf.toString();

    }

    /**
     * Get hex string of computed digest from imput
     * @param input
     * @param digestAlgorithm
     * @return hex string or <code>null</code> if something went wrong
     */
    public static String getHexDigest(String input, String digestAlgorithm) {
        try {
            return getHexDigest(input.getBytes("UTF-8"), digestAlgorithm);
        } catch (UnsupportedEncodingException ex) {
            return null;
        }
    }

    /**
     * Get hex string of computed digest from imput
     * @param input
     * @param digestAlgorithm
     * @return hex string or <code>null</code> if something went wrong
     */
    public static String getHexDigest(byte[] input, String digestAlgorithm) {
        byte[] result = getBytesDigest(input, digestAlgorithm);
        return toHex(result);
    }

    /**
     * Get bytes of computed digest from imput
     * @param input
     * @param digestAlgorithm
     * @return hex string or <code>null</code> if something went wrong
     */
    public static byte[] getBytesDigest(byte[] input, String digestAlgorithm) {
        byte[] result = null;
        try {
            MessageDigest digest = MessageDigest.getInstance(digestAlgorithm);
            result = digest.digest(input);
        } catch (Exception ex) {
            //TODO log
            return null;
        }
        return result;
    }

    public static byte[] getBytesDigest(File file, String digestAlgorithm) throws NoSuchAlgorithmException, IOException {
        return getBytesDigest(new FileInputStream(file), digestAlgorithm);
    }

    public static byte[] getBytesDigest(InputStream input, String digestAlgorithm) throws NoSuchAlgorithmException, IOException {
        MessageDigestBufferProcessor digestProcessor = new MessageDigestBufferProcessor(digestAlgorithm);
        return getBytesDigest(input, digestProcessor);
    }

    public static byte[] getBytesDigest(InputStream input, MessageDigestBufferProcessor digestProcessor) throws IOException {        
        try {
            IOHelper.copy(input, null, true, IOHelper.DEFAULT_BUFFER_SIZE, digestProcessor);
            return digestProcessor.getDigest();
        } finally {
            IOHelper.closeQuietly(input);
        }
    }

    public static String getHexDigest(File input, String digestAlgorithm) throws NoSuchAlgorithmException, IOException {
        return toHex(getBytesDigest(input, digestAlgorithm));
    }

    public static String getHexDigest(InputStream input, String digestAlgorithm) throws NoSuchAlgorithmException, IOException {
        return toHex(getBytesDigest(input, digestAlgorithm));
    }

    public static class MessageDigestBufferProcessor implements BufferProcessor {

        private MessageDigest digest;

        public MessageDigestBufferProcessor(String algorithm) throws NoSuchAlgorithmException {
            digest = MessageDigest.getInstance(algorithm);
        }

        public void update(byte[] input, int offset, int len) {
            digest.update(input, offset, len);
        }

        public byte[] getDigest() {
            return digest.digest();
        }
    }
}
