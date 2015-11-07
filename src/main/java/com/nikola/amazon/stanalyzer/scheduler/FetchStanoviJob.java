package com.nikola.amazon.stanalyzer.scheduler;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jolokia.jmx.JsonMBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.nikola.amazon.stanalyzer.dao.GenericDAO;
import com.nikola.amazon.stanalyzer.entity.JobExecutionSummary;
import com.nikola.amazon.stanalyzer.entity.JobExecutionSummaryMessageSeverity;
import com.nikola.amazon.stanalyzer.entity.Stan;
import com.nikola.amazon.stanalyzer.entity.StanCena;
import com.nikola.amazon.stanalyzer.mail.MailSender;
import com.nikola.amazon.stanalyzer.util.StanMatcher;

@Component("fetchStanoviJob")
@JsonMBean
public class FetchStanoviJob {
	
	private static Logger LOG = LoggerFactory.getLogger(FetchStanoviJob.class);
	
	private boolean isRunning;
	
	@Autowired
	private GenericDAO<com.nikola.amazon.stanalyzer.entity.Stan, Long> stanDao;
	
	@Autowired
	private GenericDAO<com.nikola.amazon.stanalyzer.entity.StanCena, Long> stanCenaDao;
	
	@Autowired
	private MailSender stanUpdateMailSender;
	
	@Autowired
	private List<AbstractStanFetchClient> fetchClients;
	
	@Scheduled(fixedDelay=5000000, initialDelay=5000)
	public void crawlPages() {
		isRunning = true;
//		for(AbstractStanFetchClient fetchClient : fetchClients) {
//			getStanovi(fetchClient);
//		}
		LOG.info("FetchStanoviJob"+System.currentTimeMillis());
		isRunning= false;
	}
	
	public JobExecutionSummary getStanovi(AbstractStanFetchClient stanFetchClient) {
		
		JobExecutionSummary jobSummary = new JobExecutionSummary();
		jobSummary.setSajt(stanFetchClient.getSajt());
		jobSummary.setStart(new Timestamp(System.currentTimeMillis()));
		
		try {
			List<Stan> stanovi = stanFetchClient.getStanFromUrl();
			insertOrUpdateStan(stanovi);
			jobSummary.setMessage("Number of fetched entities : "+stanovi.size());
			jobSummary.setSeverity(JobExecutionSummaryMessageSeverity.INFO);
		}
		catch(ElementsNotFoundException ex) {
			LOG.warn("error getting page elements", ex);
			jobSummary.setSeverity(JobExecutionSummaryMessageSeverity.WARNING);
			jobSummary.setMessage(ex.getMessage());
		}
		catch (Exception e) {
			LOG.error("error getting page", e);
			jobSummary.setSeverity(JobExecutionSummaryMessageSeverity.ERROR);
			jobSummary.setMessage(e.getMessage());
			
		} 
		
		jobSummary.setEnd(new Timestamp(System.currentTimeMillis()));
		jobSummary.setUrl(stanFetchClient.getFirstUrl());
		return jobSummary;
		}

	private void insertOrUpdateStan(List<Stan> stanovi) {
		List<Stan> stanoviInsert = new ArrayList<>(stanovi.size());
		List<StanCena> ceneToInsert = new ArrayList<>();
		List<StanCena> ceneToUpdate = new ArrayList<>();
		for(Stan fetchedStan : stanovi) {
			Map<String,Object> parameters= new HashMap<>();
			parameters.put("kvadratura", fetchedStan.getKvadratura());
			parameters.put("sobnost", fetchedStan.getSobnost());
			parameters.put("sprat", fetchedStan.getSprat());
			Stan existingStan = (Stan) stanDao.executeNamedQueryUniqueResult("findStanByKvadraturaSpratAndSobnost", parameters);
			//nema takvog, proveri sa drugim upitom
			if(existingStan == null) {
				stanoviInsert.add(fetchedStan);
			}
			else {
				//isti im je link(equals), ili su isti i po lokaciji (donekle)
				if(existingStan.equals(fetchedStan) || StanMatcher.same(fetchedStan, existingStan)) {
					manageNewCena(existingStan,fetchedStan,ceneToInsert,ceneToUpdate);
				}
				else {
					//nije ista lokacija
					stanoviInsert.add(fetchedStan);
				}
			}
		}
		
		stanDao.insertBulk(stanoviInsert);
		stanCenaDao.updateBulk(ceneToUpdate);
		stanCenaDao.insertBulk(ceneToInsert);
	}

	

	private void manageNewCena(Stan existingStan, Stan fetchedStan,List<StanCena> ceneToInsert, List<StanCena> ceneToUpdate) {
		StanCena newCena = fetchedStan.getCene().get(0);
		newCena.setStan(existingStan);
		List<Object> existingPrices = stanDao.getCene(existingStan.getId());
		for(Object o : existingPrices) {
			StanCena sc = (StanCena)o;
			if(sc.getDatumDo() == null) {
				sc.setDatumDo(new Timestamp(System.currentTimeMillis()));
				ceneToUpdate.add(sc);
			}
		}
		ceneToInsert.add(newCena);
	}

	/*private void handleMailNotifications(StanCena newCena, StanCena sc, Stan existingStan) {
		if(newCena.getIznos() > sc.getIznos()) {
			sendPriceMailChange(newCena,sc,existingStan,false);
		}
		else if(newCena.getIznos() < sc.getIznos()) {
			sendPriceMailChange(newCena,sc,existingStan,true);
		}
	}

	private void sendPriceMailChange(StanCena iznos, StanCena iznos2, Stan existingStan, boolean priceDrop) {
			stanUpdateMailSender.sendMail("Cena stana "+existingStan.getLokacija()+
					"["+existingStan.getKvadratura()+"m2] je " +(priceDrop ? "opala za " : "porasla za ") 
					+ Math.abs(((iznos.getIznos()/iznos2.getIznos())-1f)) +" procenta!");
	}*/

	public void setStanDao(
			GenericDAO<com.nikola.amazon.stanalyzer.entity.Stan, Long> stanDao) {
		this.stanDao = stanDao;
	}

	public void setStanUpdateMailSender(MailSender stanUpdateMailSender) {
		this.stanUpdateMailSender = stanUpdateMailSender;
	}

	public void setStanCenaDao(
			GenericDAO<com.nikola.amazon.stanalyzer.entity.StanCena, Long> stanCenaDao) {
		this.stanCenaDao = stanCenaDao;
	}

	public void setFetchClients(List<AbstractStanFetchClient> fetchClients) {
		this.fetchClients = fetchClients;
	}
	
	@ManagedAttribute(description="Is the job runninng flag")
	public boolean getIsRunning() {
		return isRunning;
	}
	
	
}