package com.ulang.nts;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class RequestBodyUtil {

    /**
     * 将文件路径数组封装为{@link List<MultipartBody.Part>}
     *
     * @param key 对应请求正文中name的值。目前服务器给出的接口中，所有图片文件使用<br>
     *            同一个name值，实际情况中有可能需要多个
     */

    public static List<MultipartBody.Part> filesToMultipartBodyParts(List<File> files, String key) {
        List<MultipartBody.Part> parts = new ArrayList<>(files.size());
        for (File file : files) {
            RequestBody requestBody = RequestBody.create(MediaType.parse("image/png"), file);
            MultipartBody.Part part = MultipartBody.Part.createFormData(key, file.getName(), requestBody);
            parts.add(part);
        }
        return parts;
    }

    /**
     * 将参数封装成requestBody形式上传参数
     *
     * @param param 参数
     * @return RequestBody
     */
    public static RequestBody convertToRequestBody(String param) {
        RequestBody requestBody = RequestBody.create(MediaType.parse("text/plain"), param);
        return requestBody;
    }

    /**
     * 将文件进行转换
     *
     * @param file 为文件类型
     * @return
     */
    public static RequestBody convertToRequestBodyMap(File file) {

        RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        return requestBody;
    }

    public static Map<String, RequestBody> getParams() {

        String sid = "123456";//每次识别的唯一标识，该 sid 最好在每一次识别中都进行设置，以保持唯一性。
        String username = "admin";
        String auth = "123456";
        String idx = "1";//上传语音编号 1开始
        String islast = "1";//是否最后一段  0 不是 1最后
        String did = "1761216";//上传设备唯一标识

        Map<String, RequestBody> map = new HashMap<>();
        map.put("sid", convertToRequestBody(sid));
        map.put("username", convertToRequestBody(username));
        map.put("auth", convertToRequestBody(auth));
        map.put("idx", convertToRequestBody(idx));
        map.put("islast", convertToRequestBody(islast));
        map.put("did", convertToRequestBody(did));
        return map;
    }
}
