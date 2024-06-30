package Decoder;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;

public class GcpDecoder extends Decoder{
    public GcpDecoder(String cloud) {
        super(cloud);
    }

    @Override
    protected String DecodeJson(JSONObject jsonObject) {
        StringBuilder builder = new StringBuilder();
        builder.append("API调用名称为：").append(jsonObject.getString("Name")).append("。\n");
        builder.append("该方法的作用是：").append(jsonObject.getString("Desc")).append("。\n");
        builder.append("HTTP请求方法和URL为：").append(jsonObject.getString("HttpPath")).append("。\n");
        JSONArray array = jsonObject.getJSONArray("PathParameters");
        if (array != null && !array.isEmpty()) {
            builder.append("在HTTP请求URL中包含的参数有：\n");
            for (int i = 0; i < array.size(); i++) {
                JSONObject object = array.getJSONObject(i);
                builder.append(i).append(". ").append(object.getString("name"));
                builder.append(", 该参数的类型是").append(object.getString("type"));
                builder.append(", 该参数的说明是").append(object.getString("desc"));
                if (object.getString("example")!=null) {
                    builder.append(", 比如这个参数的值可以是").append(object.getString("example"));
                }
            }
        }
        return "";
    }
}
