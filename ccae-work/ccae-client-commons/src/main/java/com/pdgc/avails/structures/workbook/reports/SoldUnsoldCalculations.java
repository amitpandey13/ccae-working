package com.pdgc.avails.structures.workbook.reports;

import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Data
public class SoldUnsoldCalculations {
    private boolean hasRights;
    private boolean availsToSell;
    private boolean isSold;
    private boolean noRightsEntered;
    private String customerName;
    private String customerId;
    private Integer numOfPrimaryLanguages;
    private Set<Integer> primaryLanguageIds;
    private Integer numOfSoldPrimaryLanguages;

    public SoldUnsoldCalculations() {
        hasRights = false;
        availsToSell = false;
        isSold = false;
        noRightsEntered = false;
        customerName = "";
        primaryLanguageIds = new HashSet<>();
        numOfSoldPrimaryLanguages = 0;
        numOfPrimaryLanguages = 0;
    }

    public void setPrimaryLanguageIds(Set<Integer> primaryLanguageIds) {
        this.primaryLanguageIds.addAll(primaryLanguageIds);
        this.numOfPrimaryLanguages = primaryLanguageIds.size();
    }

    public void removePrimaryLanguageId(Integer id) {
        this.primaryLanguageIds.remove(id);
    }

    public void incrementNumOfSoldPrimaryLanguages() {
        this.numOfSoldPrimaryLanguages++;
    }

    public boolean isNumOfSoldEqualToNumOfPrimary() {
        return this.getNumOfSoldPrimaryLanguages().equals(this.getNumOfPrimaryLanguages());
    }
}
