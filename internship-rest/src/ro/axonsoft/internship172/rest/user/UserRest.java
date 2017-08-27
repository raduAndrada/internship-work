package ro.axonsoft.internship172.rest.user;

import java.util.List;

import javax.inject.Inject;

import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ro.axonsoft.internship172.business.api.user.UserBusiness;
import ro.axonsoft.internship172.model.base.ImtPagination;
import ro.axonsoft.internship172.model.error.BusinessException;
import ro.axonsoft.internship172.model.user.ImtUserGet;
import ro.axonsoft.internship172.model.user.ImtUserUpdate;
import ro.axonsoft.internship172.model.user.ImtUserUpdateProperties;
import ro.axonsoft.internship172.model.user.LoginUser;
import ro.axonsoft.internship172.model.user.User;
import ro.axonsoft.internship172.model.user.UserCreate;
import ro.axonsoft.internship172.model.user.UserCreateResult;
import ro.axonsoft.internship172.model.user.UserGetResult;
import ro.axonsoft.internship172.model.user.UserRecord;
import ro.axonsoft.internship172.model.user.UserSortCriterionType;
import ro.axonsoft.internship172.model.user.UserUpdateProperties;
import ro.axonsoft.internship172.model.user.UserUpdateResult;
import ro.axonsoft.internship172.rest.util.RestUtil;

@RestController
@RequestMapping(value = "/v1/users")
public class UserRest {

	private UserBusiness userBusiness;

	@Inject
	public void setUserBusiness(final UserBusiness userBusiness) {
		this.userBusiness = userBusiness;
	}

	@RequestMapping(method = RequestMethod.GET)
	@Transactional
	public ResponseEntity<UserGetResult> getUsers(@RequestParam(defaultValue = "1") final Integer page,
			@RequestParam(defaultValue = "20") final Integer pageSize,
			@RequestParam(defaultValue = "USRNM:ASC") final List<String> sort,
			@RequestParam(value = "search", required = false) final String search) {

		final UserGetResult userGetResult = userBusiness.getUsers(ImtUserGet
				.builder().pagination(ImtPagination.of(page, pageSize)).sort(RestUtil.parseSort(sort,
						UserSortCriterionType.class, UserSortCriterionCreator.USER_SORT_CRITERION_CREATOR))
				.search(search).build());

		return ResponseEntity.ok(userGetResult);
	}

	@RequestMapping(path = "/{username}", method = RequestMethod.GET)
	public ResponseEntity<UserRecord> getUser(@PathVariable final String username) {
		final UserGetResult userGetResult = userBusiness.getUsers(ImtUserGet.builder().username(username).build());
		if (userGetResult.getCount() == 0) {
			return ResponseEntity.notFound().build();
		} else {
			return ResponseEntity.ok(userGetResult.getList().get(0));
		}
	}

	@RequestMapping(method = RequestMethod.POST)
	@Transactional
	public ResponseEntity<UserCreateResult> postUser(@RequestBody final UserCreate userCreate)
			throws BusinessException {
		final UserCreateResult created = userBusiness.createUser(userCreate);
		return ResponseEntity.ok(created);
	}

	@RequestMapping(path = "/{username}", method = RequestMethod.PUT)
	public ResponseEntity<UserUpdateResult> putUser(@PathVariable final String username,
			@RequestBody final UserUpdateProperties userUpdateProperties) throws BusinessException {

		final UserUpdateResult updated = userBusiness.updateUser(ImtUserUpdate.builder().username(username)
				.properties(ImtUserUpdateProperties.builder().firstName(userUpdateProperties.getFirstName())
						.lastName(userUpdateProperties.getLastName()).email(userUpdateProperties.getEmail()).build())
				.build());
		return ResponseEntity.ok(updated);
	}

	@RequestMapping(path = "/check-password", method = RequestMethod.POST)
	public ResponseEntity<User> postUserCheckPassword(@RequestBody final LoginUser loginUser) throws BusinessException {
		final User usr = userBusiness.loginUser(loginUser.getUsername(), loginUser.getPassword());
		return ResponseEntity.ok(usr);
	}
}
