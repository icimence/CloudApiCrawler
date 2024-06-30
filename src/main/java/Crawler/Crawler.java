package Crawler;

import Util.ApiWebElement;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSONWriter;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.time.Duration;
import java.util.List;
import java.util.Random;

public abstract class Crawler {
    protected JSONObject json;

    public Crawler(JSONObject json) {
        this.json = json;
    }

    public final void fetchAPIDoc() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--lang=en-US");
        WebDriver driver = new ChromeDriver(options);
        try {
            openApiReferencePage(driver);
            List<WebElement> linkList = getAllApiReferences(driver);
            iterateAllApis(linkList, driver);
        } catch (Exception e) {
            System.out.println(e.getMessage());
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

    protected abstract void openApiReferencePage(WebDriver driver);

    protected abstract List<WebElement> getAllApiReferences(WebDriver driver);

    protected abstract JSONArray getProductList();

    public void iterateAllApis(List<WebElement> linkList, WebDriver driver) {
        Random random = new Random();
        String originalWindow = driver.getWindowHandle();
        JSONArray productList = getProductList();
        JSONObject json = productList.getJSONObject(0);
        for (WebElement elementCache : linkList) {
            int delay = 500 + random.nextInt(500);
            try {
                Thread.sleep(delay);
            } catch (InterruptedException e) {
                System.err.println("Sleep was interrupted");
            }
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
            driver.manage().timeouts().implicitlyWait(Duration.ofMillis(500));

            getApiInfo(driver, json);

            delay = 1000 + random.nextInt(2000);
            try {
                Thread.sleep(delay);
            } catch (InterruptedException e) {
                System.err.println("Sleep was interrupted");
            }
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
