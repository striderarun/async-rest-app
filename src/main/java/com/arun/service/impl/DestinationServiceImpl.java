package com.arun.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.arun.beans.Destination;
import com.arun.helper.Helper;
import com.arun.logging.Loggable;
import com.arun.service.DestinationService;


@Service
public class DestinationServiceImpl implements DestinationService {
	
    private static final Map<String, List<String>> VISITED = new HashMap<>();

    static {
        VISITED.put("Sync", Helper.getCountryList(5));
        VISITED.put("Async", Helper.getCountryList(5));
        VISITED.put("Guava", Helper.getCountryList(5));
        VISITED.put("RxJava", Helper.getCountryList(5));
    }
	
    @Loggable
	public List<Destination> visitedDestinations(String user) {
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		List<String> visited = VISITED.get(user);
		return visited.stream().map(s -> new Destination(s)).collect(Collectors.toList());
	}
	
    @Loggable
	public List<Destination> recommendedDestinations(String user) {
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		List<String> visited = VISITED.get(user);
		List<String> recommneded = Helper.getCountries(5, visited);
		return recommneded.stream().map(s -> new Destination(s)).collect(Collectors.toList());
	}

}