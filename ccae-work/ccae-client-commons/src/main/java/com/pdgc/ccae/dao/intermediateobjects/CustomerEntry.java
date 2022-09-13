package com.pdgc.ccae.dao.intermediateobjects;

import java.util.Set;

import com.pdgc.general.cache.dictionary.impl.KeyWithBusinessUnit;

import lombok.Builder;
import lombok.Getter;


/**
* 
*
* @author CCAE
*/

@Getter
@Builder
public class CustomerEntry {

	private KeyWithBusinessUnit<Long> customerId;
	private String customerName;
	private Set<Long> customerTypes;
	private Set<Long> customerGenres;
	private Set<Long> customerGroups;
	
	public String toString() {
		return customerName + " " + customerGroups.toString();
	}
}
