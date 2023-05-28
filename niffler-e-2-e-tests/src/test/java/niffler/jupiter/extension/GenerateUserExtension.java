package niffler.jupiter.extension;

import com.github.javafaker.Faker;
import niffler.db.dao.NifflerUsersDAO;
import niffler.db.dao.NifflerUsersDAOJdbc;
import niffler.db.entity.Authority;
import niffler.db.entity.AuthorityEntity;
import niffler.db.entity.UserEntity;
import org.junit.jupiter.api.extension.*;

import java.util.Arrays;
import java.util.Locale;

public class GenerateUserExtension implements ParameterResolver, BeforeEachCallback, AfterEachCallback {

    public static ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace
            .create(GenerateUserExtension.class);

    private final NifflerUsersDAO usersDAO = new NifflerUsersDAOJdbc();
    Faker nameGen = new Faker(new Locale("en-US"));

    @Override
    public void beforeEach(ExtensionContext context) {
        UserEntity ue = new UserEntity();
        ue.setUsername(nameGen.name().username());
        ue.setPassword("12345");
        ue.setEnabled(true);
        ue.setAccountNonExpired(true);
        ue.setAccountNonLocked(true);
        ue.setCredentialsNonExpired(true);
        ue.setAuthorities(Arrays.stream(Authority.values()).map(
                a -> {
                    AuthorityEntity ae = new AuthorityEntity();
                    ae.setAuthority(a);
                    return ae;
                }
        ).toList());
        usersDAO.createUser(ue);
        context.getStore(NAMESPACE).put("CreatedUser", ue);
    }

    @Override
    public void afterEach(ExtensionContext context) throws Exception {
        usersDAO.deleteUser(context.getStore(NAMESPACE).get("CreatedUser", UserEntity.class));
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext,
                                     ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter().getType().isAssignableFrom(UserEntity.class);
    }

    @Override
    public UserEntity resolveParameter(ParameterContext parameterContext,
                                       ExtensionContext extensionContext) throws ParameterResolutionException {
        return extensionContext.getStore(NAMESPACE).get("CreatedUser", UserEntity.class);
    }
}
