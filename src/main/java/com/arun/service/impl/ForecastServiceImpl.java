package com.arun.service.impl;

import org.springframework.stereotype.Service;

import com.arun.beans.Forecast;
import com.arun.helper.Helper;
import com.arun.logging.Loggable;
import com.arun.service.ForecastService;

@Service
public class ForecastServiceImpl implements ForecastService {

	@Loggable
	@Override
	public Forecast getForecast(String destination) {
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return new Forecast(destination, Helper.getForecast(destination));
	}

}
