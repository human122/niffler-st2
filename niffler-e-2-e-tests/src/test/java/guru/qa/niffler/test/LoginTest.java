package guru.qa.niffler.test;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.jupiter.annotation.ClasspathUser;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.page.MainPage;
import io.qameta.allure.Allure;
import io.qameta.allure.AllureId;
import guru.qa.niffler.model.UserJson;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class LoginTest extends BaseWebTest {
  
  @ValueSource(strings = {
      "testdata/anton.json",
      "testdata/emma.json"
  })
  @AllureId("104")
  @ParameterizedTest
  void loginTest(@ClasspathUser UserJson user) throws IOException {
    Allure.step("open page", () -> Selenide.open("http://127.0.0.1:3000/main"));
    $("a[href*='redirect']").click();
    $("input[name='username']").setValue(user.getUsername());
    $("input[name='password']").setValue(user.getPassword());
    $("button[type='submit']").click();

    $("a[href*='friends']").click();
    $(".header").should(visible).shouldHave(text("Niffler. The coin keeper."));
  }

  @Test
  void errorMessageShouldBeVisibleInCaseThatCredentialsAreBad() {
    Selenide.open(LoginPage.URL, LoginPage.class)
            .goToLoginPage()
            .checkThatPageLoaded()
            .fillLoginPage("jndfjvjnkj", "1212")
            .checkErrorMessage("Bad credentials");
  }

  @Test
  void mainPageDisplayedAfterLogInWithValidCredentials() {
    Selenide.open(LoginPage.URL, LoginPage.class)
            .goToLoginPage()
            .checkThatPageLoaded()
            .fillLoginPage("anton", "123");

    new MainPage()
            .checkThatPageLoaded();
  }

}
