package Crawler.AWS.Struct;

public class DataTypeField {
    private final String name;
    private final String desc;
    private final String type;
    private final boolean required;
    private final String constraints;

    public DataTypeField(String name, String desc, String type, boolean required, String constraints) {
        this.name = name;
        this.desc = desc;
        this.type = type;
        this.required = required;
        this.constraints = constraints;
    }

    public String getName() {
        return name;
    }

    public String getDesc() {
        return desc;
    }

    public String getType() {
        return type;
    }

    public boolean isRequired() {
        return required;
    }

    public String getConstraints() {
        return constraints;
    }
}
