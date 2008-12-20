package org.jboss.webbeans.test.beans.broken;

import javax.webbeans.Event;
import javax.webbeans.Observable;
import javax.webbeans.Observer;

import org.jboss.webbeans.test.bindings.AnimalStereotypeAnnotationLiteral;

public class OwlFinch
{
   @Observable
   private Event<String> simpleEvent;

   public void methodThatFiresEvent()
   {
      simpleEvent.fire("An event", new AnimalStereotypeAnnotationLiteral());
   }

   public void methodThatRegistersObserver()
   {
      simpleEvent.observe(new Observer<String>()
      {
         public void notify(String event)
         {
         }
      }, new AnimalStereotypeAnnotationLiteral());
   }
}