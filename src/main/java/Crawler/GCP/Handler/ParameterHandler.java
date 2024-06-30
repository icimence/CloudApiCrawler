package Crawler.GCP.Handler;

import Crawler.GCP.GCPUtil;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

public class ParameterHandler implements Handler {

    private final String name;

    public ParameterHandler(String name) {
        this.name = name;
    }

    @Override
    public void handle(JSONObject json, WebElement element, WebDriver driver) {
        JSONArray params = new JSONArray();
        json.put(name, params);
        List<WebElement> prameterList = element.findElements(By.xpath(".//tbody/tr"));
        for (WebElement parameter : prameterList) {
            JSONObject param = GCPUtil.parseGcpParamTable(parameter, driver);
            params.add(param);
        }
    }
}
