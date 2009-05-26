package org.jboss.webbeans.test.unit.xml.javaeepkg.foo;

import java.util.Date;

import javax.ejb.ScheduleExpression;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Initializer;
import javax.enterprise.inject.Named;
import javax.enterprise.inject.Produces;
import javax.event.Event;
import javax.interceptor.InvocationContext;
import javax.jms.Connection;
import javax.persistence.EntityManager;
import javax.sql.DataSource;
import javax.xml.ws.Binding;

@Named("orderBean")
@RequestScoped
public class Order
{
   private Integer integer; 
   
   private Date date;
   
   @Initializer
   public Order()
   {
      this(0, new Date(), null, null, null, null, null, null, null);
   }
   
   public Order(Integer integer, Date date, DataSource source, InvocationContext invocation, Event e, ScheduleExpression schedule, 
         EntityManager entityManager, Binding binding, Connection conn)
   {
      this.integer = integer;
      this.date = date;
   }
   
   @Produces
   public Order getOrder()
   {
      return new Order();
   }

   public Integer getInteger()
   {
      return integer;
   }

   public Date getDate()
   {
      return date;
   }
}
