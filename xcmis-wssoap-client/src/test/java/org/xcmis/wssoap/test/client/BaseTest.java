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
package org.xcmis.wssoap.test.client;

import junit.framework.TestCase;

import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

public abstract class BaseTest extends TestCase
{

   protected final Log LOG = ExoLogger.getLogger(BaseTest.class);

   protected final String cmisRepositoryId = "cmis1";

   protected final String rootFolderId = "00exo0jcr0root0uuid0000000000000";

   protected final String username = "root";

   protected final String password = "exo";

}
