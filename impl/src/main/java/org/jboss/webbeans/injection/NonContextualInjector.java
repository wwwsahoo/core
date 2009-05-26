/*
 * JBoss, Home of Professional Open Source
 * Copyright 2008, Red Hat Middleware LLC, and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,  
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.webbeans.injection;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.Callable;

import javax.enterprise.context.Dependent;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.deployment.Standard;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.InjectionPoint;

import org.jboss.webbeans.ManagerImpl;
import org.jboss.webbeans.bean.BaseBean;
import org.jboss.webbeans.context.ApplicationContext;
import org.jboss.webbeans.context.DependentContext;
import org.jboss.webbeans.resources.ClassTransformer;
import org.jboss.webbeans.util.Beans;
import org.jboss.webbeans.util.collections.ConcurrentCache;

public class NonContextualInjector
{
   
   private final Bean<?> nonContextualBean;
   
   private final ConcurrentCache<Class<?>, Set<FieldInjectionPoint<?>>> instances;
   private final ManagerImpl manager;

   public NonContextualInjector(ManagerImpl manager)
   {
      this.instances = new ConcurrentCache<Class<?>, Set<FieldInjectionPoint<?>>>();
      this.manager = manager;
      nonContextualBean = new BaseBean<Object>(manager)
      {
         
         @Override
         public Set<Annotation> getBindings()
         {
            return Collections.emptySet();
         }

         @Override
         public Class<? extends Annotation> getDeploymentType()
         {
            return Standard.class;
         }

         @Override
         public Set<InjectionPoint> getInjectionPoints()
         {
            return Collections.emptySet();
         }

         @Override
         public String getName()
         {
            return null;
         }

         @Override
         public Class<? extends Annotation> getScopeType()
         {
            return Dependent.class;
         }

         @Override
         public Set<Type> getTypes()
         {
            return Collections.emptySet();
         }

         @Override
         public boolean isNullable()
         {
            return false;
         }

         @Override
         public boolean isSerializable()
         {
            return true;
         }

         public Object create(CreationalContext<Object> creationalContext)
         {
            return null;
         }

         public void destroy(Object instance)
         {
         }
      };
   }   
   
   public void inject(final Object instance)
   {
      if (DependentContext.instance() != null && ApplicationContext.instance() != null)
      {
         DependentContext.instance().setActive(true);
         boolean startApplication = !ApplicationContext.instance().isActive();
         if (startApplication)
         {
            ApplicationContext.instance().setActive(true);
         }
         Set<FieldInjectionPoint<?>> injectionPoints = instances.putIfAbsent(instance.getClass(), new Callable<Set<FieldInjectionPoint<?>>>()
         {
            
            public Set<FieldInjectionPoint<?>> call() throws Exception
            {
               return Beans.getFieldInjectionPoints(manager.getServices().get(ClassTransformer.class).classForName(instance.getClass()), nonContextualBean);
            }
            
         }
         );
         for (FieldInjectionPoint<?> injectionPoint : injectionPoints)
         {
            injectionPoint.inject(instance, manager, null);
         }
         DependentContext.instance().setActive(false);
         if (startApplication)
         {
            ApplicationContext.instance().setActive(false);
         }
      }
   }
   
}
