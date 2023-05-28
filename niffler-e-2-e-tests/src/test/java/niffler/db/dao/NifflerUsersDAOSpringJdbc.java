package niffler.db.dao;

import niffler.db.DataSourceProvider;
import niffler.db.ServiceDB;
import niffler.db.entity.UserEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.JdbcTransactionManager;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.support.TransactionTemplate;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.UUID;


public class NifflerUsersDAOSpringJdbc implements NifflerUsersDAO {

    private final TransactionTemplate transactionTemplate;
    private final JdbcTemplate jdbcTemplate;
    private static final PasswordEncoder pe = PasswordEncoderFactories.createDelegatingPasswordEncoder();

    public NifflerUsersDAOSpringJdbc() {
        DataSourceTransactionManager transactionManager = new JdbcTransactionManager(
                DataSourceProvider.INSTANCE.getDataSource(ServiceDB.NIFFLER_AUTH));
        this.transactionTemplate = new TransactionTemplate(transactionManager);
        this.jdbcTemplate = new JdbcTemplate(transactionManager.getDataSource());
    }

    @Override
    public int createUser(UserEntity user) {
        transactionTemplate.execute(status -> {
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(connection -> {
                PreparedStatement statement = connection.prepareStatement(
                        "INSERT INTO users (username, password, enabled, account_non_expired, " +
                                "account_non_locked, credentials_non_expired) VALUES(?, ?, ?, ?, ?, ?)",
                        Statement.RETURN_GENERATED_KEYS
                );
                statement.setString(1, user.getUsername());
                statement.setString(2, pe.encode(user.getPassword()));
                statement.setBoolean(3, user.getEnabled());
                statement.setBoolean(4, user.getAccountNonExpired());
                statement.setBoolean(5, user.getAccountNonLocked());
                statement.setBoolean(6, user.getCredentialsNonExpired());
                return statement;
            }, keyHolder);

            final UUID userId = (UUID) keyHolder.getKeys().get("id");
            if (userId == null) {
                throw new IllegalStateException("Unable to retrieve generated id");
            }
            user.setId(userId);

            user.getAuthorities().forEach(authority ->
                    jdbcTemplate.update(
                            "INSERT INTO authorities (user_id, authority) VALUES (?, ?)",
                            user.getId(), authority.getAuthority().name()
                    )
            );
            return 0;
        });
        return 0;
    }

    @Override
    public String getUserId(String userName) {
        return jdbcTemplate.query("SELECT * FROM users WHERE username = ?",
                rs -> {return rs.getString(1);},
                userName
        );
    }

    @Override
    public int deleteUser(UserEntity user) {
        return transactionTemplate.execute(st -> {
            jdbcTemplate.update("DELETE from authorities a where a.user_id = ?::uuid", user.getId());
            return jdbcTemplate.update("DELETE from users u where u.id = ?::uuid", user.getId());
        });
    }

    @Override
    public UserEntity getUser(String userId) {
        return null;
    }

    @Override
    public int updateUser(UserEntity user) {
        return transactionTemplate.execute(status -> {
            jdbcTemplate.update("UPDATE users SET username = ?, password = ?, enabled = ?, account_non_expired =?," +
                            "account_non_locked =?, credentials_non_expired = ? where username = ?",
                    user.getUsername(), user.getPassword(), user.getEnabled(), user.getAccountNonExpired(),
                    user.getAccountNonLocked(), user.getCredentialsNonExpired(), user.getUsername());
            if (user.getAuthorities() != null) {
                UUID userId = UUID.fromString(getUserId(user.getUsername()));
                user.getAuthorities().forEach(a -> jdbcTemplate
                        .update("UPDATE authorities SET authority = ? WHERE user_id =?", a, userId));
            }
            return 0;
        });
    }
}