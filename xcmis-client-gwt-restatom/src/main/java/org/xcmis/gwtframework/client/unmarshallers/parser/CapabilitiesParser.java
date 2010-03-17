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

package org.xcmis.gwtframework.client.unmarshallers.parser;

import org.xcmis.gwtframework.client.CmisNameSpace;
import org.xcmis.gwtframework.client.model.EnumCapabilityACL;
import org.xcmis.gwtframework.client.model.EnumCapabilityChanges;
import org.xcmis.gwtframework.client.model.EnumCapabilityContentStreamUpdates;
import org.xcmis.gwtframework.client.model.EnumCapabilityJoin;
import org.xcmis.gwtframework.client.model.EnumCapabilityQuery;
import org.xcmis.gwtframework.client.model.EnumCapabilityRendition;
import org.xcmis.gwtframework.client.model.repository.CmisRepositoryCapabilitiesType;

import com.google.gwt.xml.client.Node;
import com.google.gwt.xml.client.NodeList;

/**
 * Created by The eXo Platform SAS.
 *	
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:   ${date} ${time}
 *
 */
public class CapabilitiesParser
{

   /**
    * Constructor.
    */
   protected CapabilitiesParser()
   {
      throw new UnsupportedOperationException(); // prevents calls from subclass
   }

   /**
    * Parse xml node to get {@link CmisRepositoryCapabilitiesType}.
    * 
    * @param node node
    * @return {@link CmisRepositoryCapabilitiesType}
    */
   public static CmisRepositoryCapabilitiesType parse(Node node)
   {
      NodeList capabilityNodes = node.getChildNodes();
      CmisRepositoryCapabilitiesType capabilityTypes = new CmisRepositoryCapabilitiesType();
      // Go throw all capabilities
      for (int j = 0; j < capabilityNodes.getLength(); j++)
      {
         Node capabNode = capabilityNodes.item(j);
         if (capabNode.getNodeName().equals(CmisNameSpace.CMIS_CAPABILITY_MULTIFILING))
         {
            capabilityTypes.setCapabilityMultifiling(Boolean.valueOf(capabNode.getFirstChild().getNodeValue()));
         }
         else if (capabNode.getNodeName().equals(CmisNameSpace.CMIS_CAPABILITY_UNFILING))
         {
            capabilityTypes.setCapabilityUnfiling(Boolean.valueOf(capabNode.getFirstChild().getNodeValue()));
         }
         else if (capabNode.getNodeName().equals(CmisNameSpace.CMIS_CAPABILITY_CONTENTSTREAM_UPDATABILITY))
         {
            capabilityTypes.setCapabilityContentStreamUpdatability(EnumCapabilityContentStreamUpdates
               .fromValue(capabNode.getFirstChild().getNodeValue()));
         }
         else if (capabNode.getNodeName().equals(CmisNameSpace.CMIS_CAPABILITY_RENDITIONS))
         {
            capabilityTypes.setCapabilityRenditions(EnumCapabilityRendition.fromValue(capabNode.getFirstChild()
               .getNodeValue()));
         }
         else if (capabNode.getNodeName().equals(CmisNameSpace.CMIS_CAPABILITY_GET_FOLDER_TREE))
         {
            capabilityTypes.setCapabilityGetFolderTree(Boolean.valueOf(capabNode.getFirstChild().getNodeValue()));
         }
         else if (capabNode.getNodeName().equals(CmisNameSpace.CMIS_CAPABILITY_GET_DESCENDANTS))
         {
            capabilityTypes.setCapabilityGetDescendants(Boolean.valueOf(capabNode.getFirstChild().getNodeValue()));
         }
         else if (capabNode.getNodeName().equals(CmisNameSpace.CMIS_CAPABILITY_CONTENTSTREAM_UPDATABILITY))
         {
            capabilityTypes.setCapabilityContentStreamUpdatability(EnumCapabilityContentStreamUpdates
               .fromValue(capabNode.getFirstChild().getNodeValue()));
         }
         else if (capabNode.getNodeName().equals(CmisNameSpace.CMIS_CAPABILITY_VERSION_SPECIFIC_FILING))
         {
            capabilityTypes.setCapabilityVersionSpecificFiling(Boolean
               .valueOf(capabNode.getFirstChild().getNodeValue()));
         }
         else if (capabNode.getNodeName().equals(CmisNameSpace.CMIS_CAPABILITY_PWC_UPDATEABLE))
         {
            capabilityTypes.setCapabilityPWCUpdatable(Boolean.valueOf(capabNode.getFirstChild().getNodeValue()));
         }
         else if (capabNode.getNodeName().equals(CmisNameSpace.CMIS_CAPABILITY_PWC_SEARCHABLE))
         {
            capabilityTypes.setCapabilityPWCSearchable(Boolean.valueOf(capabNode.getFirstChild().getNodeValue()));
         }
         else if (capabNode.getNodeName().equals(CmisNameSpace.CMIS_CAPABILITY_ALL_VERSION_SEARCHABLE))
         {
            capabilityTypes.setCapabilityAllVersionsSearchable(Boolean
               .valueOf(capabNode.getFirstChild().getNodeValue()));
         }
         else if (capabNode.getNodeName().equals(CmisNameSpace.CMIS_CAPABILITY_CHANGES))
         {
            capabilityTypes.setCapabilityChanges(EnumCapabilityChanges.fromValue(capabNode.getFirstChild()
               .getNodeValue()));
         }
         else if (capabNode.getNodeName().equals(CmisNameSpace.CMIS_CAPABILITY_QUERY))
         {
            if (capabNode.getFirstChild() == null)
            {
               capabilityTypes.setCapabilityQuery(EnumCapabilityQuery.NONE);
            }
            else
            {
               capabilityTypes.setCapabilityQuery(EnumCapabilityQuery.fromValue(capabNode.getFirstChild()
                  .getNodeValue()));
            }
         }
         else if (capabNode.getNodeName().equals(CmisNameSpace.CMIS_CAPABILITY_JOIN))
         {
            capabilityTypes.setCapabilityJoin(EnumCapabilityJoin.fromValue(capabNode.getFirstChild().getNodeValue()));
         }
         else if (capabNode.getNodeName().equals(CmisNameSpace.CMIS_CAPABILITY_ACL))
         {
            capabilityTypes.setCapabilityACL(EnumCapabilityACL.fromValue(capabNode.getFirstChild().getNodeValue()));
         }
      }
      return capabilityTypes;
   }
}
