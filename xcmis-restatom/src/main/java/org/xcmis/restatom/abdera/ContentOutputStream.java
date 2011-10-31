package org.xcmis.restatom.abdera;

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

class ContentOutputStream extends FilterOutputStream
{

   private static final int MAX_BUFFER_SIZE = 204800;

   private int outBufLength = 0;

   private boolean isFoundStart;

   private boolean isXmlWrapped;

   private boolean isFoundEnd;

   private boolean isFirstWrite = true;

   private boolean overflow;

   private boolean isPrevCycleFull;

   private ByteArrayOutputStream byteTempBufStream;

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
   public ContentOutputStream()
   {
      super(null);
      out = new ByteArrayOutputStream(MAX_BUFFER_SIZE);
      byteTempBufStream = new ByteArrayOutputStream();
   }

   @Override
   public void write(byte[] b, int off, int len) throws IOException
   {

      // if finished skip the next blocks
      if (isFoundEnd)
         return;

      // check whether xml wrapped on the first write
      if (isFirstWrite)
      {
         // run once
         isFirstWrite = false;
         // will be checked on the first iteration only
         isXmlWrapped = b[0] == 60; // is the first == '<'
      }

      if (!isXmlWrapped)
      {
         writeInternal(b, off, len);
      }
      else
      {
         // SKIP THE XML

         // finding START element
         if (!isFoundStart)
         {

            // look for the first '>'
            int indexOfStart = -1;
            for (int i = 0; i < len; i++)
            {
               if (b[i] == 62)
               {
                  indexOfStart = i;
                  break;
               }
            }

            if (indexOfStart != -1)
            {
               isFoundStart = true;
               off = indexOfStart + 1;
               len = len - off;
            }
         }
         // finding END element
         if (isFoundStart)
         {

            boolean isNotFullBlock = len < b.length;
            if (isNotFullBlock)
            {
               if (isPrevCycleFull)
               {
                  // if previous block was full
                  writeInternal(byteTempBufStream.toByteArray(), 0, -1);
                  byteTempBufStream.reset();
               }
               // set to false for skip byteTempBufStream in close()
               isPrevCycleFull = false;
               writeEnd(b, off, len);
            }
            else
            {
               // it is full block
               // middle or full last
               // maybe will be end
               if (isPrevCycleFull)
               {
                  writeInternal(byteTempBufStream.toByteArray(), 0, -1);
               }
               isPrevCycleFull = true;
               byteTempBufStream.reset();
               byteTempBufStream.write(b, off, len);
            }
         }
      }
   }

   private void writeEnd(byte[] b, int off, int len) throws IOException
   {
      // the first or the last or the one
      // let's check the end
      // get the end string no more 30 length
      int offForEnd = len < 30 ? off : off + len - 30;
      int lenForEnd = len < 30 ? len : 30;

      // look for the last '<'
      for (int indexOfEnd = offForEnd; indexOfEnd < offForEnd + lenForEnd; indexOfEnd++)
      {
         if (b[indexOfEnd] == 60)
         {
            isFoundEnd = true;
            len = indexOfEnd - off;
         }
      }
      writeInternal(b, off, len);
   }

   private void writeInternal(byte[] b, int off, int len) throws IOException
   {

      // if write previous full block, then count the 'len' from array
      if (len == -1)
         len = b.length;

      if (!overflow)
      {
         // count the bytes length, whether overflow
         outBufLength += len;
         overflow = outBufLength > MAX_BUFFER_SIZE;
      }

      if (!overflow)
      {
         // small data, use bytes
         out.write(b, off, len);
      }
      else
      {
         // large data, use file
         if (outFile == null)
         {
            // create a file
            file = File.createTempFile("cmisatom-base64-", null);
            outFile = new FileOutputStream(file);
            // write from BUF
            if (outBufLength != len)
            {
               // 'out' has bytes
               // overflow doesn't happen in the FIRST iteration
               outFile.write(((ByteArrayOutputStream)out).toByteArray());
            }
         }
         // write data in file
         outFile.write(b, off, len);
      }
   }

   @Override
   public void close() throws IOException
   {
      if (!isClosed)
      {
         isClosed = true;
         // to check BUF and write if is
         if (isPrevCycleFull)
         {
            // last block was full
            writeEnd(byteTempBufStream.toByteArray(), 0, byteTempBufStream.toByteArray().length);
         }
         if (overflow)
         {
            // was written to file, let's close it
            outFile.close();
         }
         // 'out' in the super will be closed
         super.close();
      }
   }

   public InputStream getInputStream() throws IOException
   {

      InputStream input = null;
      if (overflow)
      {
         // return file
         try
         {
            input = new ContentFileInputStream(file);
         }
         catch (FileNotFoundException e)
         {
            return null;
         }
      }
      else
      {
         // return byte array
         ByteArrayInputStream bytesInput = new ByteArrayInputStream(((ByteArrayOutputStream)out).toByteArray());
         input = new Base64InputStream(bytesInput);
      }
      return input;
   }

}
