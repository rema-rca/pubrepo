package com.nikola.amazon.stanalyzer.entity;

import java.sql.Timestamp;
import java.util.Calendar;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;


@Entity
@Table(name="STAN_CENA", catalog="stanalyzer")
@NamedNativeQueries({@NamedNativeQuery(
		name = "findLastPriceForStan",
		query = "select iznos from STAN_CENA s where s.stan_id = :stanId and datum_do is null"
		)})
public class StanCena {
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	
	@JsonIgnore
	@ManyToOne
	private Stan stan;
	
	@Column(name="iznos")
	private int iznos;
	
	@Column(name = "link")
	private String link;
	
	@Column(name="datum_od")
	private Timestamp datumOd;
	
	@Column(name="datum_do")
	private Timestamp datumDo;
	
	@Column(name="datum_oglas")
	private Calendar datumOglas;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Stan getStan() {
		return stan;
	}

	public void setStan(Stan stan) {
		this.stan = stan;
	}

	public int getIznos() {
		return iznos;
	}

	public void setIznos(int iznos) {
		this.iznos = iznos;
	}

	public Timestamp getDatumOd() {
		return datumOd;
	}

	public void setDatumOd(Timestamp datumOd) {
		this.datumOd = datumOd;
	}

	public Timestamp getDatumDo() {
		return datumDo;
	}

	public void setDatumDo(Timestamp datumDo) {
		this.datumDo = datumDo;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}
	
	

}
