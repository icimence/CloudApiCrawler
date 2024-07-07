package Utils;

import org.openqa.selenium.WebElement;

public class ApiWebElement {
    private WebElement element;
    private String apiName;

    public ApiWebElement(WebElement element, String apiName) {
        this.element = element;
        this.apiName = apiName;
    }

    public String getApiName() {
        return apiName;
    }

    public void setApiName(String apiName) {
        this.apiName = apiName;
    }

    public WebElement getElement() {
        return element;
    }

    public void setElement(WebElement element) {
        this.element = element;
    }
}
