package Crawler.GCP.Struct;

import java.util.Objects;

public abstract class GcpStruct {
    private final String name;

    GcpStruct(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj){
            return true;
        }
        if (!(obj instanceof GcpStruct)) {
            return false;
        }
        return ((GcpStruct) obj).name.equals(this.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(name);
    }
}
