package com.example.coronavirustracker.service;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.example.coronavirustracker.model.DailyReport;

public interface CoronaVirusTrackerRepository extends CrudRepository<DailyReport, Integer>{
	
	public DailyReport findTopByOrderByIdDesc();
	
	public List<DailyReport> findTop2ByOrderByDateDesc();

}
