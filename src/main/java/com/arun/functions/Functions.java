package com.arun.functions;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.arun.beans.Calculation;
import com.arun.beans.Destination;
import com.arun.beans.Forecast;
import com.arun.beans.Recommendation;
import com.arun.suppliers.Suppliers;

@Component
public class Functions {
	
	@Autowired 
	private Suppliers suppliers;
	
	public Function<List<Destination>, List<Recommendation>> processorFunction = new Function<List<Destination>, List<Recommendation>>() {

		@Override
		public List<Recommendation> apply(List<Destination> destinations) {
			ExecutorService executor = Executors.newCachedThreadPool();
			List<Recommendation> recommendations = new ArrayList<Recommendation>();

			for(Destination destination: destinations) {		
				Recommendation recommendation = new Recommendation();
				CompletableFuture<Forecast> forecastFuture = CompletableFuture.supplyAsync(suppliers.forecastSupplier(destination), executor);
				CompletableFuture<Calculation> priceFuture = CompletableFuture.supplyAsync(suppliers.priceSupplier(destination), executor);
				CompletableFuture<Recommendation> recommendationFuture = forecastFuture.thenCombine(priceFuture, this::combiner);	
				try {
					recommendation = recommendationFuture.get();
				} catch (InterruptedException | ExecutionException e) {
					e.printStackTrace();
				}
				recommendation.setDestination(destination.getDestination());
				recommendations.add(recommendation);
			}
			return recommendations;
		}
		
		private Recommendation combiner(Forecast forecast, Calculation price) {
			Recommendation rec = new Recommendation();
			rec.setForecast(forecast.getForecast());
			rec.setPrice(price.getPrice());
			return rec;
		}
				
	};
	
	public Function<List<Destination>, List<Recommendation>> parallelProcessorFunction = new Function<List<Destination>, List<Recommendation>>() {

		@Override
		public List<Recommendation> apply(List<Destination> destinations) {
			List<Recommendation> recommendations = destinations.parallelStream().map(s -> mapFunction(s)).collect(Collectors.toList());
			return recommendations;
		}
				
	};
	
	private Recommendation mapFunction(Destination destination) {
		ExecutorService executor = Executors.newCachedThreadPool();
		Recommendation recommendation = new Recommendation();
		CompletableFuture<Forecast> forecastFuture = CompletableFuture.supplyAsync(suppliers.forecastSupplier(destination), executor);
		CompletableFuture<Calculation> priceFuture = CompletableFuture.supplyAsync(suppliers.priceSupplier(destination), executor);
		CompletableFuture<Recommendation> recommendationFuture = forecastFuture.thenCombine(priceFuture, this::combiner);	
		try {
			recommendation = recommendationFuture.get();
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
		recommendation.setDestination(destination.getDestination());
		return recommendation;
	}
	
	private Recommendation combiner(Forecast forecast, Calculation price) {
		Recommendation rec = new Recommendation();
		rec.setForecast(forecast.getForecast());
		rec.setPrice(price.getPrice());
		return rec;
	}
}
