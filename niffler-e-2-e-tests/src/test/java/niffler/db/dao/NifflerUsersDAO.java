package niffler.db.dao;

import niffler.db.entity.UserEntity;

import java.sql.SQLException;

public interface NifflerUsersDAO {

    int createUser(UserEntity user);

    UserEntity getUser(String userId);

    int updateUser(UserEntity user);

    int deleteUser(String userId) throws SQLException;

    String getUserId(String userName);

}
