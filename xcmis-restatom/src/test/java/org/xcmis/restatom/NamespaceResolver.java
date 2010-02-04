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

import org.xcmis.restatom.AtomCMIS;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.xml.namespace.NamespaceContext;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id$
 */
public class NamespaceResolver implements NamespaceContext
{

   private Map<String, String> p2u = new HashMap<String, String>();

   private Map<String, String> u2p = new HashMap<String, String>();

   public NamespaceResolver()
   {
      p2u.put(AtomCMIS.CMIS_PREFIX, AtomCMIS.CMIS_NS_URI);
      u2p.put(AtomCMIS.CMIS_NS_URI, AtomCMIS.CMIS_PREFIX);
      p2u.put(AtomCMIS.CMISRA_PREFIX, AtomCMIS.CMISRA_NS_URI);
      u2p.put(AtomCMIS.CMISRA_NS_URI, AtomCMIS.CMISRA_PREFIX);
      p2u.put("atom", "http://www.w3.org/2005/Atom");
      u2p.put("http://www.w3.org/2005/Atom", "atom");
      p2u.put("app", "http://www.w3.org/2007/app");
      u2p.put("http://www.w3.org/2007/app", "app");
   }

   public String getNamespaceURI(String prefix)
   {
      return p2u.get(prefix);
   }

   public String getPrefix(String namespaceURI)
   {
      return u2p.get(namespaceURI);
   }

   public Iterator<String> getPrefixes(String namespaceURI)
   {
      return Collections.singletonList(u2p.get(namespaceURI)).iterator();
   }

}
