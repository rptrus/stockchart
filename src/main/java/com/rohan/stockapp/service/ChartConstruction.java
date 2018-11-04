package com.rohan.stockapp.service;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.net.MalformedURLException;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.function.Consumer;

import org.apache.commons.lang3.StringUtils;
import org.jfree.chart.ChartColor;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.CombinedDomainCategoryPlot;
import org.jfree.chart.renderer.category.AbstractCategoryItemRenderer;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.itextpdf.text.BadElementException;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Font.FontFamily;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.DefaultFontMapper;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfGState;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfTemplate;
import com.itextpdf.text.pdf.PdfWriter;
import com.mysql.jdbc.log.Log;
import com.rohan.stockapp.dto.StockReportElement;
import com.rohan.stockapp.enums.ReportColors;
import com.rohan.stockapp.json.Stock;
import com.rohan.stockapp.json.StockAdd;
import com.rohan.stockapp.json.StockSet;

@Component
public class ChartConstruction {
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Value("${marginLeft:36}")	 
	Float marginLeft;
	@Value("${marginRight:36}")
	Float marginRight;
	@Value("${marginTop:54}")
	Float marginTop;
	@Value("${marginBottom:36}")
	Float marginBottom;
	
	@Value("${portFolioAllocationPagePositionX:38}")
	Integer portFolioAllocationPagePositionX;
	@Value("${portFolioAllocationPagePositionY:300}")
	Integer portFolioAllocationPagePositionY;	
	@Value("${performancePagePositionX:38}")
	Integer performancePagePositionX;
	@Value("${performancePagePositionY:38}")
	Integer performancePagePositionY;	
	
	@Value("${bgImage}")
	private String backgroundImageFile;
	
	@Value("${elementtwidth:450}")
	private Integer elementWidth;	
	@Value("${elementheight:250}")
	private Integer elementHeight;
	
    Font bfBold12 = FontFactory.getFont("Verdana", 8, Font.BOLD);
    Font bfNormal = FontFactory.getFont("Verdana", 8, Font.NORMAL);
    Font bold = new Font(FontFamily.HELVETICA, 12, Font.BOLD);
    java.awt.Font smallVerdana = new java.awt.Font("Verdana", Font.NORMAL, 6);    
	
	public void makePDFChart(List<StockReportElement> stockElementList, String fullPathFilename) {
		  PdfWriter writer = null;
		  Document document = new Document(PageSize.A4, marginLeft, marginRight, marginTop, marginBottom);
		  
	      try {
	    	  writer = PdfWriter.getInstance(document, new FileOutputStream(fullPathFilename));
	          document.open();
	          addTransparentBackground(writer);
	          addEmptyLine(document,  1);
              
	          Paragraph p = new Paragraph("Report Date:\t\t\t\t"+Utils.ddMMMyyyHHmm.format(Utils.getCurrentDate()), bold );
              p.setAlignment(Element.ALIGN_JUSTIFIED);
              document.add(p);
              addEmptyLine(document, 2);
              
              document.add(stockStatsTable(stockElementList));
              addEmptyLine(document,  1);
              
              addTemplateToPage(writer, stockElementList, 0, 450, 250, 38, 300); 
              addTemplateToPage(writer, stockElementList, 1, 450, 250, 38, 38);

              // here we have all the necessary ingredients. now need to pass this in to our charting methods (1&2)
              //ChartMaker cm1 = preSetup(writer, stockElementList, /*this::myFirst,*/ 450, 250, 38, 300);              
              //ChartDoer cd1 = new ChartDoer(cm1); // will have the actual chart specific logic
            
              addEmptyLine(document,  1);
              
              document.close();
              logger.info("Document now closed.");
        } catch (Exception ex) {
        	logger.error("Exception! ",ex);
        	if (document!=null)
        		document.close();
        }
	}
	
	// WIP
	private ChartMaker preSetup(PdfWriter writer, List<StockReportElement> stockElementList, /*Runnable method, */int width,
			int height, int pagePositionX, int pagePositionY) {
		// TODO Auto-generated method stub
		int stretchFactor0 = 0;
        PdfContentByte contentByte0 = writer.getDirectContent();
        PdfContentByte contentByteLine0 = writer.getDirectContent();
        PdfTemplate templateLine0 = contentByteLine0.createTemplate(elementWidth, elementHeight+stretchFactor0);
        Graphics2D graphics2dLine0 = templateLine0.createGraphics(elementWidth, elementHeight+stretchFactor0,
          		new DefaultFontMapper());
        Rectangle2D rectangle2dLine0 = new Rectangle2D.Double(0, 0, elementWidth, elementHeight+stretchFactor0);
        ChartMaker cm1 = new ChartMaker(graphics2dLine0, rectangle2dLine0, stockElementList);
        return cm1;
        //if (type == 0) makePortfolioAllocationPieChart(stockElementList).draw(graphics2dLine0, rectangle2dLine0);
        //if (type == 1) createPerformanceGraph(stockElementList).draw(graphics2dLine0, rectangle2dLine0);
        //method.run(); // runs the thing in myFirst or mySecond
        //graphics2dLine0.dispose();
        //contentByte0.addTemplate(templateLine0, pagePositionX, pagePositionY); // positioning on page

	}
	
	public void myFirst() {
		
	}
	
	public void mySecond() {
		
	}

	private void addTemplateToPage(PdfWriter writer, List<StockReportElement> stockElementList, int type, Integer width, Integer height, Integer pagePositionX, Integer pagePositionY) {		
		int stretchFactor0 = 0;
        PdfContentByte contentByte0 = writer.getDirectContent();
        PdfContentByte contentByteLine0 = writer.getDirectContent();
        PdfTemplate templateLine0 = contentByteLine0.createTemplate(elementWidth, elementHeight+stretchFactor0);
        Graphics2D graphics2dLine0 = templateLine0.createGraphics(elementWidth, elementHeight+stretchFactor0,
          		new DefaultFontMapper());
        Rectangle2D rectangle2dLine0 = new Rectangle2D.Double(0, 0, elementWidth, elementHeight+stretchFactor0);
        if (type == 0) makePortfolioAllocationPieChart(stockElementList).draw(graphics2dLine0, rectangle2dLine0);
        if (type == 1) createPerformanceGraph(stockElementList).draw(graphics2dLine0, rectangle2dLine0);
        graphics2dLine0.dispose();
        contentByte0.addTemplate(templateLine0, pagePositionX, pagePositionY); // positioning on page
	}
	
	private void addTransparentBackground(PdfWriter writer) throws MalformedURLException, IOException, DocumentException {
        PdfContentByte canvas = writer.getDirectContentUnder();
        Image bgimage = Image.getInstance(backgroundImageFile);
        bgimage.scaleAbsoluteHeight(850);
        bgimage.setAbsolutePosition(0, 0);
        canvas.saveState();
        PdfGState state = new PdfGState();	             
        state.setFillOpacity(0.3f);
        canvas.setGState(state);
        canvas.addImage(bgimage);
        canvas.restoreState();		
	}
	
	private JFreeChart createPerformanceGraph(List<StockReportElement> stockList) {
        final CategoryDataset dataset1 = createDatasetCHCPercentage(stockList);
        final NumberAxis rangeAxis1 = new NumberAxis("Performance");
        rangeAxis1.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        AbstractCategoryItemRenderer renderer1 = new BarRenderer();

        final CategoryPlot subplot1 = new CategoryPlot(dataset1, null, rangeAxis1, renderer1);
        subplot1.setDomainGridlinesVisible(true);
        ValueAxis yAxis =subplot1.getRangeAxis();
        yAxis.setRange(0, 100);
        ((BarRenderer) subplot1.getRenderer()).setBarPainter(new StandardBarPainter());
        BarRenderer barrenderer1 = (BarRenderer)subplot1.getRenderer();
        barrenderer1.setMaximumBarWidth(0.085f);
        renderer1.setSeriesPaint(0, new Color(ReportColors.COOLBLUE.getReportColor()));
        
        final CategoryDataset dataset2 = createDatasetCHCAmount(stockList);
        final NumberAxis rangeAxis2 = new NumberAxis("Holding Amt");             
        rangeAxis2.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        final BarRenderer renderer2 = new BarRenderer();
        final CategoryPlot subplot2 = new CategoryPlot(dataset2, null, rangeAxis2, renderer2);
        subplot2.setDomainGridlinesVisible(true);
        ((BarRenderer) subplot2.getRenderer()).setBarPainter(new StandardBarPainter());
        BarRenderer barrenderer2 = (BarRenderer)subplot2.getRenderer();
        barrenderer2.setMaximumBarWidth(0.085f);

        final CategoryAxis domainAxis = new CategoryAxis("Currency");
        
        domainAxis.setTickLabelPaint(Color.BLACK);
        domainAxis.setTickLabelFont(smallVerdana);
        renderer2.setSeriesPaint(0, new Color(ReportColors.GREEN.getReportColor()));
        
        final CombinedDomainCategoryPlot plot = new  CombinedDomainCategoryPlot(domainAxis);

        plot.add(subplot1, 1);
        plot.add(subplot2, 1);        
        plot.setGap(18);

        final JFreeChart chart = new JFreeChart(
    		  "Performance Graph",
              new java.awt.Font("SansSerif", Font.BOLD, 12),
              plot,
              true
        );
        return chart;
	}
	  
	private JFreeChart makePortfolioAllocationPieChart(List<StockReportElement> stockList) {
		  DefaultPieDataset dataset = new DefaultPieDataset( );

		  BigDecimal accumulated = BigDecimal.ZERO;
		  for (StockReportElement stock : stockList) {			  
			  BigDecimal shareHolding = stock.getCurrentPrice().multiply(BigDecimal.valueOf(stock.getNumberOfUnits()));
			  accumulated = accumulated.add(shareHolding);
		  }
		  
		  for (StockReportElement stock : stockList) {
			  BigDecimal shareHolding = stock.getCurrentPrice().multiply(BigDecimal.valueOf(stock.getNumberOfUnits()));
			  BigDecimal portion = shareHolding.divide(accumulated, 2, RoundingMode.HALF_UP).multiply(BigDecimal.TEN.pow(2));
			  dataset.setValue(stock.getCode(), portion.doubleValue());
		  }


	      JFreeChart chart = ChartFactory.createPieChart(
	         "Share Portfolio Allocation",   // chart title
	         dataset,          // data
	         true,             // include legend
	         true,
	         false);
	        
	      chart.setBackgroundPaint(new ChartColor(224, 224, 224));
	      return chart;
	  }
	
    public CategoryDataset createDatasetCHCPercentage(List<StockReportElement> stockList) {
          final DefaultCategoryDataset result = 
          new DefaultCategoryDataset();
          final String rowKey = "Increase %";
          
          for (StockReportElement aStockReportElement: stockList) {
        	  result.addValue(
        			  aStockReportElement.getCurrentPrice()
        					  	.divide(aStockReportElement.getAcquiredPrice(),2,RoundingMode.HALF_EVEN)
        						.subtract(BigDecimal.ONE)
        						.multiply(BigDecimal.TEN.pow(2)).setScale(0).doubleValue(), 
        			  rowKey, 
        			  aStockReportElement.getCode()
        			  );
          }
        return result;
    }
    
    public CategoryDataset createDatasetCHCAmount(List<StockReportElement> stockList) {
          final DefaultCategoryDataset result = new DefaultCategoryDataset();
          final String rowKey = "Equity held";
          
          for (StockReportElement aStockReportElement: stockList) {
        	  result.addValue(
        			  aStockReportElement.getCurrentPrice().doubleValue(), 
        			  rowKey, 
        			  aStockReportElement.getCode()
        			  );
          }
          return result;
    }

	  
	  private PdfPTable stockStatsTable(List<StockReportElement> stockList) throws ParseException
	    {
	        float[] columnWidths = {3.5f, 2f, 2f, 2f, 2f};
	           PdfPTable table = new PdfPTable(columnWidths);
	           table.setWidthPercentage(100f);
	           insertCell(table, "Code: ", Element.ALIGN_LEFT, 1, bfBold12, Boolean.TRUE);
	           insertCell(table, "Acquired Price", Element.ALIGN_RIGHT, 1, bfBold12, Boolean.TRUE);
	           insertCell(table, "Current Price", Element.ALIGN_RIGHT, 1, bfBold12, Boolean.TRUE);
	           insertCell(table, "Movement (price)", Element.ALIGN_RIGHT, 1, bfBold12, Boolean.TRUE);
	           insertCell(table, "Movement (percent)", Element.ALIGN_RIGHT, 1, bfBold12, Boolean.TRUE);
	           stockList.stream().filter(StockReportElement::active).forEach(stock->addStock(table, stock));
	           return table;
	    }
	  
	  private void addStock(PdfPTable table, StockReportElement stock) {
          insertCell(table, stock.getCode(), Element.ALIGN_LEFT, 1, bfNormal);           
          insertCell(table, stock.getAcquiredPrice().setScale(2,RoundingMode.HALF_UP).toString(), Element.ALIGN_RIGHT, 1, bfNormal);
          insertCell(table, stock.getCurrentPrice().setScale(2,RoundingMode.HALF_UP).toString(), Element.ALIGN_RIGHT, 1, bfNormal);
          insertCell(table, stock.getCurrentPrice().subtract(stock.getAcquiredPrice()).setScale(2, RoundingMode.HALF_EVEN)+"", Element.ALIGN_RIGHT, 1, bfNormal); // % by value 
          insertCell(table, (stock.getCurrentPrice().divide(stock.getAcquiredPrice(),4,RoundingMode.HALF_EVEN)
    				.subtract(BigDecimal.ONE))
    				.multiply(BigDecimal.TEN.pow(2)).setScale(2).toString()+"%", Element.ALIGN_RIGHT, 1, bfNormal); // % by number		  
	  }
	  
	  // HELPER FUNCTIONS
	  
	  private static void insertCell(PdfPTable table, String text, int align, int colspan, Font font) {
		  insertCell(table, text, align, colspan, font, false);
	  }
	  
	  private static void insertCell(PdfPTable table, String text, int align, int colspan, Font font, boolean header){
          PdfPCell cell = new PdfPCell(new Phrase(text.trim(), font));
          cell.setHorizontalAlignment(align);
          cell.setColspan(colspan);
          //in case there is no text and you wan to create an empty row
          if(text.trim().equalsIgnoreCase("")){
           cell.setMinimumHeight(10f);
          }
          cell.setBackgroundColor(new BaseColor(249, 255, 230,0));
          if (header) cell.setBackgroundColor(new BaseColor(237, 255, 179,0)); 
          table.addCell(cell);
           
         }
	
	  private static void addEmptyLine(Document document, int number) throws DocumentException {
		  Paragraph paragraph = new Paragraph();
	        for (int i = 0; i < number; i++) {
	          paragraph.add(new Paragraph(" "));
	          document.add(paragraph);
	        }
	    }
}
