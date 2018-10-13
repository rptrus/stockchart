package com.rohan.stockapp.service;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.DefaultPieDataset;
import org.springframework.stereotype.Component;

import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Font.FontFamily;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;

@Component
public class ChartConstruction {
	
	final String BASE_DIR=File.separatorChar+"output"; // can have this as a program argument 
	
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
	             
	             ReportElements re = hackupAReportElements();
	             
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
	                Font bold = new Font(FontFamily.TIMES_ROMAN, 12, Font.BOLD);
	                p = new Paragraph(format2.format(sdate) + " to " + format2.format(edate), bold );
	                p.setAlignment(Element.ALIGN_JUSTIFIED);
	                document.add(p);

	                         
	             document.add(stockStatsTable(re));
	             
	             document.add(paragraph);
	              
	            JFreeChart chart = makePieChart();
	 			BufferedImage bufferedImage = chart.createBufferedImage(300, 300);
	 			Image image = Image.getInstance(writer, bufferedImage, 1.0f);
	 			document.add(image);
	             
	             document.add(paragraph);
	             
	             document.close();
	        } catch (Exception ex) {
	        	
	        } finally {
	        	
	        }
		  
	}
	
	  private PdfPTable stockStatsTable(ReportElements re) throws ParseException
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
	           
	              
	             Font bfBold12 = FontFactory.getFont("Verdana", 8, Font.BOLD);
	             Font bfNormal = FontFactory.getFont("Verdana", 8, Font.NORMAL);
	           
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
	           insertCell(table, "Code: " + format2.format(sdate) + " to " + format2.format(edate), Element.ALIGN_LEFT, 1, bfBold12);
	           insertCell(table, "Price", Element.ALIGN_RIGHT, 1, bfBold12);
	           insertCell(table, "Current Price", Element.ALIGN_RIGHT, 1, bfBold12);
	           insertCell(table, "Movement (price)", Element.ALIGN_RIGHT, 1, bfBold12);
	           insertCell(table, "Movement (percent)", Element.ALIGN_RIGHT, 1, bfBold12);
	           
	           /**/
	           BigDecimal domestic = new BigDecimal(re.domesticTxnAmt);
	           domestic.setScale(2, RoundingMode.HALF_UP);
	           BigDecimal domesticCount = new BigDecimal(re.domesticTxnCount);

	           BigDecimal intInelligible = new BigDecimal(re.internationalInelligibleTxn);
	           intInelligible.setScale(2, RoundingMode.HALF_UP);
	           BigDecimal intInelligibleCount = new BigDecimal(re.internationalInelligibleCount);

	           BigDecimal optout = BigDecimal.ZERO;
	           optout = optout.add(new BigDecimal(re.dccOptoutCnp));
	           optout = optout.add(new BigDecimal(re.dccOptoutCp));
	           optout = optout.setScale(2, RoundingMode.HALF_UP);
	           BigDecimal optoutCount =BigDecimal.ZERO;
	           optoutCount = optoutCount.add(new BigDecimal(re.dccOptoutCnpCount));
	           optoutCount = optoutCount.add(new BigDecimal(re.dccOptoutCpCount));
	           
	           BigDecimal optin = BigDecimal.ZERO;
	           optin = optin.add(new BigDecimal(re.dccOptinCnp));
	           optin = optin.add(new BigDecimal(re.dccOptinCp));
	           optin = optin.setScale(2, RoundingMode.HALF_UP);
	           BigDecimal optinCount =BigDecimal.ZERO;
	           optinCount = optinCount.add(new BigDecimal(re.dccOptinCnpCount));
	           optinCount = optinCount.add(new BigDecimal(re.dccOptinCpCount));

	           BigDecimal total = BigDecimal.ZERO;
	           total = total.add(domestic);
	           total = total.add(intInelligible);
	           total = total.add(optout);
	           total = total.add(optin);
	           total.setScale(2, RoundingMode.HALF_UP);
	           if (total.intValue() == 0) total = total.ONE; // RT. divide by zero workaround
	           
	           BigDecimal totalCount = BigDecimal.ZERO;
	           totalCount = totalCount.add(domesticCount);
	           totalCount = totalCount.add(intInelligibleCount); 
	           totalCount = totalCount.add(optinCount);
	           totalCount = totalCount.add(optoutCount);
	           if (totalCount.intValue() == 0) totalCount = totalCount.ONE;
	           /**/
	           
	           insertCell(table, "Domestic (€)", Element.ALIGN_LEFT, 1, bfNormal);           
	           //insertCell(table, domestic.toString(), Element.ALIGN_RIGHT, 1, bfNormal); // Amt
	           insertCell(table, domestic.setScale(2,RoundingMode.HALF_UP).toString(), Element.ALIGN_RIGHT, 1, bfNormal); // Amt
	           insertCell(table, domesticCount.toString(), Element.ALIGN_RIGHT, 1, bfNormal); // Number
	           insertCell(table, new BigDecimal(domestic.floatValue()/total.floatValue()*100).setScale(2,RoundingMode.HALF_UP).toString()+"%", Element.ALIGN_RIGHT, 1, bfNormal); // % by value 
	           insertCell(table, new BigDecimal(domesticCount.floatValue()/totalCount.floatValue()*100).setScale(2,RoundingMode.HALF_UP).toString()+"%", Element.ALIGN_RIGHT, 1, bfNormal); // % by number
	           
	           insertCell(table, "International Ineligible ("+daCurrency+")", Element.ALIGN_LEFT, 1, bfNormal);           
	           //insertCell(table, intInelligible.toString(), Element.ALIGN_RIGHT, 1, bfNormal);
	           insertCell(table, intInelligible.setScale(2, RoundingMode.HALF_UP).toString(), Element.ALIGN_RIGHT, 1, bfNormal);           
	           insertCell(table, intInelligibleCount.toString(), Element.ALIGN_RIGHT, 1, bfNormal);
	           insertCell(table, new BigDecimal(intInelligible.floatValue()/total.floatValue()*100).setScale(2,RoundingMode.HALF_UP).toString()+"%", Element.ALIGN_RIGHT, 1, bfNormal);
	           insertCell(table, new BigDecimal(intInelligibleCount.floatValue()/totalCount.floatValue()*100).setScale(2,RoundingMode.HALF_UP).toString()+"%", Element.ALIGN_RIGHT, 1, bfNormal);
	           
	           insertCell(table, "International DCC Opt-in ("+daCurrency+")", Element.ALIGN_LEFT, 1, bfNormal);
	           //insertCell(table, optin.toString(), Element.ALIGN_RIGHT, 1, bfNormal);
	           insertCell(table, optin.toString(), Element.ALIGN_RIGHT, 1, bfNormal);
	           insertCell(table, optinCount.toString(), Element.ALIGN_RIGHT, 1, bfNormal);
	           insertCell(table, new BigDecimal(optin.floatValue()/total.floatValue()*100).setScale(2,RoundingMode.HALF_UP).toString()+"%", Element.ALIGN_RIGHT, 1, bfNormal);
	           insertCell(table, new BigDecimal(optinCount.floatValue()/totalCount.floatValue()*100).setScale(2,RoundingMode.HALF_UP).toString()+"%", Element.ALIGN_RIGHT, 1, bfNormal);
	           
	           insertCell(table, "International DCC Opt-out ("+daCurrency+")", Element.ALIGN_LEFT, 1, bfNormal);
	           //insertCell(table, optout.toString(), Element.ALIGN_RIGHT, 1, bfNormal);           
	           insertCell(table, optout.setScale(2, RoundingMode.HALF_UP).toString(), Element.ALIGN_RIGHT, 1, bfNormal);           
	           insertCell(table, optoutCount.toString(), Element.ALIGN_RIGHT, 1, bfNormal);
	           insertCell(table, new BigDecimal(optout.floatValue()/total.floatValue()*100).setScale(2,RoundingMode.HALF_UP).toString()+"%", Element.ALIGN_RIGHT, 1, bfNormal);
	           insertCell(table, new BigDecimal(optoutCount.floatValue()/totalCount.floatValue()*100).setScale(2,RoundingMode.HALF_UP).toString()+"%", Element.ALIGN_RIGHT, 1, bfNormal);
	                                 
	           insertCell(table, "Total", Element.ALIGN_LEFT, 1, bfBold12);           
	           //insertCell(table, total.toString(), Element.ALIGN_RIGHT, 1, bfNormal);
	           insertCell(table, total.setScale(2, RoundingMode.HALF_UP).toString(), Element.ALIGN_RIGHT, 1, bfNormal);
	           insertCell(table, totalCount.toString(), Element.ALIGN_RIGHT, 1, bfNormal);
	           insertCell(table, "100%", Element.ALIGN_RIGHT, 1, bfNormal);
	           insertCell(table, "100%", Element.ALIGN_RIGHT, 1, bfNormal);
	           
	           
	           
	          return table;
	    }
	  
	  private JFreeChart makePieChart() {
		  DefaultPieDataset dataset = new DefaultPieDataset( );
	      dataset.setValue("IPhone 5s", new Double( 20 ) );
	      dataset.setValue("SamSung Grand", new Double( 20 ) );
	      dataset.setValue("MotoG", new Double( 40 ) );
	      dataset.setValue("Nokia Lumia", new Double( 10 ) );

	      JFreeChart chart = ChartFactory.createPieChart(
	         "Mobile Sales",   // chart title
	         dataset,          // data
	         true,             // include legend
	         true,
	         false);
	         
	      int width = 640;   /* Width of the image */
	      int height = 480;  /* Height of the image */
	      
	      return chart;
	  }
	  
	  private ReportElements hackupAReportElements() {
		  ReportElements re = new ReportElements();
		  re.id="sadas";
		  re.dccCardholderCurrency="AUD";
		  re.dccOptinCnp=12;
		  re.dccOptinCp=232;
		  re.dccOptoutCnp=22;
		  re.domesticTxnAmt=23234;
		  re.domesticTxnCount=2;
		  return re;
	  }
	  
	  private static class ReportElements implements Comparable {
	        public String id;
	        public String merchantCurrency;
	        public String merchantSttlDate;
	        public String merchantSttlName;
	        public String merchantId;
	        public String merchantRebate;        
	        public double domesticTxnAmt;
	        public int domesticTxnCount;
	        public double internationalInelligibleTxn;
	        public int internationalInelligibleCount;
	        public double dccOptinCp;
	        public double dccOptinCnp;
	        public int dccOptinCpCount;
	        public int dccOptinCnpCount;
	        public double dccOptoutCp;
	        public double dccOptoutCnp;
	        public int dccOptoutCpCount;
	        public int dccOptoutCnpCount;
	        public String dccCardholderCurrency; // daily only
	        public String dccPerformanceTerminalIds; // daily only
	        public java.util.List vdccCardholderCurrency = new java.util.Vector(); // MTD only
	        public java.util.List vdccPerformanceTerminalIds = new java.util.Vector(); //MTD only
	        
	        
	        private void dataDump() {
	            System.out.println("#----DATA DUMP-------#");
	            System.out.println(id);
	            System.out.println(merchantCurrency);
	            System.out.println(merchantSttlName);
	            System.out.println(merchantId);
	            System.out.println(domesticTxnAmt);
	            System.out.println(domesticTxnCount);
	            System.out.println(internationalInelligibleTxn);
	            System.out.println(internationalInelligibleCount);                        
	            System.out.println(dccOptinCp);
	            System.out.println(dccOptinCnp);
	            System.out.println(dccOptinCpCount);
	            System.out.println(dccOptinCnpCount);
	            System.out.println(dccOptoutCp);
	            System.out.println(dccOptoutCpCount);
	            System.out.println(dccCardholderCurrency);
	            System.out.println(dccPerformanceTerminalIds);
	            System.out.println("#--------------------#");
	        }
	        
	        // combined optin
	        private double optin() {
	            return dccOptinCp+dccOptinCnp;
	        }
	        
	        // combined optout
	        private double optout() {
	            return dccOptoutCp+dccOptoutCnp;
	        }
	        
	        // this will be the divisor
	        private boolean isOptInPercentDivideByZero() {
	            return optin()+optout()==0;
	        }
	        
	        /**
	         * merchant rebate expressed as 'visual' percentage. actual percentage is /100
	         * @return
	         */
	        private double rebate() {
	            return new Double(merchantRebate).doubleValue()/100;
	        }

	        // compare dates for sorting this object collection
	        public int compareTo(Object other) {
	            if (new Integer(merchantSttlDate).intValue() > new Integer(((ReportElements)other).merchantSttlDate).intValue())
	                return +1;
	            else if (new Integer(merchantSttlDate).intValue() < new Integer(((ReportElements)other).merchantSttlDate).intValue()) 
	                return -1;
	            else return 0;
	        }
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
