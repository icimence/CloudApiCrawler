package Crawler;

import java.util.List;

public class ProductStruct {
    private final String name;
    private final String desc;
    private final List<String> categories;
    private final String docUrl;
    private String first_release_time;
    private String offline_time;

    public ProductStruct(String name, String desc, List<String> categories, String docUrl, String first_release_time, String offline_time) {
        this.name = name;
        this.desc = desc;
        this.categories = categories;
        this.docUrl = docUrl;
        this.first_release_time = first_release_time;
        this.offline_time = offline_time;
    }

    public ProductStruct(String name, String desc, List<String> categories, String docUrl) {
        this.name = name;
        this.desc = desc;
        this.categories = categories;
        this.docUrl = docUrl;
    }

    public String getName() {
        return name;
    }

    public String getDesc() {
        return desc;
    }

    public List<String> getCategories() {
        return categories;
    }

    public String getDocUrl() {
        return docUrl;
    }

    public String getFirst_release_time() {
        return first_release_time;
    }

    public String getOffline_time() {
        return offline_time;
    }
}
