package xyz.kingsword.dfs.util;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author wzh Date 2019/1/30 18:09
 * @version 1.0
 **/
@Data
@Component
@ConfigurationProperties(prefix = "qiniu")
public class Constant {

    private String accessKey;

    private String secretKey;

    private String bucket;

    private String path;
}
