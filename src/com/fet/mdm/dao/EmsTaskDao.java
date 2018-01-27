package com.fet.mdm.dao;

import org.dom4j.Element;

public interface EmsTaskDao {
	public void insertTask(String data);
	public void insertTaskWithIVR(String data);
	public void addCustomerInfoComments(Element rootElement);
}
