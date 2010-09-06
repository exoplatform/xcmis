/**
 * Copyright (C) 2010 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.xcmis.restatom.abdera;

import org.apache.abdera.factory.Factory;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.ExtensibleElementWrapper;
import org.xcmis.restatom.AtomCMIS;
import org.xcmis.spi.model.AllowableActions;

import javax.xml.namespace.QName;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: AllowableActionsElement.java 2 2010-02-04 17:21:49Z andrew00x $
 */
public class AllowableActionsElement extends ExtensibleElementWrapper
{

   /**
    * Instantiates a new allowable actions element.
    *
    * @param internal the internal
    */
   public AllowableActionsElement(Element internal)
   {
      super(internal);
   }

   /**
    * Instantiates a new allowable actions element.
    *
    * @param factory the factory
    * @param qname the qname
    */
   public AllowableActionsElement(Factory factory, QName qname)
   {
      super(factory, qname);
   }

   /**
    * Builds the element.
    *
    * @param actions the actions
    */
   public void build(AllowableActions actions)
   {
      if (actions != null)
      {
         declareNS(org.apache.abdera.util.Constants.ATOM_NS, "atom");
         declareNS(org.apache.abdera.util.Constants.APP_NS, "app");

         addSimpleExtension(AtomCMIS.CAN_DELETE_OBJECT, Boolean.toString(actions.isCanDeleteObject()));
         addSimpleExtension(AtomCMIS.CAN_UPDATE_PROPERTIES, Boolean.toString(actions.isCanUpdateProperties()));
         addSimpleExtension(AtomCMIS.CAN_GET_FOLDER_TREE, Boolean.toString(actions.isCanGetFolderTree()));
         addSimpleExtension(AtomCMIS.CAN_GET_PROPERTIES, Boolean.toString(actions.isCanGetProperties()));
         addSimpleExtension(AtomCMIS.CAN_GET_OBJECT_RELATIONSHIPS, Boolean.toString(actions
            .isCanGetObjectRelationships()));
         addSimpleExtension(AtomCMIS.CAN_GET_OBJECT_PARENTS, Boolean.toString(actions.isCanGetObjectParents()));
         addSimpleExtension(AtomCMIS.CAN_GET_FOLDER_PARENT, Boolean.toString(actions.isCanGetFolderParent()));
         addSimpleExtension(AtomCMIS.CAN_GET_DESCENDANTS, Boolean.toString(actions.isCanGetDescendants()));
         addSimpleExtension(AtomCMIS.CAN_MOVE_OBJECT, Boolean.toString(actions.isCanMoveObject()));
         addSimpleExtension(AtomCMIS.CAN_DELETE_CONTENT_STREAM, Boolean.toString(actions.isCanDeleteContentStream()));
         addSimpleExtension(AtomCMIS.CAN_CHECK_OUT, Boolean.toString(actions.isCanCheckOut()));
         addSimpleExtension(AtomCMIS.CAN_CANCEL_CHECK_OUT, Boolean.toString(actions.isCanCancelCheckOut()));
         addSimpleExtension(AtomCMIS.CAN_CHECK_IN, Boolean.toString(actions.isCanCheckIn()));
         addSimpleExtension(AtomCMIS.CAN_SET_CONTENT_STREAM, Boolean.toString(actions.isCanSetContentStream()));
         addSimpleExtension(AtomCMIS.CAN_GET_ALL_VERSIONS, Boolean.toString(actions.isCanGetAllVersions()));
         addSimpleExtension(AtomCMIS.CAN_ADD_OBJECT_TO_FOLDER, Boolean.toString(actions.isCanAddObjectToFolder()));
         addSimpleExtension(AtomCMIS.CAN_REMOVE_OBJECT_FROM_FOLDER, Boolean.toString(actions
            .isCanRemoveObjectFromFolder()));
         addSimpleExtension(AtomCMIS.CAN_GET_CONTENT_STREAM, Boolean.toString(actions.isCanGetContentStream()));
         addSimpleExtension(AtomCMIS.CAN_APPLY_POLICY, Boolean.toString(actions.isCanApplyPolicy()));
         addSimpleExtension(AtomCMIS.CAN_GET_APPLIED_POLICIES, Boolean.toString(actions.isCanGetAppliedPolicies()));
         addSimpleExtension(AtomCMIS.CAN_REMOVE_POLICY, Boolean.toString(actions.isCanRemovePolicy()));
         addSimpleExtension(AtomCMIS.CAN_GET_CHILDREN, Boolean.toString(actions.isCanGetChildren()));
         addSimpleExtension(AtomCMIS.CAN_CREATE_DOCUMENT, Boolean.toString(actions.isCanCreateDocument()));
         addSimpleExtension(AtomCMIS.CAN_CREATE_FOLDER, Boolean.toString(actions.isCanCreateFolder()));
         addSimpleExtension(AtomCMIS.CAN_CREATE_RELATIONSHIP, Boolean.toString(actions.isCanCreateRelationship()));
         addSimpleExtension(AtomCMIS.CAN_DELETE_TREE, Boolean.toString(actions.isCanDeleteTree()));

         /* renditions */
         addSimpleExtension(AtomCMIS.CAN_GET_RENDITIONS, Boolean.toString(actions.isCanGetRenditions()));

         /* ACL */
         addSimpleExtension(AtomCMIS.CAN_GET_ACL, Boolean.toString(actions.isCanGetACL()));
         addSimpleExtension(AtomCMIS.CAN_APPLY_ACL, Boolean.toString(actions.isCanApplyACL()));

      }
   }

}
