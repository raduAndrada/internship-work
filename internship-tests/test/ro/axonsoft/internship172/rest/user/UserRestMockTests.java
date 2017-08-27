package ro.axonsoft.internship172.rest.user;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;
import java.io.InputStream;

import javax.sql.DataSource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import ro.axonsoft.internship172.business.api.user.UserBusiness;
import ro.axonsoft.internship172.model.user.ImtUser;
import ro.axonsoft.internship172.model.user.ImtUserCreate;
import ro.axonsoft.internship172.model.user.ImtUserCreateResult;

@RunWith(SpringRunner.class)
@WebMvcTest(UserRest.class)
public class UserRestMockTests {

	@Autowired
	private MockMvc mvc;

	@MockBean
	private UserBusiness userBusiness;

	@MockBean
	private DataSource dataSource;

	@Test
	public void postUser() throws Exception {
		given(userBusiness.createUser(ImtUserCreate.builder()
				.basic(ImtUser.builder().username("alex").email("alexandru.deac@axonsoft.ro").firstName("Alexandru")
						.lastName("DEAC").build())
				.password("alexpass").build()))
						.willReturn(ImtUserCreateResult.builder().basic(ImtUser.builder().username("alex")
								.email("alexandru.deac@axonsoft.ro").firstName("Alexandru").lastName("DEAC").build())
								.build());
		mvc.perform(post("/v1/users").content(readJson("01-i")).contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(content().json(readJson("01-e")));
	}

	private String readJson(final String fileSuffix) throws IOException {
		try (InputStream in = getClass().getResourceAsStream(getClass().getSimpleName() + "-" + fileSuffix + ".json")) {
			try (final java.util.Scanner s = new java.util.Scanner(in)) {
				s.useDelimiter("\\A");
				final String string = s.hasNext() ? s.next() : "";
				return string;
			}
		}
	}
}
