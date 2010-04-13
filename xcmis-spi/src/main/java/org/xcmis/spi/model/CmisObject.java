package org.xcmis.spi.model;


import org.xcmis.spi.model.Permission.BasicPermissions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Info about CMISobject that contains properties, allowable actions,
 * relationships of object, etc. Set of this info determined by the user
 * request. It minds any of getters may return <code>null</code> or empty
 * collections if info provided by method was not requested even object contains
 * such information.
 *
 * @author <a href="mailto:alexey.zavizionov@exoplatform.com">Alexey
 *         Zavizionov</a>
 * @version $Id: CmisObject.java 34360 2009-07-22 23:58:59Z sunman $
 */
public class CmisObject
{

   private Map<String, Property<?>> properties;

   private List<AccessControlEntry> acl;

   private boolean exactACL;

   private Set<String> policyIds;

   private List<CmisObject> relationships;

   private List<Rendition> renditions;

   private AllowableActions allowableActions;

   private ChangeInfo changeInfo;

   private ObjectInfo objectInfo;

   private String pathSegment;

   public CmisObject()
   {
   }

   public CmisObject(Map<String, Property<?>> properties, List<AccessControlEntry> acl, boolean exactACL, Set<String> policyIds,
      List<CmisObject> relationships, List<Rendition> renditions, AllowableActions allowableActions,
      ChangeInfo changeInfo, ObjectInfo objectInfo, String pathSegment)
   {
      this.properties = properties;
      this.acl = acl;
      this.exactACL = exactACL;
      this.policyIds = policyIds;
      this.relationships = relationships;
      this.renditions = renditions;
      this.allowableActions = allowableActions;
      this.changeInfo = changeInfo;
      this.objectInfo = objectInfo;
      this.pathSegment = pathSegment;
   }

   /**
    * @return object's ACL. Even object has not any applied ACL this method must
    *         return empty list but never <code>null</code>
    */
   public List<AccessControlEntry> getACL()
   {
      if (acl == null)
      {
         acl = new ArrayList<AccessControlEntry>();
      }
      return acl;
   }

   /**
    * @return allowable actions
    */
   public AllowableActions getAllowableActions()
   {
      return allowableActions;
   }

   /**
    * @return change info
    */
   public ChangeInfo getChangeInfo()
   {
      return changeInfo;
   }

   /**
    * @return external information about object. It is useful if other method
    *         does not provide required information about object because to
    *         caller constraint. For example {@link #getProperties()} does not
    *         contains all required properties to build correct AtomPub document
    */
   public ObjectInfo getObjectInfo()
   {
      return objectInfo;
   }

   /**
    * @return path segment of object relative to the folder that contains this
    *         object. For Document may be 'cmis:name' or content stream filename
    */
   public String getPathSegment()
   {
      return pathSegment;
   }

   /**
    * @return set of policy IDs applied to the object. Even object has not any
    *         applied policies this method must return empty collection but
    *         never <code>null</code>
    */
   public Collection<String> getPolicyIds()
   {
      if (policyIds == null)
      {
         policyIds = new HashSet<String>();
      }
      return policyIds;
   }

   /**
    * @return object's properties, never <code>null</code>
    */
   public Map<String, Property<?>> getProperties()
   {
      if (properties == null)
      {
         properties = new HashMap<String, Property<?>>();
      }
      return properties;
   }

   /**
    * @return objects relationships. Even object has not any relationships this
    *         method must return empty list but never <code>null</code>
    */
   public List<CmisObject> getRelationship()
   {
      if (relationships == null)
      {
         relationships = new ArrayList<CmisObject>();
      }
      return relationships;
   }

   /**
    * @return content stream renditions. There is no rendition contents stream
    *         just information about available renditions. Even object has not
    *         any renditions this method must return empty list but never
    *         <code>null</code>
    */
   public List<Rendition> getRenditions()
   {
      if (renditions == null)
      {
         renditions = new ArrayList<Rendition>();
      }
      return renditions;
   }

   /**
    * @return <code>true</code> if method {@link #getACL()} provide information
    *         about all object's permissions and <code>false</code> if object's
    *         has other permissions. It may happen if repository displays only
    *         basic permissions {@link BasicPermissions}
    */
   public boolean isExactACL()
   {
      return exactACL;
   }

   // ------------------- setters ------------------

   public void setAllowableActions(AllowableActions allowableActions)
   {
      this.allowableActions = allowableActions;
   }

   public void setChangeInfo(ChangeInfo changeInfo)
   {
      this.changeInfo = changeInfo;
   }

   public void setObjectInfo(ObjectInfo objectInfo)
   {
      this.objectInfo = objectInfo;
   }

   public void setExactACL(boolean exactACL)
   {
      this.exactACL = exactACL;
   }

   public void setPathSegment(String pathSegment)
   {
      this.pathSegment = pathSegment;
   }

}