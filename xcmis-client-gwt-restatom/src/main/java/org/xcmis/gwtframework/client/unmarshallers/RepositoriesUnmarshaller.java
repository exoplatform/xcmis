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

package org.xcmis.gwtframework.client.unmarshallers;

import org.xcmis.gwtframework.client.model.restatom.CmisRepositories;
import org.xcmis.gwtframework.client.unmarshallers.parser.RepositoriesParser;
import org.xcmis.gwtframework.client.util.Unmarshallable;

import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.XMLParser;

/**
 * Repositories unmarshaller
 * 
 * Created by The eXo Platform SAS.
 *	
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:   ${date} ${time}
 *
 */
public class RepositoriesUnmarshaller implements Unmarshallable
{
   /**
    * The list of repositories. 
    */
   private CmisRepositories repositories;

   /**
    * @param repositories CMIS service
    */
   public RepositoriesUnmarshaller(CmisRepositories repositories)
   {
      this.repositories = repositories;
   }

   /**
    * @see org.exoplatform.gwt.commons.rest.Unmarshallable#unmarshal(java.lang.String)
    * 
    * @param body body
    */
   public void unmarshal(String body)
   {
      Document doc = XMLParser.parse(body);
      repositories.setRepositories(RepositoriesParser.parse(doc));
   }

}
