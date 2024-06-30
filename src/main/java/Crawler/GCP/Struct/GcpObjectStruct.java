package Crawler.GCP.Struct;

import java.util.List;

public class GcpObjectStruct extends GcpStruct {
    private String desc;
    private String jsonRepresentation;
    private List<Field> fields;

    public GcpObjectStruct(String name, String desc, String jsonRepresentation, List<Field> fields) {
        super(name);
        this.desc = desc;
        this.jsonRepresentation = jsonRepresentation;
        this.fields = fields;
    }

    public GcpObjectStruct(String name) {
        super(name);
    }

    public String getDesc() {
        return desc;
    }

    public List<Field> getFields() {
        return fields;
    }


    public String getJsonRepresentation() {
        return jsonRepresentation;
    }

    public String getType() {
        return "object";
    }



}
