/*
 * $Id: ToneDetails.java 5252 2007-02-26 14:22:16Z stube-an $
 * 
 */
package com.my.model;



public class ToneDetails implements java.io.Serializable {

	private static final long serialVersionUID = -8715850691459581543L;
	private Integer id;
	private Tone tone;
	private Language language;
	private String artist;
	private String name;
	private String shortName;

	private int hashCode = Integer.MIN_VALUE;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getArtist() {
		return artist;
	}

	public void setArtist(String artist) {
		this.artist = artist;
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

	public String getShortName() {
		return shortName;
	}

	public void setShortName(String shortName) {
		this.shortName = shortName;
	}

	public Tone getTone() {
		return tone;
	}

	public void setTone(Tone tone) {
		this.tone = tone;
	}

	@Override
	public boolean equals (Object obj) {
		if (null == obj) {
			return false;
		}
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof ToneDetails)) {
			return false;
		}
		final ToneDetails name = (ToneDetails) obj;
		if (null == this.getId() || null == name.getId()) {
			return false;
		}
		return this.getId().equals(name.getId());
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
