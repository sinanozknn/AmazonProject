package Base;

import com.thoughtworks.gauge.AfterScenario;
import com.thoughtworks.gauge.BeforeScenario;
import com.thoughtworks.gauge.ExecutionContext;
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


    private final String browser = System.getenv("browser") != null
            ? System.getenv("browser").toLowerCase()
            : "chrome";

    // *** GEREKLI: Gauge'ın çağıracağı parametresiz constructor
    public BaseTest() {
        // boş bırak
    }

    @BeforeScenario
    public void setUp(ExecutionContext executionContext) {

        switch (browser) {
            case "chrome": {
                ChromeOptions chromeOptions = new ChromeOptions();
                chromeOptions.addArguments("--incognito");
                // Windows ise .exe doğru yolu ver
                System.setProperty("webdriver.chrome.driver", "webDriver/chromedriver.exe");
                driver = new ChromeDriver(chromeOptions);
                break;
            }
            case "firefox": {
                FirefoxOptions firefoxOptions = new FirefoxOptions();
                firefoxOptions.addArguments("-private");
                System.setProperty("webdriver.gecko.driver", "webDriver/geckodriver.exe");
                driver = new FirefoxDriver(firefoxOptions);
                break;
            }
            case "ie":
            case "internetexplorer": {
                System.setProperty("webdriver.ie.driver", "drivers/IEDriverServer.exe");
                driver = new InternetExplorerDriver();
                break;
            }
            default: {
                // tanınmayan değer gelirse chrome ile devam et
                ChromeOptions chromeOptions = new ChromeOptions();
                chromeOptions.addArguments("--incognito");
                System.setProperty("webdriver.chrome.driver", "webDriver/chromedriver.exe");
                driver = new ChromeDriver(chromeOptions);
            }
        }

        driver.manage().window().maximize();

        // Selenium 4: Duration tabanlı
        webDriverWait = new WebDriverWait(driver, 45);
        // webDriverWait.pollingEvery(Duration.ofMillis(150));
    }

    @AfterScenario
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
