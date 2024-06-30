package Crawler.GCP;

import Crawler.Crawler;
import Crawler.GCP.Handler.*;
import Crawler.GCP.Struct.GcpStruct;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;
import java.util.*;

public class GCPCrawler extends Crawler {
    public static final String NAME = "gcp";
    // 找到API所在的section元素，使用id做css模糊定位
    private static final String API_AREA_CSS_SELECTOR = "[id*='/docs/'][id*='/reference/rest/index']";
    //定位到所有API元素，过滤前置的描述
    private static final String SECTION_SELECT_XPATH = "./section[not(contains(@id, '.googleapis.com'))]";

    private Set<GcpStruct> bodyStructs;

    private static final String HTTP_PATH = "HttpPath";

    private static final String PATH_PARAM = "PathParameters";

    private static final String QUERY_PARAM = "QueryParameters";

    private static final String REQUEST_BODY = "RequestBody";

    private static final String RESPONSE_BODY = "ResponseBody";

    private static final String IAM = "IAMPermissions";

    private final Map<String, Handler> handlerMap;

//todo:搞个Map<String,GCPStruct>的缓存，不用重复查询，需要传入BodyHandler处理
    public GCPCrawler(JSONObject json) {
        super(json);
        bodyStructs = new HashSet<>();
        handlerMap = new HashMap<>();
        handlerMap.put("body.HTTP_TEMPLATE", new HttpRequestHandler(HTTP_PATH));
        handlerMap.put("body.PATH_PARAMETERS", new ParameterHandler(PATH_PARAM));
        handlerMap.put("body.QUERY_PARAMETERS", new ParameterHandler(QUERY_PARAM));
        handlerMap.put("body.request_body", new BodyHandler(REQUEST_BODY, bodyStructs));
        handlerMap.put("body.response_body", new BodyHandler(RESPONSE_BODY, bodyStructs));
        handlerMap.put("body.aspect_1", new IAMHandler(IAM));
    }


    @Override
    protected void openApiReferencePage(WebDriver driver) {
        driver.get("https://cloud.google.com/docs/quotas");
        driver.manage().timeouts().implicitlyWait(Duration.ofMillis(500));

        WebElement apiReferenceLink = driver.findElement(By.linkText("REST API"));
        apiReferenceLink.click();

    }

    @Override
    protected List<WebElement> getAllApiReferences(WebDriver driver) {
        WebElement apiArea = driver.findElement(By.cssSelector(API_AREA_CSS_SELECTOR));
        List<WebElement> sections = apiArea.findElements(By.xpath(SECTION_SELECT_XPATH));
        List<WebElement> linkList = new ArrayList<>();
        for (WebElement section : sections) {
            WebElement parentName = section.findElement(By.xpath(".//a"));
            System.out.println(parentName.getText());
            List<WebElement> children = section.findElements(By.xpath(".//tbody/tr"));
            for (WebElement child : children) {
                linkList.add(child.findElement(By.xpath("./td[1]//a")));
            }
        }
        return linkList;
    }

    @Override
    protected JSONArray getProductList() {
        return super.json.getJSONArray(NAME);
    }

    @Override
    protected void getApiInfo(WebDriver driver, JSONObject json) {
        JSONObject apiJsonElement = new JSONObject();
        json.getJSONArray("apiList").add(apiJsonElement);

        WebElement titleElement = driver.findElement(By.xpath(".//h1"));
        String title = titleElement.getText();
        System.out.println(title);
        apiJsonElement.put("Name", title);

        WebElement descriptionElement = driver.findElement(By.xpath(".//section[@id='description']"));
        String description = descriptionElement.getText();
        apiJsonElement.put("Desc", description);

        List<WebElement> context = driver.findElements(By.xpath(".//section[contains(@id,'body.')]"));
        for (WebElement element : context) {
            String elementId = element.getAttribute("id");
            Handler handler = handlerMap.getOrDefault(elementId, new UnfitHandler());
            handler.handle(apiJsonElement, element, driver);
        }
        JSONArray jsonArray = new JSONArray(bodyStructs);
        apiJsonElement.put("Structs", jsonArray);
        //todo:输出Set内的所有数据
        bodyStructs = new HashSet<>();
        ((BodyHandler) handlerMap.get("body.request_body")).setBodyStructs(bodyStructs);
        ((BodyHandler) handlerMap.get("body.response_body")).setBodyStructs(bodyStructs);
        System.out.println(title + "所有参数爬取完毕");
    }

    @Override
    protected void saveJsonFile(String json) {
        String filePath = "src/main/java/ResultFile/gcp.json";
        File file = new File(filePath);
        try{
            if (!file.exists()){
                file.createNewFile();
            }

            FileWriter fw = new FileWriter(file);
            fw.write(json);

            fw.close();
            System.out.println("json写入完成");
        }catch (IOException e){
            e.printStackTrace();
        }
    }

}














