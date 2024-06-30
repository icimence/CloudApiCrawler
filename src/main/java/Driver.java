import Crawler.*;
import Crawler.AWS.AWSCrawler;
import Crawler.GCP.GCPCrawler;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;

public class Driver {

    protected Crawler crawler;

    public Driver() {
    }

    public Driver(Crawler crawler) {
        this.crawler = crawler;
    }

    //todo: 改造方法，去除Crawler中的成员变量json，json作为参数传入，内容由各个子类控制。Driver接受两个参数
    //todo: 一个是结果json，一个是crawler。
    public void runAWSCrawler() {
        JSONObject result = new JSONObject();
        JSONArray productList = new JSONArray();
        result.put("aws", productList);

        JSONObject quotaService = new JSONObject();
        quotaService.put("name", "quotaService");
        JSONArray apiList = new JSONArray();
        quotaService.put("apiList", apiList);

        productList.add(quotaService);
        crawler = new AWSCrawler(result);
        crawler.fetchAPIDoc();
    }

    public void runGCPCrawler() {
        JSONObject result = new JSONObject();
        JSONArray productList = new JSONArray();
        result.put("gcp", productList);

        JSONObject quotaService = new JSONObject();
        quotaService.put("name", "cloudQuotas");
        JSONArray apiList = new JSONArray();
        quotaService.put("apiList", apiList);

        productList.add(quotaService);
        crawler = new GCPCrawler(result);
        crawler.fetchAPIDoc();
    }

}
