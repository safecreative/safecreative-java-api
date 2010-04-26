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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * IO helper methods
 *
 * @author mpolo@safecreative.org
 *
 */
public final class IOHelper {

    public final static int DEFAULT_BUFFER_SIZE = 8 * 1024;
    public final static String DEFAULT_CHARSET = "UTF-8";
    private static Logger log = LoggerFactory.getLogger(IOHelper.class);

    public static String readString(InputStream in) throws IOException {
        return readString(in, DEFAULT_CHARSET);
    }

    public static String readString(InputStream in, String charsetName)
            throws IOException {
        return readString(in, charsetName, Long.MAX_VALUE);
    }

    public static String readString(InputStream in, String charsetName, long maxCopy)
            throws IOException {
        return readString(new InputStreamReader(in, charsetName), maxCopy);
    }

    public static String readString(Reader in) throws IOException {
        return readStringBuffer(in).toString();
    }

    public static String readString(Reader in, long maxCopy) throws IOException {
        return readStringBuffer(in, maxCopy).toString();
    }

    public static StringBuffer readStringBuffer(Reader in) throws IOException {
        return readStringBuffer(in, Long.MAX_VALUE);
    }

    public static StringBuffer readStringBuffer(Reader in, long maxCopy) throws IOException {
        StringWriter stringWriter = new StringWriter();
        copy(in, stringWriter, true, DEFAULT_BUFFER_SIZE, maxCopy);
        return stringWriter.getBuffer();
    }

    public static long copy(Reader in, Writer out) throws IOException {
        return copy(in, out, true);
    }

    public static long copy(Reader in, Writer out, boolean closeOutput) throws IOException {
        return copy(in, out, closeOutput, DEFAULT_BUFFER_SIZE, Long.MAX_VALUE);
    }

    public static long copy(Reader in, Writer out, boolean closeOutput, int buffsize, long maxCopy)
            throws IOException {
        long numWritten = 0;
        try {
            char[] buffer = new char[buffsize];
            int numRead;
            while ((numRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, numRead);
                numWritten += numRead;
                if (numWritten > maxCopy) {
                    break;
                }
            }
        } finally {
            closeQuietly(in);
            if (closeOutput) {
                closeQuietly(out);
            }
        }
        return numWritten;
    }

    public static long copy(File in, File out) throws FileNotFoundException,
            IOException {
        return copy(new FileInputStream(in), new FileOutputStream(out), true);
    }

    public static long copy(InputStream in, OutputStream out)
            throws IOException {
        return copy(in, out, true);
    }

    public static long copy(InputStream in, OutputStream out,
            boolean closeOutput) throws IOException {
        return copy(in, out, closeOutput, DEFAULT_BUFFER_SIZE, null);
    }

    public static long copy(InputStream in, OutputStream out,
            boolean closeOutput, int buffsize) throws IOException {
        return copy(in, out, closeOutput, buffsize, null);
    }

    public static long copy(InputStream in, OutputStream out,
            boolean closeOutput, int buffsize, BufferProcessor bufferProcessor) throws IOException {
        return copy(in, out, closeOutput, buffsize, bufferProcessor, Long.MAX_VALUE);
    }

    public static long copy(InputStream in, OutputStream out,
            boolean closeOutput, int buffsize, BufferProcessor bufferProcessor, long maxCopy) throws IOException {
        long numWritten = 0;
        try {
            byte[] buffer = new byte[buffsize];
            int numRead;
            while ((numRead = in.read(buffer)) != -1) {
                if (bufferProcessor != null) {
                    bufferProcessor.update(buffer, 0, numRead);
                }
                if (out != null) {
                    out.write(buffer, 0, numRead);
                }
                numWritten += numRead;
                if (numWritten > maxCopy) {
                    break;
                }
            }

        } finally {
            closeQuietly(in);
            if (closeOutput) {
                closeQuietly(out);
            }
        }
        return numWritten;
    }

    public static void writeString(String s, OutputStream os)
            throws IOException {
        copy(new ByteArrayInputStream(s.getBytes()), os);

    }

    public static String readString(File file) throws IOException {
        InputStream is = new FileInputStream(file);
        try {
            return readString(is);
        } finally {
            closeQuietly(is);
        }

    }

    public static void writeString(String data, File file) throws IOException {
        OutputStream os = null;
        try {
            os = new FileOutputStream(file);
            writeString(data, os);
        } finally {
            closeQuietly(os);
        }

    }

    public static void closeQuietly(Closeable closeable) {
        if (closeable == null) {
            return;
        }
        try {
            closeable.close();
        } catch (IOException e) {
            //TODO:log detailed
            log.warn("close quietly (no too much, review it please) {} IOException: {}", closeable, e);
        }
    }

    public static byte[] readBytes(File file) throws IOException {
        InputStream in = null;
        ByteArrayOutputStream out = null;
        try {
            in = new FileInputStream(file);
            out = new ByteArrayOutputStream((int) file.length());
            copy(in, out);
            return out.toByteArray();
        } finally {
            closeQuietly(in);
            closeQuietly(out);
        }
    }

    public static void deleteSafely(File file) {
        if (file == null) {
            return;
        }
        boolean deleted = false;
        try {
            deleted = file.delete();
        } catch (Throwable t) {
            log.debug("Failed deleting file {} due to {}", file, t);
        } finally {
            if (!deleted) {
                try {
                    file.deleteOnExit();
                } catch (Throwable t) {
                    log.warn("Failed deleting file {} on exit due to {}", file, t);
                }
            }
        }
    }
}
