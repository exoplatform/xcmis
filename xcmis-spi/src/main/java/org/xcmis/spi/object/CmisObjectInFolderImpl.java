package org.xcmis.spi.object;

import org.xcmis.messaging.CmisObjectInFolderType;

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
    * @see org.xcmis.spi.object.CmisObjectInFolder#toCmisObjectInFolderType()
    */
   public CmisObjectInFolderType toCmisObjectInFolderType()
   {
      CmisObjectInFolderType result = new CmisObjectInFolderType();
      result.setObject(object.toCmisObjectType());
      result.setPathSegment(this.pathSegment);
      return result;
   }

}
