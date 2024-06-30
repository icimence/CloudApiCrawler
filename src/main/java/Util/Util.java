package Util;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Random;
import java.util.Set;

public class Util {
    public static String getBaseUrlFromCurrentUrl(String url) {
        try {
            URI uri = new URI(url);
            return uri.getScheme() + "://" + uri.getHost();
        } catch (URISyntaxException e) {
            throw new RuntimeException("Error parsing current URL", e);
        }
    }

    public static JSONObject initApiElement(String name, String desc,
                                            String requestSyntax,
                                            String requestParameters,
                                            String ResponseSyntax,
                                            String ResponseElements,
                                            String Errors) {
        JSONObject obj = new JSONObject();
        obj.put("name", name);
        obj.put("desc", desc);
        obj.put("requestSyntax", requestSyntax);
        obj.put("requestParameters", requestParameters);
        obj.put("responseSyntax", ResponseSyntax);
        obj.put("responseElements", ResponseElements);
        obj.put("errors", Errors);
        return obj;
    }

    public static String openInNewTab(WebDriver driver, WebElement element) {
        Set<String> existingWindows = driver.getWindowHandles();
        String originalWindow = driver.getWindowHandle();

        Random random = new Random();
        int delay = 300 + random.nextInt(200);
        try {
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            System.err.println("Sleep was interrupted");
        }

        String openInNewTab = Keys.chord(Keys.CONTROL, Keys.RETURN);
        element.sendKeys(openInNewTab);

        Set<String> newHandles = driver.getWindowHandles();
        newHandles.removeAll(existingWindows);
        if (newHandles.size() == 1) {
            driver.switchTo().window(newHandles.iterator().next());
        }
        return originalWindow;
    }


}
