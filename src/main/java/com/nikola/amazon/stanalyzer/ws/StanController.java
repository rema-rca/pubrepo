package com.nikola.amazon.stanalyzer.ws;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.nikola.amazon.stanalyzer.dao.GenericDAO;
import com.nikola.amazon.stanalyzer.entity.Stan;

@RestController
@RequestMapping("/stan")
public class StanController {

	private static Logger LOG = LoggerFactory.getLogger(StanController.class);

	@Autowired
	private GenericDAO<com.nikola.amazon.stanalyzer.entity.Stan, Long> stanDao;

	@Autowired
	private MessageSourceUtil messageSourceUtil;


	@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.GET)
	public StanResponse search(
			@RequestParam(value = "length", required = false, defaultValue = "10") String beginIndexValue,
			@RequestParam(value = "start", required = false, defaultValue = "0") String pageIndexValue) {
		
		List<Stan> stanovi = stanDao.search(null, Integer.parseInt(beginIndexValue), Integer.parseInt(pageIndexValue));
		Integer maxNumberOfRecords = stanDao.count(null);
		return new StanResponse(stanovi, maxNumberOfRecords, maxNumberOfRecords);
	}
	
	@RequestMapping(value = "{stanId}/cena", produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.GET)
	public List<Object> getCena(@PathVariable("stanId") String stanId) {
		try {
			return stanDao.getCene(Long.parseLong(stanId));
		} catch (NumberFormatException e) {
			LOG.error("Error parsing stanId : "+stanId,e);;
			return null;
		}
	}
	

	public void setStanDao(
			GenericDAO<com.nikola.amazon.stanalyzer.entity.Stan, Long> stanDao) {
		this.stanDao = stanDao;
	}

	public void setMessageSourceUtil(MessageSourceUtil messageSourceUtil) {
		this.messageSourceUtil = messageSourceUtil;
	}

}
