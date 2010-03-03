package org.xcmis.spi.object;

import org.xcmis.messaging.CmisObjectInFolderListType;
import org.xcmis.messaging.CmisObjectInFolderType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

/**
 * 
 * @author <a href="mailto:alexey.zavizionov@exoplatform.com">Alexey Zavizionov</a>
 * @version $Id: CmisObjectInFolderImpl.java 34360 2009-07-22 23:58:59Z sunman $
 *
 */
public class CmisObjectInFolderImpl implements CmisObjectInFolder
{

   protected CmisObject object;

   protected String pathSegment;

   protected List<Object> any;

   private Map<QName, String> otherAttributes = new HashMap<QName, String>();

   /**
    * @see org.xcmis.spi.object.CmisObjectInFolder#getObject()
    */
   public CmisObject getObject()
   {
      return object;
   }

   /**
    * @see org.xcmis.spi.object.CmisObjectInFolder#setObject(org.xcmis.spi.object.CmisObject)
    */
   public void setObject(CmisObject value)
   {
      this.object = value;
   }

   /**
    * @see org.xcmis.spi.object.CmisObjectInFolder#getPathSegment()
    */
   public String getPathSegment()
   {
      return pathSegment;
   }

   /**
    * @see org.xcmis.spi.object.CmisObjectInFolder#setPathSegment(java.lang.String)
    */
   public void setPathSegment(String value)
   {
      this.pathSegment = value;
   }

   /**
    * @see org.xcmis.spi.object.CmisObjectInFolder#getAny()
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
    * @see org.xcmis.spi.object.CmisObjectInFolder#getOtherAttributes()
    */
   public Map<QName, String> getOtherAttributes()
   {
      return otherAttributes;
   }

   public CmisObjectInFolderType toCmisObjectInFolderType()
   {
      CmisObjectInFolderType result = new CmisObjectInFolderType();
      result.setObject(object.toCmisObjectType());
      result.setPathSegment(this.pathSegment);
      if (this.any != null)
         result.getAny().addAll(this.any);
      if (this.otherAttributes != null)
         result.getOtherAttributes().putAll(this.otherAttributes);
      return result;
   }

}
