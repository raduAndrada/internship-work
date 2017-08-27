package ro.axonsoft.internship172.rest.user;

import static org.assertj.core.api.Assertions.assertThat;

import javax.inject.Inject;

import org.junit.Test;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.google.common.collect.ImmutableMap;

import ro.axonsoft.internship172.data.tests.RecrutareRestTests;
import ro.axonsoft.internship172.data.tests.TestDbUtil;
import ro.axonsoft.internship172.model.user.ImtLoginUser;
import ro.axonsoft.internship172.model.user.ImtUser;
import ro.axonsoft.internship172.model.user.ImtUserGetResult;
import ro.axonsoft.internship172.model.user.ImtUserRecord;
import ro.axonsoft.internship172.model.user.User;
import ro.axonsoft.internship172.model.user.UserGetResult;

public class UserRestTests extends RecrutareRestTests {

	@Inject
	private TestRestTemplate restTemplate;

	@Inject
	private TestDbUtil dbUtil;

	// @Test
	// @DatabaseSetup(value = "UserRestTest-01-i.xml", type =
	// DatabaseOperation.TRUNCATE_TABLE)
	// @ExpectedDatabase(value = "UserRestTest-01-e.xml", assertionMode =
	// DatabaseAssertionMode.NON_STRICT_UNORDERED)
	// public void postUser() {
	// dbUtil.setIdentity("USR", "ID", 11);
	// pushInstant(Instant.parse("2017-08-03T11:41:00.00Z"));
	// final UserCreate toCreate =
	// ImtUserCreate.builder().basic(ImtUser.builder().firstName("BoGdAn")
	// .lastName("Șpan").username("bogDAN00").email("BOGDAN.span@axonsoft.RO").build()).password("bogdanel")
	// .build();
	// final String url = "http://localhost:8000/v1/users";
	// final ResponseEntity<UserCreateResult> userCreateResult =
	// restTemplate.postForEntity(url,
	// createRqWithAuth(toCreate, "andrei"), UserCreateResult.class,
	// ImmutableMap.of());
	// assertThat(userCreateResult.getStatusCode()).isEqualTo(HttpStatus.OK);
	// assertThat(userCreateResult.getBody())
	// .isEqualTo(ImtUserCreateResult.builder().basic(ImtUser.builder().firstName("Bogdan").lastName("ȘPAN")
	// .username("bogdan").email("bogdan.span@axonsoft.ro").build()).build());
	// }

	@Test
	@DatabaseSetup("UserRestTest-02-i.xml")
	public void getUser() {
		final String url = "http://localhost:8000/v1/users?search=gDa";
		final ResponseEntity<UserGetResult> userGetResult = restTemplate.getForEntity(url, UserGetResult.class,
				ImmutableMap.of());
		assertThat(userGetResult.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(userGetResult.getBody()).isEqualTo(ImtUserGetResult.builder()
				.addList(ImtUserRecord.builder()
						.basic(ImtUser.builder().firstName("Bogdan").lastName("ȘPAN").username("bogdan")
								.email("bogdan.span@axonsoft.ro").build())
						.build())
				.count(1).pageCount(1).pagination(1, 20).build());
	}

	@Test
	@DatabaseSetup(value = "UserRestTest-02-i.xml")
	public void postUserCheckPassword() {
		final String url = "http://localhost:8000/";
		final ResponseEntity<User> loginUserGetResult = restTemplate.postForEntity(
				"http://localhost:8000/v1/users/check-password",
				createRqWithAuth(ImtLoginUser.builder().username("bogdan").password("bogdanel").build(), "andrei"),
				User.class, ImmutableMap.of());
		assertThat(loginUserGetResult.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(loginUserGetResult.getBody()).isEqualTo(ImtUser.builder().username("bogdan").build());
	}

}
