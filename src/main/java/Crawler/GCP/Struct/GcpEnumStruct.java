package Crawler.GCP.Struct;

import java.util.List;

public class GcpEnumStruct extends GcpStruct {
    private final List<EnumField> enumFields;

    public GcpEnumStruct(List<EnumField> enumFields, String name) {
        super(name);
        this.enumFields = enumFields;
    }

    public List<EnumField> getEnumFields() {
        return enumFields;
    }


    public String getType() {
        return "enum";
    }
}
