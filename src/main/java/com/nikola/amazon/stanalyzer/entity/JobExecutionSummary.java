package com.nikola.amazon.stanalyzer.entity;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonProperty;


@XmlRootElement
@Entity
@Table(name = "JOB_EXECUTION_SUMMARY", catalog = "stanalyzer")
public class JobExecutionSummary {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@JsonProperty("DT_RowId")
	private Long id;
	
	@Enumerated(EnumType.ORDINAL)
	private Sajt sajt;
	
	@Column(name="start")
	private Timestamp start;
	
	@Column(name="end")
	private Timestamp end;
	
	
	@Column(name="execution_time")
	private Long executionTime;
	
	@Column(name="url")
	private String url;
	
	@Column(name="message")
	private String message;
	
	@Column(name="severity")
	private JobExecutionSummaryMessageSeverity severity;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Sajt getSajt() {
		return sajt;
	}

	public void setSajt(Sajt sajt) {
		this.sajt = sajt;
	}

	public Timestamp getStart() {
		return start;
	}

	public void setStart(Timestamp start) {
		this.start = start;
	}

	public Timestamp getEnd() {
		return end;
	}

	public void setEnd(Timestamp end) {
		this.end = end;
	}

	public Long getExecutionTime() {
		return executionTime;
	}

	public void setExecutionTime(Long executionTime) {
		this.executionTime = executionTime;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public JobExecutionSummaryMessageSeverity getSeverity() {
		return severity;
	}

	public void setSeverity(JobExecutionSummaryMessageSeverity severity) {
		this.severity = severity;
	}
	
	

}
