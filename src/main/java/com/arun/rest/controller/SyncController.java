package com.arun.rest.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.arun.beans.AgentResponse;
import com.arun.beans.Calculation;
import com.arun.beans.Destination;
import com.arun.beans.Forecast;
import com.arun.beans.Recommendation;
import com.arun.logging.Loggable;
import com.arun.service.CalculationService;
import com.arun.service.DestinationService;
import com.arun.service.ForecastService;

@RestController
@RequestMapping(value = "/sync")
public class SyncController {

	@Autowired
	private DestinationService destinationService;
	
	@Autowired
	private CalculationService calculationService;
	
	@Autowired
	private ForecastService forecastService;

	@Loggable
	@RequestMapping(value = "/getDetails", method = RequestMethod.GET)
	@ResponseStatus(value = HttpStatus.OK)
	public AgentResponse getDetails(@RequestParam String user) {
		AgentResponse response = new AgentResponse();
		long time = System.nanoTime();
		List<Destination> visitedPlaces = destinationService.visitedDestinations(user);
		List<Destination> recommendedPlaces = destinationService.recommendedDestinations(user);
		List<Recommendation> recommendations = new ArrayList<Recommendation>();
		for(Destination destination: recommendedPlaces) {
			Forecast forecast = forecastService.getForecast(destination.getDestination());
			Calculation calculation = calculationService.getPrice(destination.getDestination());
			Recommendation recommendation = new Recommendation();
			recommendation.setDestination(destination.getDestination());
			recommendation.setForecast(forecast.getForecast());
			recommendation.setPrice(calculation.getPrice());
			recommendations.add(recommendation);
		}
		response.setVisited(visitedPlaces);
		response.setRecommended(recommendations);
        response.setProcessingTime((System.nanoTime() - time) / 1000000);
		return response;  		
	}
}
