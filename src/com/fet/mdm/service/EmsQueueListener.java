package com.fet.mdm.service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;
import java.util.UUID;

import javax.jms.Message; 
import javax.jms.MessageListener;
import javax.jms.TextMessage;








import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.support.lob.LobHandler;
import org.springframework.jdbc.support.lob.OracleLobHandler;

import com.fet.mdm.dao.EmsTaskDao;

public class EmsQueueListener implements MessageListener
{
  private  Logger logger = Logger.getLogger(EmsQueueListener.class);
  
  private EmsTaskDao taskDao;
  
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
    	try{
        
    	 String msgText = null;
	     if (msg instanceof TextMessage) {
	       msgText = ((TextMessage)msg).getText();
	     } else {
	       msgText = msg.toString();
	     }

//	     logger.info("Message Received: "+ msgText );
	     
	     if(msgText!=null && !"".equals(msgText)){
	    	 StringReader in = new StringReader(msgText);
			 SAXReader reader = new SAXReader();
			 Document document = reader.read(in);
			 Element rootElmt = document.getRootElement();
			 
			 Node nodeComments = rootElmt.selectSingleNode("Comments");
			 Node nodeSysComments = rootElmt.selectSingleNode("SysComments");
			 Node nodeCustomerComment = rootElmt.selectSingleNode("CustomerComment");
			 Node nodeCreatedBy = rootElmt.selectSingleNode("CreatedBy");
			 Node nodeHandleBy = rootElmt.selectSingleNode("HandleBy");
			 
			 if(nodeComments != null || nodeSysComments != null || nodeCustomerComment!=null){
				 
				 String strCreatedBy = null;
				 
				 if(nodeCreatedBy != null){
					 strCreatedBy = nodeCreatedBy.getStringValue();
				 }
				 
				 String strHandleBy = null;
				 if(nodeHandleBy != null){
					 strHandleBy = nodeHandleBy.getStringValue();
				 }
				 
				 
				 
				 if("IVR".equals(strCreatedBy)){
//					 taskDao.insertTaskWithIVR(msgText);
					 logger.info("channel_type:IVR");
				 }
				 else{
					 if(!"NSP".equals(strHandleBy) && !"ESERV".equals(strHandleBy)){
						 logger.info(msgText);
						 
						 //contents of special remark directly inserted to customer_info 
						 if(nodeCustomerComment != null){
							 taskDao.addCustomerInfoComments(rootElmt);
						 }
						 else{
							 taskDao.insertTask(msgText);
						 }
						 
						 
						 logger.info("channel_type:NCP");
					 }
					 else{
						 logger.info("channel_type:Not NCP");
					 }
					
				 }
			 }
	     }
	     
	     
	     
	     if (msgText ==null || msgText.equalsIgnoreCase("")) {
	       synchronized(this) {
	         this.notifyAll(); // Notify main thread to quit
	       }
	     }
 
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
