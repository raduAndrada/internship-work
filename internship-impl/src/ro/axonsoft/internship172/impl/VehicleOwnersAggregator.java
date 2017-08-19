package ro.axonsoft.internship172.impl;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import com.google.common.collect.Maps;

import ro.axonsoft.internship172.model.api.VehicleOwnerRecord;

/**
 * Clasa de agregare care calculeaza rezultatele
 *
 * @author intern
 *
 */
public class VehicleOwnersAggregator implements Serializable {

	private static final long serialVersionUID = 1L;
	private final Date referenceDate;
	private Integer evenCount;
	private Integer oddCount;
	private final Map<String, Integer> foreigners;
	private Integer otherCountyRecordsCount;

	/**
	 * Constructorul primeste ca argument doar data de referinta poate fi o alta
	 * data decat cea curenta
	 *
	 * @param referenceDate
	 *            data luata in considerarea la efectuarea calculelor
	 */
	public VehicleOwnersAggregator(final Date referenceDate) {
		this.referenceDate = referenceDate;
		oddCount = 0;
		evenCount = 0;
		otherCountyRecordsCount = 0;
		foreigners = Maps.newHashMap();

	}

	/**
	 * Metoda prin care se efectuaza calculele
	 *
	 * @param record
	 *            inregistrarea ce contine rezultatele parsarii unei linii din
	 *            fisierul .csv de intrare
	 * @return instanta curenta
	 */

	public VehicleOwnersAggregator aggregate(final VehicleOwnerRecord record) {

		try {
			//verifica daca numarul de inmatriculare este par sau impar
			//incrementeaza variabila de clasa corespunzatoare
			record.getRegPlate().getJudet();
			if (record.getRegPlate().getDigits() % 2 == 1) {
				oddCount++;
			} else {
				evenCount++;
			}

			//verifica daca nu au trecut 30 de zile de la schimbarea domiciliului unui cetatean
			//si daca numarul de inmatriculare nu a fost schimbat
			if (!record.getIdCard().getJudet().equals(record.getRegPlate().getJudet())) {
				final Calendar cal = Calendar.getInstance();
				cal.setTime(referenceDate);
				cal.add(Calendar.DATE, -30);
				final Date result = cal.getTime();
				System.out.println("-----------");
				System.out.println(record.getIdCardIssueDate());
				if (record.getIdCardIssueDate().before(result)) {
					otherCountyRecordsCount++;
				}
			}
			//parsarea cartii de identitate
			record.getIdCard().getJudet();

		}catch (final NullPointerException e) {

	          //numararea cetatenilor straini si salvarea lor intr-o harta cu numarul lor pentru fiecare judet
            if (foreigners.get(record.getIdCard().getJudet().toString()) == null) {
                foreigners.put(record.getIdCard().getJudet().toString(), 1);
            } else {
                Integer nb = foreigners.get(record.getIdCard().getJudet().toString());
                nb++;
                foreigners.replace(record.getIdCard().getJudet().toString(), nb);
            }

        }

		return this;

	}

	public Integer getOddToEvenRatio() {
		return (int) ((double) oddCount / (double) evenCount * 100);
	}

	public Map<String, Integer> getUnregCarsCountByJud() {
		return foreigners;
	}

	public Integer getPassedRegChangeDueDate() {

		return otherCountyRecordsCount;
	}

	public Date getReferenceDate() {
		return referenceDate;
	}

	public Integer getEvenCount() {
		return evenCount;
	}

	public Integer getOddCount() {
		return oddCount;
	}

}
