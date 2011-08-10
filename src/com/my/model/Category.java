/*
 * $Id: Category.java 19977 2008-06-09 14:38:19Z bogda-zv $
 * 
 */
package com.my.model;

/**
 * $Id: Category.java 19977 2008-06-09 14:38:19Z bogda-zv $
 */
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author dumer-fl
 * @version $LastChangedRevision: 19977 $
 *
 */
public class Category implements java.io.Serializable {

	private static final long serialVersionUID = -3617268102661761565L;
	// Fields
	private Integer id;
	private Category parent;
	private int idx;
	private String categoryIdentifier;
	private boolean ranked;
	private boolean availableViaWeb;
	private boolean availableViaWap;
	private boolean availableViaIvr;
	private Integer dtmf;
	private Date created;
	private String createdBy;
	private CategoryRankType rankType;
	private Short rankAutoScheduleTime;
	private Integer rankAutoScheduleDay;
	private Integer rankAutoTopTones;
	private Date lastRefreshRanked;
	private boolean promotional;
	private Date promotionValidFrom;
	private Date promotionValidTo;
	private boolean imageUploaded;
	private int distributionRetries;

	private Map<Language, CategoryDetails> details = new HashMap<Language, CategoryDetails>();
	private List<Category> children = new ArrayList<Category>();
	private Set<ToneCategory> toneCategories;
	private List<ToneCategory> rankedToneCategories;

	private int hashCode = Integer.MIN_VALUE;

	public int getDepth() {
		if (parent == null) {
			return 0;
		}
		return parent.getDepth() + 1;
	}

	public String getDisplayName() {
		String name = null;
		Category c = this;
		while (c != null) {
			name = c.getCategoryIdentifier() + (name == null ? "" : " - " + name);
			c = c.getParent();
		}
		return name;
	}

	public boolean isAvailableViaIvr() {
		return availableViaIvr;
	}

	public void setAvailableViaIvr(boolean availableViaIvr) {
		this.availableViaIvr = availableViaIvr;
	}

	public boolean isAvailableViaWap() {
		return availableViaWap;
	}

	public void setAvailableViaWap(boolean availableViaWap) {
		this.availableViaWap = availableViaWap;
	}

	public boolean isAvailableViaWeb() {
		return availableViaWeb;
	}

	public void setAvailableViaWeb(boolean availableViaWeb) {
		this.availableViaWeb = availableViaWeb;
	}

	public String getCategoryIdentifier() {
		return categoryIdentifier;
	}

	public void setCategoryIdentifier(String categoryIdentifier) {
		this.categoryIdentifier = categoryIdentifier;
	}

	public List<Category> getChildren() {
		return children;
	}

	public void setChildren(List<Category> children) {
		this.children = children;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public Map<Language, CategoryDetails> getDetails() {
		return details;
	}

	public void setDetails(Map<Language, CategoryDetails> details) {
		this.details = details;
	}

	public Integer getDtmf() {
		return dtmf;
	}

	public void setDtmf(Integer dtmf) {
		this.dtmf = dtmf;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public int getIdx() {
		return idx;
	}

	public void setIdx(int idx) {
		this.idx = idx;
	}

	public Category getParent() {
		return parent;
	}

	public void setParent(Category parent) {
		this.parent = parent;
	}

	public List<ToneCategory> getRankedToneCategories() {
		return rankedToneCategories;
	}

	public void setRankedToneCategories(List<ToneCategory> rankedToneCategories) {
		this.rankedToneCategories = rankedToneCategories;
	}

	public Set<ToneCategory> getToneCategories() {
		return toneCategories;
	}

	public void setToneCategories(Set<ToneCategory> toneCategories) {
		this.toneCategories = toneCategories;
	}

	public boolean isRanked() {
		return ranked;
	}

	public void setRanked(boolean ranked) {
		this.ranked = ranked;
	}

	@Override
	public boolean equals (Object obj) {
		if (null == obj) {
			return false;
		}
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof Category)) {
			return false;
		}
		final Category category = (Category) obj;
		if (null == this.getId() || null == category.getId()) {
			return false;
		}
		return this.getId().equals(category.getId());
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


	/**
	 * Retrieves the day when a ranked category having the rank type  set to {@link CategoryRankType#autoRanking}
	 * will be repopulated with tones.
	 * 
	 * @return According to the AutoRankGenerationFrequency type, the following values are possible:<br/>
	 * - {@link #getAutoRankGenerationFrequency()} returns {@link CategoryAutoRankGenerationFrequency#daily} - the value doesn't matter; {@link #getRankAutoScheduleTime()} should be used<br/>
	 * - {@link #getAutoRankGenerationFrequency()} returns {@link CategoryAutoRankGenerationFrequency#weekly} - the value represents the day of the week<br/>
	 * - {@link #getAutoRankGenerationFrequency()} returns {@link CategoryAutoRankGenerationFrequency#weekly} - the value represents the day of the month<br/>
	 */
	public Integer getRankAutoScheduleDay() {
		return rankAutoScheduleDay;
	}

	/**
	 * Retrieves the day when a ranked category having the rank type  set to {@link CategoryRankType#autoRanking}
	 * will be repopulated with tones.
	 * 
	 * @param rankAutoScheduleDay According to the AutoRankGenerationFrequency type, the following values are possible:<br/>
	 * - {@link #getAutoRankGenerationFrequency()} returns {@link CategoryAutoRankGenerationFrequency#daily} - the value doesn't matter; {@link #getRankAutoScheduleTime()} should be used<br/>
	 * - {@link #getAutoRankGenerationFrequency()} returns {@link CategoryAutoRankGenerationFrequency#weekly} - the value represents the day of the week<br/>
	 * - {@link #getAutoRankGenerationFrequency()} returns {@link CategoryAutoRankGenerationFrequency#weekly} - the value represents the day of the month<br/>
	 */
	public void setRankAutoScheduleDay(Integer rankAutoScheduleDay) {
		this.rankAutoScheduleDay = rankAutoScheduleDay;
	}

	/**
	 * Retrieves the time (minutes from midnight) when an autoranked category must be repopulated.
	 * 
	 * @return minutes from midnight
	 */
	public Short getRankAutoScheduleTime() {
		return rankAutoScheduleTime;
	}

	/**
	 * Sets the time (minutes from midnight) when an autoranked category must be repopulated.
	 * 
	 * @param rankAutoScheduleTime minutes from midnight
	 */
	public void setRankAutoScheduleTime(Short rankAutoScheduleTime) {
		this.rankAutoScheduleTime = rankAutoScheduleTime;
	}

	/**
	 * Returns the rank type for a ranked category
	 * 
	 * @return rank type
	 */
	public CategoryRankType getRankType() {
		return rankType;
	}

	/**
	 * Sets the rank type for a ranked category.
	 * 
	 * @param rankType rank type
	 */
	public void setRankType(CategoryRankType rankType) {
		this.rankType = rankType;
	}

	/**
	 * Retrieves the number of tones that must be added to an auto ranked category
	 * when this is being repopulated.
	 * 
	 * @return number of tones
	 */
	public Integer getRankAutoTopTones() {
		return rankAutoTopTones;
	}

	/**
	 * Sets the number of tones that must be added to an auto ranked category
	 * when this is being repopulated.
	 * 
	 * @param rankAutoTopTones number of tones
	 */
	public void setRankAutoTopTones(Integer rankAutoTopTones) {
		this.rankAutoTopTones = rankAutoTopTones;
	}

	/**
	 * Retrieves the date when an auto ranked category has been refreshed.
	 * 
	 * @return date
	 */
	public Date getLastRefreshRanked() {
		return lastRefreshRanked;
	}

	/**
	 * Sets the date when an auto ranked category has been refreshed.
	 * 
	 * @param lastRefreshRanked date when an auto ranked category has been refreshed.
	 */
	public void setLastRefreshRanked(Date lastRefreshRanked) {
		this.lastRefreshRanked = lastRefreshRanked;
	}

	public boolean isPromotional() {
		return promotional;
	}

	public void setPromotional(boolean promotional) {
		this.promotional = promotional;
	}

	public Date getPromotionValidFrom() {
		return promotionValidFrom;
	}

	public void setPromotionValidFrom(Date promotionValidFrom) {
		this.promotionValidFrom = promotionValidFrom;
	}

	public Date getPromotionValidTo() {
		return promotionValidTo;
	}

	public void setPromotionValidTo(Date promotionValidTo) {
		this.promotionValidTo = promotionValidTo;
	}

	public boolean isPromotionOverdue(){
		if( !promotional || promotionValidTo == null ) {
			return false;
		}
		final Calendar calendar = Calendar.getInstance();
		calendar.set( Calendar.HOUR_OF_DAY, 0 );
		calendar.set( Calendar.MINUTE, 0 );
		calendar.set( Calendar.SECOND, 0 );
		calendar.set( Calendar.MILLISECOND, 0 );
		return calendar.getTime().after( promotionValidTo );
	}

	public int getTonesCount(){
		if( toneCategories == null ) {
			return 0;
		}
		return toneCategories.size();
	}

	public int getChildrenCount(){
		if( children == null ) {
			return 0;
		}
		return children.size();
	}

	public boolean isImageUploaded() {
		return imageUploaded;
	}

	public void setImageUploaded(boolean imageUploaded) {
		this.imageUploaded = imageUploaded;
	}

	public void addChildCategory(Category childCategory) {
		if (childCategory == null)
			throw new IllegalArgumentException("Null child category!");
		if (childCategory.getParent() != null)
			childCategory.getParent().getChildren().remove(childCategory);
		childCategory.setParent(this);
		children.add(childCategory);
	}
}


