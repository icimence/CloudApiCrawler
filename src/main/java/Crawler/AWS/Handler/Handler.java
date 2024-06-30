package Crawler.AWS.Handler;

import com.alibaba.fastjson2.JSONObject;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

public interface Handler {
    void handle(JSONObject json, List<WebElement> element, WebDriver driver,String name);
}
