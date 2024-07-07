package Crawler.AWS;

import Crawler.AWS.Handler.*;
import Crawler.AWS.Struct.DataType;
import Crawler.AWS.Struct.DataTypeField;
import Crawler.Crawler;
import Utils.Util;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class AWSCrawler extends Crawler {
    public static final String NAME = "aws";
    private static final String ApiPrefix = "API_";
    private static final String RequestSyntaxId = "RequestSyntax";
    private static final String RequestParametersId = "RequestParameters";
    private static final String ResponseSyntaxId = "ResponseSyntax";
    private static final String ResponseElementsId = "ResponseElements";
    private static final String ErrorsId = "Errors";
    private static final String SeeAlsoId = "SeeAlso";
    private static final String[] ElementsId = new String[]{RequestSyntaxId,
            RequestParametersId, ResponseSyntaxId, ResponseElementsId, ErrorsId};
    private static final String SearchElementFormat = "h2[id='%s']";
    private static final String XpathFormat = "//*[@id='%s']/following-sibling::*[position()<count(//*[@id='%s']/following-sibling::*)-count(//*[@id='%s']/following-sibling::*)]";
    private final Map<String, DataType> dataTypes = new HashMap<>();
    private final Map<String, Handler> handlerMap;

    public AWSCrawler(JSONObject json) {
        super(json);
        handlerMap = new HashMap<>();
        handlerMap.put(RequestSyntaxId, new UnfitHandler());
        handlerMap.put(RequestParametersId, new RequestParameterHandler());
        handlerMap.put(ResponseSyntaxId, new UnfitHandler());
        handlerMap.put(ResponseElementsId, new ResponseElementHandler(dataTypes));
        handlerMap.put(ErrorsId, new ErrorsHandler());
    }


    @Override
    public void openApiReferencePage(WebDriver driver, String url) {
        driver.get(url);
        String currentUrl = driver.getCurrentUrl();
        String baseUrl = Util.getBaseUrlFromCurrentUrl(currentUrl);

        WebElement apiHtml = driver.findElement(By.cssSelector("a[aria-label='API Reference HTML']"));
        String href = apiHtml.getAttribute("href");
        String fullUrl = href.startsWith("http") ? href : baseUrl + href;
        //此处已经进入API reference页面中
        driver.get(fullUrl);
    }

    @Override
    public List<WebElement> getAllApiReferences(WebDriver driver) {
        //todo:其他云产品可能没有直接的actions可以访问
        WebElement actionLink = driver.findElement(By.linkText("Actions"));
        actionLink.click();
        Util.sleepRandomTime(1000, 500);
        List<WebElement> apiListDiv = driver.findElements(By.xpath(".//div[@class='itemizedlist']"));
        List<WebElement> apiList = apiListDiv.stream().flatMap(e -> e.findElements(By.xpath(".//li//a"))
                .stream()).collect(Collectors.toList());

        System.out.println("=======获取到API列表如下：=======");
        apiList.forEach(e -> System.out.println(e.getText()));
        System.out.println("==============================");
        //完成所有api李彪爬取，接下来完成dataTypes爬取
        getAllDataTypes(driver);
        return apiList;
    }

    @Override
    protected JSONArray getProductList() {
        return super.json.getJSONArray(NAME);
    }


    @Override
    public void getApiInfo(WebDriver driver, JSONObject json) {
        JSONObject apiJsonElement = new JSONObject();
        json.getJSONArray("apiList").add(apiJsonElement);

        WebElement titleElement = driver.findElement(By.cssSelector("h1[class='topictitle']"));
        String title = titleElement.getText();
        System.out.println(title);
        apiJsonElement.put("Name", title);

        WebElement firstParagraph = titleElement.findElement(By.xpath("following-sibling::p[1]"));
        String description = firstParagraph.getText();
        apiJsonElement.put("Desc", description);

        List<String> existId = new ArrayList<>();
        for (String element : ElementsId) {
            String searchId = ApiPrefix.concat(title).concat("_").concat(element);
            String cssSelector = String.format(SearchElementFormat, searchId);
            List<WebElement> searchResults = driver.findElements(By.cssSelector(cssSelector));
            if (!searchResults.isEmpty()) {
                existId.add(searchId);
            }
        }

        String endFlag = ApiPrefix.concat(title).concat("_").concat(SeeAlsoId);

        for (int i = 0; i < existId.size(); i++) {
            String before = existId.get(i);
            String end;
            if (i == existId.size() - 1) {
                end = endFlag;
            } else {
                end = existId.get(i + 1);
            }
            String xpath = String.format(XpathFormat, before, before, end);
            List<WebElement> searchResults = driver.findElements(By.xpath(xpath));
            String partName = existId.get(i).substring(existId.get(i).lastIndexOf("_") + 1);
            Handler handler = handlerMap.getOrDefault(partName, new UnfitHandler());
            handler.handle(apiJsonElement, searchResults, driver, partName);
            //todo: 更加细化处理文字内容，可以考虑后续数据加工  div=>dl=>dt/dd

        }

    }

    @Override
    protected void saveJsonFile(String json) {
        String filePath = "src/main/java/ResultFile/aws.json";
        File file = new File(filePath);
        try {
            if (!file.exists()) {
                file.createNewFile();
            }

            FileWriter fw = new FileWriter(file);
            fw.write(json);

            fw.close();
            System.out.println("json写入完成");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void getAllDataTypes(WebDriver driver) {
        WebElement dataTypeLink = driver.findElement(By.linkText("Data Types"));
        String originalWindow = Util.openInNewTab(driver, dataTypeLink);
        List<WebElement> linkLists = driver.findElements(By.xpath(".//div[@class='itemizedlist']"));
        List<WebElement> links = linkLists.stream().flatMap(e -> e.findElements(By.xpath(".//li//a"))
                .stream()).collect(Collectors.toList());
        System.out.println("=======获取到DataType列表如下：=======");
        links.forEach(e -> System.out.println(e.getText()));
        System.out.println("===================================");
        for (WebElement link : links) {
            String tempWindow = Util.openInNewTab(driver, link);

            parseDataType(driver);

            driver.close();
            driver.switchTo().window(tempWindow);
        }

        driver.close();
        driver.switchTo().window(originalWindow);
    }

    private void parseDataType(WebDriver driver) {
        WebElement titleElement = driver.findElement(By.tagName("h1"));
        String name = titleElement.getText();

        WebElement descElement = driver.findElement(By.xpath("//h1/following-sibling::p[1]"));
        String desc = descElement.getText();

        List<WebElement> fieldsNameList = driver.findElements(By.xpath("//div[@class='variablelist']//dt"));
        List<WebElement> fieldsContentList = driver.findElements(By.xpath("//div[@class='variablelist']//dd"));

        List<DataTypeField> dataTypeFields = new ArrayList<>();
        DataType dataType = new DataType(name, desc, dataTypeFields);
        for (int i = 0; i < fieldsNameList.size(); i++) {
            String fieldName = fieldsNameList.get(i).getText();
            WebElement content = fieldsContentList.get(i);
            List<WebElement> pElements = content.findElements(By.tagName("p"));
            DataTypeField field = getDataTypeField(pElements, fieldName);
            dataTypeFields.add(field);
        }
        dataTypes.put(name, dataType);
    }

    public static DataTypeField getDataTypeField(List<WebElement> pElements, String fieldName) {
        String fieldDesc = pElements.get(0).getText();
        boolean required = true;
        String fieldType = "";

        StringBuilder constraints = new StringBuilder();
        for (int j = 1; j <= pElements.size() - 1; j++) {
            if (fieldType.isEmpty() && pElements.get(j).getText().toLowerCase().contains("type: ")) {
                fieldType = pElements.get(j).getText();
                int index = fieldType.indexOf(": ");
                fieldType = fieldType.substring(index + 2);
            } else if (pElements.get(j).getText().toLowerCase().contains("required: ")) {
                required = pElements.get(j).getText().toLowerCase().contains("yes");
            } else {
                constraints.append(pElements.get(j).getText());
                constraints.append("\n");
            }
        }
        String constraintsString = constraints.length() == 0 ? null : constraints.toString();

        return new DataTypeField(fieldName, fieldDesc, fieldType, required, constraintsString);
    }


}
