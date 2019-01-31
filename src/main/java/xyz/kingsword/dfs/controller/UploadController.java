package xyz.kingsword.dfs.controller;

import com.google.gson.Gson;
import com.qiniu.common.QiniuException;
import com.qiniu.common.Zone;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.util.Auth;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import xyz.kingsword.dfs.util.Constant;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

/**
 * @author wzh Date 2019/1/30 18:11
 * @version 1.0
 **/
@RequestMapping("/up")
@RestController
public class UploadController {
    private final Constant constant;

    @Autowired
    public UploadController(Constant constant) {
        this.constant = constant;
    }

    /**
     * 上传文件到七牛云存储
     *
     * @param multipartFile file
     * @return
     * @throws IOException
     */
    @PostMapping("/img")
    @ResponseBody
    public Object uploadImg(@RequestParam("file") MultipartFile multipartFile) throws IOException {

        InputStream inputStream = multipartFile.getInputStream();
        return uploadQNImg(inputStream, getUid()); // KeyUtil.genUniqueKey()生成图片的随机名
    }

    /**
     * 将图片上传到七牛云
     */
    private String uploadQNImg(InputStream file, String key) {
        // 构造一个带指定Zone对象的配置类
        Configuration cfg = new Configuration(Zone.zone2());
        // 其他参数参考类注释
        UploadManager uploadManager = new UploadManager(cfg);
        // 生成上传凭证，然后准备上传
        try {
            Auth auth = Auth.create(constant.getAccessKey(), constant.getSecretKey());
            String upToken = auth.uploadToken(constant.getBucket());
            try {
                Response response = uploadManager.put(file, key, upToken, null, null);
                // 解析上传成功的结果
                DefaultPutRet putRet = new Gson().fromJson(response.bodyString(), DefaultPutRet.class);

                return constant.getPath() + "/" + putRet.key;
            } catch (QiniuException ex) {
                Response r = ex.response;
                System.err.println(r.toString());
                try {
                    System.err.println(r.bodyString());
                } catch (QiniuException ex2) {
                    //ignore
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    private String getUid() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 6);
    }

}
