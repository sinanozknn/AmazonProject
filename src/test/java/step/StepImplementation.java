package step;

import Base.BaseMethods;
import Pages.HomePage;
import com.thoughtworks.gauge.Step;
import helper.ElementHelper;
import helper.StoreHelper;
import model.ElementInfo;
import org.junit.Assert;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class StepImplementation extends BaseMethods {




    @Step("<second> second wait")
    public void waitBySeconds(int seconds){
        waitByMilliSeconds(seconds * 1000);
    }


    @Step("Go to <url>")
    public void goToUrl(String url){

        driver.get(url);
        logger.info(url + " going to.");

    }

    @Step("Wait for <key> and click")
    public void checkElementVisibiltyAndClick(String key){
        isElementVisible(key,5);
        isElementClickable(key,10);
        clickElement(key);
    }

    @Step("Hover to <key>")
    public void hoverStep(String key){
          isElementVisible(key,5);
          hoverElement(key);
    }

    @Step("Is <key> element Visible ? <timeout>")
    public boolean isElementVisible(String key, int timeout){
        ElementInfo elementInfo = StoreHelper.INSTANCE.findElementInfoByKey(key);
        try{
            WebDriverWait wait = new WebDriverWait(driver,timeout);
           waitVisibilityOfElementLocatedBy(ElementHelper.getElementInfoToBy(elementInfo));
            return true;
        }catch (Exception e){
            logger.info(key +" not visible");
            return false;
        }
    }
    @Step("Is <key> element Clickable ? <timeout>")
    public boolean isElementClickable(String key, int timeout){
        ElementInfo elementInfo = StoreHelper.INSTANCE.findElementInfoByKey(key);
        try{
            WebDriverWait wait = new WebDriverWait(driver,timeout);
            waitClickableOfElementLocatedBy(ElementHelper.getElementInfoToBy(elementInfo));
            return true;
        }catch (Exception e){
            logger.info(key +" not visible");
            return false;
        }
    }

    @Step("<key> element should be present")
    public void assertElementPresent(String key) {
        ElementInfo elementInfo = StoreHelper.INSTANCE.findElementInfoByKey(key);
        By by = ElementHelper.getElementInfoToBy(elementInfo);

        boolean isPresent;
        try {
            driver.findElement(by);
            isPresent = true;
        } catch (NoSuchElementException e) {
            isPresent = false;
        }

        Assert.assertTrue(
                String.format("Element with key '%s' is not present on the page!", key),
                isPresent
        );    logger.info("Element with key '" + key + "' is present on the page.");
    }
    @Step("Write <text> to the <key> and clear area")
    public void sendKeys(String text, String key){

        clearAndSendKey(text,key);
        logger.info(text+" written to "+key);
    }

    @Step("Go to <index> tab")
    public void goToCategoryTab(String tabKey){

        WebElement webElement = driver.findElement(By.xpath("(//ul[@class='main-nav']/li/a)["+tabKey+"]"));
        webElement.click();

    }
    @Step("Is tab text like this <tabText>")
    public void controlCategoryTab(String text ){
        isElementVisible("Active_Category_Tab_Control",5);
        System.out.println("Tab text : "+getElementText("Active_Category_Tab_Control"));
        Assert.assertEquals(text,getElementText("Active_Category_Tab_Control"));


    }
    @Step("Check page title text <tabText>")
    public final void assertPage(String expectedPageTitle){

        String titleText = driver.getTitle();
        System.out.println("Title "+titleText);
        if (expectedPageTitle.contains(driver.getTitle())) {
            System.out.println("Title "+titleText);

            throw new IllegalStateException(String.format("Expected '%s' page title. But found '%s'",expectedPageTitle ,driver.getTitle()));

        }

    }

    @Step("Choose random boutique")
    public void chooseRandomBoutique(){

        Random random = new Random();

        List<WebElement> imgSrcList = findElementsByKey("Category_Images");
        int randomBoutique = random.nextInt(imgSrcList.size());

      By by =  By.xpath("(//article[@class='component-item']/a)["+randomBoutique+"]");
      System.out.println("Random boutique by : "+by);
        logger.info("Random boutique by : "+by);

        System.out.println("Random boutique number : "+randomBoutique);
        if (randomBoutique==0)
        {
            chooseRandomBoutique();
        }

       driver.findElement(by).click();

    }

    @Step("Control boutique image with JS")
    public void controlBtqImg (){

    By boutiqueWebElement =By.xpath("//article[@class='component-item']/a/span/img");

    for(WebElement i: driver.findElements(boutiqueWebElement)) {

        controlImg(i);

    }}

    @Step("Control product image with JS")
    public void controlProductImage (){

        By productsWebElement =By.xpath("//img[contains(@class,'p-card-img')]");

        for(WebElement i: driver.findElements(productsWebElement)) {
            controlImg(i);
         }


    }
    @Step("Logger -> <text>")
    public void loggerInfo (String text){

        logger.info(text);

    }
    public void controlImg(WebElement imageWebElement){

        Object result = ((JavascriptExecutor) driver).executeScript(
                "return arguments[0].complete && "+
                        "typeof arguments[0].naturalWidth != \"undefined\" && "+
                        "arguments[0].naturalWidth > 0", imageWebElement);

        if (result instanceof Boolean) {

            Boolean loaded = (Boolean) result;

            if(!loaded) {

                logger.info(String.format("\nImage of '%s' element at '%s' has not been loaded.\\n",imageWebElement, imageWebElement.getLocation()));

            }else {

            }

        }else {

            logger.info(String.format("\nImage of '%s' element at '%s' has not been loaded.\n",imageWebElement, imageWebElement.getLocation()));

        }

    }


    @Step("Find broken images with http <key>")
    public WebDriver findBrokenImagesWithHttp(String key) throws IOException {
        ElementInfo elementInfo = StoreHelper.INSTANCE.findElementInfoByKey(key);
        List<WebElement> images = driver.findElements(ElementHelper.getElementInfoToBy(elementInfo));

        for (WebElement image : images) {
            String imageSrc = image.getAttribute("src");
            URL url = new URL(imageSrc);
            URLConnection urlConnection = url.openConnection();
            HttpURLConnection httpURLConnection = (HttpURLConnection) urlConnection;
            httpURLConnection.setConnectTimeout(5000);
            httpURLConnection.connect();

            if (httpURLConnection.getResponseCode() != 200)
                logger.warn(imageSrc + " >> " + httpURLConnection.getResponseCode() + " >> "
                        + httpURLConnection.getResponseMessage());

            httpURLConnection.disconnect();
        }
        return driver;
    }
    @Step("Javascript ile tıkla <key>")
    public void javaScriptClicker(String key){
        isElementVisible(key,5);
        isElementClickable(key,5);
        ElementInfo elementInfo = StoreHelper.INSTANCE.findElementInfoByKey(key);
        WebElement element = driver.findElement(ElementHelper.getElementInfoToBy(elementInfo));
        JavascriptExecutor executor = (JavascriptExecutor)driver;
        executor.executeScript("arguments[0].click();", element);
    }
    protected boolean isPresent(By locator) {
        try {
            driver.findElement(locator);
            return true;
        } catch (NoSuchElementException ignored) {
            return false;
        }
    }
    protected void click(By locator) {
        try {
            waitClickable(locator).click();
        } catch (Exception e) {
            WebElement el = waitVisible(locator);
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", el);
        }
    }    protected WebElement waitVisible(By locator) {
        return webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    protected WebElement waitClickable(By locator) {
        return webDriverWait.until(ExpectedConditions.elementToBeClickable(locator));
    }
    protected void type(By locator, String text) {
        WebElement el = waitVisible(locator);
        el.clear();
        el.sendKeys(text);
    }
    /** Elementi ekrana kaydır (merkeze) */
    protected void scrollIntoView(WebElement el) {
        ((JavascriptExecutor) driver)
                .executeScript("arguments[0].scrollIntoView({block:'center'});", el);
    }
    protected void selectByValue(By selectLocator, String value) {
        WebElement el = waitVisible(selectLocator);
        new Select(el).selectByValue(value);
    }

    public void switchToNewTabIfOpened() {
        ArrayList<String> handles = new ArrayList<>(driver.getWindowHandles());
        if (handles.size() > 1) {
            driver.switchTo().window(handles.get(handles.size() - 1));
        }
    }

    /** Aktif sekmeyi kapatıp (eğer 1'den fazlaysa) diğerine geç. */
    public void closeCurrentTabIfMoreThanOneAndSwitch() {
        ArrayList<String> handles = new ArrayList<>(driver.getWindowHandles());
        if (handles.size() > 1) {
            String current = driver.getWindowHandle();

            driver.close();

            handles = new ArrayList<>(driver.getWindowHandles());
            driver.switchTo().window(handles.get(0));
        } else {
            driver.navigate().back();
        }
    }

}




