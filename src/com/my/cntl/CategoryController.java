package com.my.cntl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractCommandController;
import org.springframework.web.servlet.mvc.AbstractController;
import org.springframework.web.servlet.mvc.AbstractFormController;
import org.springframework.web.servlet.mvc.ParameterizableViewController;
import org.springframework.web.servlet.mvc.SimpleFormController;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import com.my.model.Category;
import com.my.service.CategoryService;


public class CategoryController extends AbstractController{
	
	private CategoryService categoryService;
	private MyCustomView customView;
	
	
	
	public void setCategoryService(CategoryService categoryService) {
		this.categoryService = categoryService;
	}
	
	public void setCustomView(MyCustomView customView) {
		this.customView = customView;
	}
	
	/*
	 * Spring Request Work Flow
	 * 1. Discover the request Locale
	 * 	- By default the servlet discovers the locale by locking at the header of the client request
	 * 	- To override this extend LocaleResolver Interface
	 * 2. Load File data if the request is multipart (file uploads)
	 * 	- Spring integrate Jakarta Commons’ FileUpload library or 
	 * 	- You can extend the MultipartResolver interface if provided libraries not suitable
	 * 3. Locate the Controller responsible for handling the request
	 * 	- The HandlerMapping interface provides the abstraction for mapping requests to their handlers.
	 * 	- Typically a request is mapped to a handler (Controller) via a URL, but other implementations
	 * 	 could use cookies, request parameters
	 * 		- Path Matching (Ant Style wildcard characters)-
	 * 		? 	Matches a single character
	 * 		* 	Matches zero or more characters
	 * 		** 	Matches zero or more directories
	 * 		- Examples -
	 * 		/app/*.x 	Matches all .x files in the app directory
	 * 		- BeanNameUrlHandlerMapping -
	 * 		This class treats any bean with a name or alias that starts with the / character 
	 * 		as a potential request handler.
	 * 		The bean name, or alias, is then matched against incoming request URLs using Ant-style 
	 * 		path matching.
	 * 		<bean name="/home"
				class="com.apress.expertspringmvc.flight.web.HomeController">
				<property name="flightService" ref="flightService" />
			</bean>
	 * 4. Locate any request interceptors for this request and call preHandle()
	 * 5. Invoke the Controller.
	 * 6. Call postHandle() methods on any interceptors.
	 * 7. Handle Exception (if any) with a HandlerExceptionResolver.
	 * 8. Render view if a ModelAndView was returned by the controller
	 * 9. Call afterCompletion() methods on any interceptors.
	 * 
	 * DispatchServlet
	 * This servlet controll the request flow, delegating to the various components that make up
	 * the flow.
	 * 
	 * When initialized, the DispatchServlet it will search the WebApplicationContext for one or 
	 * more instances of the elements that make up the processing pipeline (such as ViewResolvers
	 * or HandlerMappings).
	 * 
	 * Processing elements of the same type (multiple ViewResolvers for example) are chained 
	 * 
	 * The DispatcherServlet uses the Ordered interface to sort many of its collections of delegates.
	 * 
	 * To order anything that implements the Ordered interface, simply give it a property named order. 
	 * The lower the number, the higher it will rank.  Usually, the first element to respond with a 
	 * non-null value wins.
	 * 
	 * With exception for MultipartResolver, HandlerExceptionResolver, or ViewResolver, the Dispatcher
	 * will create the following defaults if implementation of the said components are not found
	 * in the application context.
	 * 
	 * 	• org.springframework.web.servlet.handler.BeanNameUrlHandlerMapping
		• org.springframework.web.servlet.mvc.SimpleControllerHandlerAdapter
		• org.springframework.web.servlet.view.InternalResourceViewResolver
		• org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver
		• org.springframework.web.servlet.theme.FixedThemeResolver
		
		
		- Default Mapping -
		in the case that no other mapping can satisfy the request.
		<bean name="/*"
			class="com.apress.expertspringmvc.flight.web.HomeController">
			<property name="flightService" ref="flightService" />
		</bean>
		
		TIP:
		When using BeanNameUrlHandlerMapping all request handlers must be singletons. 
		Cannot map to prototype beans.
		
		Prototype beans are non-singleton beans. A new bean instance is created for 
		every call to getBean()on the ApplicationContext.
		
		--- SimpleUrlHandlerMapping ---
		Is able to map to prototype request handlers, and it allows you to create 
		complex mappings between handlers and interceptors.
		
		Must declare it inside your ApplicationContext. Now, the Dispatcher will not
		automatically  create an instance of the default BeanNameUrlHandlerMapping, so
		you will need to explicitly declare it if you also need BeanNameUrlHandler
		
		<bean
			class="org.springframework.web.servlet.handler.SimpleUrlHandlerMapping">
			<property name="urlMap">
			<map>
			<entry key="/home" value-ref="homeController" />
			</map>
			</property>
		</bean>
		TIP: The above usage is for singleton handlers. For prototype handlers configure
		as follows:
		<bean
			class="org.springframework.web.servlet.handler.SimpleUrlHandlerMapping">
			<property name="mappings">
			<props>
			<prop key="/home">homeController</prop>
			</props>
			</property>
		</bean>
		
		-- HandlerExceptionResolver --
		
		Spring MVC can catch the exception for you and route the request to a particular 
		error page or other exception handling code.

		any mapped exceptions you have specified in the web.xml will still apply if the 
		exception isn’t handled by a HandlerExceptionResolver.
		
		SimpleMappingExceptionResolver maps exceptions to view names by the exception 
		class name or a substring of the class name.
		
		Can be configured for individual Controllers or for globally for all handlers.
		
		<bean id="exceptionMapping"
			class="org.springframework.web.servlet.handler.SimpleMappingExceptionResolver">
			<property name="exceptionMappings">
			<props>
			<prop key="ApplicationException">appErrorView</prop>
			<prop key="SomeOtherException">someErrorView</prop>
			<prop key="java.lang.Exception">genericErrorView</prop>
			</props>
			</property>
		</bean>
		
		-Caution- Mapping exceptions to individual handlers only works if the handler is 
		a singleton.
		
		--- LocaleResolver ---
		The DispatcherServlet will default to AcceptHeaderLocaleResolver class if no other 
		LocaleResolvers are specified in the ApplicationContext.
		
		This implementation simply delegates to the HttpServletRequest’s getLocale() method
		
		FixedLocaleResolver allows you to override the client's setting but ...
		
		CookieLocaleResolver and the SessionLocaleResolver support changing and storing the 
		Locale across requests.
		

	 */

	/*
	 * Spring Controller are Examples of Template Pattern Implementations
	 * The Template pattern is used to separate the variant sections of an algorithm from 
	 * the invariant sections to allow for easy customization and substitution. In other words, 
	 * a template of the algorithm is created, with the specifics intended to be filled in later.
	 * 
	 * The AbstractController's implementation of handleRequest() applies the Template pattern, 
	 * as it defines a well-known work flow (the algorithm) but provides an extension point for 
	 * specifics via the handleRequestInternal() method.
	 * 
	 * (non-Javadoc)
	 * @see org.springframework.web.servlet.mvc.AbstractController#handleRequestInternal(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 * 
	 * NOTES:
	 * AbstractController extends ApplicationContextAware
	 * 	- AbstractController is perfect for read-only pages and simple requests.
	 * 
	 * 	Therefore it can obtain a reference to its ApplicationContext and
	 *  MessageSource (the ApplicationContext extends MessageSource)
	 *  	- ApplicationContext provides support for loading messages and  making them available
	 * 	
	 * you must define a bean in your configuration of type MessageSource and with the 
	 * name messageSource.
	 * 
	 * ApplicationContext takes this MessageSource and nests it within its default 
	 * MessageSource
	 * 
	 * Implementation of MessageSource are:
	 * 	- ResourceBundleMessageSource - loads messages using a Java ResourceBundle
	 *  - ReloadableResourceMessageSource - like the above but allows schedule reloading of messages
	 * REMINDER: The ApplicationContext is an extension of the BeanFactory which provides
	 * 			  the facility for registration on lookup of beans plus many more ... features
	 * 
	 * ModelAndView:
	 * - Holds A collection of Objects which makes up the response (the Model) and the page
	 * (the view) the user will see next.
	 * 
	 * Model is a Map of arbitrary objects,and the View is typically specified with a 
	 * logical name which is later resolve to an actual View instance.
	 * 
	 * The View is responsible for rendering the response (provided in the Model)
	 * 
	 * An instance of org.springframework.web.servlet.ViewResolver provides the 
	 * mappings between logical View names and the View instance.
	 * 
	 * InternalResourceViewResolver ViewResolver is perfect for JSP views
	 * 		- Internal Resource refers to any resource accessible via the Servlet API's RequestDispatcher.
	 *  - the InternalResourceViewResolver does not require explicit mappings between 
	 *  	View names and View instances,
	 *  	- This works when your logical View names from the ModelAndView match 
	 *  	filename substrings of your JSP pages.
	 *  - To turn on an InternalResourceViewResolver, simply define it as a bean 
	 *  	in the springservlet.xml file
	 * 
	 * TIPS:
	 * - Controllers are intended to be singletons, just like servlets. As singletons, 
	 * they will handle concurrent requests,1 so they should never maintain state during a request.
	 */
	@Override
	protected ModelAndView handleRequestInternal(HttpServletRequest arg0,
			HttpServletResponse arg1) throws Exception {

	
		setCacheSeconds(10);
		
		List<Category> categories = categoryService.getAll();
		
		Map model = new HashMap();
		model.put("categories", categories);
		ModelAndView mav = new ModelAndView("categories", model);
		
		return mav;
		/*
		 * Demo - using Custom View
		Map model = new HashMap();
		model.put("Greeting", "Hello World");
		model.put("Server time", new Date());
		return new ModelAndView(this.customView, model);
		*/
		
		
	}
	
	/*
	 * configures this Controller to respond only to HTTP GET requests,because GET has 
	 * semantics that most closely match that of â€œread.â€
	 * 
	 * this restriction prohibits potentially malicious clients from exploiting the 
	 * system in unintended ways
	 * 
	 * By setting synchronizeOnSession to true, the Controller will ensure that multiple calls from
	 * the same client are handle serially.
	 * If it is possible to directly modify state that is stored in the session, itâ€™s useful 
	 * to serialize access to
	 */
	public CategoryController() {
		
		setSupportedMethods(new String[]{METHOD_GET});
		
		setSynchronizeOnSession(true);
		
		// Browsers will be told it is acceptable to cache the response for five minutes.
		// this can be a simple way to reduce bandwidth and CPU cycles for your application
		setCacheSeconds(5*60);
		
		/*
		 * NOTE:
		 * These types of configurations can also be set in the bean definition of the Controller.
		 * However, the best place to put configurations that never change is in the Code
		 */
	}
	
	/*
	 *  ParameterizableViewController subclass of AbstractController
	 *  	- use this controller simply to display a view using its name.
	 * 		- The name can be set in the viewName property.
	 * 
	 * Set the view name in the bean configuration file
	 */
	public class CategoryPVCotroller extends ParameterizableViewController {
		
	}
	
	
	/* MultiActionController is subclass of AbstractController
	 *  - it lets you provide as many implementations of public ModelAndView(HttpServletRequest, HttpServletResponse)
	 *  - you can map multiple URLs to the same controller and use different methods to process the various URLs.
	 *  - delegate and methodNameResolver properties tell the MultiActionController 
	 *    which method on which object to invoke for each request
	 *  - if delegate is null methods on the controller itself are used
	 */
	public class CategoryMAController extends MultiActionController {
		
	}
	
	//----------------------------------------------------------------------------
	
	/*
	 * Command Controllers
	 * allows a command object properties to be populated from the <FORM> submission.
	 */
	
	//----------------------------------------------------------------------------
	
	/*
	 * This class is not designed to actually handle HTML FORM submissions, but it provides 
	 * basic support for validation and data binding.
	 */
	public class CategoryACController extends AbstractCommandController {
		
		@Override
		protected ModelAndView handle(HttpServletRequest arg0,
				HttpServletResponse arg1, Object arg2, BindException arg3)
				throws Exception {
			// TODO Auto-generated method stub
			return null;
		}
	}
	
	/*
	 * this command controller will process the values in HttpServletRequest and 
	 * populate the controller command object.
	 * 		- has the ability to detect duplicate form submission,
	 * 		- allows you to specify the views that are to be displayed in the code 
	 * 			rather than in the Spring context file
	 * 		- the method Map referenceData() returns the model (java.util.Map) for 
	 * 			the form view. To this map you can easily pass any parameter that you 
	 * 			would use on the form page.
	 */
	public class CategoryAFController extends AbstractFormController {
		

		@Override
		protected ModelAndView showForm(HttpServletRequest arg0,
				HttpServletResponse arg1, BindException arg2) throws Exception {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		protected ModelAndView processFormSubmission(HttpServletRequest arg0,
				HttpServletResponse arg1, Object arg2, BindException arg3)
				throws Exception {
			// TODO Auto-generated method stub
			return null;
		}
	}
	
	/*
	 * you can specify the views to be displayed for the initial view and a success view;
	 * you can set the command object you need to populate with the submitted data.
	 */

	/*
	 * The controller uses the method isFormSubmission() to determine if the HTTP request is 
	 * either a form viewing or form submission.
	 * 
	 * The default implementation of this method merely checks the HTTP method, and if it is a 
	 * POST then isFormSubmission() returns true.
	 * 
	 * If indeed the request is not a form submission, the controller will then consider this 
	 * request as the first of two requests
	 * 
	 * It will call formBackingObject() method to create an instance of the form object
	 * By default, this method will return an instance of the class specified with setCommandClass().
	 * 
	 * the controller then creates the DataBinder and calls the initBinder() life cycle method.
	 * 
	 * 
	 * To store the form object in the session for the duration of the controllers work flow call
	 * setSessionForm() with a value of true.
	 * 
	 * Before the view is rendered, the referenceData() callback is called.
	 * 
	 * This life cycle method allows you to assemble and return any auxiliary objects required to 
	 * render the view.
	 * 
	 * The form object will automatically be sent to the form view, so use this method to put anything 
	 * else into the model the form page might need. By
	 * 
	 * ---------------------------------------------------
	 * Property			Default		Des
	 * formView 		null 		The name of the view that contains the form.
	 * successView 		null 		The name of the view to display on successful completion of form submission.
	 * bindOnNewForm 	false 		Should parameters be bound to the form bean on initial views of the form? (Parameters will still be bound on form submission.)
	 * sessionForm 		false 		Should the form bean be stored in the session between form view and form submission?
	 * commandName 		"command"	Logical name for the form bean.
	 * commandClass 	null 		The class of the form bean.
	 * validator(s) 	null		One or more Validators that can handle objects of type configured by commandClass property.
	 * validateOnBinding true		Should the Controller validate the form bean after binding during a form submission?
	 */
	public static class CategorySFController extends SimpleFormController {
		/* ------------ NOTES ON NESTED CLASS --------------------
		 * This inner class be be declared static in order to prevent the following execption:
		 * No default constructor found; nested exception is java.lang.NoSuchMethodException: 
		 * com.my.cntl.CategoryController$CategorySFController.<init>()
		 * 
		 */
		
		private CategoryService categoryService;
		private CategoryValidator validator;
		
		public void setCategoryService(CategoryService categoryService) {
			this.categoryService = categoryService;
		}
		
		public void setValidator(CategoryValidator validator) {
			this.validator = validator;
		}
		
		public CategorySFController() {
			//set the object this controller creates
			setCommandClass(Category.class);
			
			// set the name that will be used to reference the command object
			setCommandName("category");
			
			//specifies the view that will be used to display the form
			setFormView("category");
			
		}
		
		// override the initBinder() method to register CustomEitors
		@Override
		protected void initBinder(HttpServletRequest request,
				ServletRequestDataBinder binder) throws Exception {
			
			/*
			 * register CustomDateEditor for the java.util.Date class to the ServletRequestDataBinder.
			 * ServletRequestDataBinder extends the org.springframework. validation.DataBinder class
			 * to bind the request parameters to the JavaBean properties.
			 * 
			 * this lets our controller know to use our date editor (CustomDateEditor) to set properties
			 * of type java.util.Date on the command object.
			 * 
			 */
			SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
			dateFormat.setLenient(false);
			binder.registerCustomEditor(Date.class, null,new CustomDateEditor(dateFormat, false));
			
		}
		
			protected ModelAndView onSubmit(HttpServletRequest request,
			HttpServletResponse response, Object command,
			BindException errors) throws Exception {
				System.out.println(command);
				//is a RedirectView that will redirect to the /categories.jsp page
			return new ModelAndView("categories-r");
			}
			
			/*
			 * How to prepare the command object so it contains data retrieved from the business layer.
			 * To do this, override the formBackingObject() method.
			 * By default, this method will return an instance of the class specified with 
			 * setCommandClass().
			 */
			@Override
			protected Object formBackingObject(HttpServletRequest request)
					throws Exception {
				
				String strId = request.getParameter("id");
				Integer id = Integer.parseInt(strId);
				Category cat = categoryService.get(id);
				
				return cat;
			}
			
			/*
			 * The formBackingObject will return an instance of class set in setCommandClass().
			 * If we have more data we want we can override the referenceData() method
			 */
			
			@Override
			protected Map referenceData(HttpServletRequest req) throws Exception {
				Map data = new HashMap();
				data.put("language", "English");
				return data;
			}
	}
	
	
	public static class CategoryValidator implements Validator {
		public boolean supports(Class clazz) {
			return clazz.isAssignableFrom(Category.class);
		}
		
		public void validate(Object obj, Errors errors) {
			Category category = (Category)obj;
			if (category.getDisplayName() == null || category.getDisplayName().length() == 0) {
				//add a validation error with errorCode set to required.
				//This code identifies a message resource, which needs to be resolved using 
				// a messageSource bean.
				// there should be a key named 'required' in a properties file 
				errors.rejectValue("name", "required", "");
			}
		}
		
	}
	
	/*
	 * Example - Using Spring ValidationUtils
	 * Spring provides the convenience utility class ValidationUtils for easier, 
	 * more intuitive validation. Using ValidationUtils,
	 */
	public static class CategoryValidator2 implements Validator {
		public boolean supports(Class clazz) {
			return clazz.isAssignableFrom(Category.class);
		}
		
		public void validate(Object obj, Errors errors) {
			Category category = (Category)obj;
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "name", "required", "Field is required.");
		}
		
	}

	/*
	 * HandlerExceptionResolver provides information about what handler was executing when 
	 * the exception was thrown, as well as many options to handle the exception before the 
	 * request is forwarded to a user-friendly URL.
	 * 
	 * This is the same end result as when using the exception mappings defined in web.xml.
	 * 
	 * SimpleMappingExceptionResolver enables you to take the class name of any exception 
	 * that might be thrown and map it to a view name.
	 * 
	 * Note that only exceptions that occur before rendering the view are resolved by the 
	 * ExceptionResolver.
	 */
	
	
	/*
	 * There is no need to implement or extend  a Controller interface
	 * To be able to use this annotation, you have to add component scanning 
	 * to your configuration files
	 */
	@Controller
	@RequestMapping("/category.html")
	public static class InnerCategoryController {
		public ModelAndView displayCategories(){
			return null;
		}
		
	}
}

