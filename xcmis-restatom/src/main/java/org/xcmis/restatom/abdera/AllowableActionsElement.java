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
import org.xcmis.core.CmisAllowableActionsType;
import org.xcmis.restatom.AtomCMIS;

import javax.xml.namespace.QName;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id$
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

   // Never use in real life. Useful for tests.
   /**
    * Gets the allowable actions.
    * 
    * @return the allowable actions
    */
   public CmisAllowableActionsType getAllowableActions()
   {
      CmisAllowableActionsType actions = new CmisAllowableActionsType();

      Element el = null;
      el = getExtension(AtomCMIS.CAN_DELETE_OBJECT);
      actions.setCanDeleteObject(Boolean.valueOf(el != null ? el.getText() : null));
      el = getExtension(AtomCMIS.CAN_UPDATE_PROPERTIES);
      actions.setCanUpdateProperties(Boolean.valueOf(el != null ? el.getText() : null));
      el = getExtension(AtomCMIS.CAN_GET_FOLDER_TREE);
      actions.setCanGetFolderTree(Boolean.valueOf(el != null ? el.getText() : null));
      el = getExtension(AtomCMIS.CAN_GET_PROPERTIES);
      actions.setCanGetProperties(Boolean.valueOf(el != null ? el.getText() : null));
      el = getExtension(AtomCMIS.CAN_GET_OBJECT_RELATIONSHIPS);
      actions.setCanGetObjectRelationships(Boolean.valueOf(el != null ? el.getText() : null));
      el = getExtension(AtomCMIS.CAN_GET_OBJECT_PARENTS);
      actions.setCanGetObjectParents(Boolean.valueOf(el != null ? el.getText() : null));
      el = getExtension(AtomCMIS.CAN_GET_FOLDER_PARENT);
      actions.setCanGetFolderParent(Boolean.valueOf(el != null ? el.getText() : null));
      el = getExtension(AtomCMIS.CAN_GET_DESCENDANTS);
      actions.setCanGetDescendants(Boolean.valueOf(el != null ? el.getText() : null));
      el = getExtension(AtomCMIS.CAN_MOVE_OBJECT);
      actions.setCanMoveObject(Boolean.valueOf(el != null ? el.getText() : null));
      el = getExtension(AtomCMIS.CAN_DELETE_CONTENT_STREAM);
      actions.setCanDeleteContentStream(Boolean.valueOf(el != null ? el.getText() : null));
      el = getExtension(AtomCMIS.CAN_CHECK_OUT);
      actions.setCanCheckOut(Boolean.valueOf(el != null ? el.getText() : null));
      el = getExtension(AtomCMIS.CAN_CANCEL_CHECK_OUT);
      actions.setCanCancelCheckOut(Boolean.valueOf(el != null ? el.getText() : null));
      el = getExtension(AtomCMIS.CAN_CHECK_IN);
      actions.setCanCheckIn(Boolean.valueOf(el != null ? el.getText() : null));
      el = getExtension(AtomCMIS.CAN_SET_CONTENT_STREAM);
      actions.setCanSetContentStream(Boolean.valueOf(el != null ? el.getText() : null));
      el = getExtension(AtomCMIS.CAN_GET_ALL_VERSIONS);
      actions.setCanGetAllVersions(Boolean.valueOf(el != null ? el.getText() : null));
      el = getExtension(AtomCMIS.CAN_ADD_OBJECT_TO_FOLDER);
      actions.setCanAddObjectToFolder(Boolean.valueOf(el != null ? el.getText() : null));
      el = getExtension(AtomCMIS.CAN_REMOVE_OBJECT_FROM_FOLDER);
      actions.setCanRemoveObjectFromFolder(Boolean.valueOf(el != null ? el.getText() : null));
      el = getExtension(AtomCMIS.CAN_GET_CONTENT_STREAM);
      actions.setCanGetContentStream(Boolean.valueOf(el != null ? el.getText() : null));
      el = getExtension(AtomCMIS.CAN_APPLY_POLICY);
      actions.setCanApplyPolicy(Boolean.valueOf(el != null ? el.getText() : null));
      el = getExtension(AtomCMIS.CAN_GET_APPLIED_POLICIES);
      actions.setCanGetAppliedPolicies(Boolean.valueOf(el != null ? el.getText() : null));
      el = getExtension(AtomCMIS.CAN_REMOVE_POLICY);
      actions.setCanRemovePolicy(Boolean.valueOf(el != null ? el.getText() : null));
      el = getExtension(AtomCMIS.CAN_GET_CHILDREN);
      actions.setCanGetChildren(Boolean.valueOf(el != null ? el.getText() : null));
      el = getExtension(AtomCMIS.CAN_CREATE_DOCUMENT);
      actions.setCanCreateDocument(Boolean.valueOf(el != null ? el.getText() : null));
      el = getExtension(AtomCMIS.CAN_CREATE_FOLDER);
      actions.setCanCreateFolder(Boolean.valueOf(el != null ? el.getText() : null));
      el = getExtension(AtomCMIS.CAN_CREATE_RELATIONSHIP);
      actions.setCanCreateRelationship(Boolean.valueOf(el != null ? el.getText() : null));
      el = getExtension(AtomCMIS.CAN_CREATE_POLICY);
      // XXX Removed from schemas     actions.setCanCreatePolicy(Boolean.valueOf(el != null ? el.getText() : null));
      el = getExtension(AtomCMIS.CAN_DELETE_TREE);
      actions.setCanDeleteTree(Boolean.valueOf(el != null ? el.getText() : null));

      /* renditions */
      el = getExtension(AtomCMIS.CAN_GET_RENDITIONS);
      actions.setCanGetRenditions(Boolean.valueOf(el != null ? el.getText() : null));

      /* ACL */
      el = getExtension(AtomCMIS.CAN_GET_ACL);
      actions.setCanGetACL(Boolean.valueOf(el != null ? el.getText() : null));
      el = getExtension(AtomCMIS.CAN_APPLY_ACL);
      actions.setCanApplyACL(Boolean.valueOf(el != null ? el.getText() : null));

      return actions;
   }

   /**
    * Builds the element.
    * 
    * @param actions the actions
    */
   public void build(CmisAllowableActionsType actions)
   {
      if (actions != null)
      {
         declareNS(org.apache.abdera.util.Constants.ATOM_NS, "atom");
         declareNS(org.apache.abdera.util.Constants.APP_NS, "app");

         if (actions.isCanDeleteObject() != null)
            addSimpleExtension(AtomCMIS.CAN_DELETE_OBJECT, actions.isCanDeleteObject().toString());
         if (actions.isCanUpdateProperties() != null)
            addSimpleExtension(AtomCMIS.CAN_UPDATE_PROPERTIES, actions.isCanUpdateProperties().toString());
         if (actions.isCanGetFolderTree() != null)
            addSimpleExtension(AtomCMIS.CAN_GET_FOLDER_TREE, actions.isCanGetFolderTree().toString());
         if (actions.isCanGetProperties() != null)
            addSimpleExtension(AtomCMIS.CAN_GET_PROPERTIES, actions.isCanGetProperties().toString());
         if (actions.isCanGetObjectRelationships() != null)
            addSimpleExtension(AtomCMIS.CAN_GET_OBJECT_RELATIONSHIPS, actions.isCanGetObjectRelationships().toString());
         if (actions.isCanGetObjectParents() != null)
            addSimpleExtension(AtomCMIS.CAN_GET_OBJECT_PARENTS, actions.isCanGetObjectParents().toString());
         if (actions.isCanGetFolderParent() != null)
            addSimpleExtension(AtomCMIS.CAN_GET_FOLDER_PARENT, actions.isCanGetFolderParent().toString());
         if (actions.isCanGetDescendants() != null)
            addSimpleExtension(AtomCMIS.CAN_GET_DESCENDANTS, actions.isCanGetDescendants().toString());
         if (actions.isCanMoveObject() != null)
            addSimpleExtension(AtomCMIS.CAN_MOVE_OBJECT, actions.isCanMoveObject().toString());
         if (actions.isCanDeleteContentStream() != null)
            addSimpleExtension(AtomCMIS.CAN_DELETE_CONTENT_STREAM, actions.isCanDeleteContentStream().toString());
         if (actions.isCanCheckOut() != null)
            addSimpleExtension(AtomCMIS.CAN_CHECK_OUT, actions.isCanCheckOut().toString());
         if (actions.isCanCancelCheckOut() != null)
            addSimpleExtension(AtomCMIS.CAN_CANCEL_CHECK_OUT, actions.isCanCancelCheckOut().toString());
         if (actions.isCanCheckIn() != null)
            addSimpleExtension(AtomCMIS.CAN_CHECK_IN, actions.isCanCheckIn().toString());
         if (actions.isCanSetContentStream() != null)
            addSimpleExtension(AtomCMIS.CAN_SET_CONTENT_STREAM, actions.isCanSetContentStream().toString());
         if (actions.isCanGetAllVersions() != null)
            addSimpleExtension(AtomCMIS.CAN_GET_ALL_VERSIONS, actions.isCanGetAllVersions().toString());
         if (actions.isCanAddObjectToFolder() != null)
            addSimpleExtension(AtomCMIS.CAN_ADD_OBJECT_TO_FOLDER, actions.isCanAddObjectToFolder().toString());
         if (actions.isCanRemoveObjectFromFolder() != null)
            addSimpleExtension(AtomCMIS.CAN_REMOVE_OBJECT_FROM_FOLDER, actions.isCanRemoveObjectFromFolder().toString());
         if (actions.isCanGetContentStream() != null)
            addSimpleExtension(AtomCMIS.CAN_GET_CONTENT_STREAM, actions.isCanGetContentStream().toString());
         if (actions.isCanApplyPolicy() != null)
            addSimpleExtension(AtomCMIS.CAN_APPLY_POLICY, actions.isCanApplyPolicy().toString());
         if (actions.isCanGetAppliedPolicies() != null)
            addSimpleExtension(AtomCMIS.CAN_GET_APPLIED_POLICIES, actions.isCanGetAppliedPolicies().toString());
         if (actions.isCanRemovePolicy() != null)
            addSimpleExtension(AtomCMIS.CAN_REMOVE_POLICY, actions.isCanRemovePolicy().toString());
         if (actions.isCanGetChildren() != null)
            addSimpleExtension(AtomCMIS.CAN_GET_CHILDREN, actions.isCanGetChildren().toString());
         if (actions.isCanCreateDocument() != null)
            addSimpleExtension(AtomCMIS.CAN_CREATE_DOCUMENT, actions.isCanCreateDocument().toString());
         if (actions.isCanCreateFolder() != null)
            addSimpleExtension(AtomCMIS.CAN_CREATE_FOLDER, actions.isCanCreateFolder().toString());
         if (actions.isCanCreateRelationship() != null)
            addSimpleExtension(AtomCMIS.CAN_CREATE_RELATIONSHIP, actions.isCanCreateRelationship().toString());
         //  XXX Removed from schemas     if (actions.isCanCreatePolicy() != null)
         //            addSimpleExtension(AtomCMIS.CAN_CREATE_POLICY, actions.isCanCreatePolicy().toString());
         if (actions.isCanDeleteTree() != null)
            addSimpleExtension(AtomCMIS.CAN_DELETE_TREE, actions.isCanDeleteTree().toString());

         /* renditions */
         if (actions.isCanGetRenditions() != null)
            addSimpleExtension(AtomCMIS.CAN_GET_RENDITIONS, actions.isCanGetRenditions().toString());

         /* ACL */
         if (actions.isCanGetACL() != null)
            addSimpleExtension(AtomCMIS.CAN_GET_ACL, actions.isCanGetACL().toString());
         if (actions.isCanApplyACL() != null)
            addSimpleExtension(AtomCMIS.CAN_APPLY_ACL, actions.isCanApplyACL().toString());

      }
   }

}
