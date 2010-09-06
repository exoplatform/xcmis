/**
 * Copyright (C) 2010 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.xcmis.renditions.impl;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.xcmis.spi.ContentStream;
import org.xcmis.spi.RenditionContentStream;
import org.xcmis.spi.RenditionProvider;
import org.xcmis.spi.utils.MimeType;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: PDFDocumentRenditionProvider.java 774 2010-04-16 11:56:20Z
 *          ur3cma $
 */
public class PDFDocumentRenditionProvider implements RenditionProvider
{

   private static final MimeType[] SUPPORTED_MEDIA_TYPES = new MimeType[]{new MimeType("application", "pdf")};

   private static final MimeType PRODUCED = new MimeType("image", "png");

   // TODO configurable maxHeigth & maxWidth
   /** The max height. */
   private int maxHeight = 100;

   /** The max width. */
   private int maxWidth = 100;

   /**
    * {@inheritDoc}
    */
   public int getHeight()
   {
      return -1;
   }

   /**
    * {@inheritDoc}
    */
   public String getKind()
   {
      return "cmis:thumbnail";
   }

   /**
    * {@inheritDoc}
    */
   public MimeType getProducedMediaType()
   {
      return PRODUCED;
   }

   /**
    * {@inheritDoc}
    */
   public RenditionContentStream getRenditionStream(ContentStream stream) throws IOException
   {
      PDDocument pdf = null;
      try
      {
         pdf = PDDocument.load(stream.getStream());
         PDPage page = (PDPage)pdf.getDocumentCatalog().getAllPages().get(0);
         BufferedImage image = page.convertToImage();
         // Determine scale and be sure both width and height are not greater the max
         int scale =
            (int)Math.max(Math.floor((image.getHeight() / maxHeight) + 1.0d), Math
               .floor((image.getWidth() / maxWidth) + 1.0d));
         int height = image.getHeight() / scale;
         int width = image.getWidth() / scale;
         BufferedImage scaledImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
         Graphics2D graphics2D = scaledImage.createGraphics();
         graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
         graphics2D.drawImage(image, 0, 0, width, height, null);
         graphics2D.dispose();

         ByteArrayOutputStream out = new ByteArrayOutputStream();
         ImageIO.write(scaledImage, "png", out);
         RenditionContentStream renditionStream =
            new RenditionContentStream(out.toByteArray(), null, new MimeType("image", " png"), getKind(), height, width);
         return renditionStream;
      }
      finally
      {
         if (pdf != null)
         {
            pdf.close();
         }
      }
   }

   /**
    * {@inheritDoc}
    */
   public MimeType[] getSupportedMediaType()
   {
      return SUPPORTED_MEDIA_TYPES;
   }

   /**
    * {@inheritDoc}
    */
   public int getWidth()
   {
      return -1;
   }
}
