package org.n3r.elastic.utils;

import static org.n3r.core.text.RRand.randChinese;
import static org.n3r.core.text.RRand.randDateBetween;
import static org.n3r.core.text.RRand.randLetters;

import org.n3r.core.date4j.DateTime;
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

    private static String formatter = "YYYY-MM-DDThh:mm:ss.fffZ";

    private static DateTime startDate = new DateTime("2013-01-01 00:00:00.000000000");

    private static DateTime endDate = new DateTime("2013-12-31 11:59:59.999999999");

    public static ElasticBean randomElasticBean(String id, String key, String state) {
        ElasticBean bean = new ElasticBean();
        bean.setId(id);
        bean.setKey(key);
        bean.setState(state);
        bean.setDate(randDateBetween(startDate, endDate).format(formatter));
        bean.setContentEng(randLetters(8));
        bean.setContentChs(randChinese(8));
        return bean;
    }

    public static int decimalBits(int number) {
        return (int) Math.ceil(Math.log10(number));
    }

}
