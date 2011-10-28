package org.xcmis.restatom;

import org.apache.commons.codec.binary.Base64InputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class ContentOutputStream extends FilterOutputStream {

   private static final int MAX_BUFFER_SIZE = 204800;

   private int outBufLength = 0;

   private boolean isFoundStart;
   private boolean isXmlWrapped;
   private boolean isFoundEnd;
   private boolean isFirstWrite = true;
   private boolean overflow;

   private byte[] byteTempBuf;

   private File file;
   private OutputStream outFile;

   private boolean isClosed;

   /**
    * 
    * Uses for Abdera. Will be written base64 encoded data.
    * 
    * To write data like:
    * <cmisra:base64 xmlns:cmisra="http://docs.oasis-open.org/ns/cmis/restatom/200908/">aGVsbG8=</cmisra:base64>
    * In this example the 'aGVsbG8=' is a 'hello' word.
    * 
    * @throws FileNotFoundException
    */
   public ContentOutputStream() {
      super(null);
      out = new ByteArrayOutputStream(MAX_BUFFER_SIZE);
   }

   @Override
   public void write(byte[] b, int off, int len) throws IOException {

      // if finished skip the next blocks
      if (isFoundEnd)
         return;

      // check whether xml wrapped on the first write
      if (isFirstWrite) {
         // run once
         isFirstWrite = false;
         // will be checked on the first iteration only
         isXmlWrapped = b[0]=="<".getBytes()[0];
      }

      if (!isXmlWrapped) {
         writeInternal(b, off, len);
      } else {
         // SKIP THE XML

         // finding START element
         if (!isFoundStart) {
            String original = new String(b, off, len < 130 ? len : 130);

            int indexOfStart = original.indexOf(">");

            if (indexOfStart != -1) {
               isFoundStart = true;
               off = indexOfStart + 1;
               len = len - off;
            }
         }
         // finding END element
         if (isFoundStart) {

            boolean isNotFullBlock = len < b.length;
            if (isNotFullBlock) {
               // the first or the last or the one
               // let's check the end
               // get the end string no more 30 length
               int offForEnd = len < 30 ? off : off + len - 30;
               int lenForEnd = len < 30 ? len : 30;

               String lastBlock = new String(b, offForEnd, lenForEnd);
               int indexOfEnd = lastBlock.lastIndexOf("<");

               if (indexOfEnd != -1) {
                  // has the end
                  isFoundEnd = true;
                  len = offForEnd + indexOfEnd - off;
                  // to check the correctness
               }
               if (byteTempBuf != null && byteTempBuf.length > 0) {
                  // if previous block was full
                  writeInternal(byteTempBuf, 0, byteTempBuf.length);
                  byteTempBuf = null;
               }
               writeInternal(b, off, len);
            } else {
               // it is full block
               // middle or full last
               // maybe will be end
               if (byteTempBuf != null && byteTempBuf.length > 0) {
                  writeInternal(byteTempBuf, 0, byteTempBuf.length);
               }
               byteTempBuf = new byte[len];
               System.arraycopy(b, off, byteTempBuf, 0, len);
            }
         }
      }
   }

   private void writeInternal(byte[] b, int off, int len) throws IOException {

      if (!overflow) {
         // count the bytes length, whether overflow
         outBufLength += len;
         overflow = outBufLength > MAX_BUFFER_SIZE;
      }

      if (!overflow) {
         // small data, use bytes
         out.write(b, off, len);
      } else {
         // large data, use file
         if (outFile == null) {
            // create a file
            file = File.createTempFile("cmisatom-base64-", null);
            outFile = new FileOutputStream(file);
            // write from BUF
            if (outBufLength != len) {
               // 'out' has bytes
               // overflow doesn't happen in the FIRST iteration
               outFile.write(((ByteArrayOutputStream) out).toByteArray());
            }
         }
         // write data in file
         outFile.write(b, off, len);
      }
   }

   @Override
   public void close() throws IOException {

      if (!isClosed) {
         isClosed = true;
         // to check BUF and write if is

         if (byteTempBuf != null && byteTempBuf.length > 0) {
            // last block was full
            int len = byteTempBuf.length;
            int off = 0;
            // let's check the end!
            // get the end of the string no more 30 length
            int offForEnd = len < 30 ? 0 : len - 30;
            int lenForEnd = len < 30 ? len : 30;
            String lastBlock = new String(byteTempBuf, offForEnd, lenForEnd);

            int indexOfEnd = lastBlock.lastIndexOf("<", off);
            if (indexOfEnd != -1) {
               isFoundEnd = true;
               // skip the end of xml
               len = offForEnd + indexOfEnd - off;
            }
            writeInternal(byteTempBuf, off, len);
         }

         if (overflow) {
            // was written to file
            outFile.close();
         }
         // 'out' in the super will be closed
         super.close();
      }
   }

   public InputStream getInputStream() throws IOException {

      InputStream input = null;
      if (overflow) {
         // return file
         try {
            input = new ContentFileInputStream(file);
         } catch (FileNotFoundException e) {
            return null;
         }
      } else {
         // return byte array
         ByteArrayInputStream bytesInput = new ByteArrayInputStream(
               ((ByteArrayOutputStream) out).toByteArray());
         input = new Base64InputStream(bytesInput);
      }
      return input;
   }

}
