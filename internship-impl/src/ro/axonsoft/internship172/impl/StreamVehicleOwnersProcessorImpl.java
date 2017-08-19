package ro.axonsoft.internship172.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeParseException;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;

import com.google.common.collect.Sets;

import ro.axonsoft.internship172.model.api.ImtVehicleOwnerParseError;
import ro.axonsoft.internship172.model.api.ImtVehicleOwnerRecord;
import ro.axonsoft.internship172.model.api.ImtVehicleOwnersProcessResult;
import ro.axonsoft.internship172.model.api.InvalidRoIdCardException;
import ro.axonsoft.internship172.model.api.InvalidRoRegPlateException;
import ro.axonsoft.internship172.model.api.RoIdCardParser;
import ro.axonsoft.internship172.model.api.RoIdCardProperties;
import ro.axonsoft.internship172.model.api.RoRegPlateParser;
import ro.axonsoft.internship172.model.api.RoRegPlateProperties;
import ro.axonsoft.internship172.model.api.StreamVehicleOwnersProcessor;
import ro.axonsoft.internship172.model.api.VehicleOwnerParseError;
import ro.axonsoft.internship172.model.api.VehicleOwnerRecord;
import ro.axonsoft.internship172.model.api.VehicleOwnersMetrics;
import ro.axonsoft.internship172.model.api.VehicleOwnersProcessResult;
import ro.axonsoft.internship172.model.api.VehicleOwnersProcessor;

public class StreamVehicleOwnersProcessorImpl implements StreamVehicleOwnersProcessor {

	private final RoRegPlateParser roRegPlateParser;
	private final RoIdCardParser roIdCardParser;
	private final VehicleOwnersProcessor processor;

	public StreamVehicleOwnersProcessorImpl(final RoRegPlateParser roRegPlateParser,
			final RoIdCardParser roIdCardParser, final VehicleOwnersProcessor processor) {
		this.roRegPlateParser = roRegPlateParser;
		this.roIdCardParser = roIdCardParser;
		this.processor = processor;
	}

	@Override
	public void process(final InputStream ciCarRegNbInputStream, final OutputStream processResultOutputStream)
			throws IOException {

		// citirea din input stream
		final BufferedReader br = new BufferedReader(new InputStreamReader(ciCarRegNbInputStream));
		final VehicleOwnerRecordIterator recordsIterator = new VehicleOwnerRecordIterator(br);

		VehicleOwnersMetrics metrics;
		try {
			metrics = processor.process(recordsIterator);
		} catch (final IOExceptionRuntimeWrapper e) {
			throw (IOException) e.getCause();
		}

		final VehicleOwnersProcessResult streamResult = ImtVehicleOwnersProcessResult.builder().metrics(metrics)
				.errors(recordsIterator.errors).build();

		// serializarea obiectului de iesire
		final ObjectOutputStream objectOutputStream = new ObjectOutputStream(processResultOutputStream);
		objectOutputStream.writeObject(streamResult);
		objectOutputStream.close();

	}

	// singleton pentru a obtine o harta ordonata
	private enum ErrorComparator implements Comparator<VehicleOwnerParseError> {
		ERROR_COMPARATOR;

		@Override
		public int compare(final VehicleOwnerParseError o1, final VehicleOwnerParseError o2) {
			int result = o1.getLine().compareTo(o2.getLine());
			if (result == 0) {
				result = o1.getType().compareTo(o2.getType());
			}
			return result;
		}

	}

	private class VehicleOwnerRecordIterator implements Iterator<VehicleOwnerRecord> {

		private final BufferedReader reader;
		private VehicleOwnerRecord currentRecord;
		private boolean currentRecordRead;
		private final Set<VehicleOwnerParseError> errors = Sets.newTreeSet(ErrorComparator.ERROR_COMPARATOR);
		private int lineNumber = 0;

		public VehicleOwnerRecordIterator(final BufferedReader reader) {
			this.reader = reader;
		}

		@Override
		public boolean hasNext() {
			readCurrentRecord();
			return currentRecord != null;
		}

		@Override
		public VehicleOwnerRecord next() {
			readCurrentRecord();
			if (currentRecord == null) {
				throw new IllegalStateException("Past last element");
			}
			try {
				return currentRecord;
			} finally {
				currentRecordRead = false;
			}
		}

		private void readCurrentRecord() {
			if (!currentRecordRead) {
				currentRecord = readNextValidRecord();
				currentRecordRead = true;
			}
		}

		/**
		 * Metoda recursiva pentru citirea datelor dintr-un fisier
		 *
		 * @return un record cu datele citite din fisier
		 * @throws IOExceptionRuntimeWrapper
		 *             - citirea din fisier provoaca eroare IOException -- care nu e de
		 *             tip RuntimeException si trebuie tratata. Pentru a nu trata
		 *             eroarea in blocuri cu try catch , exista un wrapper care extinde
		 *             RuntimeException si arunca o eroare de tip IOException
		 *
		 */
		private VehicleOwnerRecord readNextValidRecord() throws IOExceptionRuntimeWrapper {
			lineNumber++;

			/**
			 * Citirea din fisier, un string pentru linia curenta daca apare o linie
			 * invalida arunca eroarea IOExceptionRuntimeWrapper daca linia e null atunci
			 * procesarea se incheie -> returneaza null daca linia e goala -> citeste linia
			 * urmatoare valida
			 */
			String linieAux;
			try {
				linieAux = reader.readLine();
			} catch (final IOException e1) {
				throw new IOExceptionRuntimeWrapper(e1);
			}
			if (linieAux == null) {
				return null;
			} else if (linieAux.trim().isEmpty()) {
				return readNextValidRecord();
			}

			/**
			 * Parsarea campurilor existe pe o linie daca linie nu are cel putin 1 camp
			 * atunci este invalida si nu trebuie parsata -> se trece la linia urmatoare din
			 * fisierul de intrare
			 */
			final String[] linie = linieAux.split(";");
			if (linie.length < 2 || linie[0].length() == 0 || linie[1].length() == 0) {
				errors.add(ImtVehicleOwnerParseError.of(lineNumber, 0));
				return readNextValidRecord();
			} else {
				/**
				 * Linia poate fi parsata atunci se initilizeaza un obiect cu proprietatiile
				 * unei carti de identitate si se verifica daca linia din fisier contine o carte
				 * de identitate valida Daca cartea de identitate este invalida se adauga o
				 * eroare in lista de erori si se reinitializeaza obiectul cu proprietatiile
				 * unei carti de identitate la null altfel obiectul este setate la valoarea
				 * proprietatiilor cartii de ID
				 */
				RoIdCardProperties roIdCardProperties = null;
				try {

					roIdCardProperties = roIdCardParser.parseIdCard(linie[0]);
				} catch (final InvalidRoIdCardException e) {
					errors.add(ImtVehicleOwnerParseError.of(lineNumber, 1));
					roIdCardProperties = null;
				}

				/**
				 * Se verifica daca data citita din fisier este sau nu valida constrangeriile
				 * sunt impuse de formatul datii si de valoarea acesteia apar erori de parsare
				 * si erori pentru ca data este invalida
				 */
				Date data = null;
				try {
					data = Date.from(LocalDate.parse(linie[1]).atStartOfDay(ZoneId.systemDefault()).toInstant());

					final Date tooOld = Date
							.from(LocalDate.parse("1899-01-01").atStartOfDay(ZoneId.systemDefault()).toInstant());

					if (data == null || data.before(tooOld)) {
						errors.add(ImtVehicleOwnerParseError.of(lineNumber, 2));
					}
				} catch (final DateTimeParseException e) {
					errors.add(ImtVehicleOwnerParseError.of(lineNumber, 2));

				}
				RoRegPlateProperties roRegPlateProperties = null;

				/**
				 * Initializarea unui obiect cu proprietatiile unui numar de inmatriculare la
				 * null verificarea lungimii liniei inainte de parsare daca linia nu contine un
				 * numar de inmatriculare nu se incearca parsarea campului daca numarul este
				 * strain se reseteaza obiectul cu proprietatiile numarului la null altfel
				 * obiectul va contine proprietatiile numarului curent
				 */
				if (linie.length >= 3) {
					try {
						roRegPlateProperties = roRegPlateParser.parseRegistrationPlate(linie[2]);
					} catch (final InvalidRoRegPlateException e) {

						roRegPlateProperties = null;
					}
				}
				/**
				 * Ca o linie sa fie valida, luata in considerare pentru calculele finale ea
				 * trebuie sa contina proprietatiile unei carti de identitate romanesti si o
				 * data valida
				 */
				if (roIdCardProperties != null && data != null) {
					return ImtVehicleOwnerRecord.builder().idCard(roIdCardProperties).regPlate(roRegPlateProperties)
							.idCardIssueDate(data).build();
				} else {
					return readNextValidRecord();
				}

			}

		}

	}

	/**
	 * @author intern Clasa statica wrapper pentru IOException
	 */

	private static class IOExceptionRuntimeWrapper extends RuntimeException {

		private static final long serialVersionUID = 1L;

		public IOExceptionRuntimeWrapper(final IOException cause) {
			super(cause);
		}

	}
}