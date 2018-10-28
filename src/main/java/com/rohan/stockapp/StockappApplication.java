package com.rohan.stockapp;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.Scope;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfWriter;

@SpringBootApplication
@EnableAsync
@EnableJpaRepositories
@EntityScan
@Profile("!test")
@Configuration
public class StockappApplication {
	
	 @Value("${filename}")
	 String fileName;
	 @Value("${marginLeft}")	 
	 Float marginLeft;
	 @Value("${marginRight}")
	 Float marginRight;
	 @Value("${marginTop}")
	 Float marginTop;
	 @Value("${marginBottom}")
	 Float marginBottom;

	public static void main(String[] args) {
		SpringApplication.run(StockappApplication.class, args);
	}
	
	@Bean
	@Scope("prototype")
	public Document document() {
		return new Document(PageSize.A4, marginLeft, marginRight, marginTop, marginBottom);
	}
	
	@Bean
	@Scope("prototype")
	public PdfWriter pdf() throws FileNotFoundException, DocumentException { 
        return PdfWriter.getInstance(document(), new FileOutputStream(
                fileName));

	}
	
}
