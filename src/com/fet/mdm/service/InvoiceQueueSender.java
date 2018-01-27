package com.fet.mdm.service;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;


/**
 * Servlet implementation class InvoiceQueueSender
 */
public class InvoiceQueueSender{
	 private JmsTemplate jmsTemplate;
	 	    public void setJmsTemplate(JmsTemplate jmsTemplate) {
	 	        this.jmsTemplate = jmsTemplate;
	 	    }
	 	 
	 	    public void sendMesage() {
	 	        MessageCreator messageCreator=new MessageCreator() {
	 	        public Message createMessage(Session session) throws
	 	        JMSException {
	 	        return session.createTextMessage("I amsending Invoice message");}
	 	        };
	 	 
	 	        jmsTemplate.send("com.fet.esb.domain.crm.cie.cie2cti", messageCreator);
	 	    }
	 

}
