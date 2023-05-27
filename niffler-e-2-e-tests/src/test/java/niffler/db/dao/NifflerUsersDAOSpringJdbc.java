package niffler.db.dao;

import niffler.db.DataSourceProvider;
import niffler.db.ServiceDB;
import niffler.db.entity.UserEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.support.JdbcTransactionManager;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.support.TransactionTemplate;


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
        return 0;
    }
}