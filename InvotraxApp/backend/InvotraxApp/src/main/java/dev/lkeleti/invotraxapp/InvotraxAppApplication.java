package dev.lkeleti.invotraxapp;

import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class InvotraxAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(InvotraxAppApplication.class, args);
	}

	@Bean
	public ModelMapper modelMapper() {
		return new ModelMapper();
	}
	/*
	TODO InventorItem CRUD
	TODO InvoiceItem CRUD
	TODO InvoiceNumberSequence CRUD
	TODO InvoiceType CRUD
	TODO ProductType CRUD
	TODO SerialNumber CRUD
	 */
}
