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

package org.xcmis.spi;

/**
 * If caller try add content stream to the Document that already contains
 * content stream without specifying that to override existing content.
 * 
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: ContentAlreadyExistsException.java 2 2010-02-04 17:21:49Z andrew00x $
 */
public final class ContentAlreadyExistsException extends CmisException
{

   /**
    * Serial version UID.
    */
   private static final long serialVersionUID = 8013738281271789162L;

   /**
    * Construct instance <tt>ContentAlreadyExistsException</tt> without message.
    */
   public ContentAlreadyExistsException()
   {
      super();
   }

   /**
    * Construct instance <tt>ContentAlreadyExistsException</tt> with message.
    * 
    * @param message the detail message about exception
    * @see Throwable#getMessage()
    */
   public ContentAlreadyExistsException(String message)
   {
      super(message);
   }

   /**
    * Construct instance <tt>ContentAlreadyExistsException</tt> with message and
    * cause exception.
    * 
    * @param message the detail message about exception
    * @param cause the cause exception
    * @see Throwable#getCause()
    * @see Throwable#getMessage()
    */
   public ContentAlreadyExistsException(String message, Throwable cause)
   {
      super(message, cause);
   }

   /**
    * Construct instance <tt>ContentAlreadyExistsException</tt> with cause
    * exception.
    * 
    * @param cause the cause exception
    * @see Throwable#getCause()
    */
   public ContentAlreadyExistsException(Throwable cause)
   {
      super(cause);
   }

}
