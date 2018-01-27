package com.fet.common.model;

import java.io.Serializable;
import java.util.Date;
import oracle.sql.CLOB;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "EMS_TASK")
public class EmsTask implements Serializable,java.lang.Cloneable{
	
	private String taskId;
	
	private Date scheduleon;
	
	private String status;
	
	private int retry_times;
	
	private Date last_updtime;
	
	private CLOB data;
	
	
	@Column(name = "TASKID")
	public String getTaskId() {
		return taskId;
	}
	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}
	
	@Column(name = "SCHEDULEDON")
	public Date getScheduleon() {
		return scheduleon;
	}
	public void setScheduleon(Date scheduleon) {
		this.scheduleon = scheduleon;
	}
	
	@Column(name = "STATUS")
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
	@Column(name = "RETRY_TIMES")
	public int getRetry_times() {
		return retry_times;
	}
	public void setRetry_times(int retry_times) {
		this.retry_times = retry_times;
	}
	
	@Column(name = "LAST_UPDTIME")
	public Date getLast_updtime() {
		return last_updtime;
	}
	public void setLast_updtime(Date last_updtime) {
		this.last_updtime = last_updtime;
	}
	
	@Column(name = "DATA")
	public CLOB getData() {
		return data;
	}
	public void setData(CLOB data) {
		this.data = data;
	}
	
	
}
