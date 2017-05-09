package com.arun.suppliers;

import java.util.List;
import java.util.function.Supplier;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.arun.beans.Calculation;
import com.arun.beans.Destination;
import com.arun.beans.Forecast;
import com.arun.service.CalculationService;
import com.arun.service.DestinationService;
import com.arun.service.ForecastService;

@Component
public class Suppliers {
	
	@Autowired
	private DestinationService destinationService;
	
	@Autowired
	private CalculationService calculationService;
	
	@Autowired
	private ForecastService forecastService;

	public Supplier<Forecast> forecastSupplier(Destination destination) {
		return () -> {
			return forecastService.getForecast(destination.getDestination());
		};	
	}
	
	public Supplier<Calculation> priceSupplier(Destination destination) {
		return () -> {
			return calculationService.getPrice(destination.getDestination());
		};
	}
	
	public Supplier<List<Destination>> visitedDestinationSupplier(String user) {
		return () -> {
			System.out.println(String.format("Executing thread: %s", Thread.currentThread().getName()));
			return destinationService.visitedDestinations(user);
		};
	}
	
	public Supplier<List<Destination>> recommendedDestinationSupplier(String user) {
		return () -> {
			return destinationService.recommendedDestinations(user);
		};
	}
}	

	/**Suppliers using anonymous inner classes*/
	/*public Supplier<Forecast> forecastSupplier(Destination destination) {
		Supplier<Forecast> forecastSupplier = new Supplier<Forecast>() {

			@Override
			public Forecast get() {
				return forecastService.getForecast(destination.getDestination());
			}
		};
		return forecastSupplier;
	}
	
	public Supplier<Calculation> priceSupplier(Destination destination) {
		Supplier<Calculation> priceSupplier = new Supplier<Calculation>() {

			@Override
			public Calculation get() {
				return calculationService.getPrice(destination.getDestination());
			}
		};
		return priceSupplier;
	}
	
	public Supplier<List<Destination>> visitedDestinationSupplier(String user) {
		Supplier<List<Destination>> destinationSupplier = new Supplier<List<Destination>>() {

			@Override
			public List<Destination> get() {
				return destinationService.visitedDestinations(user);
			}
		};
		return destinationSupplier;
	}
	
	public Supplier<List<Destination>> recommendedDestinationSupplier(String user) {
		Supplier<List<Destination>> destinationSupplier = new Supplier<List<Destination>>() {

			@Override
			public List<Destination> get() {
				return destinationService.recommendedDestinations(user);
			}
		};
		return destinationSupplier;
	}*/

