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

package org.xcmis.wssoap.impl;

import org.xcmis.messaging.CmisFaultType;
import org.xcmis.messaging.EnumServiceException;
import org.xcmis.soap.CmisException;
import org.xcmis.spi.ConstraintException;
import org.xcmis.spi.ContentAlreadyExistsException;
import org.xcmis.spi.FilterNotValidException;
import org.xcmis.spi.InvalidArgumentException;
import org.xcmis.spi.NotSupportedException;
import org.xcmis.spi.ObjectNotFoundException;
import org.xcmis.spi.PermissionDeniedException;
import org.xcmis.spi.StorageException;
import org.xcmis.spi.StreamNotSupportedException;
import org.xcmis.spi.UpdateConflictException;
import org.xcmis.spi.VersioningException;

/**
 * @author <a href="mailto:max.shaposhnik@exoplatform.com">Max Shaposhnik</a>
 * @version $Id: ExceptionFactory.java 2 2010-02-04 17:21:49Z andrew00x $
 */
public class ExceptionFactory
{

   /**
    * Creates CmisException from the other types of exc-s. 
    * 
    * @param param Exception
    * @return exception CmisException
    * @throws CmisException if any exception occured
    */
   public static CmisException generateException(Exception param) throws CmisException
   {
      CmisFaultType fault = new CmisFaultType();
      String msg = param.getMessage();
      fault.setMessage(msg);

      if (param instanceof ConstraintException)
         fault.setType(EnumServiceException.CONSTRAINT);
      else if (param instanceof ContentAlreadyExistsException)
         fault.setType(EnumServiceException.CONTENT_ALREADY_EXISTS);
      else if (param instanceof FilterNotValidException)
         fault.setType(EnumServiceException.FILTER_NOT_VALID);
      else if (param instanceof InvalidArgumentException)
         fault.setType(EnumServiceException.INVALID_ARGUMENT);
      else if (param instanceof ConstraintException)
         fault.setType(EnumServiceException.NAME_CONSTRAINT_VIOLATION);
      else if (param instanceof NotSupportedException)
         fault.setType(EnumServiceException.NOT_SUPPORTED);
      else if (param instanceof ObjectNotFoundException)
         fault.setType(EnumServiceException.OBJECT_NOT_FOUND);
      else if (param instanceof PermissionDeniedException)
         fault.setType(EnumServiceException.PERMISSION_DENIED);
      else if (param instanceof StorageException)
         fault.setType(EnumServiceException.STORAGE);
      else if (param instanceof StreamNotSupportedException)
         fault.setType(EnumServiceException.STREAM_NOT_SUPPORTED);
      else if (param instanceof UpdateConflictException)
         fault.setType(EnumServiceException.UPDATE_CONFLICT);
      else if (param instanceof VersioningException)
         fault.setType(EnumServiceException.VERSIONING);
      else
         fault.setType(EnumServiceException.RUNTIME);
      return new CmisException(msg, fault, param);
   }

}
