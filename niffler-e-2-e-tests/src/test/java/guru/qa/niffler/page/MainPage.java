package guru.qa.niffler.page;

import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.ElementsCollection;
import guru.qa.niffler.config.Config;

import java.time.Duration;

import static com.codeborne.selenide.Selenide.$$x;

public class MainPage extends BasePage<MainPage> {

    public static final String URL = Config.getConfig().getAuthUrl() + "main";

    private final ElementsCollection headers = $$x("//h2");

    @Override
    public MainPage checkThatPageLoaded() {
        headers.shouldHave(CollectionCondition.size(2), Duration.ofSeconds(2));
        headers.shouldHave(CollectionCondition.anyMatch(
                "Header text", h -> h.getText().equals("Add new spending")));
        headers.shouldHave(CollectionCondition.anyMatch(
                "Header text", h -> h.getText().equals("History of spendings")));
        return this;
    }
}
