package org.xcmis.spi.object;

import org.xcmis.spi.AccessControlEntry;
import org.xcmis.spi.AllowableActions;
import org.xcmis.spi.Rendition;
import org.xcmis.spi.Permission.BasicPermissions;

import java.util.Collection;
import java.util.List;
import java.util.Map;

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
public interface CmisObject
{

   /**
    * @return object's properties, never <code>null</code>
    */
   Map<String, Property<?>> getProperties();

   /**
    * @return allowable actions
    */
   AllowableActions getAllowableActions();

   /**
    * @return objects relationships. Even object has not any relationships this
    *         method must return empty list but never <code>null</code>
    */
   List<CmisObject> getRelationship();

   /**
    * @return change info
    */
   ChangeInfo getChangeInfo();

   /**
    * @return object's ACL. Even object has not any applied ACL this method must
    *         return empty list but never <code>null</code>
    */
   List<AccessControlEntry> getACL();

   /**
    * @return <code>true</code> if method {@link #getACL()} provide information
    *         about all object's permissions and <code>false</code> if object's
    *         has other permissions. It may happen if repository displays only
    *         basic permissions {@link BasicPermissions}
    */
   boolean isExactACL();

   /**
    * @return set of policy IDs applied to the object. Even object has not any
    *         applied policies this method must return empty collection but
    *         never <code>null</code>
    */
   Collection<String> getPolicyIds();

   /**
    * @return content stream renditions. There is no rendition contents stream
    *         just information about available renditions. Even object has not
    *         any renditions this method must return empty list but never
    *         <code>null</code>
    */
   List<Rendition> getRenditions();

   /**
    * @return path segment of object relative to the folder that contains this
    *         object. For Document may be 'cmis:name' or content stream filename
    */
   String getPathSegment();

   /**
    * @return external information about object. It is useful if other method
    *         does not provide required information about object because to
    *         caller constraint. For example {@link #getProperties()} does not
    *         contains all required properties to build correct AtomPub document
    */
   ObjectInfo getObjectInfo();

}