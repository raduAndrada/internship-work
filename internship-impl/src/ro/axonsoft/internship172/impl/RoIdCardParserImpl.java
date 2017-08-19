package ro.axonsoft.internship172.impl;

import java.io.Serializable;
import java.util.regex.Pattern;

import com.google.common.base.CharMatcher;

import ro.axonsoft.internship172.model.api.ImtRoIdCardProperties;
import ro.axonsoft.internship172.model.api.InvalidRoIdCardException;
import ro.axonsoft.internship172.model.api.InvalidRoIdCardSeriesException;
import ro.axonsoft.internship172.model.api.Judet;
import ro.axonsoft.internship172.model.api.RoIdCardParser;
import ro.axonsoft.internship172.model.api.RoIdCardProperties;
import ro.axonsoft.internship172.model.api.RoIdCardSeriesJudMapper;

/**
 * Implementarea interfetei de parsarea unei carti de identitate
 *
 * @author intern
 *
 */

public class RoIdCardParserImpl implements RoIdCardParser, Serializable {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private final RoIdCardSeriesJudMapper roIdCardSeriesJudMapper;

	/**
	 * Constructor care primeste un mapper pentru a determina judetul de care
	 * apartine detinatorul cartii de identitate
	 */
	public RoIdCardParserImpl(final RoIdCardSeriesJudMapper newRoIdCardSeriesJudMapper) {
		roIdCardSeriesJudMapper = newRoIdCardSeriesJudMapper;
	}

	/**
	 * Parsarea unui String cu un card de identitate intr-un obiect de tipul
	 * RoIdCardProperties arunca o exceptie daca stringul nu corespunde cu o carte
	 * de identitate romaneasca sau contine erori
	 */
	@Override
	public RoIdCardProperties parseIdCard(final String idCard) throws InvalidRoIdCardException {

		// scoaterea spatiilor din string + scrierea lui cu litere mari pentru a
		// evita
		// orice eroare ar putea aparea in cazul unor cartii scrise cu litere
		// mici si
		// spatii

		final String aux = idCard.replaceAll("\\s+", "").toUpperCase();

		// verificare daca stringul primit nu contine caractere care nu apartin
		// alfabetului

		final Pattern p = Pattern.compile("[^a-zA-Z0-9]");
		final boolean hasSpecialChar = p.matcher(aux).find();
		// s-au gasit caractere speciale sau stringul e gol => error pentru card
		// invalid
		if (hasSpecialChar || aux.isEmpty()) {
			throw new InvalidRoIdCardException();
		}

		// gasirea cifrelor
		final String theDigits = CharMatcher.digit().retainFrom(aux);
		// extragerea primelor caractere din string pana la aparitia primei
		// cifre
		final String series = CharMatcher.digit().removeFrom(aux);

		// daca judet nu are 2 litere inseamna ca este un judet invalid
		if (series.length() != 2) {
			throw new InvalidRoIdCardException();
		}

		// maparea stringului care corespunde unui judet pe judetul respectiv
		Judet judet;
		try {
			judet = roIdCardSeriesJudMapper.mapIdCardToJud(series);
		} catch (final InvalidRoIdCardSeriesException e) {
			throw new InvalidRoIdCardException(String.format("Serie invalida %s", series), e);
		}

		if (theDigits.length() != 6) {
			throw new InvalidRoIdCardException();
		}

		// incercare de parsarea a stringului cu cifrele de pe cartea de
		// identitate
		// posibile erori: apar litere sau lungimea nu e egala cu 6
		final Integer digits = Integer.valueOf(theDigits);

		// implementarea interfetei cu proprietatiile unei carti de identitate
		return ImtRoIdCardProperties.builder().judet(judet).series(series).number(digits).build();

	}
}
