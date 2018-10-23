/*
 * HashPassword.java
 * 
 * Copyright (C) 2017 Universidad de Sevilla
 * 
 * The use of this project is hereby constrained to the conditions of the
 * TDG Licence, a copy of which you may download from
 * http://www.tdg-seville.info/License.html
 */

package utilities;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.util.Assert;

import utilities.internal.ConsoleReader;
import utilities.internal.ThrowablePrinter;

public class DateToMiliseconds {

	public static void main(final String[] args) throws IOException {
		ConsoleReader reader;
		String line;

		try {
			System.out.println("Convierte una fecha del tipo 'dd/MM/yyyy HH:mm' a milisegundos");
			System.out.println("-----------------");

			reader = new ConsoleReader();

			line = reader.readLine();
			while (!line.equals("quit")) {
				Assert.isTrue(line.matches("^\\d{2}\\/\\d{2}\\/\\d{4} \\d{2}:\\d{2}$"), "La línea no se corresponde con el formato");
				final SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm");
				final Date date = formatter.parse(line);
				System.out.println(date.getTime());
				line = reader.readLine();
			}
		} catch (final Throwable oops) {
			ThrowablePrinter.print(oops);
		}
	}
}
