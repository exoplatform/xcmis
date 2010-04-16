package org.xcmis.client.gwt.restatom.sample.client;

import org.xcmis.client.gwt.restatom.sample.client.application.SampleForm;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.shared.HandlerManager;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class SampleApplication implements EntryPoint
{
   /**
    * @see com.google.gwt.core.client.EntryPoint#onModuleLoad()
    */
   public void onModuleLoad()
   {
      HandlerManager eventBus = new HandlerManager(null);
      /*Create main client form for displaying content*/
      new SampleForm(eventBus);
   }

}
