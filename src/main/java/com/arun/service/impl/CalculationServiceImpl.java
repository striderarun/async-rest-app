package com.arun.service.impl;

import java.util.Random;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.arun.beans.Calculation;
import com.arun.logging.Loggable;
import com.arun.service.CalculationService;

@Service
public class CalculationServiceImpl implements CalculationService {

	@Loggable
	@Override
	public Calculation getPrice(String destination) {
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		Random rand = new Random(); 
		Calculation c = new Calculation(destination, rand.nextInt(1000));
		return c;
	}

	
}
