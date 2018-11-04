package com.rohan.stockapp.service;

public class ChartDoer implements Runnable {
	
	ChartMaker chartMaker;

	public ChartDoer(ChartMaker cm1) {
		this.chartMaker = cm1;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		System.out.println("Chart stuff here using the object");
	}
	
	

}
