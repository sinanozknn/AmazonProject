package Pages;

import com.thoughtworks.gauge.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import step.StepImplementation;
import java.util.Properties;
import java.io.FileInputStream;
import java.io.IOException;


public class SigningPage extends StepImplementation{



    private final By emailInput    = By.cssSelector("#ap_email, #ap_email_login, input[name='email']");
    private final By continueBtn   = By.id("continue");
    private final By passwordInput = By.cssSelector("#ap_password, input[name='password']");
    private final By signInBtn     = By.id("signInSubmit");




    public void login(String email, String password) {
        if (email == null || email.isBlank() || password == null || password.isBlank()) {
            throw new IllegalArgumentException("Email/Password boş geldi. Run Configuration'a ekleyin.");
        }


        waitVisible(emailInput);
        type(emailInput, email);
        click(continueBtn);


        waitVisible(passwordInput);
        type(passwordInput, password);
        click(signInBtn);
    }
    @Step("Amazon'a giriş yapılır")
    public void loginfunction() {
        Properties prop = new Properties();
        String pass = null;
        String email = null;
        try {
            prop.load(new FileInputStream("config.properties"));
            email = prop.getProperty("email");
            pass = prop.getProperty("pass");

        } catch (IOException e) {
            e.printStackTrace();
        }

        HomePage homePage = new HomePage();
        homePage.acceptCookiesIfPresent();
        homePage.goToSignIn();

        login(email, pass);
        logger.info("Amazon sayfasına giriş yapıldı");
    }

}
