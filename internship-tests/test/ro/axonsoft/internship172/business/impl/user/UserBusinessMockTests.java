package ro.axonsoft.internship172.business.impl.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.time.Instant;

import javax.inject.Inject;

import org.junit.Test;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.google.common.collect.ImmutableList;

import ro.axonsoft.internship172.business.api.user.UserBusiness;
import ro.axonsoft.internship172.data.api.user.MdfUserEntity;
import ro.axonsoft.internship172.data.api.user.UserDao;
import ro.axonsoft.internship172.data.api.user.UserEntity;
import ro.axonsoft.internship172.data.api.user.UserEntityGet;
import ro.axonsoft.internship172.data.tests.RecrutareBusinessMockTests;
import ro.axonsoft.internship172.model.error.BusinessException;
import ro.axonsoft.internship172.model.error.ErrorProperties;
import ro.axonsoft.internship172.model.error.ImtErrorProperties;
import ro.axonsoft.internship172.model.user.ImtUser;
import ro.axonsoft.internship172.model.user.ImtUserCreate;
import ro.axonsoft.internship172.model.user.ImtUserCreateResult;
import ro.axonsoft.internship172.model.user.MdfUser;
import ro.axonsoft.internship172.model.user.MdfUserRecord;
import ro.axonsoft.internship172.model.user.UserCreateResult;
import ro.axonsoft.internship172.model.user.UsernameDoesNotExistErrorSpec;
import ro.axonsoft.internship172.model.user.UsernmTakenErrorSpec;

public class UserBusinessMockTests extends RecrutareBusinessMockTests {

	@Inject
	private UserBusiness userBusiness;

	@MockBean
	private UserDao userDao;

	@Test
	public void testCreateUser_success() throws Exception {
		final Instant createInstant = Instant.parse("2017-08-03T11:41:00.00Z");
		pushInstant(createInstant);
		final UserEntity userEntity = MdfUserEntity.create().setPassword("285d5c2472da8ab36eafa627023df5a7")
				.setRecord(MdfUserRecord.create().setBasic(MdfUser.create().setEmail("bogdan.span@axonsoft.ro")
						.setFirstName("Bogdan").setLastName("SPAN").setUsername("bogdan")));
		given(userDao.addUser(userEntity)).willReturn(1);
		final UserCreateResult userCreateResult = userBusiness
				.createUser(
						ImtUserCreate
								.builder().basic(ImtUser.builder().firstName("bogdAn").lastName("sPAn")
										.username("BOGDAN").email("bogdan.span@axonsoft.ro").build())
								.password("bogdanel").build());
		assertThat(userCreateResult).isEqualTo(ImtUserCreateResult.builder().basic(ImtUser.builder().firstName("Bogdan")
				.lastName("SPAN").username("bogdan").email("bogdan.span@axonsoft.ro").build()).build());
		verify(userDao).addUser(userEntity);
	}

	@Test
	public void testCreateUser_fail_usernameExists() throws Exception {
		final Instant createInstant = Instant.parse("2017-08-03T11:41:00.00Z");
		pushInstant(createInstant);
		given(userDao.getUser(isA(UserEntityGet.class))).willReturn(ImmutableList.of(MdfUserEntity.create()));
		ErrorProperties errorProperties;
		try {
			userBusiness
					.createUser(
							ImtUserCreate
									.builder().basic(ImtUser.builder().firstName("bogdAn").lastName("sPAn")
											.username("bogdan").email("bogdan.span@axonsoft.ro").build())
									.password("bogdanel").build());
			errorProperties = null;
		} catch (final BusinessException e) {
			errorProperties = e.getProperties();
		}
		// nu se face insert
		verify(userDao, never()).addUser(isA(UserEntity.class));
		assertThat(errorProperties)
				.isEqualTo(ImtErrorProperties.builder().key(UsernmTakenErrorSpec.USERNM_TAKEN.getKey())
						.addVar(UsernmTakenErrorSpec.Var.USRNM.getName(), "bogdan").build());
	}

	@Test
	public void testLogin_Fail_UsernWrong() throws Exception {
		final Instant createInstant = Instant.parse("2017-08-03T11:41:00.00Z");
		pushInstant(createInstant);
		final UserEntity userEntity = MdfUserEntity.create().setPassword("285d5c2472da8ab36eafa627023df5a7")
				.setRecord(MdfUserRecord.create().setBasic(MdfUser.create().setEmail("bogdan.span@axonsoft.ro")
						.setFirstName("Bogdan").setLastName("SPAN").setUsername("bogdan")));
		given(userDao.addUser(userEntity)).willReturn(1);
		ErrorProperties errorProperties;
		try {
			userBusiness.loginUser("bogdaaaa", "bagdanel");
			errorProperties = null;
		} catch (final BusinessException e) {
			errorProperties = e.getProperties();
		}
		// verify(userDao, never()).updateUser(isA(UserEntityUpdate.class));
		assertThat(errorProperties).isEqualTo(
				ImtErrorProperties.builder().key(UsernameDoesNotExistErrorSpec.USERNM_DOES_NOT_EXIST.getKey())
						.addVar(UsernameDoesNotExistErrorSpec.Var.USRNM.getName(), "bogdaaaa").build());
	}

}
