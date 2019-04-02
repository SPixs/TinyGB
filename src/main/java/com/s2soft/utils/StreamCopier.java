package com.s2soft.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;

/**
 * utilty for copy of InputStream to outputStream <BR>
 * The copy is bufferised with a default buffesize of 2048. <BR>
 * (may be useful in unit_tst)
 */
public class StreamCopier {

	//   ============================ Constants ==============================

	//	 =========================== Attributes ==============================

	//	 =========================== Constructor =============================

	//	 ========================== Access methods ===========================

	//	 ========================= Treatment methods =========================
    
    public static int copy(InputStream in,
                           OutputStream out)
        throws IOException
    {
        return StreamCopier.copy(in, out, 2048);
    }
    
    /**
     * Copy stream <CODE>in</CODE> to stream <CODE>out</CODE><BR>
     * The copy is done by bufferisation of size 2048 bytes
     * <B> DO NOT close the stream in</B><BR>
     * <B>flush the stream out, DO NOT close it</B><BR>
     * 
     * @return the number of bytes written
     */
    public static int copy(InputStream in,
                           OutputStream out,
                           int bufferSize)
        throws IOException
    {
        byte buffer[] = new byte[bufferSize];
        int iLen  = in.read(buffer, 0, buffer.length);
        int read = 0;
        while(iLen != -1) {
            read += iLen;
           out.write(buffer, 0, iLen);
            iLen = in.read(buffer, 0, buffer.length);
        }
        out.flush();
        return read;
    }
    
    public static int copy(Reader in, Writer out) throws IOException {
    	return copy(in, out, 2048);
    }
    
    public static int copy(Reader in, Writer out, int bufferSize) throws IOException {
    	char buffer[] = new char[bufferSize];
        int iLen  = in.read(buffer, 0, buffer.length);
        int read = 0;
        while(iLen != -1) {
            read += iLen;
           out.write(buffer, 0, iLen);
            iLen = in.read(buffer, 0, buffer.length);
        }
        out.flush();
        return read;
    }
    
     
}