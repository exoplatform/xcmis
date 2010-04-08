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

package org.xcmis.client.gwt.client.model.restatom;

import org.xcmis.client.gwt.client.model.type.TypeDefinition;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by The eXo Platform SAS.
 *	
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:   ${date} ${time}
 *
 */
public class TypeList
{
   /**
    * Types.
    */
   private List<TypeDefinition> types;

   /**
    * @return List containing {@link CmisTypeDefinitionType}
    */
   public List<TypeDefinition> getTypes()
   {
      if (types == null)
      {
         types = new ArrayList<TypeDefinition>();
      }
      return types;
   }

   /**
    * @param types types
    */
   public void setTypes(List<TypeDefinition> types)
   {
      this.types = types;
   }

}
