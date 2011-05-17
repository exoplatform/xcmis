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
package org.ow2.bonita.facade.uuid;


/**
 * This class implements the UUID for {@link org.ow2.bonita.facade.runtime.ProcessInstance}
 */
public class ProcessInstanceUUID extends AbstractUUID {

  private static final long serialVersionUID = 5998937898749679099L;

  protected ProcessInstanceUUID() {
    super();
  }

  public ProcessInstanceUUID(final ProcessInstanceUUID src) {
    super(src);
  }

  public ProcessInstanceUUID(final String value) {
    super(value);
  }

  public ProcessInstanceUUID(final ProcessDefinitionUUID processUUID, final  long instanceNb) {
    this(processUUID + SEPARATOR + instanceNb);
  }

  @Deprecated
  public ProcessDefinitionUUID getProcessDefinitionUUID() {
    final String processUUID = value.substring(0, value.lastIndexOf(SEPARATOR));
    return new ProcessDefinitionUUID(processUUID);
  }

  @Deprecated
  public long getInstanceNb() {
    return new Long(value.substring(value.lastIndexOf(SEPARATOR) + SEPARATOR.length()));
  }

}
