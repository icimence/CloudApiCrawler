package Crawler.AWS.Handler;

import com.alibaba.fastjson2.JSONObject;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.stream.Collectors;

public class UnfitHandler implements Handler {
    @Override
    public void handle(JSONObject json, List<WebElement> elements, WebDriver driver, String name) {
        String allText = elements.stream().map(WebElement::getText).collect(Collectors.joining(";"));
        json.put(name, allText);
    }
}
