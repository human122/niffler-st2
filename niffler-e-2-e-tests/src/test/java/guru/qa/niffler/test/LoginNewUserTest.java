package guru.qa.niffler.test;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.db.entity.UserEntity;
import guru.qa.niffler.jupiter.extension.GenerateUserExtension;
import io.qameta.allure.Allure;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class LoginNewUserTest extends BaseWebTest {

  private static final String TEST_PWD = "12345";

  @Test
  @ExtendWith(GenerateUserExtension.class)
  void loginTest(UserEntity ue) {
    Allure.step("open page", () -> Selenide.open("http://127.0.0.1:3000/main"));
    $("a[href*='redirect']").click();
    $("input[name='username']").setValue(ue.getUsername());
    $("input[name='password']").setValue(TEST_PWD);
    $("button[type='submit']").click();

    $("a[href*='friends']").click();
    $(".header").should(visible).shouldHave(text("Niffler. The coin keeper."));
  }
}
