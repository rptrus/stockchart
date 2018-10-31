package com.rohan.stockapp;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

import org.jfree.chart.ChartColor;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.CombinedDomainCategoryPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import com.itextpdf.text.pdf.DefaultFontMapper;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Font.FontFamily;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.html.WebColors;
import com.itextpdf.text.pdf.FontMapper;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfGState;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfTemplate;
import com.itextpdf.text.pdf.PdfWriter;
import com.rohan.stockapp.dto.StockReportElement;



@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class ChartConstructionTest {
	
	final String BASE_DIR=File.separatorChar+"output"; // can have this as a program argument
	
    Font bfBold12 = FontFactory.getFont("Verdana", 8, Font.BOLD);
    Font bfNormal = FontFactory.getFont("Verdana", 8, Font.NORMAL);
    
    final String BG =BASE_DIR+File.separatorChar+"ferny.jpg";
    
	
	@Test
	public void makePDFChart() {
		  String fileName = BASE_DIR+File.separatorChar+"MyChart.pdf";
		  
		  PdfWriter writer = null;
		  
		  // step 1
	        Document document = new Document(PageSize.A4, 36, 36, 54, 36);
	        try {
	           
	            // step 2
	            writer = PdfWriter.getInstance(document, new FileOutputStream(
	                    fileName));
	            
	            Paragraph paragraph = new Paragraph();
	            addEmptyLine(paragraph, 1);
	            
	             //TableHeader1 event = new TableHeader1(); // TODO stamp each page
	             
	             //writer.setPageEvent(event);
	             
	             document.open();
	             
	             // transparency
	             
	             PdfContentByte canvas = writer.getDirectContentUnder();
	             Image bgimage = Image.getInstance(BG);
	             //image.scaleAbsolute(PageSize.A4.rotate());
	             bgimage.scaleAbsoluteHeight(850);
	             bgimage.setAbsolutePosition(0, 0);	             
	             canvas.saveState();
	             PdfGState state = new PdfGState();	             
	             state.setFillOpacity(0.3f);
	             canvas.setGState(state);
	             canvas.addImage(bgimage);
	             canvas.restoreState();
	             
	             //
	             	             
	             
	             document.add(paragraph);
	             //document.add(merchantDetailTable(re, reportType));
	             //document.add(table1(MerchantNo));           
	             
	             document.add(paragraph);
	             
	                SimpleDateFormat format1 = new SimpleDateFormat("yyyymmdd");
	                SimpleDateFormat format2 = new SimpleDateFormat("dd MMM yyyy");
	                
	                Calendar cal = Calendar.getInstance();
	                cal.setTime(new java.util.Date());
	                java.util.Date edate = cal.getTime();
	                cal.add(Calendar.DATE, -3);
	                java.util.Date sdate = cal.getTime();
	                
	                //Date sdate = format1.parse(startDateArg);
	                //Date edate = format1.parse(endDateArg);	                
	                
	                
	                document.add(paragraph);
	                
	           //insert column headings
	           // comes from args
	                Paragraph p;
	                Font bold = new Font(FontFamily.HELVETICA, 12, Font.BOLD);
	                p = new Paragraph(/*format2.format(sdate) + " to " + */"Report Date "+format2.format(edate), bold );
	                p.setAlignment(Element.ALIGN_JUSTIFIED);
	                document.add(p);
	                document.add(paragraph);

	                         
	             document.add(stockStatsTable(hackStocks()));
	             
	             document.add(paragraph);
	             
//	            JFreeChart piechart = makePieChart(hackStocks());
//	 			BufferedImage bufferedImagePie = piechart.createBufferedImage(300, 300);
//	 			Image imagePie = Image.getInstance(writer, bufferedImagePie, 1.0f);
//	 			document.add(imagePie);

	             PdfContentByte contentByte0 = writer.getDirectContent();
	             
		            int width0=450;
		            int height0 = 250;
		            int stretchFactor0 = 0, stretchFactor20 =0;            
		            { stretchFactor0 = 0; stretchFactor20=0; }	            
		            PdfContentByte contentByteLine0 = writer.getDirectContent();
		            PdfTemplate templateLine0 = contentByteLine0.createTemplate(width0, height0+stretchFactor0);
		            Graphics2D graphics2dLine0 = templateLine0.createGraphics(width0, height0+stretchFactor0,
		                    new DefaultFontMapper());
		            Rectangle2D rectangle2dLine0 = new Rectangle2D.Double(0, 0, width0,
		                    height0+stretchFactor0); // make bigger
		            System.out.println("STOP HERE!!!!!!!!!!!!!!!!!!!");
	            makePieChart(hackStocks()).draw(graphics2dLine0, rectangle2dLine0);
	            graphics2dLine0.dispose();
	            contentByte0.addTemplate(templateLine0, 38, 300); // positioning on page

	 			
	             
	            document.add(paragraph);
	            	            
	            PdfContentByte contentByte = writer.getDirectContent();
	 			
	            int width=450;
	            int height = 250;
	            int stretchFactor = 0, stretchFactor2 =0;            
	            { stretchFactor = 0; stretchFactor2=0; }	            
	            PdfContentByte contentByteLine = writer.getDirectContent();
	            PdfTemplate templateLine = contentByteLine.createTemplate(width, height+stretchFactor);
	            Graphics2D graphics2dLine = templateLine.createGraphics(width, height+stretchFactor,
	                    new DefaultFontMapper());
	            Rectangle2D rectangle2dLine = new Rectangle2D.Double(0, 0, width,
	                    height+stretchFactor); // make bigger

	            createPerformanceGraph(2, hackStocks()).draw(graphics2dLine, rectangle2dLine);
	             
	            graphics2dLine.dispose();
	            contentByte.addTemplate(templateLine, 38, 38); // positioning on page
	            
	             
	            document.add(paragraph);

	             
	            document.close();
	        } catch (Exception ex) {
	        	System.out.println(ex);
	        } finally {
	        	
	        }
		  
	}
		
	  private PdfPTable stockStatsTable(List<StockReportElement> stockList) throws ParseException
	    {

	        String daCurrency= null;
	        
	        float[] columnWidths = {3.5f, 2f, 2f, 2f, 2f};
	           //create PDF table with the given widths
	           PdfPTable table = new PdfPTable(columnWidths);
	           //PdfPTable table = new PdfPTable(5);
	           // set table width a percentage of the page width
	           table.setWidthPercentage(100f);
	           //Font bfBold12 = new Font(FontFamily.HELVETICA, 8, Font.BOLD, new BaseColor(0, 0, 0));
	           //Font bfNormal = new Font(FontFamily.HELVETICA, 8, Font.NORMAL, new BaseColor(0, 0, 0));
	           
	              
//	             Font bfBold12 = FontFactory.getFont("Verdana", 8, Font.BOLD);
//	             Font bfNormal = FontFactory.getFont("Verdana", 8, Font.NORMAL);
	           
	                SimpleDateFormat format1 = new SimpleDateFormat("yyyymmdd");
	                SimpleDateFormat format2 = new SimpleDateFormat("dd MMM yyyy");
	                
	                Calendar cal = Calendar.getInstance();
	                cal.setTime(new java.util.Date());
	                java.util.Date edate = cal.getTime();
	                cal.add(Calendar.DATE, -3);
	                java.util.Date sdate = cal.getTime();
	                
	                //Date sdate = format1.parse(startDateArg);
	                //Date edate = format1.parse(endDateArg);	                
	                
	                
	           //insert column headings
	           // comes from args
	           insertCell(table, "Code: ", Element.ALIGN_LEFT, 1, bfBold12);
	           insertCell(table, "Acquired Price", Element.ALIGN_RIGHT, 1, bfBold12);
	           insertCell(table, "Current Price", Element.ALIGN_RIGHT, 1, bfBold12);
	           insertCell(table, "Movement (price)", Element.ALIGN_RIGHT, 1, bfBold12);
	           insertCell(table, "Movement (percent)", Element.ALIGN_RIGHT, 1, bfBold12);
	           
	           stockList.stream().filter(StockReportElement::active).forEach(stock->addStock(table, stock));
	           
	           
	          return table;
	    }
	  
	  private void addStock(PdfPTable table, StockReportElement stock) {
          insertCell(table, stock.getCode(), Element.ALIGN_LEFT, 1, bfNormal);           
          insertCell(table, stock.getAcquiredPrice().setScale(2,RoundingMode.HALF_UP).toString(), Element.ALIGN_RIGHT, 1, bfNormal);
          insertCell(table, stock.getCurrentPrice().setScale(2,RoundingMode.HALF_UP).toString(), Element.ALIGN_RIGHT, 1, bfNormal);
          Float total = 100f; 
          insertCell(table, stock.getCurrentPrice().subtract(stock.getAcquiredPrice()).setScale(2, RoundingMode.HALF_EVEN)+"", Element.ALIGN_RIGHT, 1, bfNormal); // % by value 
          insertCell(table, (stock.getCurrentPrice().divide(stock.getAcquiredPrice(),2,RoundingMode.HALF_EVEN)
    				.subtract(BigDecimal.ONE))
    				.multiply(BigDecimal.TEN.pow(2)).setScale(0).toString().toString()+"%", Element.ALIGN_RIGHT, 1, bfNormal); // % by number		  
	  }

	    private JFreeChart createPerformanceGraph(int type, List<StockReportElement> stockList) { //RT
	        
	        System.out.println("TYPE: "+type);

	        final CategoryDataset dataset1 = createDatasetCHCPercentage(type, stockList);
	        final NumberAxis rangeAxis1 = new NumberAxis("Performance");
	        rangeAxis1.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
	        //final BarRenderer renderer1 = new BarRenderer();
	        
	        org.jfree.chart.renderer.category.AbstractCategoryItemRenderer renderer1 = null;
	        
	        if (type==1) { // XXX not used for stocks
	            /*final LineAndShapeRenderer */renderer1 = new LineAndShapeRenderer();
	        }
	        else if (type==2) {
	            /*final BarRenderer */renderer1 = /*new StackedBarRenderer();*/new BarRenderer();	            
	        }
	        //renderer1.setBaseToolTipGenerator(new StandardCategoryToolTipGenerator());

	        final CategoryPlot subplot1 = new CategoryPlot(dataset1, null, rangeAxis1, renderer1);
	        subplot1.setDomainGridlinesVisible(true);
	        ValueAxis yAxis =subplot1.getRangeAxis(); // show percentage y axis to 100
	        yAxis.setRange(0, 100);

	        if (type==2) {
	            ((BarRenderer) subplot1.getRenderer()).setBarPainter(new StandardBarPainter());
	            /*
	        BarRenderer br = (BarRenderer) subplot1.getRenderer(); 
	        br.setMaximumBarWidth(0.2);// NOT WORKING?!? why?
	        br.setItemMargin(0.1);
	             */
	            BarRenderer barrenderer = (BarRenderer)subplot1.getRenderer();
	            barrenderer.setMaximumBarWidth(0.085f);
	            //barrenderer.setBase(-100);
	        }

	        renderer1.setSeriesPaint(0, new Color(0x0191C8)); //sky blue 0x7EB5D6 // yeah
	        //renderer1.setSeriesPaint(0, new Color(0xCCCC99)); //lightbrowny
	        //renderer1.setSeriesPaint(1, new Color(0x6A6A5A));
	        //renderer1.setSeriesPaint(1, new Color(0xf5f5f5)); // light gray

	        
	        final CategoryDataset dataset2 = createDatasetCHCAmount(type, hackStocks());              
	        final NumberAxis rangeAxis2 = new NumberAxis("Holding Amt");             
	        rangeAxis2.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
	        final BarRenderer renderer2 = new BarRenderer();
	        //renderer2.setBase(-50); // need to set this as per lowest value. good use of findFirst here
	        //renderer2.setBaseToolTipGenerator(new StandardCategoryToolTipGenerator());
	        final CategoryPlot subplot2 = new CategoryPlot(dataset2, null, rangeAxis2, renderer2);
	        subplot2.setDomainGridlinesVisible(true);
	        ((BarRenderer) subplot2.getRenderer()).setBarPainter(new StandardBarPainter());
	        BarRenderer barrenderer = (BarRenderer)subplot2.getRenderer();
	        barrenderer.setMaximumBarWidth(0.085f);
	        //barrenderer.setBase(-100);


	        final CategoryAxis domainAxis = new CategoryAxis(type==1 ? "Day":"Currency");
	        
	        //
	        domainAxis.setTickLabelPaint(Color.BLACK);
	        domainAxis.setTickLabelFont(new java.awt.Font("Verdana", Font.NORMAL, 6));
	        renderer2.setSeriesPaint(0, new Color(0x5cbf56)); // yellow 0xFFCC00 // yeah
	        //
	        
	        final CombinedDomainCategoryPlot plot = new  CombinedDomainCategoryPlot(domainAxis);


	        plot.add(subplot1, 1);
	        plot.add(subplot2, 1);        
	        plot.setGap(18);

	        String title="Performance Graph";
	        /*
	        final JFreeChart chart = new JFreeChart(
	        title,
	        plot);
	        */
	      final JFreeChart chart = new JFreeChart(
	              title,
	              new java.awt.Font("SansSerif", Font.BOLD, 12),
	              plot,
	              true
	          );
	      //chart.setBackgroundPaint(Color.WHITE);	      
	      //chart.setBackgroundPaint(new ChartColor(124, 146, 138));
	        
	        return chart;
	  }
	    
	    public CategoryDataset createDatasetCHCPercentage(int type, List<StockReportElement> stockList) {

	        //List currencyVector = re.vdccCardholderCurrency;
	        System.out.println("ABOUT TO SORT1!");
	        
	          final DefaultCategoryDataset result = 
	           new DefaultCategoryDataset();
	          
//	        for (int i = 0; i < currencyVector.size(); i++) {
//	            if (new Double(((CurrencyData)currencyVector.get(i)).dccOptInAmtPercentage).doubleValue() > 0) // RT. jose
//	                result.addValue(new Double(((CurrencyData)currencyVector.get(i)).dccOptInAmtPercentage), "Opt in percentage", ((CurrencyData)currencyVector.get(i)).currencyCode);            
//	        }
	          
	          final String rowKey = "Increase %";
/*	          
	          result.addValue(20, "Increase %", "WBC");
	          result.addValue(26, "Increase %", "WBC");
	          result.addValue(10, "Increase %", "VAS");
	          result.addValue(90, "Increase %", "VAS");
*/
	          
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
	    
	    public CategoryDataset createDatasetCHCAmount(int type, List<StockReportElement> stockList) {
	        
	        //List currencyVector = re.vdccCardholderCurrency;        
	        System.out.println("ABOUT TO SORT2!");

	          final DefaultCategoryDataset result 
	           = new DefaultCategoryDataset();
	          /*
	          result.addValue(Double.valueOf("15.5"), "Stock Code", "WBC");
	          result.addValue(Double.valueOf("35.5"), "Stock Code", "VAS");
	          */
	          
	          final String rowKey = "Equity held";
	          
	          for (StockReportElement aStockReportElement: stockList) {
	        	  result.addValue(
	        			  aStockReportElement.getCurrentPrice()
	        					  	.doubleValue(), 
	        			  rowKey, 
	        			  aStockReportElement.getCode()
	        			  );
	          }

	        return result;
	    }



		

	private JFreeChart makePieChart(List<StockReportElement> stockList) {
		  DefaultPieDataset dataset = new DefaultPieDataset( );

		  BigDecimal accumulated = BigDecimal.ZERO;
		  for (StockReportElement stock : stockList) {			  
			  Random rand = new Random();
			  int size = stockList.size();
			  int bound = 100/size;
			  
			  // TODO  Compute this result outside of the presentation layer
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
	        
	      //chart.setBackgroundPaint(ChartColor.LIGHT_GRAY);
	      chart.setBackgroundPaint(new ChartColor(224, 224, 224));
	      //chart.setBackgroundPaint(new ChartColor(124, 146, 138));
	      
	      int width = 640;   /* Width of the image */
	      int height = 480;  /* Height of the image */
	      
	      return chart;
	  }
	  
	  private List hackStocks() {
		  List<StockReportElement> stocks = new ArrayList<>();
		  StockReportElement[] stok = new StockReportElement[4];
		  stok[0] = new StockReportElement(LocalDateTime.of(2014, Month.JANUARY, 1,0,0,0), "VAS", new BigDecimal(68.00), new BigDecimal(72.10), 10);
		  stok[1] = new StockReportElement(LocalDateTime.of(2015, Month.JULY, 11,0,0,0), "WBC", new BigDecimal(27.00), new BigDecimal(30.50), 20);
		  stok[2] = new StockReportElement(LocalDateTime.of(2015, Month.MAY, 14,0,0,0), "KGN", new BigDecimal(2.22), new BigDecimal(3.00), 30);
		  stok[3] = new StockReportElement(LocalDateTime.of(2013, Month.MAY, 23,0,0,0), "CBA", new BigDecimal(67.00), new BigDecimal(69.50), 40);
		  return Arrays.asList(stok);
	  }
	  
	  
	  
	  // HELPER FUNCTIONS
	  
	  private static void insertCell(PdfPTable table, String text, int align, int colspan, Font font){
          
          //create a new cell with the specified Text and Font
          PdfPCell cell = new PdfPCell(new Phrase(text.trim(), font));
          //set the cell alignment
          cell.setHorizontalAlignment(align);
          //set the cell column span in case you want to merge two or more cells
          cell.setColspan(colspan);
          //in case there is no text and you wan to create an empty row
          if(text.trim().equalsIgnoreCase("")){
           cell.setMinimumHeight(10f);
          }
          cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
          cell.setBackgroundColor(WebColors.getRGBColor("#d6f5d6"));
          //add the call to the table
          table.addCell(cell);
           
         }
	
	  private static void addEmptyLine(Paragraph paragraph, int number) {
	        for (int i = 0; i < number; i++) {
	          paragraph.add(new Paragraph(" "));
	        }
	    }
	  
	  class TableHeader1 extends PdfPageEventHelper {
		  
	  }
	  
}
