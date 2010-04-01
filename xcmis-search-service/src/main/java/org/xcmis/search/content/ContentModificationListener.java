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
package org.xcmis.search.content;

import org.xcmis.search.content.command.InvocationContext;

import java.util.List;
import java.util.Set;

/**
 * Interface of listener changes of content. 
 */
public interface ContentModificationListener
{
   /**
    * Notify listener about changes in content.
    * 
    * @param changes - added content
    * @param removedEntries - removed content
    * @param invocationContext
    * @throws IndexModificationException
    */
   void update(ContentEntry addedEntry, String removedEntry) throws IndexModificationException;

   /**
    * Notify listener about changes in content.
    * 
    * @param changes - added content
    * @param removedEntries - removed content
    * @param invocationContext
    * @throws IndexModificationException
    */
   void update(List<ContentEntry> addedEntries, Set<String> removedEntries, InvocationContext invocationContext)
      throws IndexModificationException;

   /**
    * Notify listener about changes in content with default {@link InvocationContext}
    * 
    * @param changes - added content
    * @param removedEntries - removed content
    * @param invocationContext
    * @throws IndexModificationException
    */
   void update(List<ContentEntry> addedEntries, Set<String> removedEntries) throws IndexModificationException;
}
