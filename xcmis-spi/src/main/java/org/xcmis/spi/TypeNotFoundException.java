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
 * May be thrown if requested type not found in repository.
 * 
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: TypeNotFoundException.java 2 2010-02-04 17:21:49Z andrew00x $
 */
public final class TypeNotFoundException extends CmisRuntimeException
{

   /**
    * Serial version UID.
    */
   private static final long serialVersionUID = -1469958876745748865L;

   /**
    * Construct instance <tt>TypeNotFoundException</tt> without message.
    */
   public TypeNotFoundException()
   {
      super();
   }

   /**
    * Construct instance <tt>TypeNotFoundException</tt> with message.
    * 
    * @param message the detail message about exception
    * @see Throwable#getMessage()
    */
   public TypeNotFoundException(String message)
   {
      super(message);
   }

}
