package Crawler.GCP.Struct;

public class EnumField {
    private final String name;
    private final String desc;

    public EnumField(String name, String desc) {
        this.name = name;
        this.desc = desc;
    }

    public String getName() {
        return name;
    }

    public String getDesc() {
        return desc;
    }

}
