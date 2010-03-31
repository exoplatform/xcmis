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

package org.xcmis.client.gwt.client.unmarshallers.parser;

import org.xcmis.client.gwt.client.CmisNameSpace;
import org.xcmis.client.gwt.client.model.repository.CmisRepositoryInfo;

import com.google.gwt.xml.client.Node;
import com.google.gwt.xml.client.NodeList;

/**
 * Created by The eXo Platform SAS.
 *	
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:   ${date} ${time}
 *
 */
public class RepositoryInfoParser
{
   
   /**
    * Constructor.
    */
   protected RepositoryInfoParser()
   {
      throw new UnsupportedOperationException(); // prevents calls from subclass
   }
   
   /**
    * @param node node
    * @param repositoryInfo repository info
    */
   public static void parse(Node node, CmisRepositoryInfo repositoryInfo)
   {
      //Check whether it is repository information node, if not - return
      if (!node.getNodeName().equals(CmisNameSpace.WORKSPACE))
      {
         return;
      }

      NodeList items = node.getChildNodes();
      Node repositoryInfoNode = null;
      for (int i = 0; i < items.getLength(); i++)
      {
         if (items.item(i).getNodeName().equals(CmisNameSpace.CMIS_REPOSITORY_INFO))
         {
            repositoryInfoNode = items.item(i);
         }
      }
      if ((repositoryInfoNode != null) && (repositoryInfoNode.getChildNodes().getLength() > 0))
      {
         NodeList repositoryInfoList = repositoryInfoNode.getChildNodes();
         for (int i = 0; i < repositoryInfoList.getLength(); i++)
         {
            Node item = repositoryInfoList.item(i);
            if (item.getNodeName().equals(CmisNameSpace.CMIS_REPOSITORY_ID))
            {
               repositoryInfo.setRepositoryId(item.getFirstChild().getNodeValue());
            }
            else if (item.getNodeName().equals(CmisNameSpace.CMIS_REPOSITORY_NAME))
            {
               repositoryInfo.setRepositoryName(item.getFirstChild().getNodeValue());
            }

            else if (item.getNodeName().equals(CmisNameSpace.CMIS_THIN_CLIENT_URI))
            {
               if (item.getFirstChild() != null)
               {
                  repositoryInfo.setThinClientURI(item.getFirstChild().getNodeValue());
               }
            }
            else if (item.getNodeName().equals(CmisNameSpace.CMIS_PRINCIPAL_ANYONE))
            {
               if (item.getFirstChild() != null)
               {
                  repositoryInfo.setPrincipalAnyone(item.getFirstChild().getNodeValue());
               }
            }
            else if (item.getNodeName().equals(CmisNameSpace.CMIS_PRINCIPAL_ANONYMOUS))
            {
               if (item.getFirstChild() != null)
               {
                  repositoryInfo.setPrincipalAnonymous(item.getFirstChild().getNodeValue());
               }
            }

            else if (item.getNodeName().equals(CmisNameSpace.CMIS_LATEST_CHANGE_TOKEN))
            {
               if (item.getFirstChild() != null)
               {
                  repositoryInfo.setLatestChangeLogToken(item.getFirstChild().getNodeValue());
               }
            }

            else if (item.getNodeName().equals(CmisNameSpace.CMIS_REPOSITORY_DESCRIPTION))
            {
               if (item.getFirstChild() != null)
               {
                  repositoryInfo.setRepositoryDescription(item.getFirstChild().getNodeValue());
               }
            }
            else if (item.getNodeName().equals(CmisNameSpace.CMIS_VENDOR_NAME))
            {
               repositoryInfo.setVendorName(item.getFirstChild().getNodeValue());
            }
            else if (item.getNodeName().equals(CmisNameSpace.CMIS_PRODUCT_NAME))
            {
               repositoryInfo.setProductName(item.getFirstChild().getNodeValue());
            }
            else if (item.getNodeName().equals(CmisNameSpace.CMIS_PRODUCT_VERSION))
            {
               repositoryInfo.setProductVersion(item.getFirstChild().getNodeValue());
            }
            else if (item.getNodeName().equals(CmisNameSpace.CMIS_ROOT_FOLDER_ID))
            {
               repositoryInfo.setRootFolderId(item.getFirstChild().getNodeValue());
            }
            else if (item.getNodeName().equals(CmisNameSpace.CMIS_CAPABILITIES))
            {
               repositoryInfo.setCapabilities(CapabilitiesParser.parse(item));
            }
            else if (item.getNodeName().equals(CmisNameSpace.CMIS_VERSION_SUPPORTED))
            {
               repositoryInfo.setCmisVersionSupported(item.getFirstChild().getNodeValue());
            }
            else
            {
               repositoryInfo.getAny().add(item);
            }
         }
      }
   }
}
