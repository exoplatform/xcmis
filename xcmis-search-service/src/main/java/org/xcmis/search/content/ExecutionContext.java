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

/**
 * An ExecutionContext is a representation of the environment or context in
 * which a component or operation is operating. Some components require this
 * context to be passed into individual methods, allowing the context to vary
 * with each method invocation. Other components require the context to be
 * provided before it's used, and will use that context for all its operations
 * (until it is given a different one).
 * <p>
 * ExecutionContext instances are immutable, so components may hold onto
 * references to them without concern of those contexts changing.
 * </p>
 */
public class ExecutionContext
{
   
}
