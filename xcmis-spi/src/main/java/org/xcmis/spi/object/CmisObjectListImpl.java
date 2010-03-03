package org.xcmis.spi.object;

import org.xcmis.messaging.CmisObjectListType;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

/**
 * @author <a href="mailto:alexey.zavizionov@exoplatform.com">Alexey Zavizionov</a>
 * @version $Id: CmisObjectList.java 34360 2009-07-22 23:58:59Z sunman $
 *
 */
public class CmisObjectListImpl implements CmisObjectList
{

   protected List<CmisObject> objects;

   protected boolean hasMoreItems;

   protected BigInteger numItems;

   protected List<Object> any;

   private Map<QName, String> otherAttributes = new HashMap<QName, String>();

   /**
    * @see org.xcmis.spi.object.CmisObjectList#getObjects()
    */
   public List<CmisObject> getObjects()
   {
      if (objects == null)
      {
         objects = new ArrayList<CmisObject>();
      }
      return this.objects;
   }

   /**
    * @see org.xcmis.spi.object.CmisObjectList#isHasMoreItems()
    */
   public boolean isHasMoreItems()
   {
      return hasMoreItems;
   }

   /**
    * @see org.xcmis.spi.object.CmisObjectList#setHasMoreItems(boolean)
    */
   public void setHasMoreItems(boolean value)
   {
      this.hasMoreItems = value;
   }

   /**
    * @see org.xcmis.spi.object.CmisObjectList#getNumItems()
    */
   public BigInteger getNumItems()
   {
      return numItems;
   }

   /**
    * @see org.xcmis.spi.object.CmisObjectList#setNumItems(java.math.BigInteger)
    */
   public void setNumItems(BigInteger value)
   {
      this.numItems = value;
   }

   /**
    * @see org.xcmis.spi.object.CmisObjectList#getAny()
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
    * @see org.xcmis.spi.object.CmisObjectList#getOtherAttributes()
    */
   public Map<QName, String> getOtherAttributes()
   {
      return otherAttributes;
   }

   public CmisObjectListType toCmisObjectList()
   {
      CmisObjectListType result = new CmisObjectListType();
      result.setHasMoreItems(this.hasMoreItems);
      result.setNumItems(this.numItems);
      if (objects != null)
      {
         for (CmisObject obj : objects)
         {
            result.getObjects().add(obj.toCmisObjectType());
         }
      }
      if (this.any != null)
         result.getAny().addAll(this.any);
      if (this.otherAttributes != null)
         result.getOtherAttributes().putAll(this.otherAttributes);
      return result;
   }

}
