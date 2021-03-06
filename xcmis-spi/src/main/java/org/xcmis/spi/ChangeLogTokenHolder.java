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

package org.xcmis.spi;

/**
 * Holder for change log token. It must be used for passing in method
 * {@link Connection#getContentChanges(ChangeLogTokenHolder, boolean, String, boolean, boolean, int)}
 * change log token from which new set of changes event should be started. Also
 * this holder will kept change log token of latest retrieved change event. This
 * token will be passed to caller of
 * {@link Connection#getContentChanges(ChangeLogTokenHolder, boolean, String, boolean, boolean, int)}
 * . After that caller can use this token to get next set of change event.
 * 
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: ChangeLogTokenHolder.java 316 2010-03-09 15:20:28Z andrew00x $
 */
public final class ChangeLogTokenHolder extends TokenHolder<String>
{
}
