package Crawler.AWS.Handler;

import Crawler.AWS.AWSCrawler;
import Crawler.AWS.Struct.DataType;
import Crawler.AWS.Struct.DataTypeField;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ResponseElementHandler implements Handler {
    private final Map<String, DataType> dataTypeMap;

    public ResponseElementHandler(Map<String, DataType> dataTypeMap) {
        this.dataTypeMap = dataTypeMap;
    }

    @Override
    public void handle(JSONObject json, List<WebElement> elements, WebDriver driver, String name) {
        List<WebElement> divElements = elements.stream()
                .filter(element -> "div".equals(element.getTagName()))
                .collect(Collectors.toList());

        JSONArray elementsArray = new JSONArray();
        json.put(name, elementsArray);
        List<DataType> usedTypes = new ArrayList<>();


        if (divElements.size() == 1) {
            WebElement div = divElements.get(0);
            List<WebElement> paramNames = div.findElements(By.tagName("dt"));
            List<WebElement> paramDescriptions = div.findElements(By.tagName("dd"));
            for (int i = 0; i < paramNames.size(); i++) {
                WebElement paramDesc = paramDescriptions.get(i);
                if (!paramDesc.findElements(By.tagName("a")).isEmpty()) {
                    WebElement linkElement = paramDesc.findElements(By.tagName("a")).get(0);
                    usedTypes.add(dataTypeMap.get(linkElement.getText()));
                    String description = paramDesc.findElement(By.xpath("./p[1]")).getText();
                    String type = "object (".concat(linkElement.getText()).concat(")");
                    DataTypeField dataTypeField = new DataTypeField(paramNames.get(i).getText(), description, type, true, null);
                    elementsArray.add(dataTypeField);
                } else {
                    List<WebElement> pElements = paramDesc.findElements(By.tagName("p"));
                    DataTypeField dataTypeField = AWSCrawler.getDataTypeField(pElements, paramNames.get(i).getText());
                    elementsArray.add(dataTypeField);
                }
            }

            if (!usedTypes.isEmpty()) {
                JSONArray typesArray = new JSONArray(usedTypes);
                json.put("Structs", typesArray);
            }

        } else {
            Handler handler = new UnfitHandler();
            handler.handle(json, elements, driver, name);
        }
    }
}
