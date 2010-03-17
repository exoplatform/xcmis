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
package org.xcmis.sp.jcr.exo.query;

import org.exoplatform.services.jcr.config.RepositoryConfigurationException;
import org.exoplatform.services.jcr.config.RepositoryEntry;
import org.exoplatform.services.jcr.config.WorkspaceEntry;
import org.exoplatform.services.jcr.dataflow.ItemStateChangesLog;
import org.exoplatform.services.jcr.dataflow.persistent.ItemsPersistenceListener;
import org.exoplatform.services.jcr.impl.dataflow.persistent.WorkspacePersistentDataManager;
import org.picocontainer.Startable;
import org.xcmis.search.content.command.InvocationContext;
import org.xcmis.search.content.command.read.GetChildNodesCommand;
import org.xcmis.search.content.command.read.GetNodeCommand;
import org.xcmis.search.content.command.read.GetPropertiesCommand;
import org.xcmis.search.content.command.read.GetPropertyCommand;
import org.xcmis.search.content.interceptors.ReadOnlyInterceptor;
import org.xcmis.sp.jcr.exo.RepositoriesManagerImpl;
import org.xcmis.sp.jcr.exo.RepositoryImpl;
import org.xcmis.spi.RepositoriesManager;

import javax.jcr.RepositoryException;

/**
 * Class that serves as a proxy for access to content and 
 * listening to the changes in it.
 */
public class ContentProxy extends ReadOnlyInterceptor implements ItemsPersistenceListener, Startable
{
   private final WorkspacePersistentDataManager workspacePersistentDataManager;

   private String cmisReposotoryId;

   private final RepositoriesManagerImpl repositoriesManager;

   /**
    * @param workspacePersistentDataManager
    * @throws RepositoryConfigurationException 
    * @throws RepositoryException 
    */
   public ContentProxy(final RepositoryEntry repositoryEntry, final WorkspaceEntry workspaceEntry,
      final RepositoriesManager cmisRepositoriesManager, final RepositoriesManagerImpl repositoriesManager,
      WorkspacePersistentDataManager workspacePersistentDataManager) throws RepositoryException,
      RepositoryConfigurationException
   {
      super();
      this.repositoriesManager = repositoriesManager;
      this.workspacePersistentDataManager = workspacePersistentDataManager;

      //register proxi in cmis repository
      RepositoryImpl cmisRepository =
         (RepositoryImpl)((RepositoriesManagerImpl)cmisRepositoriesManager).getRepository(repositoryEntry.getName(),
            workspaceEntry.getName());
      this.cmisReposotoryId = cmisRepository.getId();

      //register listener of changes.
      workspacePersistentDataManager.addItemPersistenceListener(this);
   }

   /**
    * @see org.xcmis.search.content.interceptors.ReadOnlyInterceptor#visitGetChildNodesCommand(org.xcmis.search.content.command.InvocationContext, org.xcmis.search.content.command.read.GetChildNodesCommand)
    */
   @Override
   public Object visitGetChildNodesCommand(InvocationContext ctx, GetChildNodesCommand command) throws Throwable
   {

      return null;
   }

   /**
    * @see org.xcmis.search.content.interceptors.ReadOnlyInterceptor#visitGetNodeCommand(org.xcmis.search.content.command.InvocationContext, org.xcmis.search.content.command.read.GetNodeCommand)
    */
   @Override
   public Object visitGetNodeCommand(InvocationContext ctx, GetNodeCommand command) throws Throwable
   {
      // TODO Auto-generated method stub
      return null;
   }

   /**
    * @see org.xcmis.search.content.interceptors.ReadOnlyInterceptor#visitGetPropertiesCommand(org.xcmis.search.content.command.InvocationContext, org.xcmis.search.content.command.read.GetPropertiesCommand)
    */
   @Override
   public Object visitGetPropertiesCommand(InvocationContext ctx, GetPropertiesCommand command) throws Throwable
   {
      // TODO Auto-generated method stub
      return null;
   }

   /**
    * @see org.xcmis.search.content.interceptors.ReadOnlyInterceptor#visitGetPropertyCommand(org.xcmis.search.content.command.InvocationContext, org.xcmis.search.content.command.read.GetPropertyCommand)
    */
   @Override
   public Object visitGetPropertyCommand(InvocationContext ctx, GetPropertyCommand command) throws Throwable
   {
      // TODO Auto-generated method stub
      return null;
   }

   /**
    * @see org.exoplatform.services.jcr.dataflow.persistent.ItemsPersistenceListener#isTXAware()
    */
   public boolean isTXAware()
   {
      // TODO Auto-generated method stub
      return false;
   }

   /**
    * @see org.exoplatform.services.jcr.dataflow.persistent.ItemsPersistenceListener#onSaveItems(org.exoplatform.services.jcr.dataflow.ItemStateChangesLog)
    */
   public void onSaveItems(ItemStateChangesLog itemStates)
   {

   }

   /**
    * @see org.picocontainer.Startable#start()
    */
   public void start()
   {
      repositoriesManager.addIndexService(cmisReposotoryId, this);

   }

   /**
    * @see org.picocontainer.Startable#stop()
    */
   public void stop()
   {
      // TODO Auto-generated method stub

   }

}
