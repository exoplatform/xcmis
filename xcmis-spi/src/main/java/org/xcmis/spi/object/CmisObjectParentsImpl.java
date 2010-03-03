package org.xcmis.spi.object;

import org.xcmis.messaging.CmisObjectParentsType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

/**
 * 
 * @author <a href="mailto:alexey.zavizionov@exoplatform.com">Alexey Zavizionov</a>
 * @version $Id: CmisObjectParentsImpl.java 34360 2009-07-22 23:58:59Z sunman $
 *
 */
public class CmisObjectParentsImpl implements CmisObjectParents
{

   protected CmisObject object;

   protected String relativePathSegment;

   protected List<Object> any;

   private Map<QName, String> otherAttributes = new HashMap<QName, String>();

   /**
    * @see org.xcmis.spi.object.CmisObjectParents#getObject()
    */
   public CmisObject getObject()
   {
      return object;
   }

   /**
    * @see org.xcmis.spi.object.CmisObjectParents#setObject(org.xcmis.spi.object.CmisObject)
    */
   public void setObject(CmisObject value)
   {
      this.object = value;
   }

   /**
    * @see org.xcmis.spi.object.CmisObjectParents#getRelativePathSegment()
    */
   public String getRelativePathSegment()
   {
      return relativePathSegment;
   }

   /**
    * @see org.xcmis.spi.object.CmisObjectParents#setRelativePathSegment(java.lang.String)
    */
   public void setRelativePathSegment(String value)
   {
      this.relativePathSegment = value;
   }

   /**
    * @see org.xcmis.spi.object.CmisObjectParents#getAny()
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
    * @see org.xcmis.spi.object.CmisObjectParents#getOtherAttributes()
    */
   public Map<QName, String> getOtherAttributes()
   {
      return otherAttributes;
   }

   public CmisObjectParentsType toCmisObjectParentsType()
   {
      CmisObjectParentsType result = new CmisObjectParentsType();
      if (this.object != null)
         result.setObject(this.object.toCmisObjectType());
      result.setRelativePathSegment(this.relativePathSegment);
      if (this.any != null)
         result.getAny().addAll(this.any);
      if (this.otherAttributes != null)
         result.getOtherAttributes().putAll(this.otherAttributes);
      return result;
   }

}
