package com.my.test;

public class BaseType {

	private String pval = new String("Private Value of Base Type");
	
	protected void methodA(){
		System.out.println("Base::methodA()" + this.pval);
	}
	
	protected void methodB(){
		System.out.println("Base::methodB():" + this.pval);
		methodC();
	}
	
	private void methodC(){
		System.out.println("Base::methodB():" + this.pval);
	}
}
