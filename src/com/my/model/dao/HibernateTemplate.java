package com.my.model.dao;

import java.io.InputStream;
import java.util.List;
import java.util.Properties;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import com.my.test.MediaTest;

public class HibernateTemplate {
	
	private static class SessionFactoyWrapper {
		
		private static SessionFactoyWrapper sessionFactoyWrapper_ = null;
		
		private static SessionFactory sessionFactory;
		
		public SessionFactory getSessionFactory(){
			return sessionFactory;
		}
		
		private SessionFactoyWrapper() {
			// TODO Auto-generated constructor stub
		}
		
		public static SessionFactoyWrapper init() {
			if (sessionFactoyWrapper_ == null){
				sessionFactoyWrapper_ = new SessionFactoyWrapper();
				sessionFactoyWrapper_.sessionFactory = xmlFileConfig();
			}
			
			return sessionFactoyWrapper_;
		}
		
		private static SessionFactory propFileConfig(){
			Configuration config = new Configuration();

			InputStream is =MediaTest.class.getResourceAsStream("hibernate.properties");
		
			//config.addClass(com.my.model.Category.class );
			config.addResource( "com/my/model/hbm/Category.hbm.xml" );
			config.addResource( "com/my/model/hbm/CategoryDetails.hbm.xml" );
			config.addResource( "com/my/model/hbm/Language.hbm.xml" );
			config.addResource( "com/my/model/hbm/Tone.hbm.xml" );
			config.addResource( "com/my/model/hbm/ToneCategory.hbm.xml" );
			config.addResource( "com/my/model/hbm/ToneDetails.hbm.xml" );
			
			Properties props = new Properties();
			try {
				
			    props.load(MediaTest.class.getResourceAsStream("hibernate.properties"));
			}catch(Exception e){
			    System.out.println("Error loading hibernate "+
			     "properties.");
			    e.printStackTrace();
			    System.exit(0);
			}
			
			config.setProperties( System.getProperties() );
			return config.buildSessionFactory();
		}
		
		private static SessionFactory xmlFileConfig(){
			
			//SessionFactory sessionFactory = new Configuration().configure().buildSessionFactory();
			
			// Called the configure() method on the org.hibernate.cfg.Configuration class without any arguments.
			//This tells Hibernate to look in the classpath for the configuration file.
			// The default name for the configuration file is hibernate.cfg.xml—if you change it, you 
			//will need to pass that name explicitly as an argument to the configure() method
			
			// your application should use one Hibernate SessionFactory object for each discrete database 
			// instance that it interacts with.
			// Session session = sessionFactory.openSession();
			SessionFactory sessionFactory = new Configuration().configure().buildSessionFactory();
			return sessionFactory;
		}
	}

	public List<Object> execute(HibernateCallBack hcb){
		
		SessionFactory sf  = SessionFactoyWrapper.init().getSessionFactory();
		Session sess = sf.openSession();
		
		try {
			sess.beginTransaction();
			List<Object> list = hcb.doQuery(sess);
			sess.getTransaction().commit();
			return list;
		}
		catch (HibernateException e) {
			if ( sess.getTransaction() != null )
				sess.getTransaction().rollback();
				throw e;
		}
		finally {
			sess.close();
		}
	}

}
