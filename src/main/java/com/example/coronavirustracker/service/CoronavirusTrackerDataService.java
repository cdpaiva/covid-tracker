package com.example.coronavirustracker.service;

import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.example.coronavirustracker.model.DailyReport;
import com.example.coronavirustracker.model.LocationStats;

@Service
public class CoronavirusTrackerDataService {

	@Autowired
	private CoronaVirusTrackerRepository coronaVirusTrackerRepository;

	Logger logger = LoggerFactory.getLogger(CoronavirusTrackerDataService.class);

	private static String COVID_DATA_URL = "https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/"
			+ "csse_covid_19_data/csse_covid_19_daily_reports/";

	public String getDate() {
		LocalDate yesterday = LocalDate.now().minusDays(1);
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM-dd-YYYY");
		return yesterday.format(dtf);
	}

	@PostConstruct
	@Scheduled(cron = "* * 1 * * *")
	public void checkLastUpdate() throws IOException, InterruptedException {
		DailyReport dailyReport = this.getLastReport();
		if (dailyReport != null && dailyReport.getDate().equals(LocalDate.now().minusDays(1))) {
			logger.info("Last report already fetched");
		} else {
			logger.info("Fetching data from " + LocalDate.now().minusDays(1));
			this.fetchCovidData();
		}
	}

	public void fetchCovidData() throws IOException, InterruptedException {
		List<LocationStats> newStats = new ArrayList<>();

		HttpClient client = HttpClient.newHttpClient();
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(COVID_DATA_URL + getDate() + ".csv"))
				.build();

		HttpResponse<String> httpResponse = client
				.send(request, HttpResponse.BodyHandlers.ofString());

		StringReader csvBodyReader = new StringReader(httpResponse.body());

		CSVFormat.Builder csvBuilder = CSVFormat.Builder.create()
				.setHeader(HEADERS.class)
				.setSkipHeaderRecord(true)
				.setIgnoreHeaderCase(true)
				.setTrim(true);

		Iterable<CSVRecord> records = csvBuilder
				.build()
				.parse(csvBodyReader);

		for (CSVRecord record : records) {
			if ("Brazil".equals(record.get(HEADERS.Country_Region))) {
				LocationStats locationStat = new LocationStats();
				locationStat.setCountry(record.get(HEADERS.Country_Region));
				locationStat.setState(record.get(HEADERS.Province_State));
				locationStat.setLatestTotalCases(Integer.parseInt(record.get(HEADERS.Confirmed)));

				newStats.add(locationStat);
			}
		}

		DailyReport dailyReport = new DailyReport();
		dailyReport.setDate(LocalDate.now().minusDays(1));
		for (LocationStats stat : newStats) {
			stat.setDailyReport(dailyReport);
		}
		dailyReport.setCountryStats(newStats);

		coronaVirusTrackerRepository.save(dailyReport);
	}

	public DailyReport getLastReport() {
		return coronaVirusTrackerRepository.findTopByOrderByIdDesc();
	}

	public List<DailyReport> getLastTwoReports() {
		return coronaVirusTrackerRepository.findTop2ByOrderByDateDesc();
	}

}
