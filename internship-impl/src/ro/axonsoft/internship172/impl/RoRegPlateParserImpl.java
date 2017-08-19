package ro.axonsoft.internship172.impl;

import java.io.Serializable;
import java.util.regex.Pattern;

import com.google.common.base.CharMatcher;

import ro.axonsoft.internship172.model.api.ImtRoRegPlateProperties;
import ro.axonsoft.internship172.model.api.InvalidRoRegPlateException;
import ro.axonsoft.internship172.model.api.Judet;
import ro.axonsoft.internship172.model.api.RoRegPlateParser;
import ro.axonsoft.internship172.model.api.RoRegPlateProperties;

public class RoRegPlateParserImpl implements RoRegPlateParser, Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public RoRegPlateProperties parseRegistrationPlate(final String registrationPlate)
			throws InvalidRoRegPlateException {
		// copia sirului de caractere primit
		// scoaterea spatiilor albe
		// trecerea in litere mari
		if (registrationPlate == null) {
			throw new InvalidRoRegPlateException();
		}
		final String aux = registrationPlate.replaceAll("\\s+", "").toUpperCase();

		final Pattern p = Pattern.compile("[^a-zA-Z0-9]");
		final boolean hasSpecialChar = p.matcher(aux).find();

		if (aux.isEmpty()) {
			throw new InvalidRoRegPlateException();
		}

		// gasirea primei aparitii a unui cifre
		final String theDigits = CharMatcher.digit().retainFrom(aux);

		// selectarea subsirului care incepe cu secventa de cifre din numarul de
		// inmatriculare
		final String trimmedDigits = CharMatcher.javaLetter().trimLeadingFrom(aux);

		// selectarea subsirului care se termina la ultima cifra din sirul de intrare
		final String theJudetTemp = CharMatcher.javaLetter().trimTrailingFrom(aux);

		// scoaterea numerelor din secventele selectate anterior
		final String theJudet = CharMatcher.digit().removeFrom(theJudetTemp);
		final String theLetters = CharMatcher.digit().removeFrom(trimmedDigits);
		Short digits = null;
		if (!theDigits.isEmpty()) {
			digits = Short.valueOf(theDigits);
		}

		// maparea pe judet
		Judet jd = null;

		// stringul care ar trebui sa contina numele judetului poate fi eronat => numar
		// de inmatriculare invalid
		try {
			jd = Judet.valueOf(theJudet);

		} catch (final IllegalArgumentException e) {
			throw new InvalidRoRegPlateException();
		}

		// numere din capitala pot contine chiar si 3 cifre
		int limit = 2;
		if (jd == Judet.B && theDigits.length() == 3) {
			limit = 3;
		}

		// verificarea pentru lungimea argumentelor
		if (theLetters.length() != 3 || hasSpecialChar || theDigits.length() != limit || digits == null) {
			throw new InvalidRoRegPlateException();
		}
		return ImtRoRegPlateProperties.builder().judet(jd).letters(theLetters).digits(digits).build();

	}

}
