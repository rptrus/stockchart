package com.rohan.stockapp.service;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import org.jfree.chart.ChartColor;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.DefaultPieDataset;

import com.rohan.stockapp.dto.StockReportElement;

public class ChartMaker implements Runnable {

	Graphics2D graphics2dLine0;
	Rectangle2D rectangle2dLine0;
	List<StockReportElement> stockElementList;
	JFreeChart chart;
	
	public ChartMaker(Graphics2D graphics2dLine0, Rectangle2D rectangle2dLine0,
			List<StockReportElement> stockElementList) {
		super();
		this.graphics2dLine0 = graphics2dLine0;
		this.rectangle2dLine0 = rectangle2dLine0;
		this.stockElementList = stockElementList;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		// This has our custom logic for each chart type
		  DefaultPieDataset dataset = new DefaultPieDataset( );

		  BigDecimal accumulated = BigDecimal.ZERO;
		  for (StockReportElement stock : stockElementList) {			  
			  BigDecimal shareHolding = stock.getCurrentPrice().multiply(BigDecimal.valueOf(stock.getNumberOfUnits()));
			  accumulated = accumulated.add(shareHolding);
		  }
		  
		  for (StockReportElement stock : stockElementList) {
			  BigDecimal shareHolding = stock.getCurrentPrice().multiply(BigDecimal.valueOf(stock.getNumberOfUnits()));
			  BigDecimal portion = shareHolding.divide(accumulated, 2, RoundingMode.HALF_UP).multiply(BigDecimal.TEN.pow(2));
			  dataset.setValue(stock.getCode(), portion.doubleValue());
		  }


	      /*JFreeChart */chart = ChartFactory.createPieChart(
	         "Share Portfolio Allocation",   // chart title
	         dataset,          // data
	         true,             // include legend
	         true,
	         false);
	        
	      chart.setBackgroundPaint(new ChartColor(224, 224, 224));	     
	}
	
	
}
