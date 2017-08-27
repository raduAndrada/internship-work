package ro.axonsoft.internship172.business.impl;

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

import ro.axonfost.internship172.business.api.result.ResultBusiness;
import ro.axonsoft.internship172.data.api.result.MdfResultEntity;
import ro.axonsoft.internship172.data.api.result.ResultEntity;
import ro.axonsoft.internship172.data.api.vehicleOwner.ImtVehicleOwnerEntityCount;
import ro.axonsoft.internship172.data.api.vehicleOwner.ImtVehicleOwnerEntityCriteria;
import ro.axonsoft.internship172.data.api.vehicleOwner.ImtVehicleOwnerEntityGet;
import ro.axonsoft.internship172.data.api.vehicleOwner.VehicleOwnerDao;
import ro.axonsoft.internship172.data.api.vehicleOwner.VehicleOwnerEntity;
import ro.axonsoft.internship172.data.exceptions.DatabaseIntegrityViolationException;
import ro.axonsoft.internship172.model.api.DbVehicleOwnersProcessor;
import ro.axonsoft.internship172.model.api.ImtVehicleOwnerRecord;
import ro.axonsoft.internship172.model.api.InvalidRoIdCardException;
import ro.axonsoft.internship172.model.api.InvalidRoRegPlateException;
import ro.axonsoft.internship172.model.api.Judet;
import ro.axonsoft.internship172.model.api.RoIdCardParser;
import ro.axonsoft.internship172.model.api.RoIdCardProperties;
import ro.axonsoft.internship172.model.api.RoRegPlateParser;
import ro.axonsoft.internship172.model.api.RoRegPlateProperties;
import ro.axonsoft.internship172.model.api.VehicleOwnerRecord;
import ro.axonsoft.internship172.model.api.VehicleOwnersMetrics;
import ro.axonsoft.internship172.model.api.VehicleOwnersProcessor;
import ro.axonsoft.internship172.model.base.ImtPagination;
import ro.axonsoft.internship172.model.base.MdfBatch;
import ro.axonsoft.internship172.model.base.SortDirection;
import ro.axonsoft.internship172.model.result.ImtResultCreate;
import ro.axonsoft.internship172.model.result.ImtResultErrorBasic;
import ro.axonsoft.internship172.model.result.ImtResultErrorRecord;
import ro.axonsoft.internship172.model.result.ImtResultUnregCarsCountByJudBasic;
import ro.axonsoft.internship172.model.result.ImtResultUnregCarsCountByJudRecord;
import ro.axonsoft.internship172.model.result.MdfResultBasic;
import ro.axonsoft.internship172.model.result.MdfResultRecord;
import ro.axonsoft.internship172.model.result.ResultErrorBasic;
import ro.axonsoft.internship172.model.result.ResultErrorRecord;
import ro.axonsoft.internship172.model.result.ResultUnregCarsCountByJudRecord;
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

	private VehicleOwnerDao vhoDao;
	private ResultBusiness resBusiness;

	@Inject
	public void setResDao(ResultBusiness resDao) {
		this.resBusiness = resDao;
	}

	/**
	 * Refolosirea parserelor si a procesorului Rezultatul va fi obtinut de acelasi
	 * procesor ca si pentru input de tip fisier csv
	 */

	private final RoRegPlateParser roRegPlateParser;
	private final RoIdCardParser roIdCardParser;
	private final VehicleOwnersProcessor processor;

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

		Iterable<VehicleOwnerEntity> vehicleOwnersRecordsIterable = Lists.newArrayList();
		final Integer vehicleOwnersCount = vhoDao.countVehicleOwner(ImtVehicleOwnerEntityCount.builder()
				.criteria(ImtVehicleOwnerEntityCriteria.builder().addIdBatchSelect(batchId).build()).build());
		for (int startIndex = 0; startIndex < vehicleOwnersCount; startIndex += pageSize) {
			final List<VehicleOwnerEntity> vehicleOwnersList = vhoDao
					.getVehicleOwner(ImtVehicleOwnerEntityGet.builder()
							.sort(ImmutableList.of(ImtVehicleOwnerSortCriterion.builder()
									.criterion(VehicleOwnerSortCriterionType.RO_ID_CARD)

									.direction(SortDirection.ASC).build()))
							.criteria(ImtVehicleOwnerEntityCriteria.builder().addIdBatchSelect(batchId).build())
							.pagination(ImtPagination.builder().pageSize(pageSize).offset(new Long(startIndex)).build())
							.build());
			vehicleOwnersRecordsIterable = Iterables.concat(vehicleOwnersRecordsIterable, vehicleOwnersList);
		}

		final VehicleOwnerRecordIterator recordsIterator = new VehicleOwnerRecordIterator(
				vehicleOwnersRecordsIterable.iterator());
		final VehicleOwnersMetrics metrics = processor.process(recordsIterator);

		final List<ResultUnregCarsCountByJudRecord> unregCarsCountByJud = Lists.newArrayList();

		metrics.getUnregCarsCountByJud()
				.forEach((k, v) -> unregCarsCountByJud.add(ImtResultUnregCarsCountByJudRecord.builder().basic(
						ImtResultUnregCarsCountByJudBasic.builder().judet(Judet.valueOf(k)).unregCarsCount(v).build())
						.build()));

		final List<ResultErrorRecord> resultErrors = Lists.newArrayList();

		recordsIterator.errors
				.forEach(
						element -> resultErrors.add(ImtResultErrorRecord
								.builder().resultErrorId(null).basic(ImtResultErrorBasic.builder()
										.type(element.getType()).vehicleOwnerId(element.getVehicleOwnerId()).build())
								.build()));

		final ResultEntity RES = MdfResultEntity.create()
				.setRecord(MdfResultRecord.create()
						.setBasic(MdfResultBasic.create().setOddToEvenRatio(metrics.getOddToEvenRatio())
								.setPassedRegChangeDueDate(metrics.getPassedRegChangeDueDate())
								.setResultProcessTime(new java.sql.Timestamp(
										LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())))
						.setErrors(resultErrors).setUnregCars(unregCarsCountByJud)
						.setBatch(MdfBatch.create().setBatchId(batchId)))
				.setResultMetricsId(null);

		try {
			resBusiness.createResult(
					ImtResultCreate.builder().basic(RES.getRecord().getBasic()).errors(RES.getRecord().getErrors())
							.unregCars(RES.getRecord().getUnregCars()).batch(RES.getRecord().getBatch()).build());
		} catch (final DatabaseIntegrityViolationException e) {
			throw new DatabaseIntegrityViolationException(e);
		}
	}

	// singleton pentru a obtine o harta ordonata
	private enum ErrorComparator implements Comparator<ResultErrorBasic> {
		ERROR_COMPARATOR;

		@Override
		public int compare(final ResultErrorBasic o1, final ResultErrorBasic o2) {
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
		private final Set<ResultErrorBasic> errors = Sets.newTreeSet(ErrorComparator.ERROR_COMPARATOR);

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

					errors.add(ImtResultErrorBasic.builder().type(0).vehicleOwnerId(vehicleOwnerId).build());
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

						errors.add(ImtResultErrorBasic.builder().vehicleOwnerId(vehicleOwnerId).type(1).build());
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
							errors.add(ImtResultErrorBasic.builder().type(2).vehicleOwnerId(vehicleOwnerId).build());
						}
					} catch (final DateTimeParseException e) {
						errors.add(ImtResultErrorBasic.builder().vehicleOwnerId(vehicleOwnerId).type(2).build());

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
