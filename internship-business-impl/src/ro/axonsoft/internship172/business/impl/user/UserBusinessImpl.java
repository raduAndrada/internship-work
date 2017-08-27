package ro.axonsoft.internship172.business.impl.user;

import java.util.List;

import javax.inject.Inject;

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableSet;
import com.google.common.hash.Hashing;

import ro.axonsoft.internship172.business.api.user.UserBusiness;
import ro.axonsoft.internship172.data.api.user.ImtUserEntityCount;
import ro.axonsoft.internship172.data.api.user.ImtUserEntityCriteria;
import ro.axonsoft.internship172.data.api.user.ImtUserEntityGet;
import ro.axonsoft.internship172.data.api.user.ImtUserEntityUpdate;
import ro.axonsoft.internship172.data.api.user.MdfUserEntity;
import ro.axonsoft.internship172.data.api.user.UserDao;
import ro.axonsoft.internship172.data.api.user.UserEntity;
import ro.axonsoft.internship172.data.api.user.UserEntityCount;
import ro.axonsoft.internship172.data.api.user.UserEntityGet;
import ro.axonsoft.internship172.data.api.user.UserEntityUpdate;
import ro.axonsoft.internship172.model.base.Pagination;
import ro.axonsoft.internship172.model.error.BusinessException;
import ro.axonsoft.internship172.model.error.ErrorPropertiesBuilder;
import ro.axonsoft.internship172.model.user.ImtUser;
import ro.axonsoft.internship172.model.user.ImtUserCreateResult;
import ro.axonsoft.internship172.model.user.ImtUserGetResult;
import ro.axonsoft.internship172.model.user.ImtUserRecord;
import ro.axonsoft.internship172.model.user.ImtUserUpdateProperties;
import ro.axonsoft.internship172.model.user.ImtUserUpdateResult;
import ro.axonsoft.internship172.model.user.MdfUser;
import ro.axonsoft.internship172.model.user.MdfUserRecord;
import ro.axonsoft.internship172.model.user.User;
import ro.axonsoft.internship172.model.user.UserCreate;
import ro.axonsoft.internship172.model.user.UserCreateResult;
import ro.axonsoft.internship172.model.user.UserGet;
import ro.axonsoft.internship172.model.user.UserGetResult;
import ro.axonsoft.internship172.model.user.UserNoChangeErrorSpec;
import ro.axonsoft.internship172.model.user.UserPssdWrongErrorSpec;
import ro.axonsoft.internship172.model.user.UserRecord;
import ro.axonsoft.internship172.model.user.UserUpdate;
import ro.axonsoft.internship172.model.user.UserUpdateResult;
import ro.axonsoft.internship172.model.user.UsernameDoesNotExistErrorSpec;
import ro.axonsoft.internship172.model.user.UsernmTakenErrorSpec;

public class UserBusinessImpl implements UserBusiness {

	private UserDao userDao;

	@Inject
	public void setUserDao(final UserDao userDao) {
		this.userDao = userDao;
	}

	@Override
	public UserCreateResult createUser(final UserCreate userCreate) throws BusinessException {
		final UserEntity userEntity = buildUserEntityForCreate(userCreate);

		if (!checkUsername(userEntity)) {
			throw new BusinessException("Username already exists!",
					ErrorPropertiesBuilder.of(UsernmTakenErrorSpec.USERNM_TAKEN)
							.var(UsernmTakenErrorSpec.Var.USRNM, userCreate.getBasic().getUsername()).build());
		}

		userDao.addUser(userEntity);
		return ImtUserCreateResult.builder().basic(ImtUser.copyOf(userEntity.getRecord().getBasic())).build();

	}

	@Override
	public UserUpdateResult updateUser(final UserUpdate userUpdate) throws BusinessException {
		final UserEntityUpdate userEntityUpdate = buildUserEntityForUpdate(userUpdate);

		if (!userChanged(userEntityUpdate)) {

			throw new BusinessException("No field updated", ErrorPropertiesBuilder.of(UserNoChangeErrorSpec.NO_CHANGE)
					.var(UserNoChangeErrorSpec.Var.USRNM, userUpdate.getUsername().toLowerCase()).build());
		}
		userDao.updateUser(userEntityUpdate);

		return ImtUserUpdateResult.builder()
				.properties(ImtUserUpdateProperties.builder().firstName(userEntityUpdate.getFirstName())
						.lastName(userEntityUpdate.getLastName()).email(userEntityUpdate.getEmail()).build())
				.username(userEntityUpdate.getCriteria().getUsernameIncl().iterator().next()).build();
	}

	@Override
	public UserGetResult getUsers(final UserGet userGet) {

		final UserEntityGet userEntityGet = buildUserEntityGet(userGet);
		final int count = userDao.countUser(buildUserEntityCount(userGet));

		final List<UserEntity> users = userDao.getUser(userEntityGet);
		return ImtUserGetResult.builder()
				.list(users.stream().map(UserEntity::getRecord).map(ImtUserRecord::copyOf)
						.map(UserRecord.class::cast)::iterator)
				.count(count).pagination(userGet.getPagination())
				.pageCount(getPageCount(count, userGet.getPagination())).build();
	}

	@Override
	public User loginUser(String username, String password) throws BusinessException {
		final String passwd = Hashing.md5().hashString(password, Charsets.UTF_8).toString();

		if (!checkUsernameForLogin(username)) {
			throw new BusinessException("Utilizatorul nu exista!",
					ErrorPropertiesBuilder.of(UsernameDoesNotExistErrorSpec.USERNM_DOES_NOT_EXIST)
							.var(UsernameDoesNotExistErrorSpec.Var.USRNM, username).build());
		}
		if (!checkPasswordForLogin(username, passwd)) {
			throw new BusinessException("Parola este gresita!", ErrorPropertiesBuilder
					.of(UserPssdWrongErrorSpec.PSSD_WRONG).var(UserPssdWrongErrorSpec.Var.PSSD, username).build());
		}
		final UserEntity user = userDao.getUser(ImtUserEntityGet.builder()
				.criteria(ImtUserEntityCriteria.builder().addUsernameIncl(username).build()).build()).get(0);

		return ImtUser.builder().username(user.getRecord().getBasic().getUsername()).build();
	}

	private int getPageCount(final int count, final Pagination pagination) {
		int pageCount;
		if (pagination == null) {
			pageCount = 1;
		} else {
			pageCount = count / pagination.getPageSize();
			if (count % pagination.getPageSize() != 0) {
				pageCount += 1;
			}
		}
		return pageCount;
	}

	private UserEntityCount buildUserEntityCount(final UserGet userGet) {
		return ImtUserEntityCount.builder().criteria(ImtUserEntityCriteria.builder()
				.usernameIncl(userGet.getUsername() != null ? ImmutableSet.of(userGet.getUsername().toLowerCase())
						: ImmutableSet.of())
				.build()).search(userGet.getSearch()).build();
	}

	private boolean checkUsername(final UserEntity userEntity) {
		return userDao
				.getUser(ImtUserEntityGet.builder()
						.criteria(ImtUserEntityCriteria.builder()
								.addUsernameIncl(userEntity.getRecord().getBasic().getUsername()).build())
						.build())
				.size() < 1;
	}

	private String toUpperFirstChar(final String str) {
		return Character.toUpperCase(str.charAt(0)) + str.substring(1, str.length()).toLowerCase();
	}

	private String splitFirstName(final String firstName) {
		final String[] splitted = firstName.split("\\s+");

		String res = "";
		for (int i = 0; i < splitted.length; i++) {
			if (i > 0) {
				res += " ";
			}
			res += toUpperFirstChar(splitted[i]);
		}

		return res;
	}

	private UserEntity buildUserEntityForCreate(final UserCreate userCreate) {
		final UserEntity userEntity = MdfUserEntity.create()
				.setRecord(MdfUserRecord.create()
						.setBasic(MdfUser.create().from(userCreate.getBasic())
								.setUsername(userCreate.getBasic().getUsername().toLowerCase())
								.setEmail(userCreate.getBasic().getEmail().toLowerCase())
								.setFirstName(splitFirstName(userCreate.getBasic().getFirstName()))
								.setLastName(userCreate.getBasic().getLastName().toUpperCase())))
				.setPassword(Hashing.md5().hashString(userCreate.getPassword(), Charsets.UTF_8).toString());
		return userEntity;
	}

	private UserEntityUpdate buildUserEntityForUpdate(final UserUpdate userUpdate) {
		final UserEntityUpdate userEntityUpdate = ImtUserEntityUpdate.builder()
				.criteria(ImtUserEntityCriteria.builder()
						.usernameIncl(ImmutableSet.of(userUpdate.getUsername().toLowerCase())).build())
				.firstName(userUpdate.getProperties().getFirstName() != null
						? splitFirstName(userUpdate.getProperties().getFirstName())
						: null)
				.lastName(userUpdate.getProperties().getLastName() != null
						? userUpdate.getProperties().getLastName().toUpperCase()
						: null)
				.email(userUpdate.getProperties().getEmail() != null
						? userUpdate.getProperties().getEmail().toLowerCase()
						: null)
				.build();

		return userEntityUpdate;
	}

	private UserEntityGet buildUserEntityGet(final UserGet userGet) {
		return ImtUserEntityGet.builder().pagination(userGet.getPagination()).search(userGet.getSearch())
				.sort(userGet.getSort())
				.criteria(
						ImtUserEntityCriteria.builder()
								.usernameIncl(userGet.getUsername() != null
										? ImmutableSet.of(userGet.getUsername().toLowerCase())
										: ImmutableSet.of())
								.build())
				.build();
	}

	private boolean checkUsernameForLogin(final String username) {
		return userDao
				.getUser(ImtUserEntityGet.builder()
						.criteria(ImtUserEntityCriteria.builder().addUsernameIncl(username).build()).build())
				.size() > 0;
	}

	private boolean checkPasswordForLogin(final String username, final String password) {
		final UserEntity get = userDao.getUser(ImtUserEntityGet.builder()
				.criteria(ImtUserEntityCriteria.builder().addUsernameIncl(username).build()).build()).get(0);
		if (get.getPassword() != null) {
			return sameProperty(get.getPassword(), password);
		} else {
			return false;
		}
	}

	private boolean userChanged(final UserEntityUpdate userEntityUpdate) {
		final List<UserEntity> users = userDao.getUser(ImtUserEntityGet.builder()
				.criteria(ImtUserEntityCriteria.builder()
						.addUsernameIncl(userEntityUpdate.getCriteria().getUsernameIncl().iterator().next()).build())
				.build());
		if (!users.isEmpty()) {
			final User user = users.get(0).getRecord().getBasic();

			if (sameProperty(user.getFirstName(), userEntityUpdate.getFirstName())
					&& sameProperty(user.getLastName(), userEntityUpdate.getLastName())
					&& sameProperty(user.getEmail(), userEntityUpdate.getEmail())) {
				return false;
			}
			return true;
		}

		return false;
	}

	private boolean sameProperty(final String newProp, final String dbProp) {
		if (newProp == null) {
			return true;
		}
		if (newProp.equals(dbProp)) {
			return true;
		}
		return false;
	}

}