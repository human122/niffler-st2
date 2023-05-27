package niffler.db.dao;

import niffler.db.entity.UserEntity;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.sql.SQLException;

public interface NifflerUsersDAO {

    PasswordEncoder pe = PasswordEncoderFactories.createDelegatingPasswordEncoder();

    int createUser(UserEntity user);

    UserEntity getUser(String userId);

    int updateUser(UserEntity user);

    int deleteUser(UserEntity user) throws SQLException;

    String getUserId(String userName);

}
