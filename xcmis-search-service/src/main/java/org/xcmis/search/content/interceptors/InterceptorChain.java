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
package org.xcmis.search.content.interceptors;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xcmis.search.content.command.InvocationContext;
import org.xcmis.search.content.command.VisitableCommand;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Knows how to build and manage an chain of interceptors. Also in charge with invoking methods on the chain.
 */
public class InterceptorChain
{
   /**
    * reference to the first interceptor in the chain
    */
   private CommandInterceptor firstInChain;

   private static final Log log = LogFactory.getLog(InterceptorChain.class);

   /**
    * Constructs an interceptor chain having the supplied interceptor as first.
    */
   public InterceptorChain(CommandInterceptor first)
   {
      this.firstInChain = first;
   }

   void printChainInfo()
   {
      if (log.isDebugEnabled())
      {
         log.debug("Interceptor chain is: " + toString());
      }
   }

   /**
    * Inserts the given interceptor at the specified position in the chain (o based indexing).
    *
    * @throws IllegalArgumentException if the position is invalid (e.g. 5 and there are only 2 interceptors in the chain)
    */
   public synchronized void addInterceptor(CommandInterceptor interceptor, int position)
   {
      if (position == 0)
      {
         interceptor.setNext(firstInChain);
         firstInChain = interceptor;
         return;
      }
      if (firstInChain == null)
      {
         return;
      }
      CommandInterceptor it = firstInChain;
      int index = 0;
      while (it != null)
      {
         if (++index == position)
         {
            interceptor.setNext(it.getNext());
            it.setNext(interceptor);
            return;
         }
         it = it.getNext();
      }
      throw new IllegalArgumentException("Invalid index: " + index + " !");
   }

   /**
    * Removes the interceptor at the given postion.
    *
    * @throws IllegalArgumentException if the position is invalid (e.g. 5 and there are only 2 interceptors in the chain)
    */
   public synchronized void removeInterceptor(int position)
   {
      if (firstInChain == null)
      {
         return;
      }
      if (position == 0)
      {
         firstInChain = firstInChain.getNext();
         return;
      }
      CommandInterceptor it = firstInChain;
      int index = 0;
      while (it != null)
      {
         if (++index == position)
         {
            if (it.getNext() == null)
            {
               return; //nothing to remove
            }
            it.setNext(it.getNext().getNext());
            return;
         }
         it = it.getNext();
      }
      throw new IllegalArgumentException("Invalid position: " + position + " !");
   }

   /**
    * Returns the number of interceptors in the chain.
    */
   public int size()
   {
      int size = 0;
      CommandInterceptor it = firstInChain;
      while (it != null)
      {
         size++;
         it = it.getNext();
      }
      return size;

   }

   public String getInterceptorDetails()
   {
      StringBuilder sb = new StringBuilder("Interceptor chain: \n");
      int count = 0;
      for (CommandInterceptor i : asList())
      {
         count++;
         sb.append("   ").append(count).append(". ").append(i).append("\n");
      }
      return sb.toString();
   }

   /**
   * Returns an unmofiable list with all the interceptors in sequence.
   * If first in chain is null an empty list is returned.
   */
   public List<CommandInterceptor> asList()
   {
      if (firstInChain == null)
      {
         return Collections.emptyList();
      }

      List<CommandInterceptor> retval = new LinkedList<CommandInterceptor>();
      CommandInterceptor tmp = firstInChain;
      do
      {
         retval.add(tmp);
         tmp = tmp.getNext();
      }
      while (tmp != null);
      return Collections.unmodifiableList(retval);
   }

   /**
    * Removes all the occurences of supplied interceptor type from the chain.
    */
   public synchronized void removeInterceptor(Class<? extends CommandInterceptor> clazz)
   {
      if (firstInChain.getClass() == clazz)
      {
         firstInChain = firstInChain.getNext();
      }
      CommandInterceptor it = firstInChain.getNext();
      CommandInterceptor prevIt = firstInChain;
      while (it != null)
      {
         if (it.getClass() == clazz)
         {
            prevIt.setNext(it.getNext());
         }
         prevIt = it;
         it = it.getNext();
      }
   }

   /**
    * Adds a new interceptor in list after an interceptor of a given type.
    *
    * @return true if the interceptor was added; i.e. the afterInterceptor exists
    */
   public synchronized boolean addAfterInterceptor(CommandInterceptor toAdd,
      Class<? extends CommandInterceptor> afterInterceptor)
   {
      CommandInterceptor it = firstInChain;
      while (it != null)
      {
         if (it.getClass().equals(afterInterceptor))
         {
            toAdd.setNext(it.getNext());
            it.setNext(toAdd);
            return true;
         }
         it = it.getNext();
      }
      return false;
   }

   /**
    * Adds a new interceptor in list after an interceptor of a given type.
    *
    * @return true if the interceptor was added; i.e. the afterInterceptor exists
    */
   public synchronized boolean addBeforeInterceptor(CommandInterceptor toAdd,
      Class<? extends CommandInterceptor> beforeInterceptor)
   {
      if (firstInChain.getClass().equals(beforeInterceptor))
      {
         toAdd.setNext(firstInChain);
         firstInChain = toAdd;
         return true;
      }
      CommandInterceptor it = firstInChain;
      while (it.getNext() != null)
      {
         if (it.getNext().getClass().equals(beforeInterceptor))
         {
            toAdd.setNext(it.getNext());
            it.setNext(toAdd);
            return true;
         }
         it = it.getNext();
      }
      return false;
   }

   /**
    * Appends at the end.
    */
   public void appendIntereceptor(CommandInterceptor ci)
   {
      CommandInterceptor it = firstInChain;
      while (it.hasNext())
      {
         it = it.getNext();
      }
      it.setNext(ci);
      // make sure we nullify the "next" pointer in the last interceptor.
      ci.setNext(null);
   }

   /**
    * Walks the command through the interceptor chain. The received ctx is being passed in.
    * @throws Throwable 
    */
   @SuppressWarnings("deprecation")
   public Object invoke(InvocationContext ctx, VisitableCommand command) throws Throwable
   {
      //ctx.setCommand(command);
      try
      {
         return command.acceptVisitor(ctx, firstInChain);
      }
      catch (InterruptedException ie)
      {
         Thread.currentThread().interrupt();
         return null;
      }
   }

   /**
    * @return the first interceptor in the chain.
    */
   public CommandInterceptor getFirstInChain()
   {
      return firstInChain;
   }

   /**
    * Mainly used by unit tests to replace the interceptor chain with the starting point passed in.
    *
    * @param interceptor interceptor to be used as the first interceptor in the chain.
    */
   public void setFirstInChain(CommandInterceptor interceptor)
   {
      this.firstInChain = interceptor;
   }

   /**
    * Returns all interceptors which extend the given command interceptor.
    */
   public List<CommandInterceptor> getInterceptorsWhichExtend(Class<? extends CommandInterceptor> interceptorClass)
   {
      List<CommandInterceptor> result = new ArrayList<CommandInterceptor>();
      for (CommandInterceptor interceptor : asList())
      {
         boolean isSubclass = interceptorClass.isAssignableFrom(interceptor.getClass());
         if (isSubclass)
         {
            result.add(interceptor);
         }
      }
      return result;
   }

   /**
    * Returns all the interceptors that have the fully qualified name of their class equal with the supplied class name.
    */
   public List<CommandInterceptor> getInterceptorsWithClassName(String fqName)
   {
      CommandInterceptor iterator = firstInChain;
      List<CommandInterceptor> result = new ArrayList<CommandInterceptor>(2);
      while (iterator != null)
      {
         if (iterator.getClass().getName().equals(fqName))
         {
            result.add(iterator);
         }
         iterator = iterator.getNext();
      }
      return result;
   }

   /**
    * Checks whether the chain contains the supplied interceptor instance.
    */
   public boolean containsInstance(CommandInterceptor interceptor)
   {
      CommandInterceptor it = firstInChain;
      while (it != null)
      {
         if (it == interceptor)
         {
            return true;
         }
         it = it.getNext();
      }
      return false;
   }

}
