package com.fet.mdm.service;

import javax.jms.JMSException;
import javax.jms.Message; 
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.fet.mdm.dao.EmsTaskDao;

public class ExampleListener implements MessageListener
{
  private static Log logger = LogFactory.getLog(ExampleListener.class);
  
  private EmsTaskDao taskDao;
  
  private static int counter = 0;
  
  private boolean quit = false;
  
  public void setEmsTaskDao(EmsTaskDao taskDao){
	  this.taskDao = taskDao;
  }
  
  public EmsTaskDao getEmsTaskDao(){
	  return taskDao;
  }

  public void onMessage(Message msg)
  {
    
    if (msg instanceof TextMessage)
    {
      try
      {
        
    	 String msgText;
	     if (msg instanceof TextMessage) {
	       msgText = ((TextMessage)msg).getText();
	     } else {
	       msgText = msg.toString();
	     }

	     System.out.println("Message Received: "+ msgText );
	     
	     
	     System.out.println("JayTest_TaskDao_isNull==>"+ (taskDao == null) );

	     taskDao.insertTask(msgText);
	     
	     if (msgText ==null || msgText.equalsIgnoreCase("")) {
	       synchronized(this) {
	         quit = true;
	         this.notifyAll(); // Notify main thread to quit
	       }
	     }
	     
	     System.out.println("JayTest_3");
      }
      catch (Exception ex)
      {
        ex.printStackTrace();
      }
    }
    else
    {
      logger.info("non-TextMessage");
    }
  }
}
