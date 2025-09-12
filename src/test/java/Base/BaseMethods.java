package Base;

import com.thoughtworks.gauge.Step;
import helper.ElementHelper;
import helper.StoreHelper;
import model.ElementInfo;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.List;

public class BaseMethods extends BaseTest {



    Actions actions = new Actions(driver);

    public void waitByMilliSeconds(long milliseconds){
        try {
            logger.info(milliseconds + " miliseconds wait.");
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    protected List<WebElement> findElementsByKey(String key) {
        ElementInfo elementInfo = StoreHelper.INSTANCE.findElementInfoByKey(key);
        waitPresenceOfElementLocatedBy(ElementHelper.getElementInfoToBy(elementInfo));
        return driver.findElements(ElementHelper.getElementInfoToBy(StoreHelper.INSTANCE.findElementInfoByKey(key)));
    }

    protected WebElement findElementByKey(String key) {

        ElementInfo elementInfo = StoreHelper.INSTANCE.findElementInfoByKey(key);
        waitPresenceOfElementLocatedBy(ElementHelper.getElementInfoToBy(elementInfo));
        return findElementsByKey(key).get(elementInfo.getIndex());
    }

    protected String getElementText(String key) {
        return findElementByKey(key).getText();
    }

    protected void  clickElement(String key) {
        WebElement webElement = findElementByKey(key);
        webElement.click();
    }



    protected void clearAndSendKey(String text, String key) {
        WebElement webElement = findElementByKey(key);
        webElement.clear();
        webElement.sendKeys(text);
    }

    protected void waitPresenceOfElementLocatedBy(By by) {
        try {

            webDriverWait.until(ExpectedConditions
                    .presenceOfElementLocated(by));
        }catch (Exception e){
            this.logger.error(by + "--> presenceOfElementLocated error. " + e.getStackTrace().toString());
        }
    }

    protected void waitVisibilityOfElementLocatedBy(By by) {
        try {
            webDriverWait.until(ExpectedConditions
                    .visibilityOfElementLocated(by));
        }catch (Exception e ){
            this.logger.error( by + "---> visibilityOfElementLocated error. " + e.getStackTrace().toString());
        }
    }
    protected void waitClickableOfElementLocatedBy(By by) {
        try {
            webDriverWait.until(ExpectedConditions
                    .elementToBeClickable(by));
        }catch (Exception e ){
            this.logger.error( by + "---> clickableOfElementLocated error. " + e.getStackTrace().toString());
        }
    }

    protected void hoverElement(String key){
        ElementInfo elementInfo = StoreHelper.INSTANCE.findElementInfoByKey(key);
        WebElement webElement = driver.findElement(ElementHelper.getElementInfoToBy(elementInfo));
        actions.moveToElement(webElement).build().perform();
    }


}
