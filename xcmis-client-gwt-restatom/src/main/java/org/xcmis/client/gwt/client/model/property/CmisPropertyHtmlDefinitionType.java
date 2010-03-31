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

package org.xcmis.client.gwt.client.model.property;

import org.xcmis.client.gwt.client.model.choice.CmisChoiceHtml;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 
 * @version $Id:
 *
 */
public class CmisPropertyHtmlDefinitionType extends CmisPropertyDefinitionType
{

   /**
    * Default value.
    */
   protected CmisPropertyHtml defaultValue;

   /**
    * Choice.
    */
   protected List<CmisChoiceHtml> choice;

   /**
    * @return {@link CmisPropertyHtml}
    */
   public CmisPropertyHtml getDefaultValue()
   {
      return defaultValue;
   }

   /**
    * @param value value
    */
   public void setDefaultValue(CmisPropertyHtml value)
   {
      this.defaultValue = value;
   }

   /**
    * @return List containing {@link CmisChoiceHtml}
    */
   public List<CmisChoiceHtml> getChoice()
   {
      if (choice == null)
      {
         choice = new ArrayList<CmisChoiceHtml>();
      }
      return this.choice;
   }

}
