package org.ow2.bonita.facade.uuid;
/**
 * Copyright (C) 2006  Bull S. A. S.
 * Bull, Rue Jean Jaures, B.P.68, 78340, Les Clayes-sous-Bois
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation
 * version 2.1 of the License.
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License along with this
 * program; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth
 * Floor, Boston, MA  02110-1301, USA.
 * 
 * Modified by Matthieu Chaffotte - BonitaSoft S.A.
 **/


/**
 * This class implements the UUID for {@link org.ow2.bonita.facade.def.majorElement.ProcessDefinition}
 */
public class ProcessDefinitionUUID extends AbstractUUID {

  private static final long serialVersionUID = -4872434654376672460L;
  
  protected ProcessDefinitionUUID() {
    super();
  }

  public ProcessDefinitionUUID(final ProcessDefinitionUUID src) {
    super(src);
  }

  public ProcessDefinitionUUID(final String value) {
    super(value);
  }

  public ProcessDefinitionUUID(final String processName, final String processVersion) {
    this(processName + SEPARATOR + processVersion);
  }

  @Deprecated
  public String getProcessName() {
    return value.split(SEPARATOR)[0];
  }

  @Deprecated
  public String getProcessVersion() {
    return value.split(SEPARATOR)[1];
  }

}
