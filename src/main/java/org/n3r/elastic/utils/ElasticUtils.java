package org.n3r.elastic.utils;

import static org.n3r.core.text.RRand.randChinese;
import static org.n3r.core.text.RRand.randDateBetween;
import static org.n3r.core.text.RRand.randLetters;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.n3r.elastic.bean.ElasticBean;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.util.ResourceUtils;

import com.google.common.base.Throwables;

public class ElasticUtils {

    public static Resource getFileResource(String filePath) {
        try {
            Resource[] results = new PathMatchingResourcePatternResolver().getResources(
                    ResourceUtils.FILE_URL_PREFIX + filePath);
            return results.length > 0 ? results[0] : null;
        } catch (Exception e) {
            throw Throwables.propagate(e);
        }
    }

    public static Resource[] getFileResources(String filePathPattern) {
        try {
            return new PathMatchingResourcePatternResolver().getResources(
                    ResourceUtils.FILE_URL_PREFIX + filePathPattern);
        } catch (Exception e) {
            throw Throwables.propagate(e);
        }
    }

    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

    private static Date startDate = new Date(1356969600000L); // 20130101 000000000

    private static Date endDate = new Date(1388462399999L); // 20131231 115959999

    public static ElasticBean randomElasticBean(String id, String key, String state) {
        ElasticBean bean = new ElasticBean();
        bean.setId(id);
        bean.setKey(key);
        bean.setState(state);
        bean.setDate(sdf.format(randDateBetween(startDate, endDate)));
        bean.setContentEng(randLetters(8));
        bean.setContentChs(randChinese(8));
        return bean;
    }

}
