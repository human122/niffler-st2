package niffler.test;

import io.qameta.allure.AllureId;
import niffler.api.UserDataService;
import niffler.jupiter.annotation.ClasspathUser;
import niffler.model.UserJson;
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
        userService.updateUserInfo(user).execute();
        UserJson upd = userService.currentUser(user.getUsername()).execute().body();
        Assertions.assertEquals(upd.getSurname(), user.getSurname());
        Assertions.assertEquals(upd.getCurrency(), user.getCurrency());
    }
}
