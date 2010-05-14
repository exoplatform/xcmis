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

import org.xcmis.client.gwt.client.CMIS;
import org.xcmis.client.gwt.client.model.repository.CmisRepositoryInfo;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.Node;
import com.google.gwt.xml.client.NodeList;

/**
 * Created by The eXo Platform SAS.
 *	
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:   ${date} ${time}
 *
 */
public class RepositoriesParser
{
   
   /**
    * Constructor.
    */
   protected RepositoriesParser()
   {
      throw new UnsupportedOperationException(); // prevents calls from subclass
   }
   
   /**
    * Get list of available repositories for CMIS service endpoint. Info about
    * each repository contains at least following properties: repository ID,
    * repository name and repository URI.
    * 
    * @param response the XML Document
    * @return List containing {@link CmisRepositoryInfoType}
    */
   public static List<CmisRepositoryInfo> parse(Document response)
   {
      List<CmisRepositoryInfo> repositoryInfoList = new ArrayList<CmisRepositoryInfo>();
      NodeList nodeList = response.getElementsByTagName(CMIS.WORKSPACE);
      if (nodeList != null && nodeList.getLength() > 0)
      {
         for (int i = 0; i < nodeList.getLength(); i++)
         {
            Node node = nodeList.item(i);
            CmisRepositoryInfo repositoryInfo = new CmisRepositoryInfo();
            RepositoryInfoParser.parse(node, repositoryInfo);
            repositoryInfoList.add(repositoryInfo);
            repositoryInfo.setCollections(CollectionsParser.parse(node));
         }
      }
      return repositoryInfoList;
   }
}
