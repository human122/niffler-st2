package niffler.db.dao;

import niffler.db.DataSourceProvider;
import niffler.db.ServiceDB;
import niffler.db.entity.AuthorityEntity;
import niffler.db.entity.UserEntity;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.sql.DataSource;
import java.sql.*;
import java.util.UUID;

public class NifflerUsersDAOJdbc implements NifflerUsersDAO {

    private static final DataSource ds = DataSourceProvider.INSTANCE.getDataSource(ServiceDB.NIFFLER_AUTH);

    @Override
    public int createUser(UserEntity user) {
        int executeUpdate = 0;

        try (Connection conn = ds.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement stUser = conn.prepareStatement("INSERT INTO users "
                    + "(username, password, enabled, account_non_expired, account_non_locked, credentials_non_expired) "
                    + " VALUES (?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
                 PreparedStatement stAutorities = conn.prepareStatement("INSERT INTO authorities (user_id, authority) VALUES (?, ?)")
            ) {

                stUser.setString(1, user.getUsername());
                stUser.setString(2, pe.encode(user.getPassword()));
                stUser.setBoolean(3, user.getEnabled());
                stUser.setBoolean(4, user.getAccountNonExpired());
                stUser.setBoolean(5, user.getAccountNonLocked());
                stUser.setBoolean(6, user.getCredentialsNonExpired());
                executeUpdate = stUser.executeUpdate();
                final UUID finalUserId;
                try (ResultSet resultSet = stUser.getGeneratedKeys()) {
                    if (resultSet.next()) {
                        finalUserId = UUID.fromString(resultSet.getString(1));
                        user.setId(finalUserId);
                    } else {
                    throw new SQLException("User not found");
                    }
                }

                for (AuthorityEntity authority : user.getAuthorities()) {
                    stAutorities.setObject(1, finalUserId);
                    stAutorities.setString(2, authority.getAuthority().name());
                    stAutorities.addBatch();
                    stAutorities.clearParameters();
                }
                stAutorities.executeBatch();
            } catch (Exception e) {
                e.printStackTrace();
                System.err.print("Create user failed");
                conn.rollback();
                conn.setAutoCommit(true);
            }
            conn.commit();
            conn.setAutoCommit(true);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return executeUpdate;
    }

    @Override
    public UserEntity getUser(String userId) {
        UserEntity user = new UserEntity();
        try (Connection conn = ds.getConnection();
            PreparedStatement st = conn.prepareStatement("SELECT * FROM users u where u.id = ?::uuid")) {
            st.setString(1, userId);
            ResultSet resultSet = st.executeQuery();
            if (resultSet.next()) {
                user.setId(UUID.fromString(resultSet.getString("id")));
                user.setUsername(resultSet.getString("username"));
                user.setEnabled(resultSet.getBoolean("enabled"));
                user.setAccountNonExpired(resultSet.getBoolean("account_non_expired"));
                user.setAccountNonLocked(resultSet.getBoolean("account_non_locked"));
                user.setCredentialsNonExpired(resultSet.getBoolean("credentials_non_expired"));
                return user;
            } else {
                throw new IllegalArgumentException("Can`t find user by given userId: " + userId);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int updateUser(UserEntity user) {
        int executeUpdate;

        try (Connection conn = ds.getConnection();
             PreparedStatement st = conn.prepareStatement("UPDATE users set username = ?, password = ?, enabled = ?, " +
                     "account_non_expired = ?, account_non_locked = ?, credentials_non_expired = ?"
                     + " where id = ?::uuid")) {
            st.setString(1, user.getUsername());
            st.setString(2, pe.encode(user.getPassword()));
            st.setBoolean(3, user.getEnabled());
            st.setBoolean(4, user.getAccountNonExpired());
            st.setBoolean(5, user.getAccountNonLocked());
            st.setBoolean(6, user.getCredentialsNonExpired());
            st.setString(7, String.valueOf(user.getId()));

            executeUpdate = st.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return executeUpdate;
    }

    @Override
    public int deleteUser(UserEntity user) {
        int executeDelete;
        try (Connection conn = ds.getConnection();
             PreparedStatement st = conn.prepareStatement("DELETE from authorities a where a.user_id = ?::uuid")) {
            st.setObject(1, user.getId());
            st.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        try (Connection conn = ds.getConnection();
             PreparedStatement st = conn.prepareStatement("DELETE from users u where u.id = ?::uuid")) {
            st.setObject(1, user.getId());
            executeDelete = st.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return executeDelete;
    }

    @Override
    public String getUserId(String userName) {
        try (Connection conn = ds.getConnection();
             PreparedStatement st = conn.prepareStatement("SELECT * FROM users WHERE username = ?")) {
            st.setString(1, userName);
            ResultSet resultSet = st.executeQuery();
            if (resultSet.next()) {
                return resultSet.getString(1);
            } else {
                throw new IllegalArgumentException("Can`t find user by given username: " + userName);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}