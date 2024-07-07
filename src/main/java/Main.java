import Crawler.AWS.AWSProductCrawler;
import Crawler.ProductCrawler;
import Crawler.ProductStruct;
import CrawlerExceptions.CrawlerException;
import Utils.Util;

import java.util.Arrays;
import java.util.List;

public class Main {
    public static void main(String[] args) throws CrawlerException {
        Driver driver = new Driver();
//        driver.runAWSCrawler("https://docs.aws.amazon.com/detective/");
//        driver.runAWSCrawler("https://docs.aws.amazon.com/servicequotas/");
//        driver.runGCPCrawler();
        ProductCrawler productCrawler = new AWSProductCrawler();
        List<ProductStruct> products = productCrawler.getAllProduct();
        while (!products.isEmpty()) {
            ProductStruct product = Util.getRandomItem(products);
            try {
                System.out.println("======开始爬取" + product.getName() + "======");
                driver.runAWSCrawler(product.getDocUrl());
                System.out.println("======结束爬取" + product.getName() + "======");
            } catch (Exception e) {
                System.out.println("!!!!!!!!!!!!!!!产品爬取失败" + product.getName() + "!!!!!!!!!!!!");
                System.out.println("Error running aws crawler,error: " + e.getMessage());
            }
        }
    }


}
