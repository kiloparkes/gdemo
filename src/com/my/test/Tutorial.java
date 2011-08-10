package com.my.test;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class Tutorial {

	public void generics(){

		/*
		 * Assignment of an element myUntypedIntList to a specific type will require casting
		 * can result in run-time error
		 * 
		 */
		List myUntypedIntList = new LinkedList();
		
		//mark a list as being restricted to contain a particular data type?
		List<Integer> myIntList = new LinkedList<Integer>();
		//We say that List is a generic interface that takes a type parameter--in this case, Integer.
		//The compiler can now check the type correctness of the program at compile-time.
	}
	
	/*
	 * The stuff in angle brackets are the declarations of the formal type parameters 
	 * of the interfaces MyList and MyIterator. 
	 * In the invocation (usually called a parameterized type), all occurrences of the 
	 * formal type parameter (E in this case) are replaced by the actual type argument 
	 * (in this case, Integer).
	 * 
	 * When a generic declaration is invoked, the actual type arguments are substituted 
	 * for the formal type parameters. 
	 */
	public interface MyList <E>{
	    void add(E x);
	    MyIterator<E> iterator();
	}

	public interface MyIterator<E>{
	    E next();
	    boolean hasNext();
	}
	
	/*
	 * Wildcards 
	 */
	/*
	 * NOTE:Collection<Object> is not a supertype of all kinds of collections
	 * In general, if Foo is a subtype (subclass or subinterface) of Bar, and G is 
	 * some generic type declaration, it is not the case that G<Foo> is a subtype of G<Bar>.
	 */
	void wildCardGenerics(Collection<Object> c){
		/*
			although we are using parameter type Object we can only pass collection of
			Object to this method (see above note)
			
			It is better to use Wildcard...
			The supertype of all kinds of collections? It's written Collection<?> 
			(pronounced "collection of unknown")
		*/
		for (Object e : c) {
	        System.out.println(e);
	    }

	}
	
	void wildCardGenerics2(Collection<?> c){
		
		for (Object e : c) {
	        System.out.println(e);
	    }

		//List<Object> c1 = new ArrayList<String>(); -- compile error 
		List<?> c2 = new ArrayList<String>();	// -- Ok
		
		//c2.add(new Object()); //-- compile error. The collection type is unknown so cannot assign
		
		Object y = c2.get(0); // -OK Can, however, reference

	}

	
	public abstract class Shape {
	    public abstract void draw(Canvas c);
	}

	public class Circle extends Shape {
	    private int x, y, radius;
	    public void draw(Canvas c) {
	    }
	}

	public class Rectangle extends Shape {
	    private int x, y, width, height;
	    public void draw(Canvas c) {
	    }
	}

	public class Canvas {
	    public void draw(Shape s) {
	        s.draw(this);
	   }
    /*
	 * Bounded Wildcards
	 */
	    
	/*
     *  the type rules say that drawAll() can only be called on lists of exactly Shape:
     *  it cannot, for instance, be called on a List<Circle>.
     *  What we really want is for the method to accept a list of any kind of shape:  (See below)
     */
	   
	 public void drawAll(List<Shape> shapes) {
	        for (Shape s: shapes) {
	            s.draw(this);
	       }
	 }

	 /*
	  * There is, as usual, a price to be paid for the flexibility of using wildcards. 
	  * That price is that it is now illegal to write into shapes in the body of the method
	  * 
	  */
	 public void drawAll2(List< ? extends Shape> shapes) {
		 
		 	//shapes.add(0, new Rectangle()); --- compile error . The second parameter of add is an unknown subtype of shape
	        for (Shape s: shapes) {
	            s.draw(this);
	       }
	 }
	 
	}
	
	/*
	 * Generic Methods
	 * When to use generic methods
	 */
	
	interface MyCollection<E> {
	    public boolean containsAll(MyCollection<?> c);  // actual argument can be a collection of any type
	    public boolean addAll(MyCollection<? extends E> c); // actual argument can only be a collection of type/sub-type of E
	}
	
	/*
	 * Version of MyCollection using generic methods
	 * TIPS:
	 * The above interface using wildcrad is better because we simply just want to have subtyping.
	 * Generic methods allow type parameters to be used to express dependencies among the types 
	 * of one or more arguments to a method and/or its return type. 
	 * Here we have only one parameter and the return types do not depend on the type parameter
	 * 
	 */
	interface MyCollection2<E> {
	    public <T> boolean containsAll(MyCollection2<T> c); // actual argument can be a collection of a specified type T
	    public <T extends E> boolean addAll(MyCollection2<T> c); // actual argument can only be a collection of a specified type T
	    // Hey, type variables can have bounds too!
	}


	public static void main(String[] args) {
		String of = args[0];
		System.out.println(of);
		String fileID = of.substring(0, of.indexOf(".wav"));
		System.out.println(fileID);
		int id = Integer.parseInt(fileID);
		NumberFormat nf = new DecimalFormat("00");
		System.out.println(nf.format(id%100));
		
	
	}

}
