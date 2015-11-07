package com.nikola.amazon.stanalyzer.scheduler;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nikola.amazon.stanalyzer.entity.Sajt;
import com.nikola.amazon.stanalyzer.entity.Stan;
import com.nikola.amazon.stanalyzer.entity.StanCena;


public class HaloOglasiFetchClient extends AbstractStanFetchClient {
	
	private String url;
	
	private static Logger LOG = LoggerFactory.getLogger(HaloOglasiFetchClient.class);
	
	@Override
	protected String getFirstUrl() {
		return url.replaceAll("&amp;", "&").replaceAll(" ", "");
	}

	@Override
	protected List<String> getUrlsFromPagination(Document document) throws ElementsNotFoundException{
		Elements pagination = document.select("div.pagination");
		if(pagination.isEmpty()) {
			throw new ElementsNotFoundException("Could not not find pagination element");
		}
		Elements links = pagination.select("a");
			if(links.isEmpty()) {
				throw new ElementsNotFoundException("Could not not find pagination links");
			}
		
		List<String> linkValues = new ArrayList<>();
		for(Element link : links) {
			linkValues.add(link.attr("href"));
		}
		
		//filter last pagination
		linkValues.remove(linkValues.size()-1);
		return linkValues;
	}


	@Override
	protected Sajt getSajt() {
		return Sajt.HALO_OGLASI;
	}

	@Override
	protected String getStanDivSelector() {
		return "div.result_nek";
	}

	@Override
	protected Stan transformDivToStan(Element element) throws ElementsNotFoundException {
		Stan stan = new Stan();
		String lokacijaSaGradom = element.select("h3").text();
		String lokacija = null;
		/*
		 * "Beograd • Opština Rakovica • Skojevsko naselje • Luke Vojvodica" ->
		 * "Beograd,Opština Rakovica,Skojevsko naselje,Luke Vojvodica"
		 */
		if(StringUtils.isNotBlank(lokacijaSaGradom)) {
			lokacija = lokacijaSaGradom.replaceAll(" • ", ",");
		}

		
		Elements select = element.select("p.data");
		if(select.isEmpty()) {
			throw new ElementsNotFoundException("Could not find 'p.data' element");
		}
		Elements ems = select.select("em");//.get(1).text();
		if(ems.isEmpty() || ems.size() < 2) {
			throw new ElementsNotFoundException("Could not find 'em' element, or there are less then 2 ems");
		}
		
		String kvadraturaValue = ems.get(1).text();
		int kvadratura = 0;
		try {
			kvadratura = Integer.parseInt(kvadraturaValue.replaceAll("\\D+",""));
		}
		catch(NumberFormatException e) {
			LOG.warn("Could not parse kvadratura : "+kvadraturaValue+", will remain 0", e);
		}
		
		String sobnost = ems.get(2).text().trim();
		Elements vlasnikElement = element.select("h6 > em");
		
		/* Vlasnik - ako je vlasnik, to je to, ako je agencija, zapisi logo, ako ga nema, ostaje agencija */
		if(vlasnikElement.isEmpty()) {
			throw new ElementsNotFoundException("Could not find 'h6 > em' from "+getStanDivSelector()+" element");
		}
		
		String vlasnik = vlasnikElement.text();

		/* cena, ako postoji, bez euro znaka */
		Elements cenaLink = element.select("div.cena > a");
		if(cenaLink.isEmpty()) {
			throw new ElementsNotFoundException("Could not find 'div.cena > a' from "+getStanDivSelector()+" element");
		}
		String cenaValue = cenaLink.text();
		int cena = 0;
		try {
			cena = Integer.parseInt(cenaValue.replaceAll("\\D+",""));
		}
		catch(NumberFormatException e) {
			LOG.warn("Could not parse cenaValue : "+cenaValue+", will remain 0", e);
		}

		/* link ka oglasu */
		String link = cenaLink.attr("href").startsWith("/") 
				? ("http://" + getSajt().getName() + cenaLink.attr("href"))
					: cenaLink.attr("href");
				

		Optional<Document> documentForUrl = getDocumentForUrl(link);
		if(documentForUrl.isPresent()) {
			
		}
		
		stan.setLink(link);
		stan.setVlasnik(vlasnik);
		stan.setSobnost(sobnost);
		stan.setSajt(getSajt());
		stan.setKvadratura(kvadratura);
		stan.setLokacija(lokacija);
		
		StanCena stanCena = new StanCena();
		stanCena.setLink(link);
		stanCena.setDatumOd(new Timestamp(System.currentTimeMillis()));
		stanCena.setDatumDo(null);
		stanCena.setIznos(cena);
		stan.addCena(stanCena);
		
		return stan;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	

//	@Override
//	protected List<String> createAdditionalUrlsFromFirst(String firstUrlToCrawl, int numberOfExistingpages) {
//		List<String> additionalUrls = new ArrayList<>();
//		int offsetBeginIndex = firstUrlToCrawl.indexOf("offset=") + "offset=".length();
//		
//		for(int i = numberOfExistingpages;i < 15; i++) {
//			String url = firstUrlToCrawl.substring(0,offsetBeginIndex) 
//					+ i + firstUrlToCrawl.substring(offsetBeginIndex+1, firstUrlToCrawl.length());
//			additionalUrls.add(url);
//		}
//		return additionalUrls;
//	}
	
}
