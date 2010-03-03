package org.xcmis.spi.object;

import org.w3c.dom.Element;
import org.xcmis.core.CmisAccessControlListType;
import org.xcmis.core.CmisAllowableActionsType;
import org.xcmis.core.CmisChangeEventType;
import org.xcmis.core.CmisListOfIdsType;
import org.xcmis.core.CmisObjectType;
import org.xcmis.core.CmisPropertiesType;
import org.xcmis.core.CmisRenditionType;

import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

/**
 * 
 * @author <a href="mailto:alexey.zavizionov@exoplatform.com">Alexey Zavizionov</a>
 * @version $Id: CmisObject.java 34360 2009-07-22 23:58:59Z sunman $
 *
 */
public interface CmisObject
{

   /**
    * Gets the value of the properties property.
    * 
    * @return
    *     possible object is
    *     {@link CmisPropertiesType }
    *     
    */
   public abstract CmisPropertiesType getProperties();

   /**
    * Sets the value of the properties property.
    * 
    * @param value
    *     allowed object is
    *     {@link CmisPropertiesType }
    *     
    */
   public abstract void setProperties(CmisPropertiesType value);

   /**
    * Gets the value of the allowableActions property.
    * 
    * @return
    *     possible object is
    *     {@link CmisAllowableActionsType }
    *     
    */
   public abstract CmisAllowableActionsType getAllowableActions();

   /**
    * Sets the value of the allowableActions property.
    * 
    * @param value
    *     allowed object is
    *     {@link CmisAllowableActionsType }
    *     
    */
   public abstract void setAllowableActions(CmisAllowableActionsType value);

   /**
    * Gets the value of the relationship property.
    * 
    * <p>
    * This accessor method returns a reference to the live list,
    * not a snapshot. Therefore any modification you make to the
    * returned list will be present inside the JAXB object.
    * This is why there is not a <CODE>set</CODE> method for the relationship property.
    * 
    * <p>
    * For example, to add a new item, do as follows:
    * <pre>
    *    getRelationship().add(newItem);
    * </pre>
    * 
    * 
    * <p>
    * Objects of the following type(s) are allowed in the list
    * {@link CmisObjectType }
    * 
    * 
    */
   public abstract List<CmisObject> getRelationship();

   /**
    * Gets the value of the changeEventInfo property.
    * 
    * @return
    *     possible object is
    *     {@link CmisChangeEventType }
    *     
    */
   public abstract CmisChangeEventType getChangeEventInfo();

   /**
    * Sets the value of the changeEventInfo property.
    * 
    * @param value
    *     allowed object is
    *     {@link CmisChangeEventType }
    *     
    */
   public abstract void setChangeEventInfo(CmisChangeEventType value);

   /**
    * Gets the value of the acl property.
    * 
    * @return
    *     possible object is
    *     {@link CmisAccessControlListType }
    *     
    */
   public abstract CmisAccessControlListType getAcl();

   /**
    * Sets the value of the acl property.
    * 
    * @param value
    *     allowed object is
    *     {@link CmisAccessControlListType }
    *     
    */
   public abstract void setAcl(CmisAccessControlListType value);

   /**
    * Gets the value of the exactACL property.
    * 
    * @return
    *     possible object is
    *     {@link Boolean }
    *     
    */
   public abstract Boolean isExactACL();

   /**
    * Sets the value of the exactACL property.
    * 
    * @param value
    *     allowed object is
    *     {@link Boolean }
    *     
    */
   public abstract void setExactACL(Boolean value);

   /**
    * Gets the value of the policyIds property.
    * 
    * @return
    *     possible object is
    *     {@link CmisListOfIdsType }
    *     
    */
   public abstract CmisListOfIdsType getPolicyIds();

   /**
    * Sets the value of the policyIds property.
    * 
    * @param value
    *     allowed object is
    *     {@link CmisListOfIdsType }
    *     
    */
   public abstract void setPolicyIds(CmisListOfIdsType value);

   /**
    * Gets the value of the rendition property.
    * 
    * <p>
    * This accessor method returns a reference to the live list,
    * not a snapshot. Therefore any modification you make to the
    * returned list will be present inside the JAXB object.
    * This is why there is not a <CODE>set</CODE> method for the rendition property.
    * 
    * <p>
    * For example, to add a new item, do as follows:
    * <pre>
    *    getRendition().add(newItem);
    * </pre>
    * 
    * 
    * <p>
    * Objects of the following type(s) are allowed in the list
    * {@link CmisRenditionType }
    * 
    * 
    */
   public abstract List<CmisRenditionType> getRendition();

   /**
    * Gets the value of the any property.
    * 
    * <p>
    * This accessor method returns a reference to the live list,
    * not a snapshot. Therefore any modification you make to the
    * returned list will be present inside the JAXB object.
    * This is why there is not a <CODE>set</CODE> method for the any property.
    * 
    * <p>
    * For example, to add a new item, do as follows:
    * <pre>
    *    getAny().add(newItem);
    * </pre>
    * 
    * 
    * <p>
    * Objects of the following type(s) are allowed in the list
    * {@link Element }
    * {@link Object }
    * 
    * 
    */
   public abstract List<Object> getAny();

   /**
    * Gets a map that contains attributes that aren't bound to any typed property on this class.
    * 
    * <p>
    * the map is keyed by the name of the attribute and 
    * the value is the string value of the attribute.
    * 
    * the map returned by this method is live, and you can add new attribute
    * by updating the map directly. Because of this design, there's no setter.
    * 
    * 
    * @return
    *     always non-null
    */
   public abstract Map<QName, String> getOtherAttributes();

   /**
    * Getter for ObjectInfo element.
    * 
    * @return
    */
   public abstract ObjectInfo getObjectInfo();

   /**
    * Setter for ObjectInfo element.
    * 
    * @param objectInfo
    */
   public abstract void setObjectInfo(ObjectInfo objectInfo);
   
   /**
    * Create an CmisObjectType instance from currebt object.
    * @return
    */
   public CmisObjectType toCmisObjectType();

}