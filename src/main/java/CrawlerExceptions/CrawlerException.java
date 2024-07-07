package CrawlerExceptions;

public class CrawlerException extends Exception {
    private final String url;
    private final String message;

    public CrawlerException(String url, String message) {
        this.url = url;
        this.message = message;
    }

    public String getUrl() {
        return url;
    }

    @Override
    public String getMessage() {
        return message;
    }


}
