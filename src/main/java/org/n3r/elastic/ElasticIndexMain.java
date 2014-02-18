package org.n3r.elastic;

import static org.n3r.config.Config.getInt;
import static org.n3r.config.Config.getStr;
import static org.n3r.core.lang.RStr.toStr;

import java.io.IOException;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.n3r.config.impl.DefaultConfigable;
import org.n3r.config.impl.PropertiesConfigable;
import org.n3r.elastic.iface.FileLineReader;
import org.n3r.elastic.thread.ElasticThread;
import org.n3r.elastic.utils.ElasticUtils;
import org.n3r.elastic.utils.TimeLagUtils;
import org.n3r.lang.Pair;
import org.springframework.core.io.Resource;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Throwables;

public class ElasticIndexMain {

    public static void main(String[] args) {
        if (!verifyArgs(args)) return;

        System.out.println("提交Elastic索引数据开始.");
        Date begin = new Date();
        ExecutorService pool = Executors.newFixedThreadPool(getConfigInt("elasticThreadCount", defaultPoolSize));
        try {
            Resource[] srcResources = ElasticUtils.getFileResources(srcFilePathPattern);
            for (Resource src : srcResources) {
                pool.execute(new ElasticThread().setSrcFilePath(src.getFile().getAbsolutePath())
                        .setElasticHost(getConfigStr("elasticHost"))
                        .setElasticPort(getConfigInt("elasticPort"))
                        .setElasticIndex(getConfigStr("elasticIndex"))
                        .setElasticType(getConfigStr("elasticType"))
                        .setLineReader(lineReader));
            }
        } catch (IOException e) {
            throw Throwables.propagate(e);
        } finally {
            pool.shutdown();
            System.out.println("提交Elastic索引数据完成, 耗时: " + TimeLagUtils.formatLagBetween(begin, new Date()));
        }
    }

    private static String srcFilePathPattern = null;

    private static DefaultConfigable extConf = new DefaultConfigable();

    private static final int defaultPoolSize = 10;

    private static FileLineReader lineReader = new FileLineReader() {
        @Override
        public Pair<String, byte[]> readLine(long lineNum, String lineContent) throws Exception {
            Map<String, Object> contMap = JSON.parseObject(lineContent, Map.class);
            return new Pair<String, byte[]>(toStr(contMap.get("id")), lineContent.getBytes("UTF-8"));
        }
    };

    private static boolean verifyArgs(String[] args) {
        if (args.length <= 0) {
            System.out.println("未指定参数。");
            return false;
        }

        String extendConfigFile = null;
        for (int i = 0; i < args.length; i += 2) {
            if (i + 1 >= args.length) {
                System.out.println("参数数量错误。");
                return false;
            }

            if (ElasticArgs.FILE_ARG.equals(args[i])) {
                srcFilePathPattern = args[i + 1];
            } else if (ElasticArgs.CONF_ARG.equals(args[i])) {
                extendConfigFile = args[i + 1];
            }
        }

        if (srcFilePathPattern == null) {
            System.out.println("未指定源文件路径。");
            return false;
        }

        if (extendConfigFile != null) {
            Resource extRes = ElasticUtils.getFileResource(extendConfigFile);
            if (extRes == null) return true;
            extConf = new PropertiesConfigable(extRes);
        }

        return true;
    }

    private static int getConfigInt(String key) {
        return extConf.getInt(key, getInt(key));
    }

    private static int getConfigInt(String key, int defValue) {
        return extConf.getInt(key, getInt(key, defValue));
    }

    private static String getConfigStr(String key) {
        return extConf.getStr(key, getStr(key));
    }

}
