package ro.axonsoft.internship172.data.api.user;

import java.util.List;

public interface UserDao {

    int addUser(UserEntity user);

    int updateUser(UserEntityUpdate update);

    int deleteUser(UserEntityDelete delete);

    Integer countUser(UserEntityCount count);

    List<UserEntity> getUser(UserEntityGet get);

}
