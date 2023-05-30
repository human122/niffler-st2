package guru.qa.niffler.test;

import guru.qa.niffler.api.UserDataService;
import guru.qa.niffler.jupiter.annotation.ClasspathUser;
import io.qameta.allure.AllureId;
import guru.qa.niffler.model.UserJson;
import okhttp3.OkHttpClient;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.io.IOException;

public class UserDataUpdateTest {

    @ValueSource(strings = {
            "testdata/anton.json",
            "testdata/emma.json"
    })
    @AllureId("107")
    @ParameterizedTest
    void updateDataTest(@ClasspathUser UserJson user) throws IOException {
        final OkHttpClient httpClient = new OkHttpClient.Builder().build();

        final Retrofit retrofit = new Retrofit.Builder()
                .client(httpClient)
                .baseUrl("http://127.0.0.1:8089")
                .addConverterFactory(JacksonConverterFactory.create())
                .build();

        UserDataService userService = retrofit.create(UserDataService.class);
        UserJson originalUser = userService.currentUser(user.getUsername()).execute().body();
        UserJson changedUser = userService.updateUserInfo(user).execute().body();
        Assertions.assertNotEquals(originalUser.getSurname(), changedUser.getSurname());
        Assertions.assertNotEquals(originalUser.getCurrency(), changedUser.getCurrency());
    }
}
