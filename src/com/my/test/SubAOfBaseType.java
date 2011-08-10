package com.my.test;

public class SubAOfBaseType extends BaseType {

	private String pval = new String("Private Value of SubA");
	
	@Override
	protected void methodA() {
		System.out.println("Sub::methodA()" + this.pval);
		
	}
	
	protected void methodB() {
		System.out.println("Sub::methodB()" + this.pval);
		super.methodB();
	}
	
	public static void main(String[] args) {
		SubAOfBaseType obj = new SubAOfBaseType();
		obj.methodA();
		obj.methodB();
	}
}
