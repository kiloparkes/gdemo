/*
 * $Id: CategoryDetails.java 8850 2007-08-06 08:59:51Z stube-an $
 * 
 */
package com.my.model;



/**
 * @author dumer-fl
 * @version $LastChangedRevision: 8850 $
 *
 */
public class CategoryDetails implements java.io.Serializable {

	private static final long serialVersionUID = -4274932067000927424L;
	private Integer id;
	private Category category;
	private Language language;
	private String name;
	private String description;

	private int hashCode = Integer.MIN_VALUE;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Language getLanguage() {
		return language;
	}

	public void setLanguage(Language language) {
		this.language = language;
	}

	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public boolean equals (Object obj) {
		if (null == obj) {
			return false;
		}
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof CategoryDetails)) {
			return false;
		}
		final CategoryDetails details = (CategoryDetails) obj;
		if (null == this.getId() || null == details.getId()) {
			return false;
		}
		return this.getId().equals(details.getId());
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
