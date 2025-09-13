package Pages;

import com.thoughtworks.gauge.Step;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedCondition;
import step.StepImplementation;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class SearchResultPage extends StepImplementation {


    private static String lastKeyword;


    // ---- Locators ----
    private final By productCards = By.cssSelector("div.s-main-slot [data-asin][data-component-type='s-search-result']");
    private final By titleLinkInCard = By.cssSelector("h2 a, h2 a.a-link-normal");
    private final By minPriceInput = By.cssSelector("#low-price, input[name='low-price']");
    private final By maxPriceInput = By.cssSelector("#high-price, input[name='high-price']");
    private final By sortSelect   = By.id("s-result-sort-select");


    private void waitCards() {
        webDriverWait.until((ExpectedCondition<Boolean>) d -> d.findElements(productCards).size() > 0);
    }

    private void gotoSearchUrl(String keyword) throws UnsupportedEncodingException {
        String url = "https://www.amazon.com.tr/s?k=" + URLEncoder.encode(keyword, String.valueOf(StandardCharsets.UTF_8));
        driver.navigate().to(url);
    }

    public void waitForResultsRobust(String keyword) throws UnsupportedEncodingException {
        try {
            waitCards();
        } catch (TimeoutException first) {
            gotoSearchUrl(keyword);
            try {
                waitCards();
            } catch (TimeoutException second) {
                driver.navigate().refresh();
                waitCards();
            }
        }
    }


    private void ensureAtLeastNCards(int n) {
        int guard = 0, sameCountStreak = 0, last = -1;
        while (collectProductCards().size() < n && guard < 12) {
            ((JavascriptExecutor) driver).executeScript("window.scrollTo(0, document.body.scrollHeight)");
            try { Thread.sleep(300); } catch (InterruptedException ignored) {}
            int now = collectProductCards().size();
            sameCountStreak = (now == last) ? sameCountStreak + 1 : 0;
            last = now;
            if (sameCountStreak >= 3) break;
            guard++;
        }
    }

    private boolean goNextPageIfAvailable() {
        try {
            List<WebElement> next = driver.findElements(By.cssSelector("a.s-pagination-next"));
            if (next.isEmpty()) return false;
            WebElement btn = next.get(0);
            if ("true".equalsIgnoreCase(btn.getAttribute("aria-disabled"))) return false;
            safeOpenLink(btn);
            waitCards();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private void safeOpenLink(WebElement link) {
        scrollIntoView(link);
        try {
            link.click();
        } catch (Exception e) {
            String href = null;
            try { href = link.getAttribute("href"); } catch (Exception ignored) {}
            if (href == null || href.isBlank()) throw new RuntimeException(e);
            driver.navigate().to(href);
        }
    }


    private WebElement pickBestLink(WebElement card) {
        List<WebElement> dp = card.findElements(By.cssSelector("a[href*='/dp/']"));
        if (!dp.isEmpty()) return dp.get(0);
        List<WebElement> titles = card.findElements(titleLinkInCard);
        if (!titles.isEmpty()) return titles.get(0);
        List<WebElement> imgs = card.findElements(By.cssSelector("a.a-link-normal.s-no-outline"));
        if (!imgs.isEmpty()) return imgs.get(0);
        return null;
    }

    private List<WebElement> collectProductCards() {
        return driver.findElements(productCards);
    }

    public int resultCount() {
        return (int) collectProductCards().stream().filter(Objects::nonNull).count();
    }

    private String extractTitle(WebElement root) {
        try {
            return root.findElement(By.cssSelector("h2")).getText().trim();
        } catch (Exception e) { return ""; }
    }

    private int extractPriceTL(WebElement root) {
        try {
            String whole = root.findElement(By.cssSelector(".a-price span.a-price-whole"))
                    .getText().replaceAll("[^0-9]", "");
            String frac = "00";
            try {
                frac = root.findElement(By.cssSelector(".a-price span.a-price-fraction"))
                        .getText().replaceAll("[^0-9]", "");
                if (frac.length() == 1) frac = frac + "0";
            } catch (Exception ignored) {}
            String merged = whole + frac;
            return Integer.parseInt(merged);
        } catch (Exception e) {
            try {
                String txt = root.findElement(By.cssSelector(".a-price .a-offscreen"))
                        .getAttribute("innerText");
                String digits = txt.replaceAll("[^0-9]", "");
                if (digits.length() >= 3) return Integer.parseInt(digits);
            } catch (Exception ignored) {}
            return 0;
        }
    }

    private int extractReviewCount(WebElement root) {
        String[] csses = new String[] {
                "span.a-size-base.s-underline-text",
                "span.a-size-base.s-underline-text.aok-inline-block",
                "span[aria-label*='oy']",
                "span[aria-label*='değerlendirme']",
                "span[aria-label*='ratings']",
                "span[aria-label*='reviews']"
        };
        for (String sel : csses) {
            try {
                WebElement el = root.findElement(By.cssSelector(sel));
                String txt = (el.getAttribute("aria-label") != null && !el.getAttribute("aria-label").isBlank())
                        ? el.getAttribute("aria-label") : el.getText();
                String digits = txt.replaceAll("[^0-9]", "");
                if (!digits.isBlank()) return Integer.parseInt(digits);
            } catch (Exception ignored) {}
        }

        try {
            WebElement el = root.findElement(By.cssSelector("span.a-icon-alt"));
            WebElement parent = el.findElement(By.xpath("./ancestor::span[contains(@class,'a-declarative') or contains(@class,'a-icon-row')]"));
            WebElement rc = parent.findElement(By.xpath(".//following::span[contains(@class,'s-underline-text')][1]"));
            String digits = rc.getText().replaceAll("[^0-9]", "");
            if (!digits.isBlank()) return Integer.parseInt(digits);
        } catch (Exception ignored) {}
        return 0;
    }

    private double extractStars(WebElement root) {
        String[] csses = new String[] {
                "i.a-icon-star-small span.a-icon-alt",
                "span[aria-label*='yıldız']",
                "span[aria-label*='stars']",
                "span.a-icon-alt"
        };
        for (String sel : csses) {
            try {
                WebElement el = root.findElement(By.cssSelector(sel));
                String t = (el.getAttribute("aria-label") != null && !el.getAttribute("aria-label").isBlank())
                        ? el.getAttribute("aria-label") : el.getText();
                java.util.regex.Matcher m = java.util.regex.Pattern
                        .compile("([0-9]+(?:[\\.,][0-9]+)?)")
                        .matcher(t);
                if (m.find()) {
                    String num = m.group(1).replace(",", "."); // TR ondalık
                    return Double.parseDouble(num);
                }
            } catch (Exception ignored) {}
        }
        return 0.0;
    }

    private boolean isSponsored(WebElement card) {
        if (!card.findElements(By.cssSelector("[data-component-type='sp-sponsored-result']")).isEmpty()) {
            return true;
        }
        return !card.findElements(
                By.xpath(".//span[contains(normalize-space(.),'Sponsorlu')]")
        ).isEmpty();
    }


    private static class ProductCard {
        final WebElement root;
        final String title;
        final int priceTL;
        final double stars;
        final int reviews;
        final WebElement link;
        ProductCard(WebElement r, String t, int p, double s, int rc, WebElement l) {
            root = r; title = t; priceTL = p; stars = s; reviews = rc; link = l;
        }
    }

    private List<ProductCard> buildCards() {
        ensureAtLeastNCards(20);

        List<ProductCard> out = new ArrayList<>();
        for (WebElement card : collectProductCards()) {
            try {
                if (card == null) continue;
                if (isSponsored(card)) continue;

                WebElement link = pickBestLink(card);
                if (link == null) continue;

                String title = extractTitle(card);
                if (title == null || title.isBlank()) continue;

                int price = extractPriceTL(card);
                int rc = extractReviewCount(card);
                double stars = extractStars(card);

                if (price <= 0) continue;        // <<< ekle
                out.add(new ProductCard(card, title, price, stars, rc, link));
            } catch (StaleElementReferenceException ignored) {
            } catch (Exception ignored) {}
        }

        if (out.isEmpty() && goNextPageIfAvailable()) {
            return buildCards();
        }
        return out;
    }

    private void resetToBareSearch() {
        try {
            if (lastKeyword == null || lastKeyword.isBlank()) {
                // Hiç set edilmemişse, mevcut URL’den k paramını dene (emniyet kemeri)
                String url = driver.getCurrentUrl();
                String k = null;
                try {
                    java.net.URI uri = java.net.URI.create(url);
                    String q = uri.getQuery();
                    if (q != null) {
                        for (String part : q.split("&")) {
                            if (part.startsWith("k=")) {
                                k = java.net.URLDecoder.decode(part.substring(2), java.nio.charset.StandardCharsets.UTF_8);
                                break;
                            }
                        }
                    }
                } catch (Exception ignored) {}
                if (k != null && !k.isBlank()) lastKeyword = k;
            }
            if (lastKeyword == null || lastKeyword.isBlank()) {
                throw new IllegalStateException("Arama anahtarı (lastKeyword) bilinmiyor; önce arama adımını çalıştırın.");
            }

            String clean = "https://www.amazon.com.tr/s?k=" +
                    java.net.URLEncoder.encode(lastKeyword, String.valueOf(java.nio.charset.StandardCharsets.UTF_8));
            driver.navigate().to(clean);
            waitCards();
        } catch (Exception ignored) { }
    }




    private List<ProductCard> topReviewedSortedByPriceAsc(int topN) {
        List<ProductCard> cards = buildCards();
        if (cards.isEmpty()) return cards;


        cards.sort(Comparator.comparingInt((ProductCard c) -> c.reviews).reversed());


        int n = Math.max(1, topN);
        List<ProductCard> top = cards.stream()
                .limit(n)
                .collect(Collectors.toList());


        top.sort(Comparator.comparingInt((ProductCard c) -> c.priceTL == 0 ? Integer.MAX_VALUE : c.priceTL));


        boolean allZero = top.stream().allMatch(c -> c.priceTL == 0);
        if ((top.isEmpty() || allZero) && goNextPageIfAvailable()) {
            return topReviewedSortedByPriceAsc(topN);
        }
        return top;
    }


    public void openNthProduct(int n) {
        // kartlar yeterli değilse aşağı kaydır
        ensureAtLeastNCards(n + 2);
        List<WebElement> cards = collectProductCards();
        if (cards.size() < n) throw new IllegalStateException("Yeterli ürün yok. Bulunan: " + cards.size());
        WebElement link = pickBestLink(cards.get(n - 1));
        if (link == null) throw new IllegalStateException("Ürün linki bulunamadı (n=" + n + ")");
        safeOpenLink(link);
    }


    public void openTopReviewedByRank(int rank) {
        resetToBareSearch(); // filtreleri temizler
        List<ProductCard> top = topReviewedSortedByPriceAsc(12);
        if (top.size() < rank) {
            throw new IllegalStateException("Listelenecek uygun ürün bulunamadı (rank=" + rank + ", size=" + top.size() + ").");
        }
        WebElement link = top.get(rank - 1).link;
        safeOpenLink(link);
    }



    public void openCheapestAmongTopReviewed() {
        openTopReviewedByRank(1);
    }


    public void applyPriceRange(String keyword, int minTry, int maxTry) throws UnsupportedEncodingException {
        try {
            type(minPriceInput, String.valueOf(minTry));
            type(maxPriceInput, String.valueOf(maxTry));
            driver.findElement(maxPriceInput).sendKeys(Keys.ENTER);
            waitCards();
        } catch (Exception e) {
            int minKurus = minTry * 100;
            int maxKurus = maxTry * 100;
            String url = "https://www.amazon.com.tr/s?k=" + URLEncoder.encode(keyword, String.valueOf(StandardCharsets.UTF_8))
                    + "&rh=p_36%3A" + minKurus + "-" + maxKurus;
            driver.navigate().to(url);
            waitCards();
        }
    }

    public void sortByPriceLowToHigh() {
        try {
            selectByValue(sortSelect, "price-asc-rank");
        } catch (Exception e) {
            driver.navigate().refresh();
            selectByValue(sortSelect, "price-asc-rank");
        }
        waitCards();
    }


    @Step("<keyword> anahtar kelimesiyle arama yapılır")
    public void search(String keyword) throws UnsupportedEncodingException {
        lastKeyword = keyword;
        new HomePage().search(keyword);

    }

    @Step("fiyat aralığını <min> - <max> TRY olarak uygulanır")
    public void applyPriceRange2(int min, int max) throws UnsupportedEncodingException {
        applyPriceRange(lastKeyword, min, max);
    }

    @Step("sonuçları en düşük fiyata göre sıralanır")
    public void sortLowToHigh() {
        sortByPriceLowToHigh();
    }

    @Step("sonuçlardaki 7. ürün açılır")
    public void openSeventhProduct() {
        openNthProduct(7);
        switchToNewTabIfOpened();
    }
}
