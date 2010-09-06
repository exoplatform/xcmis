/*
 * Copyright (C) 2009 eXo Platform SAS.
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
package org.xcmis.search.lucene.index;

import java.io.File;
import java.io.IOException;

/**
 * Created by The eXo Platform SAS. <br/>Date:
 * 
 * @author <a href="karpenko.sergiy@gmail.com">Karpenko Sergiy</a>
 * @version $Id: ConcurrentDirectoryFactory.java 2 2010-02-04 17:21:49Z andrew00x $
 */
public interface ConcurrentDirectoryFactory
{
   /**
    * Create file with locking of <code>dirCount</code> parent directories.
    * 
    * @param dir - parent directory of file
    * @param filename - name of file
    * @param dirCount - count of parent directories that will be locked during
    *          create file execution
    * @return File - created file
    * @throws IOException - I/O Exception
    * @throws TransactionLogException - if file already exist, or file or it's
    *           parent directory was not created (or not exist)
    */
   File createFile(File dir, String filename, int dirCount) throws IOException, TransactionLogException;

   /**
    * Try remove directory and parent directories. Maximum count of removed
    * directories is <code>dirCount</code>.
    * 
    * @param dir - directory to remove.
    * @param dirCount - maximum count of removed directories. <code>dir</code>
    *          counted to.
    * @throws TransactionLogException - if directory do not contain subfiles, but
    *           was not removed.
    */
   void removeDirectory(File dir, int dirCount) throws TransactionLogException;

}
