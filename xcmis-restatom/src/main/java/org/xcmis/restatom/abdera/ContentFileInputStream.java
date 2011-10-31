package org.xcmis.restatom.abdera;

import org.apache.commons.codec.binary.Base64InputStream;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilterInputStream;
import java.io.IOException;

class ContentFileInputStream extends FilterInputStream
{

   private File file;

   private boolean deleted;

   public ContentFileInputStream(File file) throws FileNotFoundException
   {
      super(new Base64InputStream(new FileInputStream(file)));
      this.file = file;
   }

   @Override
   public void close() throws IOException
   {
      try
      {
         super.close();
      }
      finally
      {
         if (!deleted)
         {
            deleted = file.delete();
         }
      }
   }

}
