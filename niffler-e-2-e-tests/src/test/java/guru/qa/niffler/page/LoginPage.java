package guru.qa.niffler.page;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.config.Config;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$x;

public class LoginPage extends BasePage<LoginPage> {

    public static final String URL = Config.getConfig().getFrontUrl() + "login";

    private final SelenideElement loginLink = $("a[href*='redirect']");
    private final SelenideElement usernameInput = $x("//input[@placeholder='Type your username']");
    private final SelenideElement passwordInput = $x("//input[@placeholder='Type your password']");
    private final SelenideElement signInBtn = $("button[type='submit']");

    public LoginPage goToLoginPage() {
        loginLink.shouldBe(visible).click();
        return this;
    }

    @Override
    public LoginPage checkThatPageLoaded() {
        usernameInput.should(Condition.exist);
        passwordInput.should(Condition.exist);
        signInBtn.shouldHave(Condition.text("Sign In"));
        return this;
    }

    public LoginPage fillLoginPage(String username, String password) {
        usernameInput.val(username);
        passwordInput.val(password);
        signInBtn.click();
        return this;
    }

    public LoginPage checkErrorMessage(String expectedMessage) {
        $(".form__error").shouldHave(text(expectedMessage));
        return this;
    }

}
