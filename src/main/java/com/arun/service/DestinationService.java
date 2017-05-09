package com.arun.service;

import java.util.List;

import com.arun.beans.Destination;

public interface DestinationService {
		
	public List<Destination> visitedDestinations(String user);
	
	public List<Destination> recommendedDestinations(String user);
	
}
