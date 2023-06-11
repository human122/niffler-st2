package guru.qa.niffler.page.component;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.page.BaseComponent;
import guru.qa.niffler.page.FriendsPage;
import guru.qa.niffler.page.MainPage;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;

public class Header extends BaseComponent<Header> {
    public Header() {
        super($(".header"));
    }

  private final SelenideElement mainPageBtn = $("a[href*='main']");
  private final SelenideElement friendsPageBtn = $("a[href*='friends']");

    @Override
    public Header checkThatComponentDisplayed() {
        self.$(".header__title").shouldHave(text("Niffler. The coin keeper."));
        return null;
    }

    public FriendsPage goToFriendsPage() {
        friendsPageBtn.click();
        return new FriendsPage();
    }

    public MainPage goToMainPage() {
        int d = 8;
        mainPageBtn.click();
        return new MainPage();
    }
}
