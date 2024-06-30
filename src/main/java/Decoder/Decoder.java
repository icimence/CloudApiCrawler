package Decoder;

import com.alibaba.fastjson2.JSONObject;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public abstract class Decoder {
    private final String cloud;

    public Decoder(String cloud) {
        this.cloud = cloud;
    }

    public String getApiDesc() {
        return "";
    }

    private JSONObject getJsonFromFile() {
        String filepath = "src/main/java/ResultFile/".concat(cloud).concat(".json");

        try (BufferedReader br = new BufferedReader(new FileReader(filepath))) {
            StringBuilder content = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null){
                content.append(line);
            }
            String json = content.toString();
            JSONObject jsonObject = JSONObject.parseObject(json);

        } catch (IOException e) {
            System.err.println("读取发生失误" + e.getMessage());
        }
        return null;
    }

}
