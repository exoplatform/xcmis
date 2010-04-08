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
import org.xcmis.client.gwt.client.model.AllowableActions;

import com.google.gwt.xml.client.Node;
import com.google.gwt.xml.client.NodeList;

/**
 * Created by The eXo Platform SAS.
 *	
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:   ${date} ${time}
 *
 */
public class AllowableActionsParser
{
   /**
    * Constructor.
    */
   protected AllowableActionsParser()
   {
      throw new UnsupportedOperationException(); // prevents calls from subclass
   }
   
   /**
    * Parse xml document to CmisAllowableActionsType.
    * 
    * @param response response
    * @param allowableActions allowable actions
    */
   public static void parse(Node response, AllowableActions allowableActions)
   {
      // Getting all allowable actions' nodes in nodeList
      NodeList nodeList = response.getChildNodes();
      for (int i = 0; i < nodeList.getLength(); i++)
      {
         Node item = nodeList.item(i);
         // Property boolean value (true/false)
         Boolean propertyValue = Boolean.parseBoolean(item.getFirstChild().getNodeValue());
         // Property name
         String propertyName = item.getNodeName();
         if (propertyName.equals(CMIS.CMIS_CAN_DELETE_OBJECT))
         {
            allowableActions.setCanDeleteObject(propertyValue);
         }
         else if (propertyName.equals(CMIS.CMIS_CAN_UPDATE_PROPERTIES))
         {
            allowableActions.setCanUpdateProperties(propertyValue);
         }
         else if (propertyName.equals(CMIS.CMIS_CAN_GET_PROPERTIES))
         {
            allowableActions.setCanGetProperties(propertyValue);
         }
         else if (propertyName.equals(CMIS.CMIS_CAN_GET_OBJECT_RELATIONSHIPS))
         {
            allowableActions.setCanGetObjectRelationships(propertyValue);
         }
         else if (propertyName.equals(CMIS.CMIS_CAN_GET_OBJECT_PARENTS))
         {
            allowableActions.setCanGetObjectParents(propertyValue);
         }
         else if (propertyName.equals(CMIS.CMIS_CAN_MOVE_OBJECT))
         {
            allowableActions.setCanMoveObject(propertyValue);
         }
         else if (propertyName.equals(CMIS.CMIS_CAN_DELETE_CONTENT_STREAM))
         {
            allowableActions.setCanDeleteContentStream(propertyValue);
         }
         else if (propertyName.equals(CMIS.CMIS_CAN_CHECK_IN))
         {
            allowableActions.setCanCheckIn(propertyValue);
         }
         else if (propertyName.equals(CMIS.CMIS_CAN_CHECK_OUT))
         {
            allowableActions.setCanCheckOut(propertyValue);
         }
         else if (propertyName.equals(CMIS.CMIS_CAN_CANCEL_CHECK_OUT))
         {
            allowableActions.setCanCancelCheckOut(propertyValue);
         }
         else if (propertyName.equals(CMIS.CMIS_CAN_APPLY_POLICY))
         {
            allowableActions.setCanApplyPolicy(propertyValue);
         }
         else if (propertyName.equals(CMIS.CMIS_CAN_REMOVE_POLICY))
         {
            allowableActions.setCanRemovePolicy(propertyValue);
         }
         else if (propertyName.equals(CMIS.CMIS_CAN_CREATE_DOCUMENT))
         {
            allowableActions.setCanCreateDocument(propertyValue);
         }
         else if (propertyName.equals(CMIS.CMIS_CAN_GET_APPLIED_POLICIES))
         {
            allowableActions.setCanGetAppliedPolicies(propertyValue);
         }
         else if (propertyName.equals(CMIS.CMIS_CAN_GET_CONTENT_STREAM))
         {
            allowableActions.setCanGetContentStream(propertyValue);
         }
         else if (propertyName.equals(CMIS.CMIS_CAN_REMOVE_OBJECT_FROM_FOLDER))
         {
            allowableActions.setCanRemoveObjectFromFolder(propertyValue);
         }
         else if (propertyName.equals(CMIS.CMIS_CAN_ADD_OBJECT_TO_FOLDER))
         {
            allowableActions.setCanAddObjectToFolder(propertyValue);
         }
         else if (propertyName.equals(CMIS.CMIS_CAN_GET_ALL_VERSIONS))
         {
            allowableActions.setCanGetAllVersions(propertyValue);
         }
         else if (propertyName.equals(CMIS.CMIS_CAN_SET_CONTENT_STREAM))
         {
            allowableActions.setCanSetContentStream(propertyValue);
         }
         else if (propertyName.equals(CMIS.CMIS_CAN_GET_FOLDER_TREE))
         {
            allowableActions.setCanGetFolderTree(propertyValue);
         }
         else if (propertyName.equals(CMIS.CMIS_CAN_GET_FOLDER_PARENT))
         {
            allowableActions.setCanGetFolderParent(propertyValue);
         }
         else if (propertyName.equals(CMIS.CMIS_CAN_GET_DESCENDANTS))
         {
            allowableActions.setCanGetDescendants(propertyValue);
         }
         else if (propertyName.equals(CMIS.CMIS_CAN_GET_CHILDREN))
         {
            allowableActions.setCanGetChildren(propertyValue);
         }
         else if (propertyName.equals(CMIS.CMIS_CAN_CREATE_FOLDER))
         {
            allowableActions.setCanCreateFolder(propertyValue);
         }
         else if (propertyName.equals(CMIS.CMIS_CAN_CREATE_RELATIONSHIP))
         {
            allowableActions.setCanCreateRelationship(propertyValue);
         }
         else if (propertyName.equals(CMIS.CMIS_CAN_GET_RENDITIONS))
         {
            allowableActions.setCanGetRenditions(propertyValue);
         }
         else if (propertyName.equals(CMIS.CMIS_CAN_GET_ACL))
         {
            allowableActions.setCanGetACL(propertyValue);
         }
         else if (propertyName.equals(CMIS.CMIS_CAN_APPLY_ACL))
         {
            allowableActions.setCanApplyACL(propertyValue);
         }
         else if (propertyName.equals(CMIS.CMIS_CAN_DELETE_TREE))
         {
            allowableActions.setCanDeleteTree(propertyValue);
         }
      }
   }
}
