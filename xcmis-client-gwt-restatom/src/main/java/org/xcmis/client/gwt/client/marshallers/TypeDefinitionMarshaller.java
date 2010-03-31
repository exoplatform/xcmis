/**
 *  Copyright (C) 2010 eXo Platform SAS.
 *
 *  This is free software; you can redistribute it and/or modify it
 *  under the terms of the GNU Lesser General Public License as
 *  published by the Free Software Foundation; either version 2.1 of
 *  the License, or (at your option) any later version.
 *
 *  This software is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this software; if not, write to the Free
 *  Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 *  02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.xcmis.client.gwt.client.marshallers;

import org.xcmis.client.gwt.client.marshallers.builder.TypeXMLBuilder;
import org.xcmis.client.gwt.client.model.type.CmisTypeDefinitionType;
import org.xcmis.client.gwt.client.rest.Marshallable;

/**
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:   $
 *
 */
public class TypeDefinitionMarshaller implements Marshallable
{
   /**
    * Data for creating new type with property definitions.
    */
   private CmisTypeDefinitionType cmisTypeDefinition;
   
   /**
    * @param cmisTypeDefinition cmisTypeDefinition
    */
   public TypeDefinitionMarshaller(CmisTypeDefinitionType cmisTypeDefinition)
   {
      this.cmisTypeDefinition = cmisTypeDefinition;
   }
   
   /**
    * @see org.exoplatform.gwt.commons.rest.Marshallable#marshal()
    * @return String
    */
   public String marshal()
   {
      return TypeXMLBuilder.createType(cmisTypeDefinition);
   }

}
