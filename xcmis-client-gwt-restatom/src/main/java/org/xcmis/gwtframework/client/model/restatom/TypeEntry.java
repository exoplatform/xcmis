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

package org.xcmis.gwtframework.client.model.restatom;

import org.xcmis.gwtframework.client.model.type.CmisTypeDefinitionType;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:   $
 *
 */
public class TypeEntry
{
   /**
    * Type CMIS type definition.
    */
   CmisTypeDefinitionType typeCmisTypeDefinition;

   /**
    * Links.
    */
   List<AtomLink> links;

   /**
    * Children.
    */
   List<TypeEntry> children;

   /**
    * @return {@link CmisTypeDefinitionType}
    */
   public CmisTypeDefinitionType getTypeCmisTypeDefinition()
   {
      return typeCmisTypeDefinition;
   }

   /**
    * @param typeCmisTypeDefinition typeCmisTypeDefinition
    */
   public void setTypeCmisTypeDefinition(CmisTypeDefinitionType typeCmisTypeDefinition)
   {
      this.typeCmisTypeDefinition = typeCmisTypeDefinition;
   }

   /**
    * @return List containing {@link AtomLink}
    */
   public List<AtomLink> getLinks()
   {
      if (links == null)
      {
         links = new ArrayList<AtomLink>();
      }
      return links;
   }

   /**
    * @param links links
    */
   public void setLinks(List<AtomLink> links)
   {
      this.links = links;
   }

   /**
    * @return List containing {@link TypeEntry}
    */
   public List<TypeEntry> getChildren()
   {
      if (children == null)
      {
         children = new ArrayList<TypeEntry>();
      }
      return children;
   }

   /**
    * @param children children
    */
   public void setChildren(List<TypeEntry> children)
   {
      this.children = children;
   }

   /**
    * @param relation relation
    * @return String
    */
   public String getHref(EnumLinkRelation relation)
   {
      for (int i = 0; i < links.size(); i++)
      {
         if (links.get(i).getRelation().equals(relation))
         {
            return links.get(i).getHref();
         }
      }
      return null;
   }

   /**
    * @param relation relation
    * @param type type
    * @return String
    */
   public String getHref(EnumLinkRelation relation, String type)
   {
      for (int i = 0; i < links.size(); i++)
      {
         if (links.get(i).getRelation().equals(relation) && (links.get(i).getType().equals(type)))
         {
            return links.get(i).getHref();
         }
      }
      return null;
   }
}
