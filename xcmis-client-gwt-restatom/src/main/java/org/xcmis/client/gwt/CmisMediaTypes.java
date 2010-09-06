/*
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
package org.xcmis.client.gwt;

/**
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: 2010
 *
 */
public interface CmisMediaTypes
{
   /**
    * CMIS Query document.
    */
   String QUERY_DOCUMENT = "application/cmisquery+xml";

   /**
    * CMIS AllowableActions document.
    */
   String ALLOWABLE_ACTIONS = "application/cmisallowableactions+xml";

   /**
    * Atom Document (Entry or Feed) with any CMIS Markup.
    */
   String ATOM_DOCUMENT = "application/cmisatom+xml";

   /**
    * Atom Feed Document with CMIS Hierarchy extensions.
    */
   String ATOM_FEED_DOCUMENT = "application/cmistree+xml";

   /**
    * CMIS ACL Document.
    */
   String ACL_DOCUMENT = "application/cmisacl+xml";

   /**
    * AtomPub Service.
    */
   String ATOM_PUB_SERVICE = "application/atomsvc+xml";

   /**
    * Atom Entry.
    */
   String ATOM_ENTRY = "application/atom+xml;type=entry";

   /**
    * Atom Feed.
    */
   String ATOM_FEED = "application/atom+xml;type=feed";
}
