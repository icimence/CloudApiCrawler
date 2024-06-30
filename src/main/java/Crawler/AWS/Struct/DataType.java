package Crawler.AWS.Struct;

import java.util.List;

public class DataType {
    private final String name;
    private final String desc;
    private final List<DataTypeField> fields;


    public DataType(String name, String desc, List<DataTypeField> fields) {
        this.name = name;
        this.desc = desc;
        this.fields = fields;
    }

    public String getName() {
        return name;
    }

    public String getDesc() {
        return desc;
    }

    public List<DataTypeField> getFields() {
        return fields;
    }
}
