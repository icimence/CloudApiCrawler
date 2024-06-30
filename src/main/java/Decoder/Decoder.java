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

    private String getResultFromJsonFile() {
        String filepath = "src/main/java/ResultFile/".concat(cloud).concat(".json");
        String res="";

        try (BufferedReader br = new BufferedReader(new FileReader(filepath))) {
            StringBuilder content = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null){
                content.append(line);
            }
            String json = content.toString();
            JSONObject jsonObject = JSONObject.parseObject(json);
            res = DecodeJson(jsonObject);
        } catch (IOException e) {
            System.err.println("读取发生失误" + e.getMessage());
        }
        return res;
    }

    protected abstract String DecodeJson(JSONObject jsonObject);

}
