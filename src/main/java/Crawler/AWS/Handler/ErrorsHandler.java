package Crawler.AWS.Handler;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.stream.Collectors;

public class ErrorsHandler implements Handler {
    @Override
    public void handle(JSONObject json, List<WebElement> elements, WebDriver driver, String name) {
        List<WebElement> divElement = elements.stream()
                .filter(element -> "div".equals(element.getTagName()))
                .collect(Collectors.toList());
        JSONArray errors = new JSONArray();
        json.put(name, errors);
        if (divElement.size() == 1) {
            WebElement div = divElement.get(0);
            List<WebElement> errorNames = div.findElements(By.tagName("dt"));
            List<WebElement> errorDescriptions = div.findElements(By.tagName("dd"));
            for (int i = 0; i < errorNames.size(); i++) {
                JSONObject jsonError = new JSONObject();
                jsonError.put("name", errorNames.get(i).getText());
                List<WebElement> ErrorDescription = errorDescriptions.get(i).findElements(By.tagName("p"));
                jsonError.put("info", ErrorDescription.get(0).getText());
                String code = ErrorDescription.get(1).getText();
                int index = code.indexOf(": ");
                code = code.substring(index + 2);
                jsonError.put("code", code);
                errors.add(jsonError);
            }
        } else {
            Handler handler = new UnfitHandler();
            handler.handle(json, elements, driver, name);
        }
    }
}
