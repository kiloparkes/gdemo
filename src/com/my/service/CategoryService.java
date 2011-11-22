package com.my.service;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.List;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.aop.Advisor;
import org.springframework.aop.AfterReturningAdvice;
import org.springframework.aop.ClassFilter;
import org.springframework.aop.MethodBeforeAdvice;
import org.springframework.aop.Pointcut;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.aop.support.JdkRegexpMethodPointcut;
import org.springframework.aop.support.NameMatchMethodPointcut;
import org.springframework.aop.support.NameMatchMethodPointcutAdvisor;
import org.springframework.aop.support.StaticMethodMatcherPointcut;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.my.model.Category;
import com.my.test.MediaTest.CategoryDAO;

public class CategoryService {
	
	private CategoryDAO categoryDao;
	private static String username = "rick";
	private static String password = "rock";
	
	public void setCategoryDao(CategoryDAO categoryDao) {
		this.categoryDao = categoryDao;
	}
	
	@Transactional (propagation=Propagation.REQUIRED)
	public List<Category> getAll(){
		return categoryDao.getAll();
	}
	
	public Category get(int id ){
		return categoryDao.get(id);
	}
	
	public static void main(String[] args) {
		//simpleAroundAdviceTest();
		//beforeAdviceTest();
		//afterReturningAdviceTest();
		//SimpleStaticPointcutTest();
		//SimpleNameMatchPointcutTest();
		//SimpleRegExpPointcutTest();
		//combinedPointcutAdvisorTest();
		//aspectJPointcutTest();
		//simpleAnnotatedAspecTest();
		simpleAnnotatedBeforeAspecTest();

	}
	
	public static class UserInfo {
		private String username;
		private String password;
		public UserInfo(String username, String password) {
			this.username = username;
			this.password = password;
		}
		public String getPassword() {
			return password;
		}
		public String getUsername() {
			return username;
		}
		
		public void setPassword(String password) {
			this.password = password;
		}
	}
	
	public static class SecurityManager {
		private static ThreadLocal<UserInfo> threadLocal = new ThreadLocal<UserInfo>();
		public void login(String username, String password) {
			// assumes that all credential
			// are valid for a login

			/*
			 * In a real application, the login() method would probably check the supplied 
			 * application against a database or Lightweight Directory Access Protocol (LDAP) directory,
			 */
			threadLocal.set(new UserInfo(username, password));
		}
		public void logout() {
			threadLocal.set(null);
		}

		public UserInfo getLoggedOnUser() {
			return threadLocal.get();
		}
	}
	
	/*
	 * Understanding Proxies
	 * 
	 * 
	 * The proxy is responsible for intercepting calls to all methods and passing them 
	 * as necessary to the AOP framework for the advice to be applied.
	 * 
	 * Unlike the CGLIB proxy, the JDK proxy can only generate proxies of interfaces, not classes.
	 * Therefore, any object you wish to proxy must implement at least one interface.
	 * 
	 * when it is not possible to use interfaces for your classes, you must use CGLIB proxy
	 * 
	 * When you are using the JDK proxy, all method calls are intercepted by the JVM and 
	 * routed to the invoke() method of the proxy.
	 * 
	 * You can instruct the ProxyFactory to use a JDK proxy by specifying the list of 
	 * interfaces to proxy using setProxyInterfaces().
	 * 
	 * Using CGLIB Proxies
	 * 
	 * GLIB dynamically generates the bytecode for a new class on the fly for each proxy
	 * generates the appropriate bytecode to invoke any unadvised methods directly
	 * 
	 * TIP
	 * In order to use the CGLIB proxy when proxying an interface, you must set the 
	 * value of the optimize flag in the ProxyFactory to true using the setOptimize() method.
	 */
	
	/*
	 * The MethodInterceptor interface is the AOP Alliance standard interface for 
	 * implementing around advice for method invocation joinpoints.
	 * 
	 * One restriction in AOP, at least in Spring, is that you can’t advise final classes,
	 * because they cannot be overridden and therefore cannot be proxied.
	 * 
	 * Instead of using ProxyFactory to programmatically create proxies you should use
	 * ProxyFactoryBean class to provide declarative proxy creation.
	 * 
	 * Spring has two proxy implementations: JDK dynamic proxy and CGLIB proxy
	 * 
	 * In the past, CGLIB proxies were only used when you wanted to proxy classes rather 
	 * than interfaces
	 * 
	 * As of the 1.1 release of Spring, the CGLIB proxy is noticeably faster than JDK 
	 * dynamic proxies in most cases.
	 * 
	 * Joinpoint - a definite point during the execution of your application. A method
	 * call for example.
	 * Joinpoints in Spring
	 * in Spring AOP is that it only supports one joinpoint type: method invocation.
	 * AspectJ support many others
	 * 
	 * if you need to advise some code at a joinpoint other than a method invocation, 
	 * you can always use Spring and AspectJ together.
	 * 
	 * Aspects - the combination of point-cuts (group of joint points) and advice (the 
	 * logic that is executed at joint-point.)
	 * Aspects in Spring
	 * 
	 * Internally, ProxyFactory delegates the proxy creation process to an instance of 
	 * DefaultAopProxyFactory, which in turn delegates to either Cglib2AopProxy or 
	 * JdkDynamicAopProxy, depending on the settings of your application.
	 * 
	 * Introduction
	 * allow you to add interface implementations dynamically to any object on the fly 
	 * using the familiar interceptor concept
	 */
	
	
	/*
	 * Transaction Management
	 * You can use the traditional approach, where Spring creates proxies to the methods 
	 * in a target bean, or you can use AOP and the tx-advice tag.
	 * 
	 * Local transactions are specific to a single transactional resource (a JDBC connection, for example), 
	 * whereas global transactions are managed by the container and can include multiple transactional 
	 * resources.
	 * 
	 * The DataSourceTransactionManager controls transactions performed on JDBC Connection 
	 * instances obtained from a DataSource;
	 * 
	 * HibernateTransactionManager controls transactions performed on a Hibernate session;
	 * 
	 * JdoTransactionManager manages JDO transactions; and JtaTransactionManager delegates 
	 * transaction management to JTA.
	 */
	
	/*
	 * TIP:
	 * Think of the Advisor as the Class which contains the logic implementing the cross-cutting function (Aspect)
	 * The advice the the method of the Advisor which implement the cross-cutting function
	 * The point-cut specifies which methods of the target for which the aspect is applicable.
	 */
	
	/* ++++++++++++++++++++++++++++++ Around Advice+++++++++++++++++++++++++++++++++++++++++++*/
	
	/*
	 * Around advice 
	 * functions like a combination of before and after advice, with one 
	 * big difference—you can modify the return value.
	 * you can also prevent the method from actually executing.
	 * 
	 */
	public static class CategoryServiceAdvice implements MethodInterceptor {

		public Object invoke(MethodInvocation invocation) throws Throwable {
			System.out.println("Before invoking " + invocation.getMethod());
			Object retVal = invocation.proceed();
			System.out.println("After invoking " + invocation.getMethod());
			return retVal;
		}
		
	}

	public static void simpleAroundAdviceTest(){
		
		ApplicationContext ac = new ClassPathXmlApplicationContext("applicationContext.xml");
		CategoryService target = (CategoryService)ac.getBean("categoryService");
		
		// create the proxy
		ProxyFactory pf = new ProxyFactory();
		/*
		 * Internally, addAdvice() wraps the advice you pass it in an instance of 
		 * DefaultPointcutAdvisor, which is the standard implementation of PointcutAdvisor,
		 * and configures it with a pointcut that includes all methods by default.
		 *  
		 *  When you want more control over the Advisor that is created, or when you want 
		 *  to add an introduction to the proxy, create the Advisor yourself and use the 
		 *  addAdvisor() method of the ProxyFactory().
		 *  
		 *  You can use the same ProxyFactory instance to create many different proxies, 
		 *  each with different aspects.
		 */
		
		/*
		 * The Proxy needs to know about the advisor (by means of the advice),
		 * the advisee (the target), and all the methods of the target to which
		 * the advice is applicable (point-cut).
		 *
		 * No point-cut specified..
		 * If we do not specify which methods, then by default all methods of the target
		 * will receive advice.
		 */
		pf.addAdvice(new CategoryServiceAdvice());
		
		pf.setTarget(target);
		
		CategoryService proxy =  (CategoryService)pf.getProxy();
		Category c = proxy.get(106);
		System.out.println("Category is " + c);
		
	}

	/* ++++++++++++++++++++++++++++++BeforeAdvice+++++++++++++++++++++++++++++++++++++++++++*/
	/*
	 * Before advice can modify the arguments passed to a method and can prevent the 
	 * method from executing by raising an exception.
	 
	 * To check whether or not a user is authenticated and, if so, whether or not 
	 * the user is permitted to access the methods on SecureBean, we need to create 
	 * advice that executes before the method and checks the UserInfo object returned 
	 * by SecurityManager.getLoggedOnUser() against the set of credentials for allowed 
	 * users.
	 */
	public static class SecurityAdvice implements MethodBeforeAdvice {
		private SecurityManager securityManager;
		public SecurityAdvice() {
			this.securityManager = new SecurityManager();
		}
		public void before(Method method, Object[] args, Object target)
		throws Throwable {
			System.out.println("method.getName()="  + method.getName());
			if(args.length != 0)
				System.out.println("args[0]="  + args[0]);
			
			UserInfo user = securityManager.getLoggedOnUser();
			if (user == null) {
				System.out.println("No user authenticated");
				throw new SecurityException(
						"You must log in before attempting to invoke the method: "
						+ method.getName());
			} else if (username.equals(user.getUsername())) {
				System.out.println("Logged in user is " + username + " - OKAY!");
			} else {
				System.out.println("Logged in user is " + user.getUsername()
						+ " NOT GOOD :(");
				throw new SecurityException("User " + user.getUsername()
						+ " is not allowed access to method " + method.getName());
			}
			
			// change the argument to a different value
			if(args.length != 0)
				args[0]= 106;
		}
	}

	public static  void beforeAdviceTest(){
		
		// get the security manager
		SecurityManager mgr = new SecurityManager();
		
		// create the proxy which will intercept calls to method of CategoryService
		CategoryService secureService = getCategoryService();
		
		// simulate none-logged in user
		//secureService.getAll();
		
		// simulate the work of a login service.
		// once logged in the account will be stored in memory
		mgr.login("rick", "rick123");
		
		// simulate access of secured method - NOTE that secureService is really a proxy.
		Category c = secureService.get(1);
		System.out.println("Category is " + c);
		
		
	}
	
	public  static CategoryService getCategoryService(){
		
		ApplicationContext ac = new ClassPathXmlApplicationContext("applicationContext.xml");
		CategoryService target = (CategoryService)ac.getBean("categoryService");
		
		
		// create the advice
		SecurityAdvice advice = new SecurityAdvice();
		
		// get the proxy
		ProxyFactory factory = new ProxyFactory();
		factory.setTarget(target);
		factory.addAdvice(advice);
		return (CategoryService)factory.getProxy();
	}
	
	/* ++++++++++++++++++++++++++++++ After returning Advice+++++++++++++++++++++++++++++++++++++++++++*/
	
	/*
	 * After Returning Advice
	 * You can neither change the execution path nor prevent the method from executing.
	 * cannot modify the return value in the after returning advice
	 * you are restricted to performing some additional processing.
	 * it can throw an exception that can be sent up the stack instead of the return value.
	 */
	
	public static class HistoryAdvice implements AfterReturningAdvice{

		public void afterReturning(Object returnVal, Method method, Object[] args,
				Object target) throws Throwable {
			System.out.println(" Target is " + target.getClass());
			System.out.println(" return is " + returnVal);
			
		}
		
	}
	
	public static  void afterReturningAdviceTest(){
		
		
		ApplicationContext ac = new ClassPathXmlApplicationContext("applicationContext.xml");
		CategoryService target = (CategoryService)ac.getBean("categoryService");
		
		
		// create the advice
		HistoryAdvice advice = new HistoryAdvice();
		
		// get the proxy
		ProxyFactory factory = new ProxyFactory();
		factory.setTarget(target);
		factory.addAdvice(advice);
		
		CategoryService service = (CategoryService)factory.getProxy();

		Category c = service.get(106);
		System.out.println("Category is " + c);
		
		
	}
	
	/* ++++++++++++++++++++++++++++++ Throws Advice+++++++++++++++++++++++++++++++++++++++++++*/
	/*
	 * Throws Advice
	 * throws advice only executes if the method threw an exception
	 * you can’t choose to ignore the exception that was raised and return a value for 
	 * the method instead.
	 * 
	 * you can make to the program flow is to change the type of exception that is thrown.
	 * 
	 * you can also use throws advice to provide centralized error logging across your 
	 * application
	 * 
	 * can advise all classes in that API and reclassify the exception hierarchy into 
	 * something more manageable and descriptive.
	 */
	
	/* ++++++++++++++++++++++++++++++ Specifying Point-cuts+++++++++++++++++++++++++++++++++++++++++++*/
	/*
	 * proxy.addAdvisor() behind the scenes, creating an instance of DefaultPointcutAdvisor 
	 * and configuring it with a pointcut that points to all methods.
	 * 
	 * By using pointcuts, you can configure the methods to which an advice applies
	 * 
	 * ADVISOR
	 * an Advisor is Spring’s representation of an aspect,
	 */
	
	
	public static class SimpleStaticPointcut extends StaticMethodMatcherPointcut {

		public boolean matches(Method method, Class cls) {
			return ("get".equals(method.getName()));
		}
		
		/*
		 * Though not necessary, have overridden the getClassFilter() method
		 * to return a ClassFilter instance whose matches() method only returns 
		 * true for the CategoryService class
		 */
		
		
		public ClassFilter getClassFilter() {
			return new ClassFilter() {
				public boolean matches(Class cls) {
					return (cls == CategoryService.class);
				}
			};
		}
	}
	
	public static void SimpleStaticPointcutTest(){
		
		ApplicationContext ac = new ClassPathXmlApplicationContext("applicationContext.xml");
		CategoryService target = (CategoryService)ac.getBean("categoryService");
		
		// create the proxy
		ProxyFactory pf = new ProxyFactory();

		// used this to let the proxy know which methods are to receive advice
		Pointcut pc = new SimpleStaticPointcut();
		Advisor advisor = new DefaultPointcutAdvisor(pc, new CategoryServiceAdvice());
		
		//pass the advisor to the proxy
		pf.addAdvisor(advisor);
		
		pf.setTarget(target);
		
		CategoryService service =  (CategoryService)pf.getProxy();
		service.get(106);
	}
	
	/*
	 * Using Simple Name Matching
	 * To match based on just the name of the method, ignoring the method signature and return type.
	 * use the NameMatchMethodPointcut to match against a list of method names
	 * 
	 */
	public static void SimpleNameMatchPointcutTest(){

		ApplicationContext ac = new ClassPathXmlApplicationContext("applicationContext.xml");
		CategoryService target = (CategoryService)ac.getBean("categoryService");
		
		// create the proxy
		ProxyFactory pf = new ProxyFactory();
		
		
		// create the advisor
		NameMatchMethodPointcut pc = new NameMatchMethodPointcut();
		pc.addMethodName("getAll");
		Advisor advisor = new DefaultPointcutAdvisor(pc, new CategoryServiceAdvice());
		pf.addAdvisor(advisor);
		
		pf.setTarget(target);
		
		CategoryService service =  (CategoryService)pf.getProxy();
		service.getAll();

	}
	
	public static void SimpleRegExpPointcutTest(){
		
		ApplicationContext ac = new ClassPathXmlApplicationContext("applicationContext.xml");
		CategoryService target = (CategoryService)ac.getBean("categoryService");

		// create the proxy
		ProxyFactory pf = new ProxyFactory();
		
		// create the advisor
		JdkRegexpMethodPointcut pc = new JdkRegexpMethodPointcut();
		pc.setPattern("*get*");
		/*
		 * Spring matches the fully qualified name of the method, so for foo1(), Spring is 
		 * matching against com.apress.prospring.ch6.regexppc.RegexpBean.foo1, hence the 
		 * leading .* in the pattern.
		 */
		
		Advisor advisor = new DefaultPointcutAdvisor(pc, new CategoryServiceAdvice());
		pf.addAdvisor(advisor);
		
		pf.setTarget(target);
		
		CategoryService service =  (CategoryService)pf.getProxy();
		service.getAll();
		service.get(106);

	}
	
	/*
	 * instead of using the NameMatchMethodPointcut coupled with a DefaultPointcutAdvisor
	 * we could simply used a NameMatchMethodPointcutAdvisor
	 */
	public static void combinedPointcutAdvisorTest(){

		ApplicationContext ac = new ClassPathXmlApplicationContext("applicationContext.xml");
		CategoryService target = (CategoryService)ac.getBean("categoryService");

		// create the proxy
		ProxyFactory pf = new ProxyFactory();
		
		// create the advisor
		NameMatchMethodPointcutAdvisor advisor = new
		NameMatchMethodPointcutAdvisor(new CategoryServiceAdvice());
		advisor.addMethodName("get");
		

		pf.addAdvisor(advisor);
		
		pf.setTarget(target);
		
		CategoryService proxy =  (CategoryService)pf.getProxy();
		proxy.getAll();

	}
	
	/*
	 * Using AspectJExpressionPointcut
	 */
	/*
	 * The AspectJ expression language is a very powerful language, allowing a lot of different
	 *  joinpoints in a pointcut expression. However, since AspectJExpressionPointcut is used with 
	 *  Spring AOP, which supports only method execution joinpoints, execution is the only joinpoint 
	 *  from AspectJ expression language that can be used.
	 */
	
	public static void aspectJPointcutTest(){
		
		ApplicationContext ac = new ClassPathXmlApplicationContext("applicationContext.xml");
		CategoryService target = (CategoryService)ac.getBean("categoryService");


		// create the proxy
		ProxyFactory pf = new ProxyFactory();
		
		//
		AspectJExpressionPointcut pc = new AspectJExpressionPointcut();
		pc.setExpression("execution(* com.my.service..CategoryService.getAll*(..))");
		
		// create the advisor
		Advisor advisor = new DefaultPointcutAdvisor(pc, new CategoryServiceAdvice());
		
		pf.addAdvisor(advisor);
		
		pf.setTarget(target);
		
		CategoryService proxy =  (CategoryService)pf.getProxy();
		proxy.get(106);

	}
	
	/*
	 * Using ComposablePointcut
	 * Consider the situation where you want to pointcut all getter and setter methods on a bean. 
	 * You have a pointcut for getters and a pointcut for setters, but you don’t have one for both.
	 * 
	 * You combine the two pointcuts into a single pointcut using ComposablePointcut instead
	 * of creating two separate
	 * 
	 * ComposablePointcut supports two methods: union() and intersection().
	 * 
	 * By default, ComposablePointcut is created with a ClassFilter that matches all classes 
	 * and a MethodMatcher that matches all methods,
	 * 
	 * The union() and intersection() methods are both overloaded to accept ClassFilter and 
	 * MethodMatcher arguments.
	 * 
	 * you can think of the union() method as an any match, in that it returns true if any of 
	 * the matchers it is wrapping return true.
	 * 
	 * you can think of the intersection() method as an all match, in that it only returns 
	 * true if all its wrapped matchers return true.
	 */
	
	/* ++++++++++++++++++++++++++++++ Annotated Aspects +++++++++++++++++++++++++++++++++++++++++++*/
	
	/*
	 * The first annotation, @Aspect simply tells Spring to treat this bean as an aspect, 
	 * that is, to extract the pointcuts and advice from it.
	 */
	
	@Aspect
	public static class LoggingAspect {
		
		// CategoryService is an advisee
		// The point-cut is all methods
		// The code before and after the call to the target (pjp.proceed) is the Advice
		// Together all this is an Aspect.
		
		// Spring will create a proxy for this advised bean .. see XML configuration file
		@Around("execution(* com.my.service.CategoryService.getAll(..))")
		public Object log(ProceedingJoinPoint pjp) throws Throwable {
			System.out.println("Before");
			Object ret = pjp.proceed();
			System.out.println("After");
			return ret;
		}
	}
	
	public static void simpleAnnotatedAspecTest(){
		ApplicationContext ac = new ClassPathXmlApplicationContext("applicationContext.xml");
		CategoryService target = (CategoryService)ac.getBean("categoryService");
		
		// As you can see, we need not to create any proxy. However, all calls to
		// CategoryService will be intercepted and advice will be applied is necessary.
		target.get(106);
	}
	
	
	/*
	 * Wrapping Pointcuts in a class
	 * made the class final and created a private constructor: because we intend this class 
	 * only as a holder for the @pointcut methods,
	 */
	public static final class SystemPointcuts {
		private SystemPointcuts() {
		}
		
		//apply advice when a method of CategoryService is called
		@org.aspectj.lang.annotation.Pointcut("execution(* com.my.service.CategoryService.get(..))")
		public void testBeanExecution2() { }
		/*
		 * (the first *) means any return type
		 * (the last *(..)) means any method and any argument
		 * use (com.my.service..*.*) to represent any class in package com.my.service
		 */
		
		//apply advice when a method is called from within CategoryService
		@org.aspectj.lang.annotation.Pointcut("within(com.my.service.CategoryService)")
		public void fromTestBeanExecution() { }
		
		
		/*
		 * Combining different types of point cuts
		 */
		
		//apply advice when a method is called from within com.my.service package
		@org.aspectj.lang.annotation.Pointcut("within(com.my.service..*)")
		private void withinProSpringPackage() { }
		
		@org.aspectj.lang.annotation.Pointcut("execution(* com.my.service.CategoryService.*(..)) &&" +
		"within(com.my.service..*)")
		public void same1() { }
		
		@org.aspectj.lang.annotation.Pointcut("execution(* com.my.service.CategoryService.*(..)) &&" +
			"withinProSpringPackage()")
		public void same2() { }
		
		@org.aspectj.lang.annotation.Pointcut("testBeanExecution2() && withinProSpringPackage()")
		public void same3() { }
		
		@org.aspectj.lang.annotation.Pointcut("execution(* com.my.service..*.*(..)) &&" +
				"!execution(* com.my.service..*.set*(..)) &&" +
				"!execution(* com.my.service..*.get*(..))")
				public void serviceExecution() { }
		/*
		 * we can use it in the tx:advice and make every method in all classes in the 
		 * com.my.service package transactional, as long it is not a simple getter or setter.
		 * 
		 * TIP:
		 * use the @Transactional annotation to mark a method to be transactional; it is even 
		 * simpler than using @pointcuts.
		 */
	}
	
	
	/*
	 * Reusing Pointcuts
	 * 
	 */
	@Aspect
	public static class LoggingAspect2 {
		
		@org.aspectj.lang.annotation.Pointcut("execution(* com.my.service.CategoryService.get(..))")
		private void testBeanExecution() { }
		
		@Around("testBeanExecution()")
		public Object log(ProceedingJoinPoint pjp) throws Throwable {
			System.out.println("Before");
			Object ret = pjp.proceed();
			System.out.println("After");
			return ret;
		}
		
		// see above SystemPointcuts class
		@Around("CategoryService$SystemPointcuts.testBeanExecution2()")
		public Object logw(ProceedingJoinPoint pjp) throws Throwable {
			System.out.println("SystemPointcuts.testBeanExecution2():Before");
			Object ret = pjp.proceed();
			System.out.println("SystemPointcuts.testBeanExecution2():After");
		return ret;
		}
	}

	
	@Aspect
	public static class BeforeAspect {
		@org.aspectj.lang.annotation.Pointcut("execution(* com.my.service.*.*(..))")
		private void serviceExecution() { }
		
		// Call to all methods in the com.my.service package, except to the login method
		// will be advised by the security advisor.
		@org.aspectj.lang.annotation.Pointcut(
		"execution(* com.my.service.CategoryService$SecurityManager.login(..))")
		private void loginExecution() { }
		
		//Notice: Need to exclude the login method()
		@Before("serviceExecution() && !loginExecution()")
		public void beforeLogin() throws Throwable {
			SecurityManager securityManager = new SecurityManager();
			if (securityManager.getLoggedOnUser() == null)
				throw new RuntimeException("Must login to call this method.");
			else
				System.out.println("Logged in user " + securityManager.getLoggedOnUser().getUsername());
		}
	}
	
	
	public static void simpleAnnotatedBeforeAspecTest(){
		ApplicationContext ac = new ClassPathXmlApplicationContext("applicationContext.xml");
		CategoryService target = (CategoryService)ac.getBean("categoryService");
		
		SecurityManager sm = new SecurityManager();
		sm.login(username, password);
		
		// As you can see, we need not to create any proxy. However, all calls to
		// CategoryService will be intercepted and advice will be applied is necessary.
		target.get(106);
		target.getAll();
	}
	
	/*
	 * The usual application for after returning advice is to perform auditing.
	 */
	@Aspect
	public class AfterAspect {
		@AfterReturning("execution(* com.my.service.*.*(..))")
		public void auditCall() {
			System.out.println("After method call");
		}
	}
	
	/*
	 * Improved after aspect with the advisor method taking the joinpoint
	 * This way the audit messages cannot access details of the call
	 * the most interesting information we can obtain from the JoinPoint is 
	 * the Method being executed and its arguments.
	 * 
	 * The only trouble is that we can’t use the JoinPoint to find the return value.
	 * To do this, we must stop using the JoinPoint argument and explicitly set the 
	 * returning() and argNames() properties of the @AfterReturning annotation
	 */
	@Aspect
	public static class AfterAspect2 {
	@AfterReturning("execution(* com.my.service.*.*(..))")
		public void auditCall(JoinPoint jp) {
		System.out.println("After method call of " + jp);
		}
	}
	
	@Aspect
	public class AfterAspect3 {
		@AfterReturning(
				pointcut = "execution(* com.my.service.*.*(..))",
				returning = "ret", argNames = "ret")
				public void auditCall(Object ret) {
					System.out.println("After method call of " + ret);
						if (ret instanceof UserInfo) {
							((UserInfo)ret).setPassword("******");
				}
		}
	}
	
	/*
	 * This advice executes when its target method throws an exception.
	 */
	
	@Aspect
	public class AfterThowingAspect {
		@AfterThrowing(
				pointcut = "execution(* com.my.service.*.*(..))",
				throwing = "ex", argNames = "ex")
				public void logException(IOException ex) {
					System.out.println("After method call of " + ex);
				}
	}
	
//	/*
//	 * After Advice
//	 * This executes regardless of whether the target method completes normally or 
//	 * throws an exception.
//	 */
//	
//	
//	/*
//	 * Around Advice
//	 * the advice needs at least one argument, and it must return a value.
//	 * The argument specifies the target being invoked, and the return value 
//	 * specifies the return value,
//	 * 
//	 * typically find around advice in transaction management:
//	 * In this advice, you may choose not to invoke the target at all,
//	 * may invoke the target multiple times (to handle retries for example)
//	 */
//	@Aspect
//	public class CachingAspect {
//		/*
//		 * This is an incomplete implementation of caching taken from the book
//		 */
//		@Around("execution(* com.apress.prospring2.ch06.services.*.*(..))")
//		public Object cacheCalls(ProceedingJoinPoint pjp) throws Throwable {
//			Object cacheRet = null;
//			if (cacheRet == null) {
//				// invoke the target
//				Object ret = pjp.proceed();
//				cacheRet = ret;
//				return ret;
//			}
//			return cacheRet;
//		}
//	}
	/*
	 * How to access the arguments of the target method in the advice code?
	 * @AspectJ supports binding, where we can bind a value from the target to an argument 
	 * in the advice.
	 * 
	 */
	@Aspect
	public static class BindingAspect {
		@Around(value =
			"execution(* com.my.service..*(..)) " +
			"&& args(id)",
			argNames = "pjp, id")
			public Object discountEnforcement(ProceedingJoinPoint pjp, Integer id)
		throws Throwable {
			/*
			 * This Aspect will execute whenever a method in the com.my.service
			 * package is invoked as long as it takes a single argument (the args clause)
			 * and as long as the type of id is Integer (inferred from the data type
			 * of the advice method arguments)
			 * The annotation attribute (argNames) specifies what the advice method
			 * names are.
			 * argNames() will which argument gets the bound value from the pointcut expression
			 */
			System.out.println("ID=" + id);
			return pjp.proceed();
		}
	}
//	
//	/*
//	 * Introduction
//	 * By using introductions, you can add new functionality to an existing object dynamically.
//	 * You can introduce an implementation of any interface to an existing object.
//	 * 
//	 * Suppose you have an interface, Lockable, that defines the method for locking and unlocking 
//	 * an object. You could simply implement this interface manually for every class you wish to 
//	 * make lockable.
//	 * However, this would result in a lot of duplicated code across many classes.
//	 * 
//	 * You can refactor the implementation into an abstract base class, but you lose your one shot 
//	 * at concrete inheritance, and you still have to check the lock status in every method that modifies 
//	 * the state of the object.
//	 * 
//	 * Using an introduction, you can centralize the implementation of the Lockable interface 
//	 * into a single class and, at runtime, have any object you wish adopt this implementation 
//	 * of Lockable.
//	 * 
//	 * Now we have centralized locking, but what about all the code you need to write to check the 
//	 * lock status? Well, an introduction is simply an extension of a method interceptor, and as such, 
//	 * it can intercept any method on the object on which the introduction was made.
//	 * 
//	 * Using this feature, you could check the lock status before any calls are made to setter methods
//	 * and throw an Exception if the object is locked.
//	 * 
//	 * All of this code is encapsulated in a single place, and none of the Lockable objects need to 
//	 * be aware of this.
//	 */
//	
//	
	
	/*
	 * Non-Annotated Aspects - Aspect Using the aop Namespace
	 * If you cannot use annotations (perhaps you are running on a pre-1.5 JDK or you just don’t 
	 * like them), you can use XML configuration using the aop namespace.
	 */
	
	public static class LoggingAspect3 {
		
		public Object log(ProceedingJoinPoint pjp) throws Throwable {
			System.out.println("AOP Namespace:Before");
			Object ret = pjp.proceed();
			System.out.println("AOP Namespace:After");
			return ret;
		}
	}
}
