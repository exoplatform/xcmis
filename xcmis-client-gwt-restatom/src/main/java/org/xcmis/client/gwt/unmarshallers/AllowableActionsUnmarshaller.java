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

package org.xcmis.client.gwt.unmarshallers;

import org.xcmis.client.gwt.CMIS;
import org.xcmis.client.gwt.model.AllowableActions;
import org.xcmis.client.gwt.rest.Unmarshallable;
import org.xcmis.client.gwt.rest.UnmarshallerException;
import org.xcmis.client.gwt.unmarshallers.parser.AllowableActionsParser;

import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.NodeList;
import com.google.gwt.xml.client.XMLParser;

/**
 * Created by The eXo Platform SAS.
 *	
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:   ${date} ${time}
 *
 */
public class AllowableActionsUnmarshaller implements Unmarshallable
{

   /**
    * Response for CmisAllowableActionsType.
    */
   private AllowableActions allowableActions;

   /**
    * @param allowableActions allowable actions
    */
   public AllowableActionsUnmarshaller(AllowableActions allowableActions)
   {
      this.allowableActions = allowableActions;
   }

   /**
    * @see org.exoplatform.gwtframework.commons.rest.Unmarshallable#unmarshal(java.lang.String)
    * 
    * @param body body
    * @throws UnmarshallerException 
    */
   public void unmarshal(String body) throws UnmarshallerException
   {
      if (body != null && body.length() > 0)
      {
         try
         {
            Document doc = XMLParser.parse(body);
            NodeList nodeList = doc.getElementsByTagName(CMIS.ALLOWABLE_ACTIONS);
            if (nodeList != null && nodeList.getLength() > 0)
            {
               AllowableActionsParser.parse(nodeList.item(0), allowableActions);
            }
         }
         catch (Exception e)
         {
            throw new UnmarshallerException("Unable to parse allowbale actions response.");
         }
      }
   }

}
