package com.nikola.amazon.stanalyzer.ws;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.nikola.amazon.stanalyzer.entity.Stan;


@XmlRootElement
public class StanResponse {
	
	public StanResponse(List<Stan> stanovi, int recordsFiltered, int recordsTotal) {
		this.stanovi = stanovi;
		this.recordsFiltered = recordsFiltered;
		this.recordsTotal = recordsTotal;
	}
	
	private final List<Stan> stanovi;
	
	private final int recordsFiltered;
	
	private final int recordsTotal; 

	@JsonProperty("data")
	public List<Stan> getStanovi() {
		return stanovi;
	}

	@JsonProperty("recordsFiltered")
	public int getRecordsFiltered() {
		return recordsFiltered;
	}

	@JsonProperty("recordsTotal")
	public int getRecordsTotal() {
		return recordsTotal;
	}
	
	

}
