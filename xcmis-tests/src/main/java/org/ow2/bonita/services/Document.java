package org.ow2.bonita.services;


import java.util.Date;

import org.ow2.bonita.facade.uuid.ProcessDefinitionUUID;
import org.ow2.bonita.facade.uuid.ProcessInstanceUUID;

public interface Document extends DocumentationObject {

  public String getAuthor();

  public Date getCreationDate();

  public String getLastModifiedBy();

  public Date getLastModificationDate();

  public boolean isLatestVersion();

  public boolean isMajorVersion();

  public String getVersionLabel();

  public String getVersionSeriesId();

  public String getContentMimeType();

  public String getContentFileName();

  public long getContentSize();
  
  public ProcessInstanceUUID getProcessInstanceUUID();

  public ProcessDefinitionUUID getProcessDefinitionUUID();
  
}
