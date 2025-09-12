package Pages;

import com.thoughtworks.gauge.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import step.StepImplementation;

public class SigningPage extends StepImplementation{


    // Daha sağlam: Amazon bazen farklı id/name kullanıyor.
    private final By emailInput    = By.cssSelector("#ap_email, #ap_email_login, input[name='email']");
    private final By continueBtn   = By.id("continue");
    private final By passwordInput = By.cssSelector("#ap_password, input[name='password']");
    private final By signInBtn     = By.id("signInSubmit");




    public void login(String email, String password) {
        if (email == null || email.isBlank() || password == null || password.isBlank()) {
            throw new IllegalArgumentException("Email/Password boş geldi. Run Configuration'a ekleyin.");
        }

        // 1) Email alanı gerçekten görünene kadar bekle
        waitVisible(emailInput);
        type(emailInput, email);
        click(continueBtn);

        // 2) Parola alanı görünene kadar bekle
        waitVisible(passwordInput);
        type(passwordInput, password);
        click(signInBtn);
    }
    @Step("Amazon'a giriş yaparım")
    public void login() {
        String email = "sinanozkan.ie@gmail.com";
        String pass  = "Sinan.11111";

        HomePage homePage = new HomePage();
        homePage.acceptCookiesIfPresent();
        homePage.goToSignIn();
        // isElementVisible("AMZ_Product_GoToCart",5);
        login(email, pass);
        logger.info("Amazon sayfasına giriş yapıldı");
    }

}
