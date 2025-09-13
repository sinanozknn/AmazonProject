package Pages;

import com.thoughtworks.gauge.Step;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.WebDriverWait;
import step.StepImplementation;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ReviewPage extends  StepImplementation {


        private final By reviewItems        = By.cssSelector("[data-hook='review-collapsed']");

        private final By emptyState         = By.cssSelector("#cm-cr-empty-state, div[data-hook='no-reviews-section']");
        private final By reviewsContainer   = By.id("cm_cr-review_list");



    /** Sayfada görünen ilk N yorumu topla (yoksa boş liste döner). */
        public List<String> collectTopNReviews(int maxCount) {

            try {
                new WebDriverWait(driver,10).until(drv ->
                        !drv.findElements(reviewItems).isEmpty()
                                || !drv.findElements(emptyState).isEmpty()
                                || !drv.findElements(reviewsContainer).isEmpty()
                );
            } catch (TimeoutException ignored) {

            }

            List<WebElement> items = driver.findElements(reviewItems);
            if (items.isEmpty()) {
                // Yorum yok → boş liste
                return Collections.emptyList();

            }

            return items.stream()
                    .map(el -> {
                        try {
                            WebElement body = el.findElement(By.cssSelector("span[data-hook='review-body']"));
                            return body.getText().trim();
                        } catch (NoSuchElementException ignored) {
                            return "";
                        }
                    })
                    .filter(s -> !s.isBlank())
                    .limit(maxCount)
                    .collect(Collectors.toList());
        }

        /** Yorumları proje kökünde /reports klasörüne .txt olarak yazar. */
        public Path writeReviewsToNotepad(List<String> reviews, String fileName) {
            try {
                Path reports = Paths.get(System.getProperty("user.dir"), "reports");
                Files.createDirectories(reports);
                if (!fileName.toLowerCase().endsWith(".txt")) fileName += ".txt";
                Path file = reports.resolve(fileName);

                DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
                String header = "Yorumlar (" + LocalDateTime.now().format(fmt) + ")\r\n\r\n";

                String content = header + numbered(reviews);
                if (reviews.isEmpty()) {
                    content += "Bu üründe henüz yorum bulunamadı.\r\n";
                }

                Files.writeString(file, content, StandardCharsets.UTF_8,
                        StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

                return file;
            } catch (Exception e) {
                throw new RuntimeException("Yorum dosyası yazılamadı", e);
            }
        }

        private static String numbered(List<String> list) {
            StringBuilder sb = new StringBuilder();
            int i = 1;
            for (String s : list) {
                sb.append(i++).append(") ").append(s).append("\r\n\r\n");
            }
            return sb.toString();
        }
    /** Ürün detay sayfasına geri dön. */
    public ProductPage backToProduct() {
        driver.navigate().back();
        return new ProductPage();
    }

    @Step("ürün sayfasına geri dönülür")
    public void backToProduct2() {
      backToProduct();
    }

}


