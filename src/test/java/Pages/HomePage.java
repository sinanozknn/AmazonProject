package Pages;

import com.thoughtworks.gauge.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import step.StepImplementation;

public class HomePage extends StepImplementation {


        // Çerez banner
        private final By acceptCookiesBtn = By.id("sp-cc-accept");

        // Header: hesap linki
        private final By accountListLink  = By.id("nav-link-accountList");

        // Header: arama
        private final By searchInput  = By.cssSelector("#twotabsearchtextbox, input[name='field-keywords']");
        private final By searchButton = By.cssSelector("#nav-search-submit-button, input[type='submit'][value]");



    /** Çerez banner’ı varsa kapat */
        public void acceptCookiesIfPresent() {
            if (isPresent(acceptCookiesBtn)) {
                click(acceptCookiesBtn);
            }
        }

        /** Login akışına gitmek için header linki */
        public void goToSignIn() {
            click(accountListLink);
        }

        /** arama kutusu yoksa otomatik toparlanır ve anasayfaya dönüp tekrar dener */
        public void search(String keyword) {
            try {
                type(searchInput, keyword);
            } catch (TimeoutException e) {
                driver.navigate().to("https://www.amazon.com.tr/");
                acceptCookiesIfPresent();
                type(searchInput, keyword);
            }
            click(searchButton);
        }

    // 9) Arama sonuçlarına geri dön
    @Step("arama sonuçlarına geri dönerim")
    public void backToResults() {
        closeCurrentTabIfMoreThanOneAndSwitch();
    }





}


