package guru.qa.niffler.test;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.page.RegistrationPage;
import org.junit.jupiter.api.Test;

public class RegistrationWebTest extends BaseWebTest {

    @Test
    void errorMessageShouldBeVisibleInCaseThatPasswordsAreDifferent() {
        Selenide.open(RegistrationPage.URL, RegistrationPage.class)
                .checkThatPageLoaded()
                .fillRegistrationForm("jndfjvjnkj", "1212", "21547")
                .checkErrorMessage("Passwords should be equal");
    }

}
