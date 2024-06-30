package Crawler.GCP;

import Crawler.GCP.Struct.EnumField;
import Util.Util;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.List;

public class GCPUtil {
    public static JSONObject parseGcpParamTable(WebElement parameter, WebDriver driver) {
        JSONObject param = new JSONObject();
        WebElement name = parameter.findElement(By.xpath("./td[1]"));
        param.put("name", name.getText());

        WebElement type = parameter.findElement(By.xpath("./td[2]/p[1]"));
        param.put("type", type.getText());

        WebElement desc = parameter.findElement(By.xpath("./td[2]/p[2]"));
        if (desc.getText().contains("enum") && !desc.findElements(By.xpath(".//a")).isEmpty()) {
            handleEnumParamType(param, desc, driver);
        } else {
            param.put("desc", desc.getText());
        }

        try {
            WebElement Example = parameter.findElement(By.xpath("./td[2]/p[3]"));
            param.put("example", Example.getText());
        } catch (Exception e) {
            System.out.println("can't find example");
        }
        return param;
    }


    private static void handleEnumParamType(JSONObject json, WebElement element, WebDriver driver) {
        WebElement link = element.findElement(By.xpath(".//a"));
        String linkUrl = link.getAttribute("href");
        String originalWindow = Util.openInNewTab(driver, link);


        WebElement linkElement;
        if (linkUrl.contains("#")) {
            int index = linkUrl.indexOf("#");
            String id = linkUrl.substring(index + 1);
            linkElement = driver.findElement(By.id(id));
        } else {
            linkElement = driver.findElement(By.tagName("html"));
        }
        List<EnumField> enumFields = parseEnumTable(linkElement);
        JSONArray jsonArray = new JSONArray(enumFields);
        json.put("enum", jsonArray);

        driver.close();
        driver.switchTo().window(originalWindow);
    }

    public static List<EnumField> parseEnumTable(WebElement element) {
        List<WebElement> tableContent = element.findElements(By.xpath(".//tbody/tr"));
        List<EnumField> enumFieldList = new ArrayList<>();
        for (WebElement content : tableContent) {
            EnumField enumField = new EnumField(content.findElement(By.xpath("./td[1]")).getText(),
                    content.findElement(By.xpath("./td[2]")).getText());
            enumFieldList.add(enumField);
        }
        return enumFieldList;
    }


}
