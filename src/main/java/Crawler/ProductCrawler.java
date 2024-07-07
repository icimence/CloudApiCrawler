package Crawler;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public abstract class ProductCrawler {
    public final List<ProductStruct> getAllProduct() {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--lang=en-US");
        WebDriver driver = new ChromeDriver(options);
        driver.manage().timeouts().implicitlyWait(Duration.ofMillis(1500));
        try {
            return getProducts(driver);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            closeAllWindows(driver);
        }
        return new ArrayList<>();
    }

    protected abstract List<ProductStruct> getProducts(WebDriver driver);


    private void closeAllWindows(WebDriver driver) {
        for (String windowHandle : driver.getWindowHandles()) {
            driver.switchTo().window(windowHandle);
            driver.close();
        }
        driver.quit();
    }
}
