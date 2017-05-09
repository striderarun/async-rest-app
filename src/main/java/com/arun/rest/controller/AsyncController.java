package com.arun.rest.controller;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

import com.arun.beans.AgentResponse;
import com.arun.beans.Destination;
import com.arun.beans.Recommendation;
import com.arun.functions.Functions;
import com.arun.logging.Loggable;
import com.arun.suppliers.Suppliers;

@RestController
@RequestMapping(value = "/async")
public class AsyncController {
	
	@Autowired
	private Suppliers suppliers;
	
	@Autowired
	private Functions functions;
	
	@Loggable
	@RequestMapping(value = "/getDetails", method = RequestMethod.GET)
	@ResponseStatus(value = HttpStatus.OK)
	public AgentResponse getDetails(@RequestParam String user) {
		AgentResponse response = new AgentResponse();
		ExecutorService executor = Executors.newCachedThreadPool();
		long time = System.nanoTime();
		List<Destination> visitedPlaces = new ArrayList<>();
		List<Recommendation> recommendations = new ArrayList<Recommendation>();

		CompletableFuture<List<Destination>> visitedPlacesFuture = CompletableFuture.supplyAsync(suppliers.visitedDestinationSupplier(user), executor);
		CompletableFuture<List<Destination>> recommendedPlacesFuture = CompletableFuture.supplyAsync(suppliers.recommendedDestinationSupplier(user), executor);
		
		visitedPlacesFuture.thenAccept(s -> visitedPlaces.addAll(s));
		CompletableFuture<List<Recommendation>> recommendationsFuture = recommendedPlacesFuture.thenApply(functions.parallelProcessorFunction);
		
		try {
			recommendations = recommendationsFuture.get();
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
		
		response.setVisited(visitedPlaces);
		response.setRecommended(recommendations);
        response.setProcessingTime((System.nanoTime() - time) / 1000000);
		return response;  		
	}
	
	@Loggable
	@RequestMapping(value = "/deferredResult", method = RequestMethod.GET)
	@ResponseStatus(value = HttpStatus.OK)
	public DeferredResult<List<Destination>> getDeferredResult(@RequestParam String user) {
		System.out.println(String.format("Servlet thread: %s", Thread.currentThread().getName()));
		ExecutorService executor = Executors.newCachedThreadPool();
		DeferredResult<List<Destination>> deferredResult = new DeferredResult<>();    		
		CompletableFuture<List<Destination>> visitedPlacesFuture = CompletableFuture.supplyAsync(suppliers.visitedDestinationSupplier(user), executor)
				.whenCompleteAsync((result, throwable) -> deferredResult.setResult(result));
		System.out.println(String.format("Servlet thread exiting: %s", Thread.currentThread().getName()));
		return deferredResult;
		
	}
	
}