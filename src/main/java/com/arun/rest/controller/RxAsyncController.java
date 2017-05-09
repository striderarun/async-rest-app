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

import rx.Observable;
import rx.Subscriber;
import rx.schedulers.Schedulers;

@RestController
@RequestMapping(value = "/rxAsync")
public class RxAsyncController {

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
		Observable<Destination> visitedPlacesObservable = getVistiedDestinationObservable(user);
		Observable<Destination> recommendedPlacesObservable = getRecommendedDestinationObservable(user);

		Observable<Recommendation> recommendationsObservable = recommendedPlacesObservable.flatMap(destination -> {
				Observable<Forecast> forecastObservable = Observable.create((Subscriber<? super Forecast> s) -> {
					s.onNext(forecastService.getForecast(destination.getDestination()));
					s.onCompleted();
				}).subscribeOn(Schedulers.io());
				Observable<Calculation> calculationObservable = Observable.create((Subscriber<? super Calculation> s) -> {
					s.onNext(calculationService.getPrice(destination.getDestination()));
					s.onCompleted();
				}).subscribeOn(Schedulers.io());
				Observable<Recommendation> recommendationObservable = Observable.zip(forecastObservable, calculationObservable, (f, c) -> {
					Recommendation recommendation = new Recommendation();
					recommendation.setDestination(destination.getDestination());
					recommendation.setForecast(f.getForecast());
					recommendation.setPrice(c.getPrice());
					return recommendation;
				});
				return recommendationObservable;
		});	
		
		List<Destination> visitedDestinations = new ArrayList<Destination>();
		visitedPlacesObservable.subscribe(s -> visitedDestinations.add(s));
		
		List<Recommendation> recommendationsList = recommendationsObservable.toList().toBlocking().single();		
		response.setRecommended(recommendationsList);
		response.setVisited(visitedDestinations);
        response.setProcessingTime((System.nanoTime() - time) / 1000000);
		return response;  		
	}
	
	private Observable<Destination> getVistiedDestinationObservable(String user) {
        return Observable.create((Subscriber<? super Destination> s) -> {
        		List<Destination> destinations = destinationService.visitedDestinations(user);
        		for (Destination t : destinations) {
                    s.onNext(t);
                }
                s.onCompleted();
            }).subscribeOn(Schedulers.io());
    }
	
	private Observable<Destination> getRecommendedDestinationObservable(String user) {
        return Observable.create((Subscriber<? super Destination> s) -> {
        		List<Destination> destinations = destinationService.recommendedDestinations(user);
        		for (Destination t : destinations) {
                    s.onNext(t);
                }
                s.onCompleted();
            }).subscribeOn(Schedulers.io());
    }

}
