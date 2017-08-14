package ro.axonsoft.internship172.data.impl;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeParseException;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import ro.axonsoft.internship172.api.DbVehicleOwnersProcessor;
import ro.axonsoft.internship172.api.ImtVehicleOwnerRecord;
import ro.axonsoft.internship172.api.InvalidRoIdCardException;
import ro.axonsoft.internship172.api.InvalidRoRegPlateException;
import ro.axonsoft.internship172.api.Judet;
import ro.axonsoft.internship172.api.RoIdCardParser;
import ro.axonsoft.internship172.api.RoIdCardProperties;
import ro.axonsoft.internship172.api.RoRegPlateParser;
import ro.axonsoft.internship172.api.RoRegPlateProperties;
import ro.axonsoft.internship172.api.VehicleOwnerRecord;
import ro.axonsoft.internship172.api.VehicleOwnersMetrics;
import ro.axonsoft.internship172.api.VehicleOwnersProcessor;
import ro.axonsoft.internship172.data.api.vehicleOwner.ImtVehicleOwnerEntityCount;
import ro.axonsoft.internship172.data.api.vehicleOwner.ImtVehicleOwnerEntityCriteria;
import ro.axonsoft.internship172.data.api.vehicleOwner.ImtVehicleOwnerEntityGet;
import ro.axonsoft.internship172.data.api.vehicleOwner.VehicleOwnerDao;
import ro.axonsoft.internship172.data.api.vehicleOwner.VehicleOwnerEntity;
import ro.axonsoft.internship172.data.domain.ImtResultError;
import ro.axonsoft.internship172.data.domain.ImtResultMetrics;
import ro.axonsoft.internship172.data.domain.ImtResultUnregCarsCountByJud;
import ro.axonsoft.internship172.data.domain.ResultError;
import ro.axonsoft.internship172.data.domain.ResultMetrics;
import ro.axonsoft.internship172.data.domain.ResultUnregCarsCountByJud;
import ro.axonsoft.internship172.data.exceptions.DatabaseIntegrityViolationException;
import ro.axonsoft.internship172.data.mappers.ImtErrorCriteria;
import ro.axonsoft.internship172.data.mappers.ImtResultUnregCarsCriteria;
import ro.axonsoft.internship172.data.services.ResultService;
import ro.axonsoft.internship172.data.services.VehicleOwnerService;
import ro.axonsoft.internship172.model.base.ImtPagination;
import ro.axonsoft.internship172.model.base.SortDirection;
import ro.axonsoft.internship172.model.vehicleOwner.ImtVehicleOwnerSortCriterion;
import ro.axonsoft.internship172.model.vehicleOwner.VehicleOwnerSortCriterionType;

/**
 * Implementarea procesari pe baza de date
 *
 * @author Andrada
 *
 */
public class DbVehicleOwnersProcessorImpl implements DbVehicleOwnersProcessor {
	/**
	 * Se folosesc cele doua servicii - pentru extragere date de procesat si pentru
	 * punerea lor in baza de date
	 */
	private static final Logger LOG = LoggerFactory.getLogger(DbVehicleOwnersProcessor.class);

	private ResultService resultService;
	private VehicleOwnerDao vhoDao;

	/**
	 * Refolosirea parserelor si a procesorului Rezultatul va fi obtinut de acelasi
	 * procesor ca si pentru input de tip fisier csv
	 */

	private final RoRegPlateParser roRegPlateParser;
	private final RoIdCardParser roIdCardParser;
	private final VehicleOwnersProcessor processor;

	@Inject
	public void setVehicleOwnersService(final VehicleOwnerService vehicleOwnersService) {
	}

	@Inject
	public void setResultService(final ResultService resultService) {
		this.resultService = resultService;
	}

	@Inject
	public void setVehicleOwnerDao(final VehicleOwnerDao vhoDao) {
		this.vhoDao = vhoDao;
	}

	public DbVehicleOwnersProcessorImpl(final RoRegPlateParser roRegPlateParser, final RoIdCardParser roIdCardParser,
			final VehicleOwnersProcessor processor) {
		this.roRegPlateParser = roRegPlateParser;
		this.roIdCardParser = roIdCardParser;
		this.processor = processor;
	}

	@Override
	public void process(final Long batchId) {
		final Integer pageSize = 3;
		/**
		 * Procesarea se face cu obiectul vehicleOwnersRecordsIterable. Se cauta numarul
		 * de inregistrari din baza de date pentru a stii cum sa se citeasca paginile se
		 * parcurge fiecare pagina se adauga intr-un iterator Iteratorul este transmis
		 * ca argument pentru a fi procesat clasei wrapper imbricate in aceasta clasa
		 */

		LOG.info("Procesare ");

		Iterable<VehicleOwnerEntity> vehicleOwnersRecordsIterable = Lists.newArrayList();
		final Integer vehicleOwnersCount = vhoDao.countVehicleOwner(ImtVehicleOwnerEntityCount.builder()
				.criteria(ImtVehicleOwnerEntityCriteria.builder().addIdBatchSelect(batchId).build()).build());
		LOG.info("Procesarea pe pagini");
		for (int startIndex = 0; startIndex < vehicleOwnersCount; startIndex += pageSize) {
			LOG.info("Something " + vehicleOwnersCount + " " + startIndex);
			final List<VehicleOwnerEntity> vehicleOwnersList = vhoDao
					.getVehicleOwner(ImtVehicleOwnerEntityGet.builder()
							.sort(ImmutableList.of(ImtVehicleOwnerSortCriterion.builder()
									.criterion(VehicleOwnerSortCriterionType.RO_ID_CARD)

									.direction(SortDirection.ASC).build()))
							.criteria(ImtVehicleOwnerEntityCriteria.builder().addIdBatchSelect(batchId).build())
							.pagination(ImtPagination.builder().pageSize(pageSize).offset(new Long(startIndex)).build())
							.build());
			vehicleOwnersRecordsIterable = Iterables.concat(vehicleOwnersRecordsIterable, vehicleOwnersList);
			LOG.info("Procesarea paginii cu " + vehicleOwnersRecordsIterable.toString());
		}

		final VehicleOwnerRecordIterator recordsIterator = new VehicleOwnerRecordIterator(
				vehicleOwnersRecordsIterable.iterator());
		LOG.info("Total de procesare " + vehicleOwnersRecordsIterable.toString());
		final VehicleOwnersMetrics metrics = processor.process(recordsIterator);

		LOG.info("Trecerea rezultatului in formatul corespunzator ---- ");
		final List<ResultUnregCarsCountByJud> unregCarsCountByJud = Lists.newArrayList();

		metrics.getUnregCarsCountByJud().forEach((k, v) -> unregCarsCountByJud
				.add(ImtResultUnregCarsCountByJud.builder().judet(Judet.valueOf(k)).unregCarsCount(v).build()));

		final List<ResultError> resultErrors = Lists.newArrayList();

		recordsIterator.errors.forEach(element -> resultErrors.add(
				ImtResultError.builder().type(element.getType()).vehicleOwnerId(element.getVehicleOwnerId()).build()));

		final ResultMetrics resultMetrics = ImtResultMetrics.builder().batchId(batchId).resultMetricsId(null)
				.oddToEvenRatio(metrics.getOddToEvenRatio()).resultErrors(resultErrors)
				.unregCarsCountByJud(unregCarsCountByJud).passedRegChangeDueDate(metrics.getPassedRegChangeDueDate())
				.resultProcessTime(new java.sql.Timestamp(
						LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()))
				.build();

		LOG.info("Inserarea rezultatului cu errori " + resultMetrics.toString());
		try {
			resultService.insertResultMetricsWithErrors(resultMetrics, ImtErrorCriteria.of(),
					ImtResultUnregCarsCriteria.of());
		} catch (final DatabaseIntegrityViolationException e) {
			throw new DatabaseIntegrityViolationException(e);
		}
	}

	// singleton pentru a obtine o harta ordonata
	private enum ErrorComparator implements Comparator<ResultError> {
		ERROR_COMPARATOR;

		@Override
		public int compare(final ResultError o1, final ResultError o2) {
			int result = o1.getVehicleOwnerId().compareTo(o2.getVehicleOwnerId());
			if (result == 0) {
				result = o1.getType().compareTo(o2.getType());
			}
			return result;
		}

	}

	private class VehicleOwnerRecordIterator implements Iterator<VehicleOwnerRecord> {

		private VehicleOwnerRecord currentRecord;
		private final Iterator<VehicleOwnerEntity> iterator;
		private boolean currentRecordRow;
		private final Set<ResultError> errors = Sets.newTreeSet(ErrorComparator.ERROR_COMPARATOR);

		public VehicleOwnerRecordIterator(final Iterator<VehicleOwnerEntity> iterator) {
			this.iterator = iterator;
		}

		@Override
		public boolean hasNext() {
			processCurrentRow();
			return currentRecord != null;
		}

		@Override
		public VehicleOwnerRecord next() {
			processCurrentRow();
			if (currentRecord == null) {
				throw new IllegalStateException("Past last element");
			}
			try {
				return currentRecord;
			} finally {
				currentRecordRow = false;
			}
		}

		private void processCurrentRow() {
			if (!currentRecordRow) {
				currentRecord = readNextValidRecord();
				currentRecordRow = true;
			}
		}

		private VehicleOwnerRecord readNextValidRecord() {

			if (iterator.hasNext()) {
				final VehicleOwnerEntity vehicleOwner = iterator.next();
				LOG.info("Batch curent :" + vehicleOwner.getRecord().getBatch().getBatchId().toString());
				LOG.info("Elementul curent este :" + vehicleOwner.toString());
				final Long vehicleOwnerId = vehicleOwner.getVehicleOwnerId();
				/**
				 * Daca in tabela au fost inscrise valori cu null pentru cartea de identiate sau
				 * pentru data inmatricularii, inregistrarea este invalida
				 */
				if (vehicleOwner.getRecord().getBasic().getRoIdCard() == null
						|| vehicleOwner.getRecord().getBasic().getIssueDate() == null) {
					LOG.info("S-a citit o linie invalida");

					errors.add(ImtResultError.builder().type(0).vehicleOwnerId(vehicleOwnerId).build());
					return null;
				} else {

					/**
					 * O inregistrare din baza de date poate fi parsata daca proprietatiile cartii
					 * de identitate pot fi procesate Se initializeza la null si daca parser-ul
					 * reuseste parsarea sunt trecute intr-un obiect cu proprietatiile identificate,
					 * daca parser-ul nu reusete sa faca parsarea, se seteaza obiectul la null
					 */
					RoIdCardProperties roIdCardProperties = null;
					LOG.info("Inceperea procesarii cardului de identitate");
					try {

						roIdCardProperties = roIdCardParser
								.parseIdCard(vehicleOwner.getRecord().getBasic().getRoIdCard());
						LOG.info("Cardul :" + vehicleOwner.getRecord().getBasic().getRoIdCard()
								+ " a fost procesat cu succes");
					} catch (final InvalidRoIdCardException e) {
						LOG.info("Cardul :" + vehicleOwner.getRecord().getBasic().getRoIdCard() + " contine erori");

						errors.add(ImtResultError.builder().vehicleOwnerId(vehicleOwnerId).type(1).build());
						roIdCardProperties = null;
					}

					/**
					 * Se verifica daca data din tabela este valida incercand se fie parsata Daca
					 * data nu este valida atunci va ramane setata pe null, altfel va fi initilizata
					 * la valoarea din tabela Datele din tabela sunt extrase sub forma de
					 * java.sql.Date si aplicatia are o constrangere de a se folosi java.util.Date
					 * de aceea se face o conversie intre cele doua tipuri de date Erorile care pot
					 * aparea sunt inscrise in lista de erori
					 */
					Date data = null;

					try {
						LOG.info("Procesarea datei din inregistrare");
						final java.util.Date tooOld = Date
								.from(LocalDate.parse("1899-01-01").atStartOfDay(ZoneId.systemDefault()).toInstant());
						data = new Date(vehicleOwner.getRecord().getBasic().getIssueDate().getEpochSecond());
						if (data == null || data.before(tooOld)) {
							LOG.info("Data este invalida");
							errors.add(ImtResultError.builder().type(2).vehicleOwnerId(vehicleOwnerId).build());
						}
					} catch (final DateTimeParseException e) {
						errors.add(ImtResultError.builder().vehicleOwnerId(vehicleOwnerId).type(2).build());

					}
					RoRegPlateProperties roRegPlateProperties = null;
					LOG.info("Data a fost procesata cu succes");
					/**
					 * Initializarea unui obiect cu proprietatiile unui numar de inmatriculare la
					 * null verificarea lungimii liniei inainte de parsare daca linia nu contine un
					 * numar de inmatriculare nu se incearca parsarea campului daca numarul este
					 * strain se reseteaza obiectul cu proprietatiile numarului la null altfel
					 * obiectul va contine proprietatiile numarului curent
					 */
					try {
						roRegPlateProperties = roRegPlateParser
								.parseRegistrationPlate(vehicleOwner.getRecord().getBasic().getRegPlate());
						LOG.info("Procesarea numarului de inmatriculare:"
								+ vehicleOwner.getRecord().getBasic().getRegPlate() + " s-a realizat cu succes");
					} catch (final InvalidRoRegPlateException e) {
						LOG.info("Numarului de inmatriculare este invalid");
						roRegPlateProperties = null;
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

			} else {
				return null;
			}
		}
	}

}
