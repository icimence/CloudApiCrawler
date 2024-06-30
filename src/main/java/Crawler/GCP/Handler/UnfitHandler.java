package Crawler.GCP.Handler;

import com.alibaba.fastjson2.JSONObject;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;


public class UnfitHandler implements Handler {
    @Override
    public void handle(JSONObject json, WebElement element, WebDriver driver) {
        System.out.println("unfit");
    }
}
