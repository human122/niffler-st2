package niffler.db.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import niffler.db.DataSourceProvider;
import niffler.db.ServiceDB;
import niffler.db.entity.UserEntity;
import niffler.db.jpa.EmfProvider;
import niffler.db.jpa.JpaTransactionManager;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.support.JdbcTransactionManager;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.support.TransactionTemplate;

import java.sql.SQLException;
import java.util.UUID;


public class NifflerUsersDAOHibernate extends JpaTransactionManager implements NifflerUsersDAO {

    public NifflerUsersDAOHibernate() {
        super(EmfProvider.INSTANCE.getEmf(ServiceDB.NIFFLER_AUTH).createEntityManager());
    }

    @Override
    public int createUser(UserEntity user) {
        user.setPassword(pe.encode(user.getPassword()));
        persist(user);
        return 0;
    }

    @Override
    public UserEntity getUser(String userId) {
        return null;
    }

    @Override
    public int updateUser(UserEntity user) {
        return 0;
    }

    @Override
    public int deleteUser(UserEntity user) {
        remove(user);
        return 0;
    }

    @Override
    public String getUserId(String userName) {
        return em.createQuery("select u from UserEntity u where username=:username", UserEntity.class)
                .setParameter("username", userName)
                .getSingleResult()
                .getId()
                .toString();
    }
}