package Crawler.GCP.Handler;

import Crawler.GCP.GCPUtil;
import Crawler.GCP.Struct.Field;
import Crawler.GCP.Struct.GcpEnumStruct;
import Crawler.GCP.Struct.GcpStruct;
import Util.Util;
import com.alibaba.fastjson2.JSONObject;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import Crawler.GCP.Struct.GcpObjectStruct;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class BodyHandler implements Handler {

    private final String name;
    private Set<GcpStruct> bodyStructs;
    private static final String DESC = "description";
    private static final String SCHEMA = "SCHEMA_REPRESENTATION";
    private static final String FIELDS = "FIELDS";
    private static final String ENUM = "ENUM_VALUES";

    public BodyHandler(String name, Set<GcpStruct> bodyStructs) {
        this.name = name;
        this.bodyStructs = bodyStructs;
    }

    public void setBodyStructs(Set<GcpStruct> bodyStructs) {
        this.bodyStructs = bodyStructs;
    }

    @Override
    public void handle(JSONObject json, WebElement element, WebDriver driver) {
        List<WebElement> links = element.findElements(By.xpath("./p//a"));
        if (links.isEmpty()) {
            WebElement desc = element.findElement(By.xpath("./p"));
            json.put(name, desc.getText());
        } else {
            handleBodyLink(links.get(0), driver, "object");
            String bodyText = element.getText();
            bodyText = bodyText.replace(links.get(0).getText(), "object (".concat(links.get(0).getText()).concat(")"));
            json.put(name, bodyText);
        }
    }

    private void handleBodyLink(WebElement link, WebDriver driver, String mode) {
        String linkName = link.getText();
        String href = link.getAttribute("href");
        String linkText = null;
        int index = href.indexOf("#");
        //linkText可能会带有层级关系，而linkName不会
        if (index != -1) {
            linkText = href.substring(index + 1);
        }
        if (bodyStructs.contains(new GcpObjectStruct(linkName))) {
            return;
        }

        String originalWindow = Util.openInNewTab(driver, link);
        String descId = linkText == null ? DESC : linkText.concat(".").concat(DESC);
        WebElement descElement = driver.findElement(By.id(descId));
        String descText = descElement.getText();
        GcpStruct bodyStruct;

        if (mode.equals("object")) {
            String jsonId = linkText == null ? SCHEMA : linkText.concat(".").concat(SCHEMA);
            WebElement schemaElement = driver.findElement(By.id(jsonId)).findElement(By.tagName("tbody"));
            String schemaText = schemaElement.getText();

            List<WebElement> linksInJson = schemaElement.findElements(By.xpath(".//a"));
            for (WebElement linkInJson : linksInJson) {
                JavascriptExecutor js = (JavascriptExecutor) driver;
                String linkUrl = (String) js.executeScript("return arguments[0].getAttribute('href');", linkInJson);
                String recursiveMode = judgeLinkMode(driver, linkUrl);
                handleBodyLink(linkInJson, driver, recursiveMode);
            }

            String fieldsId = linkText == null ? FIELDS : linkText.concat(".").concat(FIELDS);
            WebElement fieldsElement = driver.findElement(By.id(fieldsId));
            List<Field> fields = parseFields(fieldsElement);
            bodyStruct = new GcpObjectStruct(linkName, descText, schemaText, fields);
        } else {
            String enumId = linkText == null ? ENUM : linkText.concat(".").concat(ENUM);
            WebElement enumElement = driver.findElement(By.id(enumId));
            bodyStruct = new GcpEnumStruct(GCPUtil.parseEnumTable(enumElement), linkName);
        }

        bodyStructs.add(bodyStruct);

        driver.close();
        driver.switchTo().window(originalWindow);
    }

    private String judgeLinkMode(WebDriver driver, String link) {
        String xpath = String.format("//a[@href='%s']/../..", link);
        WebElement element = driver.findElement(By.xpath(xpath));
        return element.getText().toLowerCase().contains("enum") ? "enum" : "object";
    }

    private List<Field> parseFields(WebElement element) {
        List<Field> res = new ArrayList<>();
        List<WebElement> fields = element.findElements(By.xpath(".//table/tbody/tr"));
        for (WebElement field : fields) {
            String name = field.findElement(By.xpath("./td[1]")).getText();
            WebElement content = field.findElement(By.xpath("./td[2]"));
            List<WebElement> contentList = content.findElements(By.xpath("./p"));
            String type = contentList.get(0).getText();
            String desc = contentList.stream().skip(1).map(WebElement::getText).collect(Collectors.joining("\n"));
            Field newField = new Field(name, type, desc);
            res.add(newField);
        }
        return res;
    }
}