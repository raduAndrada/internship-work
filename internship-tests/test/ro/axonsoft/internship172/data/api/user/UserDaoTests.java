package ro.axonsoft.internship172.data.api.user;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.TimeZone;

import javax.inject.Inject;

import org.junit.Test;

import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.ExpectedDatabase;
import com.github.springtestdbunit.assertion.DatabaseAssertionMode;
import com.google.common.collect.ImmutableList;

import ro.axonsoft.internship172.data.tests.RecrutareDataTests;
import ro.axonsoft.internship172.data.tests.TestDbUtil;
import ro.axonsoft.internship172.model.base.ImtPagination;
import ro.axonsoft.internship172.model.base.SortDirection;
import ro.axonsoft.internship172.model.user.ImtUserSortCriterion;
import ro.axonsoft.internship172.model.user.MdfUser;
import ro.axonsoft.internship172.model.user.MdfUserRecord;
import ro.axonsoft.internship172.model.user.UserSortCriterionType;

public class UserDaoTests extends RecrutareDataTests {

	private static final UserEntity ANDREI = MdfUserEntity
			.create().setId(0L).setRecord(MdfUserRecord.create().setBasic(MdfUser.create().setFirstName("Andrei")
					.setLastName("MORAR").setUsername("andrei").setEmail("andrei.morar@axonsoft.ro")))
			.setPassword("andreipass");

	private static final UserEntity ALEX = MdfUserEntity
			.create().setId(1L).setRecord(MdfUserRecord.create().setBasic(MdfUser.create().setFirstName("Alexandru")
					.setLastName("DEAC").setUsername("alex").setEmail("alexandru.deac@axonsoft.ro")))
			.setPassword("alexpass");

	static {
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
	}

	@Inject
	private UserDao userDao;

	@Inject
	private TestDbUtil dbUtil;

	@Test
	@DatabaseSetup(value = "UserDaoTests-01-i.xml", type = DatabaseOperation.TRUNCATE_TABLE)
	@ExpectedDatabase(value = "UserDaoTests-01-e.xml", assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED)
	public void testAddUser_2consecutiveOnEmptyDatabase() throws Exception {
		dbUtil.setIdentity("USR", "ID", 6);
		userDao.addUser(MdfUserEntity.create().from(ANDREI).setId(null));
		userDao.addUser(MdfUserEntity.create().from(ALEX).setId(null));
	}

	@Test
	@DatabaseSetup("UserDaoTests-02-i.xml")
	public void testGetUser_2noPag_orderCrtTmsDesc() {
		assertThat(userDao.getUser(ImtUserEntityGet.builder()
				.addSort(ImtUserSortCriterion.of(UserSortCriterionType.F_NM, SortDirection.ASC)).build()))
						.isEqualTo(ImmutableList.of(ALEX, ANDREI));
	}

	@Test
	@DatabaseSetup("UserDaoTests-02-i.xml")
	public void testGetUser_2samePag_orderUsrnm() {
		assertThat(userDao.getUser(ImtUserEntityGet.builder()

				.sort(ImmutableList.of(ImtUserSortCriterion.builder().criterion(UserSortCriterionType.USRNM)
						.direction(SortDirection.ASC).build()))
				.pagination(ImtPagination.of(1, 3)).build())).isEqualTo(ImmutableList.of(ALEX, ANDREI));
	}

	@Test
	@DatabaseSetup("UserDaoTests-02-i.xml")
	public void testGetUser_difPag_orderCrtTmsAsc() {
		assertThat(userDao
				.getUser(ImtUserEntityGet.builder().criteria(ImtUserEntityCriteria.builder().addIdIncl(0L).build())
						.pagination(ImtPagination.of(1, 1)).build())).isEqualTo(ImmutableList.of(ANDREI));
	}

	@Test
	@DatabaseSetup("UserDaoTests-02-i.xml")
	public void testGetUser_BuUsername_orderUsrNmAsc() {
		assertThat(userDao.getUser(ImtUserEntityGet.builder()
				.sort(ImmutableList.of(ImtUserSortCriterion.builder().criterion(UserSortCriterionType.USRNM)
						.direction(SortDirection.ASC).build()))
				.pagination(ImtPagination.of(1, 1)).search("nDr").build())).isEqualTo(ImmutableList.of(ALEX));
	}

}
