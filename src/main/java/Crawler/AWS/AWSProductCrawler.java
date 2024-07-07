package Crawler.AWS;

import Crawler.ProductCrawler;
import Crawler.ProductStruct;
import Utils.Util;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AWSProductCrawler extends ProductCrawler {
    @Override
    protected List<ProductStruct> getProducts(WebDriver driver) {
        driver.get("https://docs.aws.amazon.com/");
        WebElement headerDiv = findAllProductsDiv(driver);
        WebElement flippingButton = findFlippingButton(headerDiv);
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", headerDiv);
        List<ProductStruct> results = new ArrayList<>();
        while (flippingButton.isEnabled()) {
            Util.sleepRandomTime(500,600);
            getProductInfo(results, headerDiv);
            flippingButton.click();
            Util.sleepRandomTime(1000,500);
        }
        getProductInfo(results, headerDiv);
        return results;
    }

    private WebElement findAllProductsDiv(WebDriver driver) {
        return driver.findElement(By.xpath("//span[text()='All products']/../../../../../.."));
    }

    private WebElement findFlippingButton(WebElement element) {
        return element.findElement(By.xpath(".//ul/li[last()]/button"));
    }

    private void getProductInfo(List<ProductStruct> results, WebElement headerDiv) {
        WebElement productListDiv = headerDiv.findElement(By.xpath("following-sibling::div[1]"));
        List<WebElement> productList = productListDiv.findElements(By.xpath(".//ol/li/div"));
        for (WebElement product : productList) {
            WebElement infoDiv = product.findElement(By.xpath("./div[1]"));
            WebElement categoryDiv = product.findElement(By.xpath("./div[2]"));
            List<WebElement> categoryList = categoryDiv.findElements(By.xpath(".//ul/li"));
            List<String> categoryNames = categoryList.stream().map(WebElement::getText).collect(Collectors.toList());
            WebElement productNameElement = infoDiv.findElement(By.tagName("h5"));
            WebElement descElement = productNameElement.findElement(By.xpath("following-sibling::div[1]"));
            WebElement productLink = productNameElement.findElement(By.tagName("a"));
            String docUrl = productLink.getAttribute("href");
            ProductStruct productStruct = new ProductStruct(productNameElement.getText(), descElement.getText(), categoryNames, docUrl);
            results.add(productStruct);
        }
    }
}
