package com.nikola.amazon.stanalyzer.entity;

import com.fasterxml.jackson.annotation.JsonValue;

public enum Sajt {
	
	NEKRETNINE_RS("www.nekretnine.rs"),
	HALO_OGLASI("www.halooglasi.com");
	
	private final String name;
	
	private Sajt(String name) {
		this.name = name;
	}
	
	@JsonValue
	public String getName() {
		return name;
	}

}
