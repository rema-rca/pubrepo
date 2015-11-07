package com.nikola.amazon.stanalyzer.scheduler;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.WebResponse;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.util.FalsifyingWebConnection;
import com.nikola.amazon.stanalyzer.entity.Sajt;
import com.nikola.amazon.stanalyzer.entity.Stan;

public abstract class AbstractStanFetchClient {
	
private WebClient webClient = createWebClient();

	
public final List<Stan> getStanFromUrl() throws ElementsNotFoundException, FailingHttpStatusCodeException, MalformedURLException, IOException {
		
		String url = getFirstUrl();

		List<Stan> stanovi = new ArrayList<>();
		HtmlPage page = null;
		try {
			page = webClient.getPage(url.replaceAll(" ", ""));
			if (page != null) {
				
				Document doc = transformPageStringResponseToDocument(page);
				extractStanoviFromPage(stanovi, doc);
				//paginacija i dodatne strane ako trebaju
				List<String> paginationUrls = getUrlsFromPagination(doc);
				paginationUrls.addAll(createAdditionalUrlsFromFirst(url, paginationUrls.size()));
				//uzmi stanove iz paginacionih linkova
				for(String paginationUrl : paginationUrls) {
					//relative links
					if(paginationUrl.startsWith("/")) {
						paginationUrl = "http://" + getSajt().getName() + paginationUrl;
					}
					System.out.println("Getting page from "+paginationUrl);
					page = webClient.getPage(paginationUrl);
					doc = transformPageStringResponseToDocument(page);
					if (page != null) {
						extractStanoviFromPage(stanovi, doc);
					}
				}
			}
			
		return stanovi;	
		
		}
		finally {
			webClient.closeAllWindows();
		}
	}

private WebClient createWebClient() {
	WebClient client = new WebClient(BrowserVersion.CHROME);
	client.getOptions().setThrowExceptionOnScriptError(false);
	client.getOptions().setCssEnabled(true);
	client.setWebConnection(new FalsifyingWebConnection(client) {
		
		@Override
		public WebResponse getResponse(WebRequest request) throws IOException {
			request.setCharset("UTF-8");
			String url = request.getUrl().toString();
			if ("www.google-analytics.com".equals(request.getUrl().getHost())) {
                return createWebResponse(request, "", "application/javascript"); // -> empty script
            }
            if(url.contains(".css")) {
            	return createWebResponse(request, "", "text/css");
            }
            
            if(url.contains(".png") || url.contains(".jpg") || url.contains(".gif")) {
            	return createWebResponse(request, "", "image/jpeg");
            }
            
            if(url.contains("kurir")) {
            	return createWebResponse(request, "", "text/html");
            }
            
            if(url.contains("gemius")) {
            	return createWebResponse(request, "", "text/html");
            }
            
            return super.getResponse(request);
		}
		
	});
	
	return client;
}

private void extractStanoviFromPage(List<Stan> stanovi, Document document)
		throws ElementsNotFoundException {
		
		Elements stanDivs = document.select(getStanDivSelector());
		if(stanDivs == null || stanDivs.isEmpty()) {
			throw new ElementsNotFoundException("Could not find elements with '"+getStanDivSelector()+"' selector");
		}
		else {
			
			for (org.jsoup.nodes.Element element : stanDivs) {
				Stan stan = transformDivToStan(element);
				stanovi.add(stan);
			}
		}
}

	private Document transformPageStringResponseToDocument(HtmlPage page) {
		String pageHtml = page.getWebResponse().getContentAsString();
		Document doc = Jsoup.parse(pageHtml);
		return doc;
	}
	
	protected List<String> createAdditionalUrlsFromFirst(String firstUrlToCrawl, int existingPagesListSize) {
		return new ArrayList<String>();
	}
	
	protected abstract List<String> getUrlsFromPagination(Document document) throws ElementsNotFoundException;
	
	protected abstract Sajt getSajt();

	protected abstract String getStanDivSelector();
	
	protected abstract String getFirstUrl();
	
	protected Optional<Document> getDocumentForUrl(String url) {
		HtmlPage page;
		Document doc = null;
		try {
			page = webClient.getPage(url.replace(" ", ""));
			if (page != null) {
				doc = transformPageStringResponseToDocument(page);
			}
		} catch (FailingHttpStatusCodeException | IOException e) {
			//ignore
		}

		return Optional.ofNullable(doc);
	}
	
	protected abstract Stan transformDivToStan(org.jsoup.nodes.Element element) throws ElementsNotFoundException;
}
