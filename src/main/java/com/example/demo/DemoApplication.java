package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RestController;

@RestController
//@SpringBootApplication(scanBasePackages = "com.example.demo.security")
@SpringBootApplication
public class DemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
		//TODO:
		//	DONE: --1.Dokonczyc pobieranie z API ksiazek i okladek--
		//	DONE: --2.Stworzyc admin page do zarzadzania ksiazkami--
		//	ABANDONED: --3.Rozszerzyc strone o dodatkowe profile autorow i ich ksiazek--
		//	DONE: --4.Dodac system recenzji i ocen--
		//	ABANDONED: --5.Zarzadzanie zamowieniami poprawic (dodac timer na zamowieniu kazdym do ktorego zostanie ono zrealizowane)--
		//	ABANDONED: --6.Powiekszyc mozliwosci w koncie uzytkownika i dodac mozliwosc odwiedzania innych kont uzytkownikow--
		//	7.Dodac zakladki z Bestsellerami nowymi ksiazkami i specjalnymi ofertami. Zmienic model ksiazki.
		//	8.Upiekszyc wyglad strony. Dodac jakiegos JSa
	}

}
