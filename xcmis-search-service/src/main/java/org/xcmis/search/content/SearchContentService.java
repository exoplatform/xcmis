/*
 * Copyright (C) 2010 eXo Platform SAS.
 * 
 * This is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This software is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this software; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA, or see the FSF
 * site: http://www.fsf.org.
 */
package org.xcmis.search.content;

import org.xcmis.search.content.request.RequestProcessor;

/**
 * A component that acts as a search engine for the content . This engine
 * manages a set of indexes and provides search functionality and provides
 * various methods to (re)index the content.
 */
public interface SearchContentService
{
   /**
    * Create the {@link RequestProcessor} implementation
    * <p>
    * Note that the resulting processor must be
    * {@link SearchEngineProcessor#close() closed} by the caller when completed.
    * </p>
    * 
    * @param context
    *           the context in which the processor is to be used; never null
    * @param observer
    *           the observer of any events created by the processor; may be null
    * @param readOnly
    *           true if the processor will only be reading or searching, or
    *           false if the processor will be used to update the workspaces
    * @return the processor; may not be null
    */
   RequestProcessor createProcessor(ExecutionContext context, RequestProcessor observer, boolean readOnly);
}
