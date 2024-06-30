package Crawler.GCP.Handler;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

public class IAMHandler implements Handler {
    private final String name;

    public IAMHandler(String name) {
        this.name = name;
    }

    @Override
    public void handle(JSONObject json, WebElement element, WebDriver driver) {
        JSONArray params = new JSONArray();
        List<WebElement> elementList = element.findElements(By.xpath(".//ul/li"));
        elementList.forEach(e -> params.add(e.getText()));
        json.put(name, params);
    }
}
