package com.my.test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.bind.ServletRequestDataBinder;

import com.my.model.Category;
import com.my.model.CategoryDetails;
import com.my.model.Language;

import junit.framework.TestCase;

/*
 * NOTES:
 * The DataBinder supports setting properties directly on individual objects, or objects in arrays,
 * Lists, Sets, or Maps only. 
 * Through the use of PropertyEditors, it supports setting objects directly, or objects in Lists, 
 * arrays, or Maps. But Not in Sets.
 * 
 * 
 * There are many default PropertyEditors that come registered to the DataBinder by default.
 * 
 * Some spring Editors like CustomDateEditor requires manual registration and configuration,
 * 
 * the DataBinder can be configured to allow only accepted and approved properties. Properties 
 * not in the approved list will be dropped, and binding will continue.
 */

public class MvcTest extends TestCase {

	private Category category;
	private ServletRequestDataBinder binder;
	private MockHttpServletRequest request;
	
	@Override
	protected void setUp() throws Exception {
	
		// simulate an instance of category on the memory of the server
		// When form data is submitted values will be bind to properties of this
		// object 
		category = new Category();
		/*
		 * nameBean is used when generating an Errors instance and error messages, in the case 
		 * of errors during binding. 
		 * The name can be any String, though it is best to use names that are easily compatible
		 *  with properties files. If not provided the name will default to target.
		 *  
		 *  However, when this DataBinder is used in the MVC framework, the default name of 
		 *  the JavaBean is command.
		 *  
		 *  
		 */
		binder = new ServletRequestDataBinder(category, "nameBean");
		request = new MockHttpServletRequest();
	}
	
	public void testSimpleBind() {
		/*
		 * simulate a request submission by adding parameters,
		 */
		// just like /servlet?categoryIdentifier=Anya
		request.addParameter("categoryIdentifier", "Anya");
		
		/*
		 * The bind() method then delegates to a BeanWrapperImpl class to translate 
		 * the string expressions into JavaBean setters.
		 * 
		 * After bind returns, the bean is populated and ready to be used by the Controller.
		 */
		binder.bind(request); // performed by BaseCommandController
								// on submit so you don’t have to
		
		/*
		 * Binding TIP:
		 * Nested non-string types must be initialized to non-null value
		 * Reason is that the binder will translate, for example,
		 * 	parent.categoryIdentifier to getParent().setCategoryIdentifier("").
		 * So, if the parent property is NULL in the class Object we would get an error.
		 * 
		 * REMINDER: The name of the command object is always implied, so instead of 
		 * category.parent.categoryIdentifier we just need parent.categoryIdentifier
		 */
		assertEquals("Anya", category.getCategoryIdentifier()); // true!
		
		
		/*
		 * Binding to Lists
		 * The DataBinder uses a familiar [index] notation to reference items in a List or array.
		 * For instance, the string names[0].firstName is the same as getNames().get(0).setFirstName("value").
		 * 
		 */
		
	}
	
	public void testListBind() {
		
		Category child = new Category();
		// add a child object so that the children list will not just contain null values
		category.getChildren().add(child);
		request.addParameter("children[0].categoryIdentifier", "First Child");
		
		binder.bind(request);
		
		assertEquals("First Child", category.getChildren().get(0).getCategoryIdentifier()); // true!
	}
	
	
	public void testMapBind() {
		
		CategoryDetails details = new CategoryDetails();
		Language l = new Language();
		
		category.getDetails().put(l, details);
		request.addParameter("details['en'].name", "English Cat");
		
		binder.bind(request);
		
		assertEquals("First Child", category.getDetails().get("en").getName()); // true!
	}
	
	/* IF TWO PROPERTIES of the same type in the same class need to have different conversion
	 * you may register a PropertyEditor to be used for a specific property, instead of for 
	 * every instance of that class. Using this type of registration provides very specific 
	 * data binding on a per-property basis instead of the default per-class basis.
	 */
	public void testPerPropertyBinding() {
		
		SimpleDateFormat firstDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat secondDateFormat = new SimpleDateFormat("dd-MM-yyyy");
		
		try {
			Date firstExpected = firstDateFormat.parse("2010-01-21");
			Date secondExpected = secondDateFormat.parse("21-01-2011");
			
			CustomDateEditor firstDateEditor = new CustomDateEditor(firstDateFormat, true);
			CustomDateEditor secondDateEditor = new CustomDateEditor(secondDateFormat, true);
			
			request.addParameter("created", "2010-01-21");
			request.addParameter("lastRefreshRanked", "21-01-2011");
			
			binder.registerCustomEditor(Date.class, "created", firstDateEditor);
			binder.registerCustomEditor(Date.class, "lastRefreshRanked", secondDateEditor);
		
			
			binder.bind(request);
			
			assertEquals(firstExpected, category.getCreated()); // true!
			assertEquals(secondExpected, category.getLastRefreshRanked()); // true!
			
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		

	}
}
