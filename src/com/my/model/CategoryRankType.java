package com.my.model;

/**
 * Defines the ranking types for a ranked category.
 * 
 * @author fagur-dr
 *
 * @see Category
 */
public enum CategoryRankType {

	/**Tones within the category are manualy selected and ordered**/
    manualRanking,
	
    /**Tones within the category are automatically added by a background job based on "most sold songs criteria"
     * which means the top tones in the sales list**/
    autoRanking_MostSold,

    /**Tones within the category are automatically added by a background job based on "most played songs criteria"
     * which means the top tones that have been heard**/
    autoRanking_MostPlayed,
    
    /**Tones within the category are automatically added by a background job based on "most assigned songs criteria"
     * which means the top tones that have been assigned**/
    autoRanking_MostAssigned;
    
    public String getDescription() {
        return "domain.categoryRankType." + name();
    }
    
    public int getCode() {
        return ordinal();
    }
}
