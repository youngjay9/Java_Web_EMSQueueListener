package com.fet.mdm.dao.impl;

import com.fet.mdm.dao.EmsTaskDao;
import com.fet.common.model.DateConvert;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;









import oracle.jdbc.OraclePreparedStatement;

import org.apache.log4j.Logger;
import org.dom4j.Element;
import org.dom4j.Node;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.lob.OracleLobHandler;
import org.springframework.jdbc.support.lob.LobHandler;
import org.springframework.jdbc.core.PreparedStatementSetter;

import java.io.Writer;
import java.sql.Connection;
import java.sql.SQLException;









import oracle.jdbc.OracleConnection;
import oracle.sql.CLOB;

public class EmsTaskDaoImpl implements EmsTaskDao {

	private JdbcTemplate jdbcTemplate;
	
	private LobHandler lobHandler;
	
	private  Logger logger = Logger.getLogger(EmsTaskDaoImpl.class);
	
	private static final String sqlStr = "insert into ems_task(taskid,scheduledon,last_updtime,data,channel_type) values (?, ?, ?, ?, ?)";
	
	private static final String sqlStr2 = "insert into ems_task(taskid,scheduledon,last_updtime,data,channel_type) values (?, ?, ?, ?, ?)";
	
	public void setWmJdbcTemplate(JdbcTemplate jdbcTemplate){
		this.jdbcTemplate = jdbcTemplate;
	}
	
	public void setLobHandler(OracleLobHandler lobHandler){
		this.lobHandler = lobHandler;
	}
	
	public void insertTask(final String data) {
		
		try{
			
			final String taskId = UUID.randomUUID().toString().replaceAll("-", "");
			final java.sql.Timestamp date = new java.sql.Timestamp(new Date().getTime());
						
			final StringReader reader = new StringReader(data);
			
			jdbcTemplate.update(sqlStr, 
				new PreparedStatementSetter() {
			      public void setValues(PreparedStatement ps) throws SQLException {
			        ps.setString(1, taskId);
			        ps.setTimestamp(2, date);
			        ps.setTimestamp(3, date);
			        //只能用 setCharacterStream()才會成功寫入 clob 欄位
			        ps.setCharacterStream(4, reader, data.length());
//			        lobHandler.getLobCreator().setClobAsString(ps, 4,  data);
//			        lobHandler.getLobCreator().setClobAsCharacterStream(ps, 4, reader, length);
			        ps.setString(5, "NCP");
			      }
			    }
			);	
		}
		catch(Exception e){
			e.printStackTrace();
		}	
	}
	
	public void insertTaskWithIVR(final String data) {
		
		try{
			
			final String taskId = UUID.randomUUID().toString().replaceAll("-", "");
			final java.sql.Timestamp date = new java.sql.Timestamp(new Date().getTime());
						
			final StringReader reader = new StringReader(data);
			
			jdbcTemplate.update(sqlStr2, 
				new PreparedStatementSetter() {
			      public void setValues(PreparedStatement ps) throws SQLException {
			        ps.setString(1, taskId);
			        ps.setTimestamp(2, date);
			        ps.setTimestamp(3, date);
			        //只能用 setCharacterStream()才會成功寫入 clob 欄位
			        ps.setCharacterStream(4, reader, data.length());
			        ps.setString(5, "IVR");
			      }
			    }
			);	
		}
		catch(Exception e){
			e.printStackTrace();
		}	
	}
	
	public  String replace(String origin, String oldStr, String newStr) {
		StringBuffer buffer = new StringBuffer();
		int i = 0;
		int oldLength = oldStr.length();
		do {
			int j = origin.indexOf(oldStr, i);
			if (j >= 0) {
				buffer.append(origin.substring(i, j));
				buffer.append(newStr);
				i = j + oldLength;
			} else {
				buffer.append(origin.substring(i));
				return buffer.toString();
			}
		} while (true);
	}
	
	public String getCustomerNumberBySubscrId(long subscrId){
		
		String customerNumber = null;
		StringBuffer sqlStr = new StringBuffer();
		sqlStr.append("SELECT CI.CUSTOMER_NUMBER");
		sqlStr.append(" FROM CUSTOMER_INFO CI,CRM_MSIS CM ");
		sqlStr.append(" WHERE CI.CUSTOMER_NUMBER = CM.MSIS_CUSTOMER_NUM ");
		sqlStr.append(" AND CI.MOBILE_PHONE_NUM = CM.MSIS_MSISDN ");
		sqlStr.append(" AND CI.NUMBERFIELD2 = ").append(subscrId);
		
		
		List<Map<String, Object>> list = jdbcTemplate.queryForList(sqlStr.toString());
		if(list !=null && list.size()>0){
			list = list.subList(0, 1);
			Map<String, Object> row = list.get(0);
			customerNumber = (String)row.get("CUSTOMER_NUMBER");
		}
		
		return customerNumber;
	}
	
	public String getWMCustomerCommentByCustomerNumber(String customerNumber){
		String comment = null;
		
		StringBuffer sqlStr = new StringBuffer();
		sqlStr.append("select customer_comment from customer_info where customer_number =");
		sqlStr.append("'").append(customerNumber).append("'");
		
		logger.debug("getWMCustomerCommentByCustomerNumber_SQL==>"+sqlStr.toString());
		
		List<Map<String, Object>> list = jdbcTemplate.queryForList(sqlStr.toString());
		if(list !=null && list.size()>0){
			list = list.subList(0, 1);
			Map<String, Object> row = list.get(0);
			comment = (String)row.get("CUSTOMER_COMMENT");
		}
		
		return comment;
	}
	
	public String null2Str(Object o){
        if (o == null){
            return "";
        }else{
            return o.toString().trim();
        }
    }
	
	public void addCustomerInfoComments(Element rootElement){
		Node nodeSubsNum = rootElement.selectSingleNode("SubsNum");
		long subscriberID = Long.valueOf(nodeSubsNum.getStringValue());
		
		//Comments sended from EMS Queue
		Node nodeCustomerComment = rootElement.selectSingleNode("CustomerComment");
		String addComment = null2Str(nodeCustomerComment.getStringValue());
		//process Comments content
		addComment = replace(addComment, "'","''");
		
		//get WM customerNumber by subscriberId
		String customerNumber = getCustomerNumberBySubscrId(subscriberID);
		
		if(customerNumber!=null && !"".equals(customerNumber)){
			String newComment = null;
			
			//Comments from WM
			String old_comment = null2Str(getWMCustomerCommentByCustomerNumber(customerNumber));
			
			if(!"".equals(old_comment)){
				StringBuffer sb = new StringBuffer();
				sb.append(addComment).append("&").append(old_comment);
				newComment = sb.toString();
				int length = 0;
				try {
					length = newComment.getBytes("UTF-8").length;
					
					if(length > 250){
						length = 250;
					}
					newComment = newComment.substring(0, length);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			else{
				newComment = addComment;
			}
			
			java.util.Date date = new java.util.Date(System.currentTimeMillis());
			
			StringBuffer sql = new StringBuffer(); 
			sql.append(" UPDATE CUSTOMER_INFO SET CUSTOMER_COMMENT = '").append(null2Str(newComment)).append("'").append(",");
			sql.append(" STRINGFIELD10 = ").append("'").append(DateConvert.DateToStr(date, DateConvert.LongDatePattern)).append("'");
			sql.append(" where CUSTOMER_NUMBER = '").append(customerNumber).append("'");
			
			logger.info("addCustomerInfoComments sqlStr==>"+sql.toString());
			
			jdbcTemplate.update(sql.toString());
			
		}/* end if customerNumber != null */
	}
}
