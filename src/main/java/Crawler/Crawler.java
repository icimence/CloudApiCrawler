package Crawler;

import CrawlerExceptions.CrawlerException;
import Utils.Util;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSONWriter;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;

public abstract class Crawler {
    protected JSONObject json;

    public Crawler(JSONObject json) {
        this.json = json;
    }

    public final void fetchAPIDoc(String url) throws CrawlerException {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--lang=en-US");
        WebDriver driver = new ChromeDriver(options);
        driver.manage().timeouts().implicitlyWait(Duration.ofMillis(1500));
        try {
            openApiReferencePage(driver, url);
            List<WebElement> linkList = getAllApiReferences(driver);
            iterateAllApis(linkList, driver);
        } catch (Exception e) {
            throw new CrawlerException(url, e.getMessage()+"\n"+e.getCause()+"\n"+ Arrays.toString(e.getStackTrace()));
        } finally {
            closeAllWindows(driver);
        }
    }

    private void closeAllWindows(WebDriver driver) {
        for (String windowHandle : driver.getWindowHandles()) {
            driver.switchTo().window(windowHandle);
            driver.close();
        }
    }

    protected abstract void openApiReferencePage(WebDriver driver, String url);

    protected abstract List<WebElement> getAllApiReferences(WebDriver driver);

    protected abstract JSONArray getProductList();

    public void iterateAllApis(List<WebElement> linkList, WebDriver driver) {
        String originalWindow = driver.getWindowHandle();
        JSONArray productList = getProductList();
        JSONObject json = productList.getJSONObject(0);
        for (WebElement elementCache : linkList) {
            Util.sleepRandomTime(700, 850);
            // 打开每个链接
            String openInNewTab = Keys.chord(Keys.CONTROL, Keys.RETURN);
            elementCache.sendKeys(openInNewTab);

            //该代码是为了寻找第一个非主窗口并切换
            for (String windowHandle : driver.getWindowHandles()) {
                if (!windowHandle.equals(originalWindow)) {
                    driver.switchTo().window(windowHandle);
                    break;
                }
            }

            getApiInfo(driver, json);

            Util.sleepRandomTime(1000, 2000);
            driver.close();
            // 返回原始页面，继续下一个链接
            driver.switchTo().window(originalWindow);
        }

        System.out.println("=================final json start=====================");
        System.out.println(json.toJSONString());
        saveJsonFile(JSON.toJSONString(json, JSONWriter.Feature.PrettyFormat));
        System.out.println("=================final json end=====================");
    }


    protected abstract void getApiInfo(WebDriver driver, JSONObject json);

    protected abstract void saveJsonFile(String json);
}
