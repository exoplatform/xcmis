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
import org.apache.abdera.protocol.server.CollectionInfo;
import org.apache.abdera.protocol.server.RequestContext;
import org.apache.abdera.protocol.server.WorkspaceInfo;
import org.apache.abdera.protocol.server.impl.AbstractEntityCollectionAdapter;
import org.apache.abdera.protocol.server.impl.AbstractWorkspaceManager;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id$
 */
public class WorkspaceManagerImpl extends AbstractWorkspaceManager
{

   /**
    * {@inheritDoc}
    */
   public CollectionAdapter getCollectionAdapter(RequestContext req)
   {
      String path = req.getTargetPath();
      // skip 'cmis/<repositoryId>'
      for (int seg = 2; seg > 0; seg--)
      {
         // TODO improve
         if (path.charAt(0) == '/')
         {
            int next = path.indexOf('/', 1);
            if (next > 0)
               path = path.substring(next);
         }
      }
      for (WorkspaceInfo wi : workspaces)
      {
         for (CollectionInfo ci : wi.getCollections(req))
         {
            AbstractEntityCollectionAdapter<?> ca = (AbstractEntityCollectionAdapter<?>)ci;
            String href = ca.getHref();
            if (path.startsWith(href))
            {
               return ca;
            }
            else
            {
               // XXX improve
               if ((href.startsWith("/children") // For CmisObjectCollection.
                  && (path.startsWith("/object") || path.startsWith("/objectbypath") //
                     || path.startsWith("/file") || path.startsWith("/alternate"))//
                  ) //
                  || (href.startsWith("/types") // For CmisTypeCollection.
                  && path.startsWith("/typebyid"))) //
               {
                  return ca;
               }
            }
         }
      }
      // caller should resolve this.
      return null;
   }
}
