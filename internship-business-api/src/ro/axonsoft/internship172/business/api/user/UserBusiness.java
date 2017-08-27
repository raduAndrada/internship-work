package ro.axonsoft.internship172.business.api.user;

import ro.axonsoft.internship172.model.error.BusinessException;
import ro.axonsoft.internship172.model.user.User;
import ro.axonsoft.internship172.model.user.UserCreate;
import ro.axonsoft.internship172.model.user.UserCreateResult;
import ro.axonsoft.internship172.model.user.UserGet;
import ro.axonsoft.internship172.model.user.UserGetResult;
import ro.axonsoft.internship172.model.user.UserUpdate;
import ro.axonsoft.internship172.model.user.UserUpdateResult;

public interface UserBusiness {

	UserCreateResult createUser(UserCreate userCreate) throws BusinessException;

	UserUpdateResult updateUser(UserUpdate userUpdate) throws BusinessException;

	UserGetResult getUsers(UserGet userGet);

	User loginUser(String username, String password) throws BusinessException;

}
