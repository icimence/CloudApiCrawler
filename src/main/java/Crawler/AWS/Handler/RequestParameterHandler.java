package Crawler.AWS.Handler;

import Crawler.AWS.AWSCrawler;
import Crawler.AWS.Struct.DataTypeField;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.stream.Collectors;

public class RequestParameterHandler implements Handler{
    @Override
    public void handle(JSONObject json, List<WebElement> elements, WebDriver driver, String name) {
        List<WebElement> divElements = elements.stream()
                .filter(element-> "div".equals(element.getTagName()))
                .collect(Collectors.toList());
        JSONArray paramArray = new JSONArray();
        json.put(name,paramArray);

        if (divElements.size() == 1){
            WebElement div = divElements.get(0);
            List<WebElement> paramNames = div.findElements(By.tagName("dt"));
            List<WebElement> paramDescriptions = div.findElements(By.tagName("dd"));
            for (int i = 0; i < paramNames.size(); i++) {
                List<WebElement> pElements = paramDescriptions.get(i).findElements(By.tagName("p"));
                DataTypeField field = AWSCrawler.getDataTypeField(pElements, paramNames.get(i).getText());
                paramArray.add(field);
            }

        }else{
            Handler handler = new UnfitHandler();
            handler.handle(json, elements, driver, name);
        }
    }
}
