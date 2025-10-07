package com.centit.zuulgateway.utils.gson;

import com.centit.zuulgateway.po.PageData;
import com.centit.zuulgateway.utils.CommUtil;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @描述：
 * @作者： zhouchaoxi
 * @日期：2018/8/7
 */
public class GsonType extends TypeAdapter<Object> {
    @Override
    public void write(JsonWriter jsonWriter, Object o) throws IOException {
    }

    @Override
    public Object read(JsonReader jsonReader) throws IOException {
        // 反序列化
        JsonToken token = jsonReader.peek();
        switch (token) {
            case BEGIN_ARRAY:
                List<Object> list = new ArrayList<Object>();
                jsonReader.beginArray();
                while (jsonReader.hasNext()) {
                    list.add(read(jsonReader));
                }
                jsonReader.endArray();
                return list;
            case BEGIN_OBJECT:
                PageData map = new PageData();
                jsonReader.beginObject();
                while (jsonReader.hasNext()) {
                    map.put(jsonReader.nextName(), read(jsonReader));
                }
                jsonReader.endObject();
                return map;
            case STRING:
                return jsonReader.nextString();
            case NUMBER:
                /**
                 * 改写数字的处理逻辑，将数字值分为整型与浮点型。
                 */
                String dbNum = jsonReader.nextString();
                if (CommUtil.isInteger(dbNum)) {
                    return CommUtil.null2Int(dbNum);
                } else {
                    return CommUtil.null2Double(dbNum);
                }
            case BOOLEAN:
                return jsonReader.nextBoolean();
            case NULL:
                jsonReader.nextNull();
                return null;
            default:
                throw new IllegalStateException();
        }
    }
}
