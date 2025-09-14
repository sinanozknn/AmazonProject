package Base;

import com.thoughtworks.gauge.AfterScenario;
import com.thoughtworks.gauge.BeforeScenario;
import com.thoughtworks.gauge.ExecutionContext;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class BaseTest {

    protected static WebDriver driver;
    protected static WebDriverWait webDriverWait;
    protected static Logger logger = LogManager.getLogger(BaseTest.class);

    // Çalıştırılacak tarayıcı: Varsayılan Chrome
    private final String browser = System.getenv("browser") != null
            ? System.getenv("browser").toLowerCase()
            : "chrome";

    public BaseTest() {
        // Boş constructor Gauge için gerekli
    }

    @BeforeScenario
    public void setUp(ExecutionContext executionContext) {

        switch (browser) {
            case "chrome": {
                WebDriverManager.chromedriver().setup(); // Chromedriver otomatik indir ve ayarla
                ChromeOptions chromeOptions = new ChromeOptions();
                chromeOptions.addArguments("--incognito");
                chromeOptions.addArguments("--remote-allow-origins=*");
                driver = new ChromeDriver(chromeOptions);
                break;
            }
            case "firefox": {
                WebDriverManager.firefoxdriver().setup();
                FirefoxOptions firefoxOptions = new FirefoxOptions();
                firefoxOptions.addArguments("-private");
                driver = new FirefoxDriver(firefoxOptions);
                break;
            }
            case "ie":
            case "internetexplorer": {
                WebDriverManager.iedriver().setup();
                driver = new InternetExplorerDriver();
                break;
            }
            default: {
                WebDriverManager.chromedriver().setup();
                ChromeOptions chromeOptions = new ChromeOptions();
                chromeOptions.addArguments("--incognito");
                chromeOptions.addArguments("--remote-allow-origins=*");
                driver = new ChromeDriver(chromeOptions);
            }
        }

        driver.manage().window().maximize();
        webDriverWait = new WebDriverWait(driver, Duration.ofSeconds(20));
    }

    @AfterScenario
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
