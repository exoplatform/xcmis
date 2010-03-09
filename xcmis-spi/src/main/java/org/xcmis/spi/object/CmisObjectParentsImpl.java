package org.xcmis.spi.object;

import org.xcmis.messaging.CmisObjectParentsType;

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
    * @see org.xcmis.spi.object.CmisObjectParents#toCmisObjectParentsType()
    */
   public CmisObjectParentsType toCmisObjectParentsType()
   {
      CmisObjectParentsType result = new CmisObjectParentsType();
      if (this.object != null)
         result.setObject(this.object.toCmisObjectType());
      result.setRelativePathSegment(this.relativePathSegment);
      return result;
   }

}
