package org.xcmis.spi.object;

import org.w3c.dom.Element;
import org.xcmis.core.CmisObjectType;
import org.xcmis.messaging.CmisObjectListType;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

/**
 * 
 * @author <a href="mailto:alexey.zavizionov@exoplatform.com">Alexey Zavizionov</a>
 * @version $Id: CmisObjectList.java 34360 2009-07-22 23:58:59Z sunman $
 *
 */
public interface CmisObjectList
{

   /**
    * Gets the value of the objects property.
    * 
    * <p>
    * This accessor method returns a reference to the live list,
    * not a snapshot. Therefore any modification you make to the
    * returned list will be present inside the JAXB object.
    * This is why there is not a <CODE>set</CODE> method for the objects property.
    * 
    * <p>
    * For example, to add a new item, do as follows:
    * <pre>
    *    getObjects().add(newItem);
    * </pre>
    * 
    * 
    * <p>
    * Objects of the following type(s) are allowed in the list
    * {@link CmisObjectType }
    * 
    * 
    */
   public abstract List<CmisObject> getObjects();

   /**
    * Gets the value of the hasMoreItems property.
    * 
    */
   public abstract boolean isHasMoreItems();

   /**
    * Sets the value of the hasMoreItems property.
    * 
    */
   public abstract void setHasMoreItems(boolean value);

   /**
    * Gets the value of the numItems property.
    * 
    * @return
    *     possible object is
    *     {@link BigInteger }
    *     
    */
   public abstract BigInteger getNumItems();

   /**
    * Sets the value of the numItems property.
    * 
    * @param value
    *     allowed object is
    *     {@link BigInteger }
    *     
    */
   public abstract void setNumItems(BigInteger value);

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
   
   public CmisObjectListType toCmisObjectList();

}