package com.example.coronavirustracker.model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

@Entity
public class DailyReport {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;
	private LocalDate date;
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "dailyReport")
	private List<LocationStats> stateStats;

	public DailyReport() {
	}

	public DailyReport(LocalDate date, List<LocationStats> stateStats) {
		super();
		this.date = date;
		this.stateStats = stateStats;
	}

	public LocalDate getDate() {
		return date;
	}

	public String getFormattedDate() {
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("YYYY-LLL-dd");
		return getDate().format(dtf);
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

	public List<LocationStats> getCountryStats() {
		return stateStats;
	}

	public void setCountryStats(List<LocationStats> stateStats) {
		this.stateStats = stateStats;
	}

}
