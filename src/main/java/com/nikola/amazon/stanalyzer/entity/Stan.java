package com.nikola.amazon.stanalyzer.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlRootElement;

import org.hibernate.annotations.Formula;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

@XmlRootElement
@Entity
@Table(name = "STAN", catalog = "stanalyzer")
@NamedQueries({@NamedQuery(
		name = "findStanByKvadraturaSpratAndSobnost",
		query = "from Stan s where s.kvadratura = :kvadratura and s.sprat = :sprat and s.sobnost = :sobnost"
		)})
public class Stan {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@JsonProperty("DT_RowId")
	private Long id;

	@Column(name = "lokacija")
	private String lokacija;

	@Column(name = "kvadratura")
	private int kvadratura;
	
	@Column(name = "sprat")
	private int sprat;

	@Column(name = "link")
	private String link;
	
	@Column(name = "sobnost")
	private String sobnost;
	
	@Column(name = "vlasnik")
	private String vlasnik;

	@Enumerated(EnumType.ORDINAL)
	private Sajt sajt;

	@Formula("(select o.iznos from STAN_CENA o where o.stan_id = id and o.datum_do is null)")
	private Integer poslednjaCena;

	@Formula("ifnull( (select ((sc1.iznos / sc2.iznos) - 1) from STAN_CENA sc1, STAN_CENA sc2 "
			+ "where (sc1.stan_id = id) and (sc1.datum_do is null) and sc2.stan_id = id and "
			+ "(sc2.datum_do = (select  sc3.datum_do from STAN_CENA sc3 where sc3.stan_id = id and sc3.datum_do is not null "
			+ "order by sc3.datum_do desc limit 0,1))), 0)")
	@JsonInclude(Include.NON_NULL)
	private Float promenaJedan = 0f;

	@Transient
	@JsonIgnore
	private Float promenaDeset;

	@Transient
	@JsonIgnore
	private Float promenaSto;


	@OneToMany(fetch = FetchType.LAZY, mappedBy = "stan", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonIgnore
	private List<StanCena> cene = new ArrayList<>();

	public Stan() {
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getLokacija() {
		return lokacija;
	}

	public void setLokacija(String lokacija) {
		this.lokacija = lokacija;
	}

	public int getKvadratura() {
		return kvadratura;
	}

	public void setKvadratura(int kvadratura) {
		this.kvadratura = kvadratura;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public List<StanCena> getCene() {
		return cene;
	}

	public void setCene(List<StanCena> cene) {
		this.cene = cene;
	}

	public Sajt getSajt() {
		return sajt;
	}

	public void setSajt(Sajt sajt) {
		this.sajt = sajt;
	}

	public void addCena(StanCena cena) {
		cene.add(cena);
		cena.setStan(this);
	}

	public float getPromenaJedan() {
		return promenaJedan;
	}

	public void setPromenaJedan(float promenaJedan) {
		this.promenaJedan = promenaJedan;
	}

	public float getPromenaDeset() {
		return promenaDeset;
	}

	public void setPromenaDeset(float promenaDeset) {
		this.promenaDeset = promenaDeset;
	}

	public float getPromenaSto() {
		return promenaSto;
	}

	public void setPromenaSto(float promenaSto) {
		this.promenaSto = promenaSto;
	}

	public Integer getPoslednjaCena() {
		return poslednjaCena;
	}

	public void setPoslednjaCena(Integer poslednjaCena) {
		this.poslednjaCena = poslednjaCena;
	}

	public String getSobnost() {
		return sobnost;
	}

	public void setSobnost(String sobnost) {
		this.sobnost = sobnost;
	}

	public String getVlasnik() {
		return vlasnik;
	}

	public void setVlasnik(String vlasnik) {
		this.vlasnik = vlasnik;
	}

	public void setPromenaJedan(Float promenaJedan) {
		this.promenaJedan = promenaJedan;
	}

	public int getSprat() {
		return sprat;
	}

	public void setSprat(int sprat) {
		this.sprat = sprat;
	}

	@Override
	public String toString() {
		return "Stan [id=" + id + ", lokacija=" + lokacija + ", kvadratura="
				+ kvadratura + ", sprat=" + sprat + ", link=" + link
				+ ", sobnost=" + sobnost + ", vlasnik=" + vlasnik + ", sajt="
				+ sajt + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((link == null) ? 0 : link.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Stan other = (Stan) obj;
		if (link == null) {
			if (other.link != null)
				return false;
		} else if (!link.equals(other.link))
			return false;
		return true;
	}
	
	

}
