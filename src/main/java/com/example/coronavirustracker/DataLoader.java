package com.example.coronavirustracker;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.example.coronavirustracker.model.DailyReport;
import com.example.coronavirustracker.model.LocationStats;
import com.example.coronavirustracker.service.CoronaVirusTrackerRepository;

//@Component
public class DataLoader implements CommandLineRunner{

	@Autowired
	private CoronaVirusTrackerRepository coronaVirusTrackerRepository;
	
	private Logger logger = LoggerFactory.getLogger(DataLoader.class);
	
	@Override
	public void run(String... args) throws Exception {
		List<LocationStats> newStats = new ArrayList<>();
		
		LocationStats locationStat = new LocationStats();
		locationStat.setCountry("Brazil");
		locationStat.setState("Acre");
		locationStat.setLatestTotalCases(0);
		
		DailyReport dailyReport = new DailyReport();
		dailyReport.setDate(LocalDate.now().minusDays(1));
		for (LocationStats stat : newStats) {
			stat.setDailyReport(dailyReport);
		}
		dailyReport.setCountryStats(newStats);
		
		logger.info("Creating new DB entry");
		
		coronaVirusTrackerRepository.save(dailyReport);
		
	}

	
}
