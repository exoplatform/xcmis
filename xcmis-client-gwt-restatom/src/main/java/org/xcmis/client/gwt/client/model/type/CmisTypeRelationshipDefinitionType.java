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

package org.xcmis.client.gwt.client.model.type;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by The eXo Platform SAS.
 *	
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:   ${date} ${time}
 *
 */
public class CmisTypeRelationshipDefinitionType extends CmisTypeDefinitionType
{

   /**
    * Allowed source types.
    */
   protected List<String> allowedSourceTypes;

   /**
    * Allowed target types.
    */
   protected List<String> allowedTargetTypes;

   /**
   * @return List containing {@link String}
   */
   public List<String> getAllowedSourceTypes()
   {
      if (allowedSourceTypes == null)
      {
         allowedSourceTypes = new ArrayList<String>();
      }
      return this.allowedSourceTypes;
   }

   /**
   * @return List containing {@link String}
   */
   public List<String> getAllowedTargetTypes()
   {
      if (allowedTargetTypes == null)
      {
         allowedTargetTypes = new ArrayList<String>();
      }
      return this.allowedTargetTypes;
   }

}
