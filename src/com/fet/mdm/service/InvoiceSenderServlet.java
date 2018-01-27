package com.fet.mdm.service;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;


/**
 * Servlet implementation class InvoiceSenderServlet
 */
public class InvoiceSenderServlet extends javax.servlet.http.HttpServlet  implements javax.servlet.Servlet{
	       

	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public InvoiceSenderServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}
	
	protected void service(HttpServletRequest request,
				          HttpServletResponse response) throws ServletException, IOException {
				         WebApplicationContext ctx = WebApplicationContextUtils
				        .getRequiredWebApplicationContext(this.getServletContext());
				 
				        InvoiceQueueSender sender = (InvoiceQueueSender) ctx
				                .getBean("jmsInvoiceSender");
				        sender.sendMesage();
				    }
			
}
