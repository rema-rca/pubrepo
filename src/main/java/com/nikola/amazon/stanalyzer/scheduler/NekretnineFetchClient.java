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


public class NekretnineFetchClient extends AbstractStanFetchClient{
	
	private static Logger LOG = LoggerFactory.getLogger(NekretnineFetchClient.class);
	
	private String url;

	@Override
	protected String getStanDivSelector() {
		return "div.resultList.fixed";
	}

	@Override
	protected Stan transformDivToStan(Element element) throws ElementsNotFoundException{
		Stan stan = new Stan();
		String locationWithCity = element.select("div.resultOtherWrap > div.resultData").text();
		String roomNumber = "0+";
		String owner = null;
		int floor = 0;
		
		String priceWithSize = element.select("div.resultOtherWrap > div.resultListPrice").text();
		//"58 m2, 43.800 EUR" -> "58 m2" "43.800 EUR" -> "43.800 EUR" -> "43.800" -> 43800
		String priceValue = priceWithSize.split(",")[1].trim().split(" ")[0];
		int price = 0;
		try {
			//Prices can only have words in them e.g. "On demand"
			price = Integer.parseInt(priceValue.replaceAll("\\D+",""));
		}
		catch(NumberFormatException e) {
			LOG.warn("Could not parse price : "+priceValue+", will remain 0", e);
		}
		//"58 m2, 43.800 EUR" -> "58 m2" "43.800 EUR" -> "58 m2" -> "58"
		String sizeStringValue = priceWithSize.split(",")[0].split(" ")[0];

		int size = Integer.parseInt(sizeStringValue.replaceAll("\\D+",""));
		
		String link = element.select("a.resultImg.fixed.imgleft").attr("href");
		
		//for this site, every link must be followed...
		Optional<Document> documentForUrl = getDocumentForUrl(link);
		if(documentForUrl.isPresent()) {
			Document page = documentForUrl.get();
			//add street to location


			Elements detailElement = page.select("div.sLeftGrid.fixed > ul > li");
			if(!detailElement.isEmpty() && detailElement.size() > 3) {


				locationWithCity = findInDetailsByString(detailElement,"Adresa", 
						value -> {
						String[] values = value.split(",");
						StringBuilder builder = new StringBuilder();
						for(String singleValue : values) {
							if(!singleValue.toLowerCase().contains("srbija")) {
								builder.append(singleValue.replaceAll("\\d+", "").trim()).append(",");
							}
						}
						return builder.toString().substring(0,builder.toString().length()-1);
						});
					
				
				
				roomNumber = findInDetailsByString(detailElement,"Sobe", null);
				
				owner = findVlasnikNekretnine(page);
				
				try {
					floor = Integer.parseInt(findInDetailsByString(detailElement, "Na spratu:",null));
				}
				catch(NumberFormatException e) {
					
				}
				
			}
			
		}
		
		stan.setLink(link);
		stan.setSobnost(roomNumber);
		stan.setVlasnik(owner);
		stan.setSprat(floor);
		stan.setSajt(getSajt());
		//there can be an error in advertisement, so size is 50k(m2), but price is 50e
		stan.setKvadratura(size > price ? price : size);
		stan.setLokacija(locationWithCity);
		
		StanCena stanCena = new StanCena();
		stanCena.setLink(link);
		stanCena.setDatumOd(new Timestamp(System.currentTimeMillis()));
		stanCena.setDatumDo(null);
		stanCena.setIznos(size > price ? size : price);
		stan.addCena(stanCena);
		
		return stan;
	}


	private String findVlasnikNekretnine(Document page) {
		String vlasnik = null;
		Elements vlasnikElement = page.select("div.sContactLogo");
		if(!vlasnikElement.isEmpty()) {
			String[] tokenizedVlasnik = vlasnikElement.attr("href").split("/");
			vlasnik = tokenizedVlasnik[tokenizedVlasnik.length-2];
		}
		else {
			//vlasnik
			vlasnikElement = page.select("div.sContactRow.short > strong");
			if(!vlasnikElement.isEmpty()) {
				vlasnik = vlasnikElement.text();
			}
		}
		return vlasnik;
	}

	private String findInDetailsByString(Elements detailElement, String searchString, StringChanger extractor) {
		String returnValue = null;
		for (Element element : detailElement) {
			if(element.text().contains(searchString)) {
				Elements valueHolderElement = element.select("div.singleData");
				returnValue = valueHolderElement.text();
			}
		}
		if(extractor != null) {
			returnValue = extractor.change(returnValue);
		}
		return returnValue;
	}

	@Override
	protected List<String> getUrlsFromPagination(Document document) throws ElementsNotFoundException{
		List<String> urls = new ArrayList<>();
		Elements paginationElements = document.select("div.pagination.fixed");
		if(!paginationElements.isEmpty()) {
			paginationElements = paginationElements.get(0).select("a");
			for(Element page : paginationElements) {
				//ako nisu selektovani(prva strana), "prev" i "next" linkovi
				if(!page.hasClass("selected") && !page.hasClass("pag_last") && !page.hasClass("pag_next") && !page.hasClass("pag_prev")){
					String url =page.attr("href");
					if(StringUtils.isNotBlank(url)) {
						urls.add(url);
					}
				}
			}
		}

		return urls;
	}

	
	@Override
	protected String getFirstUrl() {
		return url.replaceAll("&amp;", "&").replaceAll(" ", "");
	}
	
	@Override
	protected Sajt getSajt() {
		return Sajt.NEKRETNINE_RS;
	}
	
	public void setUrl(String url) {
		this.url = url;
	}

}
