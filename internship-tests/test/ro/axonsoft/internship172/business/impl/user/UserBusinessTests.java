package ro.axonsoft.internship172.business.impl.user;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;

import javax.inject.Inject;

import org.junit.Test;

import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.ExpectedDatabase;
import com.github.springtestdbunit.assertion.DatabaseAssertionMode;

import ro.axonsoft.internship172.business.api.user.UserBusiness;
import ro.axonsoft.internship172.data.tests.RecrutareBusinessTests;
import ro.axonsoft.internship172.data.tests.TestDbUtil;
import ro.axonsoft.internship172.model.user.ImtUser;
import ro.axonsoft.internship172.model.user.ImtUserCreate;
import ro.axonsoft.internship172.model.user.ImtUserCreateResult;
import ro.axonsoft.internship172.model.user.User;
import ro.axonsoft.internship172.model.user.UserCreateResult;

public class UserBusinessTests extends RecrutareBusinessTests {

	@Inject
	private UserBusiness userBusiness;

	@Inject
	private TestDbUtil dbUtil;

	@Test
	@DatabaseSetup(value = "UserBusinessTests-01-i.xml", type = DatabaseOperation.TRUNCATE_TABLE)
	@ExpectedDatabase(value = "UserBusinessTests-01-e.xml", assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED)
	public void testCreateUserOnEmptyDatabase() throws Exception {
		dbUtil.setIdentity("USR", "ID", 11);
		pushInstant(Instant.parse("2017-08-03T11:41:00.00Z"));
		final UserCreateResult userCreateResult = userBusiness
				.createUser(
						ImtUserCreate
								.builder().basic(ImtUser.builder().firstName("bogdAn").lastName("sPAn")
										.username("BOGDAN").email("bogdan.span@axonsoft.ro").build())
								.password("bogdanel").build());
		assertThat(userCreateResult).isEqualTo(ImtUserCreateResult.builder().basic(ImtUser.builder().firstName("Bogdan")
				.lastName("SPAN").username("bogdan").email("bogdan.span@axonsoft.ro").build()).build());
	}

	@Test
	@DatabaseSetup(value = "UserBusinessTests-02-i.xml")
	public void testCheck_User_forLogin_succes() throws Exception {
		pushInstant(Instant.parse("2017-08-03T11:41:00.00Z"));
		final User user = userBusiness.loginUser("bogdan", "bogdanel");
		assertThat(user).isEqualTo(ImtUser.builder().username("bogdan").build());
	}

}
