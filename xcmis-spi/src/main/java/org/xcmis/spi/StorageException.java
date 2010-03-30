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
 * If the storage can't save data that user creating or updating.
 * 
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: StorageException.java 2 2010-02-04 17:21:49Z andrew00x $
 */
public class StorageException extends CmisRuntimeException
{

   /**
    * Serial version UID.
    */
   private static final long serialVersionUID = -40767473455051310L;

   /**
    * Construct instance <tt>StorageException</tt> without message.
    */
   public StorageException()
   {
   }

   /**
    * Construct instance <tt>StorageException</tt> with message.
    * 
    * @param message the detail message about exception
    * @see Throwable#getMessage()
    */
   public StorageException(String message)
   {
      super(message);
   }

   /**
    * Construct instance <tt>StorageException</tt> with message and cause
    * exception.
    * 
    * @param message the detail message about exception
    * @param cause the cause exception
    * @see Throwable#getCause()
    * @see Throwable#getMessage()
    */
   public StorageException(String message, Throwable cause)
   {
      super(message, cause);
   }

   /**
    * Construct instance <tt>StorageException</tt> with cause exception.
    * 
    * @param cause the cause exception
    * @see Throwable#getCause()
    */
   public StorageException(Throwable cause)
   {
      super(cause);
   }

}
