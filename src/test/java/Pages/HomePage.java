package Pages;

import com.thoughtworks.gauge.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import step.StepImplementation;

public class HomePage extends StepImplementation {

    private static final String BASE_URL = "https://www.amazon.com.tr/?language=tr_TR";


    private final By acceptCookiesBtn = By.id("sp-cc-accept");

    // Header: hesap/giriş linki (SigningPage bunu bekliyor)
    private final By accountListLink  = By.id("nav-link-accountList");


    private final By searchInput  = By.cssSelector("#twotabsearchtextbox, input[name='field-keywords']");
    private final By searchButton = By.cssSelector("#nav-search-submit-button, input[type='submit'][value]");


    private final By homeLogo = By.id("nav-logo-sprites");


    public void acceptCookiesIfPresent() {
        if (isPresent(acceptCookiesBtn)) {
            click(acceptCookiesBtn);
        }
    }


    public void goToSignIn() {
        click(accountListLink);
    }

    public void ensureOnHome() {

        try {
            waitVisible(searchInput);
            return;
        } catch (Exception ignored) { }

        // 2) Logo ile toparla
        try {
            if (isPresent(homeLogo)) {
                click(homeLogo);
                waitVisible(searchInput);
                return;
            }
        } catch (Exception ignored) { }


        driver.navigate().to(BASE_URL);
        acceptCookiesIfPresent();
        waitVisible(searchInput);
    }


    public void search(String keyword) {
        try {
            ensureOnHome();
            type(searchInput, keyword);
            click(searchButton);
        } catch (TimeoutException e) {

            driver.navigate().to(BASE_URL);
            acceptCookiesIfPresent();
            type(searchInput, keyword);
            click(searchButton);
        }
    }


    @Step("arama sonuçlarına geri dönülür")
    public void backToResults() {
        closeCurrentTabIfMoreThanOneAndSwitch();
    }
}
