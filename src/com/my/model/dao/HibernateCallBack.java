package com.my.model.dao;

import java.util.List;

import org.hibernate.Session;

public interface HibernateCallBack {

	List<Object> doQuery(Session s);
}
