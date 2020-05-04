package edu.southwestern.util.file;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

/**
 * From: https://stackoverflow.com/questions/4799006/in-java-how-can-i-redirect-system-out-to-null-then-back-to-stdout-again
 * 
 * Point is to redirect output to nowhere
 * 
 * @author schrum2
 *
 */
public class NullPrintStream extends PrintStream {

  public NullPrintStream() {
    super(new NullByteArrayOutputStream());
  }

  private static class NullByteArrayOutputStream extends ByteArrayOutputStream {

    @Override
    public void write(int b) {
      // do nothing
    }

    @Override
    public void write(byte[] b, int off, int len) {
      // do nothing
    }

    @Override
    public void writeTo(OutputStream out) throws IOException {
      // do nothing
    }

  }

}