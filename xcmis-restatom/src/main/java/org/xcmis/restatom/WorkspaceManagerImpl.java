/**
 * Copyright (C) 2010 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.xcmis.restatom;

import org.apache.abdera.protocol.server.CollectionAdapter;
import org.apache.abdera.protocol.server.RequestContext;
import org.apache.abdera.protocol.server.impl.AbstractWorkspaceManager;
import org.xcmis.restatom.collections.AbstractCmisCollection;
import org.xcmis.restatom.collections.AllVersionsCollection;
import org.xcmis.restatom.collections.ChangesLogCollection;
import org.xcmis.restatom.collections.CheckedOutCollection;
import org.xcmis.restatom.collections.FolderChildrenCollection;
import org.xcmis.restatom.collections.FolderDescentantsCollection;
import org.xcmis.restatom.collections.FolderTreeCollection;
import org.xcmis.restatom.collections.ParentsCollection;
import org.xcmis.restatom.collections.PoliciesCollection;
import org.xcmis.restatom.collections.QueryCollection;
import org.xcmis.restatom.collections.RelationshipsCollection;
import org.xcmis.restatom.collections.TypesChildrenCollection;
import org.xcmis.restatom.collections.TypesDescendantsCollection;
import org.xcmis.restatom.collections.UnfiledCollection;
import org.xcmis.spi.Connection;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: WorkspaceManagerImpl.java 2 2010-02-04 17:21:49Z andrew00x $
 */
public class WorkspaceManagerImpl extends AbstractWorkspaceManager
{

   /**
    * {@inheritDoc}
    */
   public CollectionAdapter getCollectionAdapter(RequestContext req)
   {
      throw new UnsupportedOperationException();
   }


   public AbstractCmisCollection<?> getCollectionAdapter(RequestContext req, Connection connection)
   {
      String path = req.getTargetPath();
      // skip 'cmis/<repositoryId>'
      for (int seg = 2; seg > 0; seg--)
      {
         if (path.charAt(0) == '/')
         {
            int next = path.indexOf('/', 1);
            if (next > 0)
               path = path.substring(next);
         }
      }
      AbstractCmisCollection<?> c = null;
      if (path.startsWith("/children") || path.startsWith("/object") || path.startsWith("/objectbypath")
         || path.startsWith("/file") || path.startsWith("/alternate"))
      {
         c = new FolderChildrenCollection(connection);
      }
      else if (path.startsWith("/types") || path.startsWith("/typebyid"))
      {
         c = new TypesChildrenCollection(connection);
      }
      else if (path.startsWith("/query"))
      {
         c =  new QueryCollection(connection);
      }
      else if (path.startsWith("/policies"))
      {
         c =  new PoliciesCollection(connection);
      }
      else if (path.startsWith("/relationships"))
      {
         c =  new RelationshipsCollection(connection);
      }
      else if (path.startsWith("/versions"))
      {
         c =  new AllVersionsCollection(connection);
      }
      else if (path.startsWith("/checkedout"))
      {
         c =  new CheckedOutCollection(connection);
      }
      else if (path.startsWith("/parents"))
      {
         c =  new ParentsCollection(connection);
      }
      else if (path.startsWith("/unfiled"))
      {
         c =  new UnfiledCollection(connection);
      }
      else if (path.startsWith("/descendants"))
      {
         c =  new FolderDescentantsCollection(connection);
      }
      else if (path.startsWith("/descendants"))
      {
         c =  new FolderDescentantsCollection(connection);
      }
      else if (path.startsWith("/changes"))
      {
         c =  new ChangesLogCollection(connection);
      }
      else if (path.startsWith("/foldertree"))
      {
         c =  new FolderTreeCollection(connection);
      }
      else if (path.startsWith("/typedescendants"))
      {
         c =  new TypesDescendantsCollection(connection);
      }
      return c;
   }

}
