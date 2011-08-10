/*
 * $Id: Language.java 19123 2008-05-16 07:07:54Z laufe-je $
 * 
 */
package com.my.model;



public class Language implements java.io.Serializable {

	private static final long serialVersionUID = 3066670087712534124L;

	private String code;

	private int hashCode = Integer.MIN_VALUE;

	public Language() {}

	public Language(String code) {
		this.code = code;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	@Override
	public boolean equals (Object obj) {
		if (null == obj) {
			return false;
		}
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof Language)) {
			return false;
		}
		final Language language = (Language) obj;
		if (null == this.getCode() || null == language.getCode()) {
			return false;
		}
		return this.getCode().equals(language.getCode());
	}

	@Override
	public int hashCode () {
		if (Integer.MIN_VALUE == hashCode) {
			if (null == code) {
				return super.hashCode();
			}
			final String hashStr = this.getClass().getName() + ":" + code.hashCode();
			hashCode = hashStr.hashCode();
		}
		return hashCode;
	}

	@Override
	public String toString() {
		return code;
	}
}
