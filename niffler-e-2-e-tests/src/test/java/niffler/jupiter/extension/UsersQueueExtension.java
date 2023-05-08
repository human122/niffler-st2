package niffler.jupiter.extension;

import io.qameta.allure.AllureId;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import niffler.jupiter.annotation.User;
import niffler.jupiter.annotation.User.UserType;
import niffler.model.UserJson;
import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ExtensionContext.Namespace;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;

public class UsersQueueExtension implements
    BeforeEachCallback,
    AfterTestExecutionCallback,
    ParameterResolver {

  public static Namespace USER_EXTENSION_NAMESPACE = Namespace.create(UsersQueueExtension.class);

  private static Queue<UserJson> USERS_WITH_FRIENDS_QUEUE = new ConcurrentLinkedQueue<>();
  private static Queue<UserJson> USERS_INVITATION_SENT_QUEUE = new ConcurrentLinkedQueue<>();
  private static Queue<UserJson> USERS_INVITATION_RECEIVED_QUEUE = new ConcurrentLinkedQueue<>();

  static {
    USERS_WITH_FRIENDS_QUEUE.addAll(
        List.of(userJson("anton", "123"), userJson("barsik", "12345"))
    );
    USERS_INVITATION_SENT_QUEUE.addAll(
        List.of(userJson("emma", "12345"), userJson("emily", "12345"))
    );
    USERS_INVITATION_RECEIVED_QUEUE.addAll(
        List.of(userJson("anna", "12345"), userJson("bill", "12345"))
    );
  }

  @Override
  public void beforeEach(ExtensionContext context) throws Exception {
    final String testId = getTestId(context);
    List<Map<UserType, UserJson>> users = new ArrayList<>();
    Parameter[] testParameters = context.getRequiredTestMethod().getParameters();
    for (Parameter parameter : testParameters) {
      User desiredUser = parameter.getAnnotation(User.class);
      if (desiredUser != null) {
        UserType userType = desiredUser.userType();

        UserJson user = null;
        while (user == null) {
          switch (userType) {
            case WITH_FRIENDS -> user = USERS_WITH_FRIENDS_QUEUE.poll();
            case INVITATION_SENT -> user = USERS_INVITATION_SENT_QUEUE.poll();
            case INVITATION_RECEIVED -> user = USERS_INVITATION_RECEIVED_QUEUE.poll();
          }
        }

        users.add(Map.of(userType, user));
        context.getStore(USER_EXTENSION_NAMESPACE).put(testId, users);
      }
    }
  }

  @SuppressWarnings("unchecked")
  @Override
  public void afterTestExecution(ExtensionContext context) throws Exception {
    final String testId = getTestId(context);
    List<Map<UserType, UserJson>> users =
            (List<Map<UserType, UserJson>>) context.getStore(USER_EXTENSION_NAMESPACE).get(testId);
    users.forEach((user) -> {
      UserType userType = user.keySet().iterator().next();
      switch (userType) {
        case WITH_FRIENDS -> USERS_WITH_FRIENDS_QUEUE.add(user.get(userType));
        case INVITATION_SENT -> USERS_INVITATION_SENT_QUEUE.add(user.get(userType));
        case INVITATION_RECEIVED -> USERS_INVITATION_RECEIVED_QUEUE.add(user.get(userType));
      }
    });
  }

  @Override
  public boolean supportsParameter(ParameterContext parameterContext,
      ExtensionContext extensionContext) throws ParameterResolutionException {
    return parameterContext.getParameter().isAnnotationPresent(User.class) &&
        parameterContext.getParameter().getType().isAssignableFrom(UserJson.class);
  }

  @SuppressWarnings("unchecked")
  @Override
  public UserJson resolveParameter(ParameterContext parameterContext,
      ExtensionContext extensionContext) throws ParameterResolutionException {
    final String testId = getTestId(extensionContext);
    ArrayList<Map<UserType, UserJson>> users = (ArrayList<Map<UserType, UserJson>>) extensionContext.getStore(USER_EXTENSION_NAMESPACE)
            .get(testId);

    UserJson userJson = (UserJson) users.stream()
            .collect(ArrayList::new, (arr, userMap) -> arr.add(userMap.entrySet()
                            .iterator().next().getValue()), ArrayList::addAll).get(parameterContext.getIndex());
    return userJson;
  }

  private String getTestId(ExtensionContext context) {
    return Objects
        .requireNonNull(context.getRequiredTestMethod().getAnnotation(AllureId.class))
        .value();
  }

  private static UserJson userJson(String userName, String password) {
    UserJson user = new UserJson();
    user.setUsername(userName);
    user.setPassword(password);
    return user;
  }
}
