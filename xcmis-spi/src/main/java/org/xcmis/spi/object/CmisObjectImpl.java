package org.xcmis.spi.object;

import org.xcmis.core.CmisAccessControlListType;
import org.xcmis.core.CmisAllowableActionsType;
import org.xcmis.core.CmisChangeEventType;
import org.xcmis.core.CmisListOfIdsType;
import org.xcmis.core.CmisObjectType;
import org.xcmis.core.CmisPropertiesType;
import org.xcmis.core.CmisRenditionType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

/**
 * Java class for CmisObject complex type.
 * 
 * @author <a href="mailto:alexey.zavizionov@exoplatform.com">Alexey Zavizionov</a>
 * @version $Id: CmisObject.java 34360 2009-07-22 23:58:59Z sunman $
 */
public class CmisObjectImpl implements CmisObject
{

   protected CmisPropertiesType properties;

   protected CmisAllowableActionsType allowableActions;

   protected List<CmisObject> relationship;

   protected CmisChangeEventType changeEventInfo;

   protected CmisAccessControlListType acl;

   protected Boolean exactACL;

   protected CmisListOfIdsType policyIds;

   protected List<CmisRenditionType> rendition;

   protected List<Object> any;

   private Map<QName, String> otherAttributes = new HashMap<QName, String>();

   protected ObjectInfo objectInfo;

   public CmisObjectImpl()
   {
   }

   public CmisObjectImpl(CmisObjectType cmisObjectType)
   {
      this.properties = cmisObjectType.getProperties();
      this.allowableActions = cmisObjectType.getAllowableActions();
      if (cmisObjectType.getRelationship() != null)
      {
         this.relationship = new ArrayList<CmisObject>();
         for (CmisObjectType rel : cmisObjectType.getRelationship())
         {
            this.relationship.add(new CmisObjectImpl(rel));
         }
      }
      this.changeEventInfo = cmisObjectType.getChangeEventInfo();
      this.acl = cmisObjectType.getAcl();
      this.exactACL = cmisObjectType.isExactACL();
      this.policyIds = cmisObjectType.getPolicyIds();
      this.rendition = cmisObjectType.getRendition();
      this.any = cmisObjectType.getAny();
      this.otherAttributes = cmisObjectType.getOtherAttributes();
      this.objectInfo = null;
   }

   /**
    * @see org.xcmis.spi.object.CmisObject#getProperties()
    */
   public CmisPropertiesType getProperties()
   {
      return properties;
   }

   /**
    * @see org.xcmis.spi.object.CmisObject#setProperties(org.xcmis.core.CmisPropertiesType)
    */
   public void setProperties(CmisPropertiesType value)
   {
      this.properties = value;
   }

   /**
    * @see org.xcmis.spi.object.CmisObject#getAllowableActions()
    */
   public CmisAllowableActionsType getAllowableActions()
   {
      return allowableActions;
   }

   /**
    * @see org.xcmis.spi.object.CmisObject#setAllowableActions(org.xcmis.core.CmisAllowableActionsType)
    */
   public void setAllowableActions(CmisAllowableActionsType value)
   {
      this.allowableActions = value;
   }

   /**
    * @see org.xcmis.spi.object.CmisObject#getRelationship()
    */
   public List<CmisObject> getRelationship()
   {
      if (relationship == null)
      {
         relationship = new ArrayList<CmisObject>();
      }
      return this.relationship;
   }

   /**
    * @see org.xcmis.spi.object.CmisObject#getChangeEventInfo()
    */
   public CmisChangeEventType getChangeEventInfo()
   {
      return changeEventInfo;
   }

   /**
    * @see org.xcmis.spi.object.CmisObject#setChangeEventInfo(org.xcmis.core.CmisChangeEventType)
    */
   public void setChangeEventInfo(CmisChangeEventType value)
   {
      this.changeEventInfo = value;
   }

   /**
    * @see org.xcmis.spi.object.CmisObject#getAcl()
    */
   public CmisAccessControlListType getAcl()
   {
      return acl;
   }

   /**
    * @see org.xcmis.spi.object.CmisObject#setAcl(org.xcmis.core.CmisAccessControlListType)
    */
   public void setAcl(CmisAccessControlListType value)
   {
      this.acl = value;
   }

   /**
    * @see org.xcmis.spi.object.CmisObject#isExactACL()
    */
   public Boolean isExactACL()
   {
      return exactACL;
   }

   /**
    * @see org.xcmis.spi.object.CmisObject#setExactACL(java.lang.Boolean)
    */
   public void setExactACL(Boolean value)
   {
      this.exactACL = value;
   }

   /**
    * @see org.xcmis.spi.object.CmisObject#getPolicyIds()
    */
   public CmisListOfIdsType getPolicyIds()
   {
      return policyIds;
   }

   /**
    * @see org.xcmis.spi.object.CmisObject#setPolicyIds(org.xcmis.core.CmisListOfIdsType)
    */
   public void setPolicyIds(CmisListOfIdsType value)
   {
      this.policyIds = value;
   }

   /**
    * @see org.xcmis.spi.object.CmisObject#getRendition()
    */
   public List<CmisRenditionType> getRendition()
   {
      if (rendition == null)
      {
         rendition = new ArrayList<CmisRenditionType>();
      }
      return this.rendition;
   }

   /**
    * @see org.xcmis.spi.object.CmisObject#getAny()
    */
   public List<Object> getAny()
   {
      if (any == null)
      {
         any = new ArrayList<Object>();
      }
      return this.any;
   }

   /**
    * @see org.xcmis.spi.object.CmisObject#getOtherAttributes()
    */
   public Map<QName, String> getOtherAttributes()
   {
      return otherAttributes;
   }

   /**
    * @see org.xcmis.spi.object.CmisObject#getObjectInfo()
    */
   public ObjectInfo getObjectInfo()
   {
      return objectInfo;
   }

   /**
    * @see org.xcmis.spi.object.CmisObject#setObjectInfo(org.xcmis.spi.object.ObjectInfo)
    */
   public void setObjectInfo(ObjectInfo objectInfo)
   {
      this.objectInfo = objectInfo;
   }

   public CmisObjectType toCmisObjectType()
   {
      CmisObjectType result = new CmisObjectType();
      result.setProperties(this.properties);
      result.setAllowableActions(this.allowableActions);
      if (this.relationship != null)
      {
         for (CmisObject rel : this.relationship)
         {
            result.getRelationship().add(rel.toCmisObjectType());
         }
      }
      result.setChangeEventInfo(this.changeEventInfo);
      result.setAcl(this.acl);
      result.setExactACL(this.exactACL);
      result.setPolicyIds(this.policyIds);
      if (this.rendition != null)
         result.getRendition().addAll(this.rendition);
      if (this.any != null)
         result.getAny().addAll(this.any);
      if (this.otherAttributes != null)
         result.getOtherAttributes().putAll(this.otherAttributes);
      return result;
   }

}
