package com.my.test;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.map.HashedMap;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.Hibernate;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.transform.DistinctRootEntityResultTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.my.model.Category;
import com.my.model.CategoryDetails;
import com.my.model.Language;
import com.my.model.ToneCategory;
import com.my.model.dao.HibernateCallBack;
import com.my.model.dao.HibernateTemplate;

public class MediaTest {
	
	/**
	 * TIPS:
	 * 1. -You cannot use the not-equal restriction to retrieve records with a NULL value in the database for that
		property (in SQL, and therefore in Hibernate, NULL represents the absence of data, and so cannot be compared
		with data)
		
		2. - WHEN to use Criteria
			- Only if you need to dynamically build queries
	 */
	
	/* --- JARS Needed for Hibernate only - not sure about the slf4j ones ---
	 * antlr-2.7.6.jar
	 * commons-collections-3.1.jar
	 * dom4j-1.6.1.jar
	 * hibernate3.jar
	 * javassist-3.9.0.GA.jar
	 * jta-1.1.jar
	 * slf4j-api-1.5.8.jar
	 * slf4j-api-1.6.1.jar
	 * slf4j-simple-1.6.1.jar
	 * 
	 * --- JARS needed for Spring (not already included for hibernate) ---
	 * spring.jar
	 * commons-logging-1.1.1.jar
	 */
	
	/*
	 * RETRIEVING OBJECTS
	 * get objects out of the database in the following ways:
	 * - starting from an already loaded object access the associated objects through
	 *   property accessor methods: parent.getParent();
	 * - Retrieving by identifier if the unique identifier is known
	 * - Using the Hibernate Query Language (HQL),
	 * 		Supports the ability to apply restrictions to properties of associated objects 
	 * 		related by reference or held in collections (to navigate the object graph 
	 * 		using query language).
	 * - Using the, Hibernate Criteria API, which provides a type-safe and object oriented way
	 * 		lets you specify constraints dynamically without direct string manipulations,
	 * 		often less readable than queries expressed in HQL.
	 * 	 
	 * - Using native SQL queries,
	 
	 *  --fetching strategy—-, 
	 *  a strategy that defines what part of the persistent object graph should be retrieved.
	 * 
	 *  Hibernate lets you specify a default fetching strategy in the mapping file 
	 *  and then override it at runtime in code.
	 *  
	 *  - Immediate fetching—The associated object is fetched immediately, using a 
	 *    sequential (versus joins) database read (or cache lookup).
	 *    	- lazy="false"
	 *  - Lazy fetching—The associated object or collection is fetched “lazily,” 
	 *    when it’s first accessed. This results in a new request to the database 
	 *    (unless the associated object is cached).
	 *    	- When lazy fetching is used, hibernate uses proxies as placeholders for
	 *    	  lazily fetched collection or single entity association.
	 *    	- to determine whether a proxy, a persistence collection, or an attribute 
	 *    	  has been lazy loaded or not, you can call the isInitialized() and 
	 *    	  isPropertyInitialized()
	 *     - You can also force a proxy or collection to become fully populated 
	 *     	 by calling the initialize()
	 *     - lazy="true"
	 *  
	 *  - Eager fetching—The associated object or collection is fetched together with 
	 *    the owning object, using an SQL outer join, and no further database request 
	 *    is required.
	 *    	- lazy="false" together with fetch="join"
	 *  
	 *  

	 * TRANSACTIONS
	 * 	- When NOT USING TRANSACTION
	 * 		need to invoke the flush() method on the session at appropriate points to ensure 
	 * 		that your changes are persisted to the database
	 *  
	 *  - The isolation levels permitted by JDBC and Hibernate are
	 *  	1 Read Uncommitted - Dirty, nonrepeatable, and phantom reads are permitted.
	 *  	2 Read Committed  - Nonrepeatable reads and phantom reads are permitted.
	 *  	4 Repeatable Read - Phantom reads are permitted.
	 *  	8 Serializable  - The rule must be obeyed absolutely.
	 *  
	 *  	- dirty - one transaction could read uncommitted data of another transaction. If the changes are
	 *  		rolled-back, the data obtained by the first transaction will become invalid
	 *  	- Non-repeatable - another transaction reads committed data, but by the time it queries again
	 *  		that same query results in different data
	 *  	- Phantom - sees different numbers of rows for the same query without having made any chances
	 *  
	 *  LOCKING
	 *  	- during updates the database obtains a lock on the respective table/data before it starts the operation
	 *  	- used to implement isolation 
	 *  	- optimistic locking - the DB acquires a lock on that data for the momentary operation on the 
	 *  	data only.
	 *  	- pessimistic locking - lock retained until the end of the transaction
	 *  	
	 *  
	 *  - DECLARATIVE Transaction Management
	 *  	- Instead in declaring transaction boundaries directly as in My Hibernate template
	 *  		use of declarative transaction management
	 *  		- With this, the beans methods are marked as being the boundaries of transactions
	 *  	- To support transactional behavior, a transaction manager bean must first be applied to
	 *  		the session factory
	 *  		- a HibernateTransactionManager in a self-contained application,
	 *  		- JtaTransactionManager in an environment in which the container is managing transactions
	 *  
	 *  
	 *  - TRANSACTION PROPAGATION RULES
	 *  	- This describes whether or not a transaction must already exists when a method is called and what
	 *  	- should happen (throw exception, suspend) 
	 *  	- for example, PROPAGATION_REQUIRED can be explained as follows:
	 *  		by calling to another service from a transactional method of your first service) second 
	 *  		transaction will participate in your first transaction and will reuse all resources of that 
	 *  		one (i.e. DB connection, etc.). 
	 *  
	 *  - TRANSACTION ATTRIBUTES
	 *  	- describes how transaction policy should be applied
	 *  	- includes the following parameters
	 *  		- Propagation behavior
	 *  		- Isolation level
	 *  		- Read-only hints
	 *  		- The transaction timeout period
	 *  		Example:
	 *  		<prop key="create*">
						PROPAGATION_REQUIRED,ISOLATION_READ_COMMITTED, -ServiceException
				</prop>
				*Propagation behavior*
				PROPAGATION_MANDATORY - 
				If no existing transaction is in progress when the method is called, an 
				exception will be thrown.
				
				PROPAGATION_REQUIRES_NEW
				Create a new transaction, suspending the current transaction if one exists. 
				
				PROPAGATION_REQUIRED
				Support a current transaction; create a new one if none exists.
				
				PROPAGATION_SUPPORTS
				Support a current transaction; execute non-transactionally if none exists. 
				
				PROPAGATION_NOT_SUPPORTED
				Do not support a current transaction; rather always execute non-transactionally.
				
				
				--------Isolation level-----------
				ISOLATION_DEFAULT - Use the default isolation level of the underlying data store
				ISOLATION_READ_UNCOMMITTED - allows uncommitted reads
					- This isolation level may be implemented using exclusive write locks.
				ISOLATION_READ_COMMITTED - allows reads that have been committed, but there is no
					guarantee that subsequent reads of the same data will yield the same result. 
					- This may be achieved using momentary shared read locks and exclusive write locks.
						Reading transactions don’t block other transactions from accessing a row.
						However, an uncommitted writing transaction blocks all other transactions 
						from accessing the row.
				ISOLATION_REPEATABLE_READ - multiple reads of the same data should yield the same result.
					However, multiple reads of multiple rows may yield different number of rows.
					This may be achieved using shared read locks and exclusive write locks. Reading transactions
					block writing transactions (but not other reading transactions), and writing transactions block
					all other transactions.
				ISOLATION_SERIALIZABLE - full table locks on all tables involve in the transaction.
				
				
				--------Roll back rules-----------
				By default, transactions are rolled back only on runtime exceptions and not on checked 
				exceptions. you can specify that a transaction be rolled back on specific checked
				exceptions as well
				
				-------Read-Only------------------
				Provides a hint to the database that this is a read only transaction allowing it to
				perform optimization as necessary.
				
				
				MORE NOTES
				----------
				Second lost updates problem—A special case of an unrepeatable read. Imagine 
				that two concurrent transactions both read a row, one writes to it and commits,
				and then the second writes to it and commits. The changes made by the first writer 
				are lost. 
				
				This can happen because in the isolation level, there is no shared read locks which
				would block writing. A write lock will be obtained preventing further reading, but
				since the second transaction had already read the data.
				
				A pessimistic lock is a lock that is acquired when an item of data is read and 
				that is held until transaction completion.
				
				Our preferred transaction isolation level (Hibernate In Action)
				- Is read committed. The database never acquires pessimistic locks unless 
				  explicitly requested by the application.
				
				The Hibernate LockMode class lets you request a pessimistic lock on 
				a particular item.
				
				Transaction tx = session.beginTransaction();
				Category cat =
				(Category) session.get(Category.class, catId, LockMode.UPGRADE);
				cat.setName("New Name");
				tx.commit();
				
				Hibernate will load the Category using a SELECT...FOR UPDATE,
				
				
				-------- CACHING ----------
				A cache keeps a representation of current database state close to the 
				application, either in memory or on disk of the application server machine.
				
				Using the first-level cache
				The session cache ensures that when the application requests the same persistent
				object twice in a particular session, it gets back the same (identical) Java instance.
				
				The Hibernate second-level cache
				Cache is shared by all sessions. The data is stored in a serialized from as
				opposed to Java Object as in the first level cache.
				
				Entities that are rarely updated are great candidate for caching
	 */
	
	/* HIBERNATE  
	 * 
	 * Modeling Sub Types
	 * - Table per class hierarchy
	 *		- create one table and use a type discriminator column to hold type information
	 *		- This is a good approach
	 *		- There is one major problem: Columns for properties declared by subclasses must
	 *		  be declared to be nullable.
	 *		- In Hibernate, we use the <subclass> element to indicate a table-per-class 
	 *		  hierarchy mapping,
	 *	Choose this approach....
	 * - Table per concrete class
	 * 		create a table for each concrete type (forget about class hierarchy)
	 * - Table per subclass
	 * 		create a base class table and refer to this table from tables of sub-types
	 * 
	 * Components
	 * Hibernate uses the term component for a user-defined class that is persisted to the same 
	 * table as the owning entity,
	 * 	<component> </component>
	 * 
	 * Managed associations
	 * Case Study: Auction (Container Managed Relationship)
	 * 		An Item can have zero or more Bids
	 * 		A Bid must be for one and only one item.
	 * 		do this 
	 * 			bid.setItem(item);
	 * 		and the container automatically calls item.getBids().add(item).
	 * 	Hibernate do not implement managed associations.
	 * 		Hibernate associations are all inherently unidirectional.
	 * The association from Bid to Item is a many-to-one association.
	 * 
	 * 	<class
			name="Bid"
			table="BID">
			...
			<many-to-one
			name="item"
			column="ITEM_ID"
			class="Item"
			not-null="true"/>
		</class>
	
	 * 	
	 * The Association from Item to Bid a one-to-many association.
	 * We also need to be able to easily fetch all the bids for a particular item.
		 public class Item {
			...
			private Set bids = new HashSet();
			public void setBids(Set bids) {
			this.bids = bids;
			}
			public Set getBids() {
			return bids;
			}
			public void addBid(Bid bid) {
			bid.setItem(this);
			bids.add(bid);
			}
			...
		}
	 *
	 *	mapping for this one-to-many association would look like this
		<class
			name="Item"
			table="ITEM">
			...
			<set name="bids" inverse="true">
			<key column="ITEM_ID"/>
			<one-to-many class="Bid"/>
			</set>
		</class>
	 *
	 *	inverse="true"
	 *  Consider what will happen if we leave out inverse="true"
	 *  	we would have two different unidirectional associations mapped to the same foreign key
	 *  	At runtime, there are two different in-memory representations of the same foreign key value:
	 *  	the item property of Bid and an element of the bids collection held by an Item.
	 *  	application modifies the association by, adding a bid to an item
	 *  		addBid()
	 *  			bid.setItem(item);
	 *  			bids.add(bid);
	 *  	Hibernate will detect two different changes to the in-memory persistent instances.
	 *  The inverse attribute tells Hibernate that the collection is a mirror image of the 
	 *  many-to-one association on the other side:
	 *  
	 *  Without the inverse attribute, Hibernate would try to execute two different SQL 
	 *  statements, both updating the same foreign key column, when we manipulate the 
	 *  association between the two instances.
	 *  
	 *  Here we tell Hibernate that it should propagate changes made at the Bid end of 
	 *  the association to the database, ignoring changes made only to the bids collection.
	 *  
	 *  if we only call item.getBids().add(bid), no changes will be made persistent.
	 *  	
	 *  -- cascading save and cascading delete --
	 *  When we instantiate a new Bid and add it to an Item, the bid should become 
	 *  persistent immediately.
	 *  
	 *  We would like to avoid the need to explicitly make a Bid persistent by calling 
	 *  save() on the Session interface.
	 *  
	 *  To accomplish this use cascade="save-update"
	 *  
	 *  	<class
				name="Item"
				table="ITEM">
				...
				<set
					name="bids"
					inverse="true"
					cascade="save-update">
					<key column="ITEM_ID"/>
					<one-to-many class="Bid"/>
				</set>
			</class>
	 *
	 *	The cascade attribute tells Hibernate to make any new Bid instance persistent 
	 *	(that is, save it in the database) if the Bid is referenced by a persistent Item.
	 *
	 *	- lifecycle of related entities --
	 *	A particular bid instance references only one item instance for its entire lifetime.
	 *  In this case, cascading both saves and deletions makes sense.
	 *  
	 *  If we enable cascading delete, the association between Item and Bid is called a 
	 *  parent/child relationship.
	 *  
	 *  In a parent/child relationship, the parent entity is responsible for the lifecycle 
	 *  of its associated child entities.
	 *  
	 *  To remodel the Item to Bid association as a parent/child relationship use
	 *  	cascade="all-delete-orphan"
	 *  used cascade="all-delete-orphan" to indicate the following:
	 *  	- newly instantiated Bid becomes persistent if the Bid is referenced by a 
	 *  		persistent Item
	 *  	- Any persistent Bid should be deleted if it’s referenced by an Item when
	 *  	 the item is deleted.
	 *  	- Any persistent Bid should be deleted if it’s removed from the bids 
	 *  	collection of a persistent Item.
	 *  
	 *  --- Implementing equals() and hashCode() ---
	 *  Java collections call equal() for compare contained objects
	 *  The default equals(), defined by java.lang.Object uses a comparison by Java identity
	 *  Hibernate guarantees that there is a unique instance for each row of the database inside a Session.
	 *  Mixing detached objects from different sessions is problematic. 
	 *  
	 *  You have to override equals() in your persistent classes.
	 *  
	 *  When you override equals(), you always need to also override hashCode() so the 
	 *  two methods are consistent (if two objects are equal, they must have the same hashcode)
	 *  
	 *  Using business key equality is the best approach.
	 *  A business key is a property, or some combination of properties, that is unique 
	 *  for each instance with the same database identity.
	 *  
	 *  For example, for Category the categoryIdentifier is unique.
	 *  Note that in RBT Admin, the Id of the Category (the primary key) is used
	 *  in the equal() method. This has the draw back that Hibernate doesn’t assign 
	 *  identifier values until an entity is saved. So, if the object is added to a 
	 *  Set before being saved, its hash code changes while it’s contained by the Set,
	 *  
	 *  Making an object persistent -----
	 *  Use the save() method
	 *  	- Hibernate obtains a JDBC connection and issue a single insert statement, but 
	 *  	only after you call commit()
	 *  		- Note that the insert statement contains only the values that were in the object
	 *  		  before save() was called.
	 */
	final Logger logger = LoggerFactory.getLogger(MediaTest.class);
	
	final static HibernateTemplate hibernateTemplate = new HibernateTemplate();
	private static ApplicationContext ctx = new ClassPathXmlApplicationContext("applicationContext.xml");
	
	@SuppressWarnings("unchecked")
	public static void main(String[] args) {
		
		MediaTest m = new MediaTest();
		//CategoryDaoImpl.createTest();
		//leftJoinTest();
		//CategoryDaoImpl.addChildrenTest();
		//CategoryDaoImpl.addParentTest();
		//CategoryDaoImpl.explicitTransactionAddChildrenTest();
		//m.listCategoriesTest();
		m.sqlJoinTest();
		//m.leftJoinTest();
	}

	/*=======================================================================================*/
	/* 				Using Hibernate without Spring Integration 
	/*=======================================================================================*/
	/*
	 * 
	 * The Hibernate Template is a template class I have created. It essentially adds transaction
	 * calls (open/close session, rollback) around calls to the database. This is a way to 
	 * centralize transaction context instead of having to write transaction context code
	 * everywhere you make database calls. The execute() method takes a HibernateCallBack type
	 * It will get a session, begin a transaction then invoke the doQuery() method. It will
	 * either commit or rollback the transaction depending on whether complete successfully or
	 * not.
	 */
	@SuppressWarnings("unchecked")
	List<Object> listTones(){
		
		return hibernateTemplate.execute(new HibernateCallBack() {
			
			/*
			 * If you want to select entire object - leave off the select clause
			 * - can then access property values using getter methods of the class
			 */
			String q = "from com.my.model.Tone t where deleted = 0 and expiryDate < sysdate or expiryDate is null";
			
			/*
			 * Can you want to specify the model class without the fully qualified name ?
			 * - Seems that you need to use fully qualified. Need to verify
			 */
			
			/*
			 * If you want to select few properties of an object then use the select clause
			 * - less network traffic and memory than getting entire object
			 * - (select distribution_status from Tone t where deleted = 1)
			 */
			
			public List<Object> doQuery(Session s) {
				return s.createQuery(q).list();
			}
		});
	}
	
	@SuppressWarnings("unchecked")
	public List listCategories(final String q){
		return hibernateTemplate.execute(new HibernateCallBack() {
			
			public List<Object> doQuery(Session s) {
				List l = s.createQuery(q).setResultTransformer(DistinctRootEntityResultTransformer.INSTANCE).list();
				return l;
			}
		});
	}
	
	public  void listCategoriesTest(){
		List<Category> c = listCategories("from com.my.model.Category p inner join fetch p.toneCategories tc where p.id = 86");
		for (Category category : c) {
			for(ToneCategory tc : category.getToneCategories()){
				System.out.println(tc.getId());
				//System.out.println(tc.getTone().getProductIdentifier());
			}
		}
	}
	/*============================================*/
	/* 		Hibernate Named Parameters		 		*/
	/*============================================*/
	/*
	 * 	Named parameter is similar to JDBC query parameters, but less error prone.
	 */
	@SuppressWarnings("unchecked")
	public List listCategories2(){
		return hibernateTemplate.execute(new HibernateCallBack() {
			
			public List<Object> doQuery(Session s) {
				Query q = s.createQuery("from com.my.model.Category where dtmf > :dtmf");
				// All occurrences of 'dtmf' will be replace with the value zero (0)
				q.setInteger("dtmf", 0);
				return q.list();
			}
		});
	}
	
	/*============================================*/
	/* 	JDBC query parameters		 		*/
	/*============================================*/
	/*
	 * 	Is similar to Hibernate named parameters but less manageable because in the 
	 *  setter method values are being bound to position instead of a name
	 */
	@SuppressWarnings("unchecked")
	public List listCategories3(){
		return hibernateTemplate.execute(new HibernateCallBack() {
			
			public List<Object> doQuery(Session s) {
				Query q = s.createQuery("from com.my.model.Category where dtmf > ?");
				q.setInteger(0, 0);
				return q.list();
			}
		});
	}
	
	/*============================================*/
	/* 	Object Type Parameter Values		 	*/
	/*============================================*/
	/*
	 * 	To provide objects as values for named parameters, Query interface has a setEntity() 
	 * 	method that takes the name of a parameter and an object (database object)
	 */
	@SuppressWarnings("unchecked")
	public List listCategories4(){
		return hibernateTemplate.execute(new HibernateCallBack() {
			
			public List<Object> doQuery(Session s) {
				Query q = s.createQuery("from com.my.model.Category where categoryIdentifier = 'Masko'");
				Category c = (Category)q.list().get(0);
				
				q = s.createQuery("from com.my.model.Category where parent = :parent");
				q.setEntity("parent", c);
				return q.list();
			}
		});
	}
	
	/*============================================*/
	/* 	Paging Thru ResultSet		 	*/
	/*============================================*/
	/*
	 *  Paging through the result set allows you to limit the size of the result set
	 *  Use the setFirstResult() to indicate the first row and setMaxResults() to
	 *  indicate the number of rows to return.
	 */
	@SuppressWarnings("unchecked")
	public List listCategories5(){
		return hibernateTemplate.execute(new HibernateCallBack() {
			
			public List<Object> doQuery(Session s) {
				Query q = s.createQuery("from com.my.model.Category");
				q.setFirstResult(4);
				q.setMaxResults(2);
				return q.list();
			}
		});
	}
	
	/*============================================*/
	/* 	Unique Result						 	*/
	/*============================================*/
	/*
	 * 	The uniqueResult() method on the Query object returns a single object, or null if 
	 * 	there are zero results.
	 * 	If there is more than one result, the uniqueResult() method throws a 
	 * 	NonUniqueResultException.
	 */
	@SuppressWarnings("unchecked")
	public List listCategories6(){
		return hibernateTemplate.execute(new HibernateCallBack() {
			
			public List<Object> doQuery(Session s) {
				Query q = s.createQuery("from com.my.model.Category where categoryIdentifier = 'Masko'");
				q.setMaxResults(1);
				Category c = (Category)q.uniqueResult();
				
				q = s.createQuery("from com.my.model.Category where parent = :parent");
				q.setEntity("parent", c);
				return q.list();
			}
		});
	}
	
	/*============================================*/
	/* 	Sorting								 	*/
	/*============================================*/
	/*	To sort the result, use the order by clause.
	 * 	Default is ascending
	 */
	@SuppressWarnings("unchecked")
	public List listCategories7(){
		return hibernateTemplate.execute(new HibernateCallBack() {
			
			public List<Object> doQuery(Session s) {
				Query q = s.createQuery("from com.my.model.Category order by categoryIdentifier desc");
				return q.list();
			}
		});
	}
	
	/*============================================*/
	/* 	Select Clause						 	*/
	/*============================================*/
	/*
	 * If it is not necessary to get entire objects, use Project to select required 
	 * columns instead of the entire object. This approach allow you to reduce network 
	 * traffic to the database server and save memory on the applicationâ€™s machine.
	 * @return
	 * 		The result in this example will contain a List of String objects
	 */
	@SuppressWarnings("unchecked")
	public List listCategories8(){
		return hibernateTemplate.execute(new HibernateCallBack() {
			
			public List<Object> doQuery(Session s) {
				Query q = s.createQuery(" select categoryIdentifier from com.my.model.Category");
				return q.list();
			}
		});
	}
	/*============================================*/
	/* 	Multi-Columns Select						 */
	/*============================================*/
	/*
	 * @return
	 * 		This result set contains a List of Object arrays
	 */
	public static List listCategories9(){
		return hibernateTemplate.execute(new HibernateCallBack() {
			
			public List<Object> doQuery(Session s) {
				Query q = s.createQuery(" select categoryIdentifier, id from com.my.model.Category");
				return q.list();
			}
		});
	}
	
	public static void multiColumnSelectTest(){
		List l = listCategories9();
		for (Object object : l) {
			Object[] x = (Object[])object;
			System.out.println(x[0] + ":" + x[1]);
		}
	}
	
	/*============================================*/
	/* 	Inner Join								 */
	/*============================================*/
	/*
	 * All rows meeting the join criteria will be contained in the result set.
	 * 	Notice that Hibernate does not return A list of objects in the result set; instead, 
	 * 	Hibernate returns a list Object arrays in the results because instances of all entity
	 * 	in the join are returned
	 */
	public static List listCategories10(){
		return hibernateTemplate.execute(new HibernateCallBack() {
			
			public List<Object> doQuery(Session s) {
				Query q = s.createQuery("from com.my.model.Category c inner join c.parent as parent");
				return q.list();
			}
		});
	}
	
	public static void innerJoinTest(){
		@SuppressWarnings("rawtypes")
		List l = listCategories10();
		for (Object object : l) {
			Object[] x = (Object[])object;
			System.out.println( ((Category)x[0]).getDisplayName() + "-" + ((Category)x[1]).getDisplayName());
		}
		
	}
	
	
	
	/*============================================*/
	/* 	Inner Join Fecth							*/
	/*============================================*/
	/*
	 * 	Fetch allows Hibernate to retrieve the objects in the collection at the time the query 
	 * 	executes. Lazy loading is overridden if set in mapping file. Only instances of the 
	 * 	first Entity are returned. Access instance of the fetched entities through the first. 
	 * 	Here parent instances will be accessed through the Category instance.
	 * 
	 * - To override lazy loading of collections
	 * 
	 * 
	 * TIP:
	 * If you use join without fetch the associated entities are returned in the result set as separate
	 * entities. Furthermore, the associated entity (collection) is not initialized on the root entity
	 * and the collection list/set will be set to null.
	 * When you use fetch this is not the case. The associated entities must be accessed via the root
	 * entity. Associated entity will be initialized in the root entity.
	 */
	public static  List listCategories11(){
		return hibernateTemplate.execute(new HibernateCallBack() {
			
			public List<Object> doQuery(Session s) {
				Query q = s.createQuery("from com.my.model.Category c inner join fetch c.children as children");
				//Query q = s.createQuery("from com.my.model.Category c inner join c.children as children");
				return q.list();
			}
		});
	}
	
	public static void fecthJoinTest(){
		@SuppressWarnings("rawtypes")
		List l = listCategories11();
		for (Object object : l) {
			Category c = (Category)object;
			System.out.println(c.getDisplayName() );
		}
	}
	
	/*============================================*/
	/* 	Distinct Results/LEFT OUTER JOIN		*/
	/*============================================*/
	/*
	 * - LEFT OUTER JOIN (Not only row where the join criteria is met, but all rows where the left hand 
	 * 			entity join parameter is null)
	 * 		- LEFT JOIN ensures that even if categories (entity on the left) has a NULL value for parent property
	 * 		- it is still be included in the result set. In other words, categories without parents will be in 
	 * 		- the result set.
	 * 		- If the the query I had used "inner join fetch c.parent as parent", the result would exclude categories
	 * 		- where parent is NULL. Change to "left outer join c.parent as parent" does not exclude this category
	 * - Distinct Results
	 * 		- the plain Join between category and tone-categories will result in a category appearing as many times
	 * 		- as there are related tone-categories. With the DistinctRootEntityResultTransformer you ensure that
	 * 		- there will be no duplicates in the result
	 */
	public static List listCategories12(){
		return hibernateTemplate.execute(new HibernateCallBack() {
			
			public List<Object> doQuery(Session s) {
				//Query q = s.createQuery("from com.my.model.Category c inner join fetch c.toneCategories as tc left outer join fetch c.parent as parent");
				Query q = s.createQuery("from com.my.model.Category c inner join fetch c.details as tc where c.id in (1,4)");
				//q.setResultTransformer(DistinctRootEntityResultTransformer.INSTANCE);
				return q.list();
			}
		});
	}
	
	public static void leftJoinTest(){
		@SuppressWarnings("rawtypes")
		List l = listCategories12();
		for (Object object : l) {
			Category c = (Category)object;
			System.out.println(c.getDisplayName());
//			Set<ToneCategory> tcSet = c.getToneCategories();
//			for (ToneCategory toneCategory : tcSet) {
//				System.out.println(toneCategory.getValidFrom());
//			}
		}
	}
	/*============================================*/
	/* 	Counting  rows								*/
	/*============================================*/
	/*
	 * 	A one element List of Integer is returned by the query
	 */
	public List listCategories13(){
		return hibernateTemplate.execute(new HibernateCallBack() {
			
			public List<Object> doQuery(Session s) {
				Query q = s.createQuery("select count (*) from com.my.model.Category");
				return q.list();
			}
		});
	}
	
	/*============================================*/
	/* 	Counting on a column						*/
	/*============================================*/
	/*
	 * 	Count the number of entries in the row set where parent is not null/empty
	 * 	A one element List of Integer object is returned by the query
	 */
	public List listCategories14(){
		return hibernateTemplate.execute(new HibernateCallBack() {
			
			public List<Object> doQuery(Session s) {
				Query q = s.createQuery("select count (parent) from com.my.model.Category");
				return q.list();
			}
		});
	}
	
	/*============================================*/
	/* 	Counting and ignoring duplicates			*/
	/*============================================*/
	/*
	 * Count the number of entries in the row set, ignoring duplicates and not including 
	 * entries where parent is empty/null
	 * A one element List of Integer object is returned by the query
	 */
	public List listCategories15(){
		return hibernateTemplate.execute(new HibernateCallBack() {
			
			public List<Object> doQuery(Session s) {
				Query q = s.createQuery("select count ( distinct parent) from com.my.model.Category");
				return q.list();
			}
		});
	}
	
	/*============================================*/
	/* 	Update Clause							*/
	/*============================================*/
	
	public List listCategories16(){
		return hibernateTemplate.execute(new HibernateCallBack() {
			
			public List<Object> doQuery(Session s) {
				Query q = s.createQuery("update com.my.model.Category set categoryIdentifier = :newCatId where categoryIdentifier = :catId");
				q.setString("newCatId", "RegionD");
				q.setString("catId", "RegionC");
				int rowCount = q.executeUpdate();
				
				System.out.println("RowCount:" + rowCount);
				q = s.createQuery("from com.my.model.Category where categoryIdentifier = :catId");
				q.setString("catId", "RegionD");
				return q.list();
			}
		});
	}
	
	/*============================================*/
	/* 	NAMED Query								*/
	/*============================================*/
	/*
	 * Advantages:
	 * - many objects can share queries
	 * - named queries could also contain native SQL queries
	 * - you can provide your HQL and SQL queries in a configuration file to your database administrators
	 * How To Use: 
	 * - Put the following in mapping file
	 * <query name="rootCategories"><![CDATA[
     			from com.my.model.Category where parent is null
     		]]>
    	</query>
	 * 
	 */
	public List listCategories17(){
		return hibernateTemplate.execute(new HibernateCallBack() {
			
			public List<Object> doQuery(Session s) {
				Query q = s.getNamedQuery("rootCategories");
				return q.list();
			}
		});
	}
	
	/*============================================*/
	/* 	Named Native SQL Name Query				*/
	/*  Invoking Store Procedures				*/
	/*============================================*/
	/*
	 * One reason to use native SQL is that you may want to call stored procedures 
	 * from your Hibernate application.
	 */
	public List listCategories18(){
		return hibernateTemplate.execute(new HibernateCallBack() {
			
			public List<Object> doQuery(Session s) {
				Query q = s.getNamedQuery("rootCategoriesSQL");
				return q.list();
			}
		});
	}
	
	/*============================================*/
	/* 	Native SQL Name Query				*/
	/*============================================*/
	/*
	 * Must specify the type of the return value
	 */
	public List listCategories19(){
		return hibernateTemplate.execute(new HibernateCallBack() {
			
			public List<Object> doQuery(Session s) {
				SQLQuery q = s.createSQLQuery("select count(dtmf) dtmfCnt from TRBT_CATEGORY");
				q.addScalar("dtmfCnt", Hibernate.LONG);
				return q.list();
			}
		});
	}
	
	public List listCategories20(){
		return hibernateTemplate.execute(new HibernateCallBack() {
			
			public List<Object> doQuery(Session s) {
				//SQLQuery q = s.createSQLQuery("select * from TRBT_CATEGORY category"); //WORKS LIKE THE BELOW
				SQLQuery q = s.createSQLQuery("select category.* from TRBT_CATEGORY category");
				q.addEntity("category", Category.class);
				return q.list();
			}
		});
	}
	
	public static List listCategories21(){
		return hibernateTemplate.execute(new HibernateCallBack() {
			
			public List<Object> doQuery(Session s) {
				//SQLQuery q = s.createSQLQuery("select c.*, d.* from TRBT_CATEGORY c, TRBT_CATEGORY_DETAILS d where c.id = d.category_id and c.id  = :cid");
				
				
				//Will cause a list of Object arrays to be returns because we specify c.* and d.* Each array will have a Category
				// and a category detail object. Which object appears first in the array depends on
				// which entity is added first (using addEntity.
				
				//SQLQuery q =  s.createSQLQuery("select c.*, d.* from TRBT_CATEGORY c, TRBT_CATEGORY_DETAILS d where c.id = d.category_id and c.id  in ( :cids)");
				
					
				// Will cause a list of Category objects to be returned
				//SQLQuery q = s.createSQLQuery("select c.* from TRBT_CATEGORY c join TRBT_CATEGORY_DETAILS d on c.id = d.category_id where c.id  in ( :cids)");
				
				
				// The fetch clause will cause a list of category object to be returned.
				// The category details object will be embedded in the category.
				Query q = s.createQuery("from com.my.model.Category c join fetch c.details as d  where c.id  in ( :cids)");
				
				
				//q.addEntity("c", Category.class);
				//q.addEntity("d", CategoryDetails.class);
				
				//q.setInteger("cid", 1);
				//q.setParameter("cid", 2);
				//q.setParameter("cid", 2, Hibernate.INTEGER);
				
				//q.setParameterList("cids", new Integer[]{1, 2});
				q.setParameterList("cids", new Integer[]{1, 4}, Hibernate.INTEGER);
				// all of the above work
				// setParameter is generally used for simple types. Hibernate will try to detect the
				// type if not specified.
				
				//This is ensure that there are no duplicate categories.
				//This does not work when joined data is specified in the select clause
				//q.setResultTransformer(DistinctRootEntityResultTransformer.INSTANCE);
				
				return q.list();
			}
		});
	}
	
	public static int categoryCount(){
		List<Object> ret = hibernateTemplate.execute(new HibernateCallBack() {
			
			public List<Object> doQuery(Session s) {
				SQLQuery q =
				s.createSQLQuery("select count(*) cnt from TRBT_CATEGORY c " +
						"where c.id  in ( :cids)");
				//q.addEntity("c", Category.class);
				q.addScalar("cnt", Hibernate.INTEGER);
				
				q.setParameterList("cids", new Integer[]{1, 4}, Hibernate.INTEGER);
				
				
				return q.list();
			}
		});
		
		return (Integer)ret.get(0);
	}
	
	public static void sqlJoinTest(){
		List l = listCategories21();
//		for (Object object : l) {
//			Object[] x = (Object[])object;
//			System.out.println( ((Category)x[0]).getDisplayName() + ":details-" + ((CategoryDetails)x[1]).getName());
//		}
		
		for(Category c : (List<Category>)l ){
			System.out.println(c.getDisplayName());
		}
		
		int c = categoryCount();
		
		System.out.println("Category Count:" + c);
	}
	
	/*============================================*/
	/* 	Criteria		Criteria				*/
	/*============================================*/
	/*
	 * Restrictions: Add a restriction to a criteria using the add() method which takes a Criterion instance
	 * Use the factory methods on the org.hibernate.criterion.Restrictions class to obtain instances of the 
	 * Criterion objects. These methods include Restrictions.eq(), Restrictions.le(), etc,. all of which return
	 * a Criterion object
	 * BEBEFITS OF CRITERIA
	 * - Programmatically build nested, structured query expressions in Java
	 * - compile-time syntax-checking that is not possible with a query language
	 */
	public List listCategories22(){
		return hibernateTemplate.execute(new HibernateCallBack() {
			
			public List<Object> doQuery(Session s) {
				Criteria cri = s.createCriteria(Category.class);
				cri.add(Restrictions.eq("categoryIdentifier", "Masko"));
				return cri.list();
			}
		});
	}
	
	/*============================================*/
	/* 	Criteria - NULL value search			*/
	/*============================================*/
	/*
	 * TIP: Cannot use Restriction.eq(). Must use isNull()
	 */
	public List listCategories23(){
		return hibernateTemplate.execute(new HibernateCallBack() {
			
			public List<Object> doQuery(Session s) {
				Criteria cri = s.createCriteria(Category.class);
				cri.add(Restrictions.isNull("parentId"));
				return cri.list();
			}
		});
	}
	
	/*============================================*/
	/* 	Criteria - Restriction.like() and ilike()*/
	/*============================================*/
	public List listCategories24(){
		return hibernateTemplate.execute(new HibernateCallBack() {
			
			public List<Object> doQuery(Session s) {
				Criteria cri = s.createCriteria(Category.class);
				cri.add(Restrictions.like("categoryIdentifier", "%o", MatchMode.END));
				return cri.list();
			}
		});
	}
	
	/*============================================*/
	/* 	Criteria - The 'And' Expression			*/
	/*============================================*/
	/*
	 * Each criterion added will be connected with the AND operator
	 */
	public List listCategories25(){
		return hibernateTemplate.execute(new HibernateCallBack() {
			
			public List<Object> doQuery(Session s) {
				Criteria cri = s.createCriteria(Category.class);
				cri.add(Restrictions.like("categoryIdentifier", "Region%")).add(Restrictions.eq("availableViaWeb", true));
				
				// alternative 
				//Criterion lhs = Restrictions.like("categoryIdentifier", "Region%");
				//Criterion rhs = Restrictions.eq("availableViaWeb", true);
				//cri.add(Restrictions.and(lhs, rhs));
				// -----------
				return cri.list();
			}
		});
	}
	
	/*============================================*/
	/* 	Criteria - The 'OR' Expression			*/
	/*============================================*/
	/*
	 * The OR Expression takes two Criterion parameters
	 * LAYZ INITIALIZATION ISSUE:
	 * Because the parent attribute is marked "lazy=true" in mapping file: 
	 * If we specify any Restriction, the resulting categories object will have a  
	 * non-initialized instance for the parent property. If you specify no restrictions
	 * ,getting all the categories then the parent association is fully loaded. 
	 * 
	 * The same behavior is seen in HQL as well.
	 */ 
	public List listCategories26(){
		return hibernateTemplate.execute(new HibernateCallBack() {
			
			public List<Object> doQuery(Session s) {
				Criteria cri = s.createCriteria(Category.class);
				//TODO:FIX Lazy initialization of entry where parent is NOT NULL
				//Criterion c1 = Restrictions.like("categoryIdentifier", "Region%");
				//Criterion c2 = Restrictions.isNotNull("parent");
				//cri.add(Restrictions.or(c1, c2));	
				
				// SOLUTION: 
				// 1. Set Lazy init to false in MAPPING FILE
				// 2. And better, set the fetch mode
				//Use setfetchMode
//				cri.setFetchMode("parent", FetchMode.JOIN);
//				Criterion c1 = Restrictions.like("categoryIdentifier", "Region%");
//				Criterion c2 = Restrictions.isNotNull("parent");
//				cri.add(Restrictions.or(c1, c2));
				return cri.list();
			}
		});
	}
	
	/*============================================*/
	/* 	Criteria - The 'OR' Expression			*/
	/*	MORE THAN 2 DIFFERENT CRITERIA
	/*============================================*/
	/*
	 * Use the Restrictions.disjunction()
	 */
	public List listCategories27(){
		return hibernateTemplate.execute(new HibernateCallBack() {
			
			public List<Object> doQuery(Session s) {
				Criteria cri = s.createCriteria(Category.class);
				Criterion c1 = Restrictions.like("categoryIdentifier", "Region%");
				Criterion c2 = Restrictions.eq("availableViaWeb", true);
				Criterion c3 = Restrictions.eq("availableViaIvr", true);
				
				Disjunction disj = Restrictions.disjunction();
				disj.add(c1);
				disj.add(c2);
				disj.add(c3);
				
				cri.add(disj);	
				return cri.list();
			}
		});
	}
	
	/*============================================*/
	/* 	Criteria - Pagination/Paging				*/
	/*============================================*/
	public List listCategories28(){
		return hibernateTemplate.execute(new HibernateCallBack() {
			
			public List<Object> doQuery(Session s) {
				Criteria cri = s.createCriteria(Category.class);
				cri.setFirstResult(0);
				cri.setMaxResults(2);
				
				return cri.list();
			}
		});
	}
	
	/*============================================*/
	/* 	Criteria - Unique ResultSet				*/
	/*============================================*/
	/*
	 * uniqueResult() method on the Criteria object returns an object or null.
	 * If there is more than one result, the uniqueResult() method throws a 
	 * HibernateException.
	 */
	public List listCategories29(){
		return hibernateTemplate.execute(new HibernateCallBack() {
			
			public List<Object> doQuery(Session s) {
				Criteria cri = s.createCriteria(Category.class);
				cri.add(Restrictions.eq("id", 85));
				Category c = (Category)cri.uniqueResult();	
				return null;
			}
		});
	}
	
	/*============================================*/
	/* 	Criteria - SORTING						*/
	/*============================================*/
	
	public List listCategories30(){
		return hibernateTemplate.execute(new HibernateCallBack() {
			
			public List<Object> doQuery(Session s) {
				Criteria cri = s.createCriteria(Category.class);
				cri.addOrder(Order.desc("id"));	
				return cri.list();
			}
		});
	}
	
	/*============================================*/
	/* 	Criteria - Restriction on an associated entity	*/
	/*============================================*/
	/*
	 * 	- createCriteria
	 * 	- createAlias
	 * - Distinct Results
	 * 		- setResultTransformer to ensure distinct categories
	 */
	
	public List listCategories31(){
		return hibernateTemplate.execute(new HibernateCallBack() {
			
			public List<Object> doQuery(Session s) {
				Criteria cri = s.createCriteria(Category.class);
				
				//SOLUTION using createAlias
				//Criteria cri2 = cri.createAlias("toneCategories", "tc", Criteria.INNER_JOIN);
				//cri2.add(Restrictions.le("tc.validFrom", new Date()));
				
				//SOLUTION using new createCriteria
				Criteria cri2 = cri.createCriteria("toneCategories", Criteria.INNER_JOIN);
				cri2.add(Restrictions.le("validFrom", new Date()));
				
				// If we need to access the parent object, must set the fetch mode because the parent
				// is lazily initialized
				cri.setFetchMode("parent", FetchMode.JOIN);
				
				// 
				cri.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
				return cri.list();
				
				// Alternatively - can also execute the query using the second criteria
				//return cri2.list(); // we can call list on either criteria
			}
		});
	}
	
	/*============================================*/
	/* 	Criteria - Projection/columns selection	*/
	/*============================================*/
	/*
	 * 	- allows you to select selected columns instead of entire objects
	 * 	- use org.hibernate.criterion.Projections factory to create Projection
	 * 	- In this example, the result will be a one-element list of Integers
	 */
	
	public List listCategories32(){
		return hibernateTemplate.execute(new HibernateCallBack() {
			
			public List<Object> doQuery(Session s) {
				Criteria cri = s.createCriteria(Category.class);
				cri.setProjection(Projections.rowCount());
				return cri.list();
			}
		});
	}
	
	/*============================================*/
	/* 	Criteria - Projection/columns selection	*/
	/*============================================*/
	/*
	 * 	- Project for a specific property
	 * 	- Projections.projectionList() to add more than one column
	 * 	- ProjectionList projList = Projections.projectionList();
	 * 	- projList.add(projections.property("categoryIdentifier"))
	 */
	
	public List listCategories33(){
		return hibernateTemplate.execute(new HibernateCallBack() {
			
			public List<Object> doQuery(Session s) {
				Criteria cri = s.createCriteria(Category.class);
				cri.setProjection(Projections.property("categoryIdentifier"));
				return cri.list();
			}
		});
	}
	
	/*============================================*/
	/* 	Criteria - Projection/columns selection	*/
	/*============================================*/
	/*
	 * 	Using SQL directly as Restriction
	 */
	public List listCategories34(){
		return hibernateTemplate.execute(new HibernateCallBack() {
			
			public List<Object> doQuery(Session s) {
				Criteria cri = s.createCriteria(Category.class);
				cri.add(Restrictions.sqlRestriction("{alias}.CATEGORY_IDENTIFIER like 'Reg%' and {alias}.AVAILABLE_VIA_WEB = 1"));
				return cri.list();
			}
		});
	}
	
	/*============================================*/
	/* 	Criteria - DETACHED CRITERIA			*/
	/*============================================*/
	/*	OPEN OPEN OPEN
	 * allows you to create a query outside the scope of a session and then execute it using 
	 * an arbitrary Session. can also be used to express a sub-query.
	 * 
	 * When we use HibernateTample as you will see later, we have to use detached criteria
	 * if we want to use criteria instead of HQL since since the session is started inside the 
	 * template method to which we pass the criteria.
	 */
	public List listCategories35(){
		return hibernateTemplate.execute(new HibernateCallBack() {
			
			public List<Object> doQuery(Session s) {
				DetachedCriteria tc = DetachedCriteria.forClass(ToneCategory.class);
				tc.createAlias("category", "c", Criteria.INNER_JOIN);
				tc.setProjection(Projections.property("c.id"));
				
				Criteria cri = s.createCriteria(Category.class);
				cri.add(Subqueries.eqAll("id", tc));
				return cri.list();
			}
		});
	}
	
	/*============================================*/
	/* 	Criteria - FILTERS						*/
	/*============================================*/
	/*
	 * 	Filter allows add restriction to query result set without the need to change the where clause
	 * 	- Hibernate filters is that you can programmatically turn filters on or off in your application code
	 * 	- filters are defined in your Hibernate mapping documents for easy maintainability
	 * 
	 * 	- Do not use filters, but excellent solution to certain types of problemsâ€”notably security and 
	 * 	personalization
	 */
	public List listCategories36(){
		return hibernateTemplate.execute(new HibernateCallBack() {
			
			public List<Object> doQuery(Session s) {
				return null;
			}
		});
	}
	
	/*=======================================================================================*/
	/* 				Using Hibernate With Spring Integration 
	/*=======================================================================================*/
	/*
	 * What Spring Offers
	 * 	- SessionFactory as Bean
	 * 		Spring represents the configured session factory as a LocalSessionFactoryBean
	 *   - With Spring you can create DAOs that take advantage of Hibernateâ€™s functionality
	 *   - Spring provides you with the invaluable HibernateDaoSupport class to form the basis 
	 *   of your DAOs
	 * 
	 * 	WARNING:
	 * Spring maintains two sets of Hibernate packages
	 * 	org.springframework.orm.hibernate... for Hibernate 2 functionality
	 * 	org.springframework.orm.hibernate3... for Hibernate 3 functionality
	 */
	
	public static interface CategoryDAO {
		List<Category> getAll();
		List<Category> getAll2();
		Category get(final Integer catId);
		void create(String strId, String name, String lang);
		Language getLanguage(String lang);
		Category updateCategory();
		Category updateCategoryMerge();
		void addChildren();
		void addParent();
		void addParentFix();
		void explicitTransactionAddChildren();
	}
	
	
	public static class CategoryDaoImpl extends HibernateDaoSupport implements CategoryDAO 
	{
		/*
		 * With boilerplate session management code (opening and closing session directly)
		 * The getSession() and releaseSession() methods are derived from the DAO class. They 
		 * are roughly equivalent to the session factoryâ€™s openSession() method and the 
		 * sessionâ€™s close() method, respectively. 
		 * 
		 * Here, the session factory is configured as a bean. The hibernate support code will use
		 * this factory to create sessions. 
		 * 
		 * But, we are still using session management explicitly. This is where Hibernate 
		 * template comes in handy.
		 */
		public List<Category> getAll(){
			Session session = getSession();
			List<Category> allCategories = (List<Category>)session.createQuery("from com.my.model.Category").list();
			
			releaseSession(session);
			return allCategories;
		}
		
		/*
		 * Example:
		 *   - Using HibernateTemplate
		 *   	HibernateTemplate hides session management.
		 *   	HibernateTemplate provides a set of methods that allows you to carry out most of the basic 
		 *      Hibernate operations in a similar single line of code.
		 *      
		 *      Methods are:
		 *      bulkUpdate() Performs a bulk update or delete on the database, according to the provided 
		 *      HQL query and entities
		 *      contains() Determines whether the given object exists as an entity in the database 
		 *      delete() Deletes an entity from the database 
		 *      find() Carries out an HQL query
		 *      get() Obtains an entity by its id (primary key) 
		 *      persist() Saves an entity to the database 
		 *      refresh() Refreshes an entity from the database
		 *      save() Saves an entity to the database
		 *      saveOrUpdate() Saves an entity to the database or updates it as appropriate
		 *      update() Updates an entity in the database
		 *      ----------------------------------------------------------------------------
		 *      When a more complex set of operations is required than can be achieved in a single line,
		 *      the execute() method is used to invoke an instance of HibernateCallback.
		 *      NOTE:NOTE:
		 *      This HibernateTemplate is like mine in that mine takes care of session management. One difference
		 *      is that I only have an execute() method. The execute() methods are similar in that they 
		 *      both take a CallBack object. 
		 */
		public List<Category> getAll2(){
			return getHibernateTemplate().find("from com.my.model.Category");
		}
		
		public Category get(final Integer catId){
			System.out.println(getSession().getTransaction());
			return (Category)getHibernateTemplate().get(Category.class, catId);
		}
		/*
		 * Example
		 * 	- Using the execute() (With HibernateCallback)
		 *  - it doesnâ€™t make the implementation particularly terse, and probably makes it 
		 *    slightly harder to understand. So, it might make sense to implemented without using
		 *    HibernateTemplate as in the getAll() method
		 *   
		 */
		public Category updateCategory(final String name, final Integer id) {
			HibernateCallback callback = new HibernateCallback() {
				public Object doInHibernate(Session session) {
					Category cat = (Category)session.get(Category.class,id);
					cat.setCategoryIdentifier(name);
					session.update(cat);
					return cat;
				}
			};
			return (Category)getHibernateTemplate().execute(callback);
		}
		
		/*
		 * Example - TRANSACTION
		 * 		- Using a Specific Isolation Level (Overriding Hibernate Isolation for specific transactions)
		 * 	NOTES:
		 * 		- Hibernate treats the isolation as a global setting
		 * 			- apply the configuration option hibernate.connection.isolation
		 * 		- to override isolation you will need to obtain the JDBC connection directly
		 * 			- alter the isolation level, begin the transaction, roll back
		 */
		public Category updateCategory2(final String name, final Integer id) {
			Session session = getSession();
			return null;
		}
	
		/*
		 * Example:
		 * 	- Declarative Transaction management
		 * The TransactionProxy intercepts all calls to this method. Inside the create() method in the proxy
		 * looks something as follows:
		 * proxy::create(String strId, String name, String lang){
		 *      try{
		 * 		session.beginTransactio()
		 * 			target.create(strId, name, lang);
		 * 		session.getTransaction().commit();
		 * 		}
		 * 		catch(){
		 * 			session.getTransction.rollback();
		 * 		}
		 * }
		 * (non-Javadoc)
		 * @see com.my.test.MediaTest.CategoryDAO#create(java.lang.String, java.lang.String, java.lang.String)
		 * O&A:
		 * 		1. Can you save one class and cause the reference class to also save with directly saving it.
		 */
		public void create(String strId, String name, String lang){
			
			// Here I am getting the transaction so that I can inspect it.
			System.out.println(getSession().getTransaction());
			
			get(1);
			
			Category c = new Category();
			c.setCategoryIdentifier(strId);
			c.setCreated(new Date());
			c.setCreatedBy("me");
			
//			DetachedCriteria dc = DetachedCriteria.forClass(Language.class);
//			dc.add(Restrictions.eq("code", lang));
//			Language l = (Language)getHibernateTemplate().findByCriteria(dc).get(0);
			
			CategoryDAO cat4Dao = (CategoryDAO) ctx.getBean("category4Dao");
			
			// invoking get language from a different DAO instance in order to demonstrate
			// propagation behavior: Note propagation behavior is always with respect to
			// invocation between two different object instances.  It does not apply to
			// calling of another method within the same instance.
			Language l = cat4Dao.getLanguage(lang);
			
			CategoryDetails  cd = new CategoryDetails();
			cd.setCategory(c);
			cd.setLanguage(l);
			cd.setName(name);
			
			Map<Language, CategoryDetails> cdMap = new HashedMap();
			cdMap.put(l, cd);
			c.setDetails(cdMap);
			
			getHibernateTemplate().save(c);
			
			//NOTE: If we did not call setDetails() we would need to call save() for the detail as
			//seen below.
			// If saving of category details fails the transaction will be rolled back
			//getHibernateTemplate().save(cd);
			
		}
		/* 
		 * ------------- Transitive persistence ----------------------------------
		 * An application may be working with an object graph consisting of persistent, detached
		 * and transient objects. For example you might get a Category in the current session
		 * (persistent category) and add as a child to this category a category object that was
		 * obtained in a previous session (detached category), and as child to the detached
		 * category add a newly created category object (transient category)
		 * 
		 * Transitive persistence is a technique that allows you to propagate persistence 
		 * to transient and detached subgraphs automatically.
		 * 
		 * In Hibernate this technique is called cascading
		 * 
		 * cascade="none", the default, tells Hibernate to ignore the association.
		 * 
		 * cascade="save-update" tells Hibernate to navigate the association when the
		 * transaction is committed and when an object is passed to save() or update()
		 * and save newly instantiated transient instances and persist changes to detached 
		 * instances.
		 * 
		 * cascade="delete" tells Hibernate to navigate the association and delete persistent 
		 * instances when an object is passed to delete().
		 * 
		 * cascade="all" means to cascade both save-update and delete, as well as calls to evict 
		 * and lock.
		 * 
		 * cascade="all-delete-orphan" means the same as cascade="all" but, in addition, 
		 * Hibernate deletes any persistent entity instance that has been removed 
		 * (dereferenced) from the association (for example, from a collection).
		 * 
		 * cascade="delete-orphan" Hibernate will delete any persistent entity instance 
		 * that has been removed (dereferenced) from the association (for example, from a collection).
		 */
		
		/*
		 * Cascading persistence -----------
		 * If a transient object is added to a persistent object and cascading is set for
		 * the transient object, then hibernate will automatically persist the transient object.
		 * In this example, there is no need to explicitly save child category or category detail.
		 * 
		
		 */
		public void addChildren(){
			
			System.out.println("session open:" + getSession().isOpen());
			Category parent = (Category)getHibernateTemplate().get(Category.class, 106);
			Category child = new Category();
			child.setCategoryIdentifier("106_child3");
			child.setCreated(new Date());
			child.setCreatedBy("rappa");
			child.setParent(parent);
			DetachedCriteria dc = DetachedCriteria.forClass(Language.class);
			dc.add(Restrictions.eq("code", "en"));
			Language l = (Language)getHibernateTemplate().findByCriteria(dc).get(0);
			
			CategoryDetails  cd = new CategoryDetails();
			cd.setCategory(child);
			cd.setLanguage(l);
			cd.setName("106_child3");
			
			Map<Language, CategoryDetails> cdMap = new HashedMap();
			cdMap.put(l, cd);
			child.setDetails(cdMap);
			
			List<Category> children = new ArrayList<Category>();
			children.add(child);
			parent.setChildren(children);
		}
		
		public static void createTest(){
			System.out.println("//---------------------------------------- create()");
			CategoryDAO cat2Dao = (CategoryDAO) ctx.getBean("category2Dao");
			cat2Dao.create("myCategory4", "My Jazz4", "en");
		}
		
		public static void addChildrenTest(){
			System.out.println("//---------------------------------------- create()");
			CategoryDAO cat2Dao = (CategoryDAO) ctx.getBean("category2Dao");
			cat2Dao.addChildren();
		}
		
		/*
		 * Cascading persistence -----------
		 * If we try to add a transient object to a persistent object then then the
		 * cascading must be set for transient object, otherwise you will get the following error:
		 * Exception in thread "main" org.springframework.dao.InvalidDataAccessApiUsageException: 
			object references an unsaved transient instance - save the transient instance before flushing: 
			com.my.model.Category; nested exception is org.hibernate.TransientObjectException: 
			object references an unsaved transient instance - save the transient instance before flushing: 
			com.my.model.Category
		 * This occurs because we try to add a transient parent category object
		 * To solve we must either add cascading for the parent property or explicitly
		 * save the transient parent object
		 * 
		 * WARNING:Adding cascading to the parent side is not recommended
		 */
		public void addParent(){
			
			Category child = (Category)getHibernateTemplate().get(Category.class, 123);
			
			// parent is transient while child is persistent
			Category parent = new Category();
			parent.setCategoryIdentifier("123_paret");
			parent.setCreated(new Date());
			parent.setCreatedBy("rappa");
	
			DetachedCriteria dc = DetachedCriteria.forClass(Language.class);
			dc.add(Restrictions.eq("code", "en"));
			Language l = (Language)getHibernateTemplate().findByCriteria(dc).get(0);
			
			CategoryDetails  cd = new CategoryDetails();
			cd.setCategory(parent);
			cd.setLanguage(l);
			cd.setName("123_parent");
			
			Map<Language, CategoryDetails> cdMap = new HashedMap();
			cdMap.put(l, cd);
			parent.setDetails(cdMap);
			
			child.setParent(parent);
		}
		
		public void addParentFix(){
			
			Category child = (Category)getHibernateTemplate().get(Category.class, 83);
			
			// parent is transient while child is persistent
			Category parent = new Category();
			parent.setCategoryIdentifier("83_paret");
			parent.setCreated(new Date());
			parent.setCreatedBy("rappa");
	
			DetachedCriteria dc = DetachedCriteria.forClass(Language.class);
			dc.add(Restrictions.eq("code", "en"));
			Language l = (Language)getHibernateTemplate().findByCriteria(dc).get(0);
			
			CategoryDetails  cd = new CategoryDetails();
			cd.setCategory(parent);
			cd.setLanguage(l);
			cd.setName("83_parent");
			
			Map<Language, CategoryDetails> cdMap = new HashedMap();
			cdMap.put(l, cd);
			parent.setDetails(cdMap);
			
			getHibernateTemplate().saveOrUpdate(parent);
			child.setParent(parent);
		}
		
		/*
		 * Cascading persistence ----------- with detached parent category
		 * In this example, the association between parent and child category
		 * is created outside of transaction.
		 *  NOTE: That here we must explicitly call save() on the child since the
		 *  parent is detached. 
		 *  
		 *  If we had added new transient sub-categories of Child Category we still would
		 *  only need to save just the child and the sub-categories of child would also
		 *  be saved.
		 *  
		 *  We could also call merge()/update() on the parent if we  have changes on the parent 
		 *  that we also want to persist. We could not use save() on the parent because it
		 *  was already persistent.
		 *  
		 *  HOW DOES HIBERNATE IDENTIFY TRANSIENT Object
		 *  - The identifier property (if it exists) is null.
		 *  NOTE: if the id property is of a primitive type we would have needed to use 
		 *  the unsaved-value="0" in the mapping file (for the identifier)
		 */
		public void explicitTransactionAddChildren(){
			
			Session s =  getSession();
			Transaction tx = s.beginTransaction();
			Category parent = (Category)getSession().get(Category.class, 63);
			Language l = (Language)s.createQuery( "from com.my.model.Language where code = 'en'").uniqueResult();
	
			tx.commit();
			s.close();
			
			// parent in detached. Is was loaded in a previous session
			
			/*
			 * If we had enabled cascade="save-update" on the <many-to-one> mapping of parent, 
			 * Hibernate would have had to navigate the whole graph of objects in memory, 
			 * synchronizing all instances with the database.
			 */
	
			parent.setPromotionValidTo(new Date());
			
			Category child = new Category();
			child.setCategoryIdentifier("84_child_1");
			child.setCreated(new Date());
			child.setCreatedBy("rappa");
			
			
			CategoryDetails  cd = new CategoryDetails();
			cd.setCategory(child);
			cd.setLanguage(l);
			cd.setName("84_child_1");
			
			Map<Language, CategoryDetails> cdMap = new HashedMap();
			cdMap.put(l, cd);
			child.setDetails(cdMap);
			
//			List<Category> children = new ArrayList<Category>();
//			children.add(child);
//			parent.setChildren(children);
			
			parent.addChildCategory(child);
			
			s =  getSession();
			tx = s.beginTransaction();
			s.save(child);
			/*
			 * The update to parent.PromotionalValidTo will not be propagated to the database
			 * because cascade='none' on the parent property side of the association. However,
			 * if we use merge the update will be added to the database. 
			 * NOTE: the updated() resulted in the following error:
			 * org.hibernate.HibernateException: Illegal attempt to associate a collection with two open sessions.
			 * 
			 */
			//s.merge(parent);
			//s.update(parent);
			tx.commit();
			s.close();
			
			
		}
		
		public static void addParentTest(){
			System.out.println("//---------------------------------------- create()");
			CategoryDAO cat2Dao = (CategoryDAO) ctx.getBean("category2Dao");
			cat2Dao.addParent();
		}
		
		public static void explicitTransactionAddChildrenTest(){
			System.out.println("//---------------------------------------- create()");
			CategoryDAO cat2Dao = (CategoryDAO) ctx.getBean("category2Dao");
			cat2Dao.explicitTransactionAddChildren();
		}
		
		public Language getLanguage(String lang){
			System.out.println(getSession().getTransaction());
			DetachedCriteria dc = DetachedCriteria.forClass(Language.class);
			dc.add(Restrictions.eq("code", lang));
			Language l = (Language)getHibernateTemplate().findByCriteria(dc).get(0);
			return l;
		}
		
		
		/*
		 * Example
		 * SaveOrUpdate vs Merge
		 * 
		 * The NonUniqueObjectException thrown when using Session.saveOrUpdate() 
		 * 
		 * Explanation------
		 * When we close an individual Hibernate Session, the persistent objects we are working 
		 * with are detached. This means the data is still in the applicationâ€™s memory, but Hibernate 
		 * is no longer responsible for tracking changes to the objects.
		 * If we then modify our persistent object and want to update it, we have to reattach the object. 
		 * During that reattachment process, Hibernate will check to see if there are any other copies of 
		 * the same object. If it finds any, it has to tell us it doesnâ€™t know what the â€œrealâ€� copy is any 
		 * more. Perhaps other changes were made to those other copies that we expect to be saved, but 
		 * Hibernate doesnâ€™t know about them, because it wasnâ€™t managing them at the time.
		 * Rather than save possibly bad data, Hibernate tells us about the problem via the 
		 * NonUniqueObjectException.
		 * 
		 * So what are we to do? In Hibernate 3, we have merge() (in Hibernate 2, use saveOrUpdateCopy()). 
		 * This method will force Hibernate to copy any changes from other detached instances onto the 
		 * instance you want to save, and thus merges all the changes in memory before the save.
		 */
		public Category updateCategory(){

			Session session = getSession();
			Transaction tx = session.beginTransaction();
			Category item = (Category) session.get(Category.class, new Integer(86));
			tx.commit();
			session.close(); // end of first session, item is detached
			 
			item.getId(); // The database identity is "1234"
			item.setAvailableViaIvr(true);
			Session session2 = getSession();
			Transaction tx2 = session2.beginTransaction();
			Category item2 = (Category) session2.get(Category.class, new Integer(86));
			session2.update(item); // Throws NonUniqueObjectException
			tx2.commit();
			session2.close();
			return null;
		}
		
		/* 
		 * Example
		 * SaveOrUpdate vs Merge
		 * Solution to NonUniqueObjectException using merge
		 */
		public Category updateCategoryMerge(){

			Session session = getSession();
			Transaction tx = session.beginTransaction();
			Category item = (Category) session.get(Category.class, new Integer(86));
			tx.commit();
			session.close(); // end of first session, item is detached
			 
			item.getId(); // The database identity is "1234"
			item.setAvailableViaIvr(true);
			Session session2 = getSession();
			Transaction tx2 = session2.beginTransaction();
			Category item2 = (Category) session2.get(Category.class, new Integer(86));
			
			// check first whether the onject already exist in the database and use merge to be safe.
			Category item3 = null;
			if(item.getId() != null)
				item3 =  (Category)session2.merge(item);
			else
				session2.update(item); 
			
			tx2.commit();
			session2.close();
			return null;
		}
		
	}
	
	/*
	 * Example
	 * save() vs persist()
	 *  Disusssion from christian from Hibernate Team -- follows
	 *  
	 * persist() is well defined. It makes a transient instance persistent. However, it doesn't guarantee that the identifier 
	 * value will be assigned to the persistent instance immediately, the assignment might happen at flush time. The spec doesn't 
	 * say that, which is the problem I have with persist().
	 * 
	 * persist() also guarantees that it will not execute an INSERT statement if it is called outside
	 * of transaction boundaries. This is useful in long-running conversations with an extended Session/persistence context.
	 *  
	 *  A method like persist() is required.
	 *  save() does not guarantee the same, it returns an identifier, and if an INSERT has to be 
	 *  executed to get the identifier (e.g. "identity" generator, not "sequence"), this INSERT 
	 *  happens immediately, no matter if you are inside or outside of a transaction. This is not 
	 *  good in a long-running conversation with an extended Session/persistence context.
	 */
	
	
	/*
	 * Example
	 * load() vs get()
	 * you should use the get() methods if you are not certain that the object is in the database
	 * because load throws an unrecoverable Exception when no object with the specified id exists
	 * The get() method returns null if the object can't be found. 
	 * load() returns a proxy object and only hits the database after the object's property is accessed
	 * This implies that you can only access the object within the context of the session (before the
	 * transaction is commited, otherwise you will get lazy initialization. See Exception below:
	 * 	org.hibernate.LazyInitializationException: 
	 * 		could not initialize proxy - no Session
	 */
	
	/*
	 * Example
	 * Reattachment of the object with a new session
	 * Discussion:
	 * While the http sessions can be long lived, we cannot afford to hold database session for as long as
	 * we can hold an http session. Therefore we must release the database session after we obtain an object
	 * Usually before the object is rendered on the user interface. If we later update this in-memory 
	 * object which is no longer associated with a session we can use the following to reattach
	 * 	- lock - you only use lock() if you’re sure that the detached instance hasn’t been modified
	 * 		because changes made before lock in called are not saved to the database. Lock does not
	 * 		force database update 
	 * 	- update - forces an update to the persistent state of the object in the database. Changes made
	 * 		before and after the update call are saved to the database
	 * 	- merge
	 *  - saveOrUpdate
	 */
	
	
	/*
	 * Example
	 * refresh()
	 * Use this if you are not in a transaction and want to make sure that you object
	 * is not out of sync with the last commited data
	 */
}


