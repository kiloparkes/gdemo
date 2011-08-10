/*
 * $Id: ToneCategory.java 5252 2007-02-26 14:22:16Z stube-an $
 * 
 */
package com.my.model;

import java.util.Date;



public class ToneCategory implements java.io.Serializable {

	private static final long serialVersionUID = 2154144928159496443L;
	private Integer id;
	private Tone tone;
	private Category category;
	private Date validFrom;
	private Date validTo;
	private Integer idx;

	private int hashCode = Integer.MIN_VALUE;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}

	public Integer getIdx() {
		return idx;
	}

	public void setIdx(Integer idx) {
		this.idx = idx;
	}

	public Tone getTone() {
		return tone;
	}

	public void setTone(Tone tone) {
		this.tone = tone;
	}

	public Date getValidFrom() {
		return validFrom;
	}

	public void setValidFrom(Date validFrom) {
		this.validFrom = validFrom;
	}

	public Date getValidTo() {
		return validTo;
	}

	public void setValidTo(Date validTo) {
		this.validTo = validTo;
	}

	@Override
	public boolean equals (Object obj) {
		if (null == obj) {
			return false;
		}
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof ToneCategory)) {
			return false;
		}
		final ToneCategory toneCat = (ToneCategory) obj;
		if (null == this.getId() || null == toneCat.getId()) {
			return false;
		}
		return this.getId().equals(toneCat.getId());
	}

	@Override
	public int hashCode () {
		if (Integer.MIN_VALUE == hashCode) {
			if (null == this.getId()) {
				return super.hashCode();
			}
			final String hashStr = this.getClass().getName() + ":" + this.getId().hashCode();
			hashCode = hashStr.hashCode();
		}
		return hashCode;
	}

	@Override
	public String toString() {
		return id.toString();
	}
}
