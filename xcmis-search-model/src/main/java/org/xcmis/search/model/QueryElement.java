/*
 * Copyright (C) 2009 eXo Platform SAS.
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
package org.xcmis.search.model;

import org.xcmis.search.QueryObjectModelVisitor;
import org.xcmis.search.VisitException;

import java.io.Serializable;

/**
 * 
 * An interface called by a visitor when that visitor is visiting the node.
 */

public interface QueryElement extends Serializable
{

   /**
    * Accepts a <code>visitor</code> and calls the appropriate visit method
    * depending on the type of this QOM node.
    * 
    * @param visitor
    *           the visitor.
    * @param context
    *           user defined data, which is passed to the visit method.
    */
   public void accept(QueryObjectModelVisitor visitor) throws VisitException;

}
