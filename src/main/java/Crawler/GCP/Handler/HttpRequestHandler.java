package Crawler.GCP.Handler;

import com.alibaba.fastjson2.JSONObject;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class HttpRequestHandler implements Handler {
    private final String name;

    public HttpRequestHandler(String name) {
        this.name = name;
    }

    @Override
    public void handle(JSONObject json, WebElement element, WebDriver driver) {
        WebElement path = element.findElement(By.xpath("./p[1]"));
        json.put(name, path.getText());
    }
}