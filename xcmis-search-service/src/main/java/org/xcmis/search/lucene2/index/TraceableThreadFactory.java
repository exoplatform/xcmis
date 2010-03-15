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
package org.xcmis.search.lucene2.index;

import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by The eXo Platform SAS.
 * 
 * @author <a href="mailto:Sergey.Kabashnyuk@gmail.com">Sergey Kabashnyuk</a>
 * @version $Id: TraceableThreadFactory.java 2 2010-02-04 17:21:49Z andrew00x $
 */
public class TraceableThreadFactory
{
   /**
    * Class logger.
    */
   private static final Log LOG = ExoLogger.getLogger(TraceableThreadFactory.class);

   private static final AtomicInteger factoryNumber = new AtomicInteger(1);

   private static List<ThreadGroup> activeThreadGroups = Collections.synchronizedList(new ArrayList<ThreadGroup>(1));

   private final ThreadGroup group;

   private String namePrefix;

   private final AtomicInteger threadNumber;

   private boolean threadDaemon;

   private int threadPriority;

   public TraceableThreadFactory()
   {
      this.group = new ThreadGroup("TraceableThreadGroup-" + factoryNumber.getAndIncrement());
      TraceableThreadFactory.activeThreadGroups.add(this.group);

      this.namePrefix = "TraceableThread-" + factoryNumber.getAndIncrement() + "-thread-";
      this.threadNumber = new AtomicInteger(1);
   }

   public String getNamePrefix()
   {
      return this.namePrefix;
   }

   public Thread newThread(Runnable r)
   {
      Thread thread = new Thread(group, r, namePrefix + threadNumber.getAndIncrement(), 0);
      thread.setDaemon(threadDaemon);
      thread.setPriority(threadPriority);

      return thread;
   }

   public void setNamePrefix(String namePrefix)
   {
      this.namePrefix = namePrefix;
   }

   /**
    * @param daemon <tt>true</tt> if all threads created must be daemon threads
    */
   public void setThreadDaemon(boolean daemon)
   {
      this.threadDaemon = daemon;
   }

   /**
    * @param threadPriority the threads priority from 1 (lowest) to 10 (highest)
    */
   public void setThreadPriority(int threadPriority)
   {
      this.threadPriority = threadPriority;
   }

   /**
    * Get a list of thread groups registered by the factory.
    * 
    * @return Returns a snapshot of thread groups
    */
   public static List<ThreadGroup> getActiveThreadGroups()
   {
      return activeThreadGroups;
   }
}
