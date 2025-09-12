package Pages;

import com.thoughtworks.gauge.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import step.StepImplementation;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import static java.lang.Thread.sleep;

public class ProductPage  extends StepImplementation {



    // Yorum linki – iki şablon
    private final By seeAllReviewsLink = By.cssSelector(
            "#reviews-medley-footer a[data-hook='see-all-reviews-link-foot'], a[data-hook='see-all-reviews-link-foot']"
    );

    // Sepete ekle butonlarının olası id/selector’ları
    private final By addToCartBtn = By.cssSelector(
            "#add-to-cart-button, #add-to-cart-button-ubb, input#add-to-cart-button-ubb, input[name='submit.add-to-cart']"
    );

    // Buying options (alternatif satıcılardan ekleme) linki
    private final By buyingOptionsLink = By.cssSelector(
            "#buybox-see-all-buying-choices-announce, a#buybox-see-all-buying-choices-announce"
    );

    // Buying options içindeki ATC
    private final By buyingOptionsAddToCart = By.cssSelector(
            "#aod-offer-list input[name='submit.addToCart'], input[name='submit.addToCart']"
    );

    // Koruma planı / upsell modalında “Hayır / Şimdi değil” vb.
    private final By noCoverageBtns = By.cssSelector(
            "#attachSiNoCoverage, [data-action='aod-close'], button[aria-label*='Hayır'], input[aria-labelledby*='attachSiNoCoverage-announce']"
    );

    // Sepet sayacı
    private final By cartCount = By.id("nav-cart-count");



    /** Header’daki sepet sayısını okur (bulamazsa 0). */
    private int currentCartCount() {
        try {
            String txt = driver.findElement(cartCount).getText().trim();
            if (txt.isEmpty()) return 0;
            return Integer.parseInt(txt);
        } catch (Exception e) {
            return 0;
        }
    }

    /** “Tüm değerlendirmeler” sayfasını aç. Gerekirse ASIN ile direkt review sayfasına git. */
    public ReviewPage openAllReviews() {
        // Linkleri sırayla dene
        List<By> candidates = Arrays.asList(
                By.cssSelector("#reviews-medley-footer a[data-hook='see-all-reviews-link-foot']"),
                By.cssSelector("a[data-hook='see-all-reviews-link-foot']"),
                By.cssSelector("a[href*='/product-reviews/']")
        );
        WebElement link = null;
        for (By loc : candidates) {
            List<WebElement> found = driver.findElements(loc);
            if (!found.isEmpty()) { link = found.get(0); break; }
        }
        if (link != null) {
            try { scrollIntoView(link); } catch (Exception ignored) {}
            click((By) link);
            return new ReviewPage();
        }
        // Fallback: ASIN ile review sayfasına git
        String asin = getASIN();
        if (asin == null || asin.isBlank()) {
            throw new IllegalStateException("Yorum linki yok ve ASIN bulunamadı.");
        }
        driver.navigate().to("https://www.amazon.com.tr/product-reviews/" + asin);
        return new ReviewPage();
    }

    /** Sepete ekleme: varyasyon seç + buying options + başarı kontrolü */
    public boolean addToCart2() throws InterruptedException {
        int before = currentCartCount();

        // 1) Direkt dene
        if (tryClickAddToCartAndConfirm(before)) return true;

        // 2) Varyasyonlar varsa birini seç ve tekrar dene
        trySelectAnyVariant();
        if (tryClickAddToCartAndConfirm(before)) return true;

        // 3) Buying options üzerinden dene
        if (tryBuyingOptionsAndConfirm(before)) return true;

        // 4) Son bir kez daha dene (bazı sayfalarda buton geç geliyor)
        sleep(500);
        if (tryClickAddToCartAndConfirm(before)) return true;

        return false;
    }

    private boolean tryClickAddToCartAndConfirm(int beforeCount) {
        try {
            waitVisible(addToCartBtn);
            click(addToCartBtn);

            // Olası koruma planı modalı
            closeProtectionIfShown();

            // Başarı mesajı ya da sepet sayacı artışı
            if (waitAddedBanner() || waitCartCountIncrease(beforeCount)) return true;
        } catch (Exception ignored) {}
        return false;
    }

    private void closeProtectionIfShown() {
        try {
            // Modal kısa süre sonra gelebilir
            sleep(400);
            List<WebElement> btns = driver.findElements(noCoverageBtns);
            if (!btns.isEmpty()) {
                try { click((By) btns.get(0)); } catch (Exception ignored) {}
            }
        } catch (Exception ignored) {}
    }

    private boolean waitAddedBanner() {
        List<By> banners = Arrays.asList(
                By.id("NATC_SMART_WAGON_CONF_MSG_SUCCESS"),
                By.cssSelector("#attach-added-to-cart-message"),
                By.cssSelector("[data-feature-name='smart-wagon']"),
                By.cssSelector("h1:contains('Sepete eklendi'), h4:contains('Sepete eklendi')")
        );
        for (By b : banners) {
            try {
                webDriverWait.until(ExpectedConditions.presenceOfElementLocated(b));
                return true;
            } catch (Exception ignored) {}
        }
        return false;
    }

    private boolean waitCartCountIncrease(int before) {
        try {
            return webDriverWait.until(d -> currentCartCount() > before);
        } catch (Exception e) {
            return currentCartCount() > before;
        }
    }

    private void trySelectAnyVariant() throws InterruptedException {
        // Dropdown’lar
        List<By> selects = Arrays.asList(
                By.id("native_size_name"),
                By.id("native_color_name"),
                By.cssSelector("select[name*='dropdown_selected_size_name']"),
                By.cssSelector("select[id*='dropdown_selected_size_name']")
        );
        for (By s : selects) {
            List<WebElement> els = driver.findElements(s);
            if (!els.isEmpty()) {
                try {
                    Select sel = new Select(els.get(0));
                    if (sel.getOptions().size() > 1) sel.selectByIndex(1);
                } catch (Exception ignored) {}
            }
        }
        // Swatch butonları
        List<By> swatches = Arrays.asList(
                By.cssSelector("[id^='variation_'] li:not(.a-selected) [role='button']"),
                By.cssSelector("[id^='variation_'] li:not(.a-selected)")
        );
        for (By sw : swatches) {
            List<WebElement> els = driver.findElements(sw);
            if (!els.isEmpty()) {
                try { click((By) els.get(0)); break; } catch (Exception ignored) {}
            }
        }
        sleep(300);
    }

    private boolean tryBuyingOptionsAndConfirm(int before) {
        try {
            List<WebElement> linkEls = driver.findElements(buyingOptionsLink);
            if (linkEls.isEmpty()) return false;
            click((By) linkEls.get(0));

            // Buying options paneli / sayfası gelsin
            webDriverWait.until(ExpectedConditions.presenceOfElementLocated(By.id("aod-container")));

            // İlk teklif → Add to Cart
            waitVisible(buyingOptionsAddToCart);
            click(buyingOptionsAddToCart);

            closeProtectionIfShown();

            if (waitAddedBanner() || waitCartCountIncrease(before)) return true;
        } catch (Exception ignored) {}
        return false;
    }

    /** Sayfadaki ASIN’i bul. */
    private String getASIN() {
        try {
            return driver.findElement(By.id("ASIN")).getAttribute("value");
        } catch (NoSuchElementException e1) {
            try {
                WebElement el = driver.findElement(By.cssSelector("[data-asin]"));
                String v = el.getAttribute("data-asin");
                if (v != null && !v.isBlank()) return v;
            } catch (Exception ignored) {}
            try {
                String url = driver.getCurrentUrl();
                int dp = url.indexOf("/dp/");
                if (dp > 0) {
                    String rest = url.substring(dp + 4);
                    int slash = rest.indexOf('/');
                    return (slash > 0 ? rest.substring(0, slash) : rest);
                }
            } catch (Exception ignored) {}
        }
        return null;
    }
    // 6) Yorumları yaz
    @Step("ürünün tüm yorumlarını açıp ilk 20'sini dosyaya yazarım")
    public void dumpReviews() {
        ProductPage product = new ProductPage();
        ReviewPage rev = product.openAllReviews();
        List<String> comments = rev.collectTopNReviews(20);
        Path file = rev.writeReviewsToNotepad(comments, "urun-yorumlari");
        System.out.println("Yorumlar kaydedildi: " + file.toAbsolutePath());
    }

    // 8) Sepete ekle (ilk ürün)
    @Step("ürünü sepete eklerim")
    public void addToCart() throws InterruptedException {
        boolean added = addToCart2();
        if (!added) throw new AssertionError("Ürün sepete eklenemedi.");
    }



    @Step("en çok yorumlu ürünler arasından en ucuz olanını sepete eklerim")
    public void addCheapestFromTopReviewed() throws InterruptedException {
        // ürün eklendikten sonra aramaya dön
        new HomePage().backToResults();

        SearchResultPage results = new SearchResultPage();
        results.openCheapestAmongTopReviewed();   // resetToBareSearch() içeride çağrılıyor (static lastKeyword sayesinde OK)
        switchToNewTabIfOpened();

        boolean added = new ProductPage().addToCart2();
        if (!added) throw new AssertionError("İkinci ürün sepete eklenemedi.");
    }

}
