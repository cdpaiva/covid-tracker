package com.example.coronavirustracker.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.coronavirustracker.model.DailyReport;
import com.example.coronavirustracker.model.LocationStats;
import com.example.coronavirustracker.service.CoronavirusTrackerDataService;

@Controller
public class HomeController {
	
	@Autowired
	private CoronavirusTrackerDataService coronavirusTrackerDataService;

	@GetMapping("/")
	public String home(Model model) {
		
		List<DailyReport> lastTwoReports = coronavirusTrackerDataService.getLastTwoReports();
		
		DailyReport currentReport = lastTwoReports.get(0);
		
		DailyReport previousReport = lastTwoReports.get(1);
		
		List<LocationStats> stateStats = currentReport.getCountryStats();
		
		int totalReportedCases = stateStats
				.stream()
				.mapToInt(s -> s.getLatestTotalCases())
				.sum();
		
		int totalPreviousReportedCases = previousReport.getCountryStats()
				.stream()
				.mapToInt(s -> s.getLatestTotalCases())
				.sum();
		
		int delta = totalReportedCases - totalPreviousReportedCases;
		
		String deltaMessage = String.format("Since the last recorded report, from %s, there were %s new cases.",
										previousReport.getFormattedDate(),
										delta);
		
		model.addAttribute("locationStats", stateStats);
		model.addAttribute("totalReportedCases", totalReportedCases);
		model.addAttribute("lastDate", currentReport.getFormattedDate());
		model.addAttribute("deltaMessage", deltaMessage);
		
		return "home";
	}
}
