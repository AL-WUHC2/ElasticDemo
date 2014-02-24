package org.n3r.elastic;

import static org.n3r.core.lang.RStr.toInteger;
import static org.n3r.core.lang.RStr.toStr;

import java.io.IOException;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.n3r.config.impl.DefaultConfigable;
import org.n3r.config.impl.PropertiesConfigable;
import org.n3r.config.impl.PropsConfigable;
import org.n3r.core.lang.Pair;
import org.n3r.elastic.iface.FileLineReader;
import org.n3r.elastic.thread.ElasticThread;
import org.n3r.elastic.utils.ElasticUtils;
import org.n3r.elastic.utils.TimeLagUtils;
import org.springframework.core.io.Resource;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Throwables;

public class ElasticIndexer {

    public void execute(String[] args) {
        if (!verifyArgs(args)) return;

        System.out.println("提交Elastic索引数据开始.");
        Date begin = new Date();
        ExecutorService pool = Executors.newFixedThreadPool(extConf.getInt("elasticThreadCount", defaultPoolSize));
        try {
            Resource[] srcResources = ElasticUtils.getFileResources(srcFilePathPattern);
            for (Resource src : srcResources) {
                pool.execute(new ElasticThread().setSrcFilePath(src.getFile().getAbsolutePath())
                        .setElasticCluster(extConf.getStr("elasticCluster", cluster))
                        .setElasticHost(extConf.getStr("elasticHost", host))
                        .setElasticPort(extConf.getInt("elasticPort", port))
                        .setElasticIndex(extConf.getStr("elasticIndex", index))
                        .setElasticType(extConf.getStr("elasticType", type))
                        .setElasticBulkNum(extConf.getInt("elasticBulkNum", bulkNum))
                        .setLineReader(lineReader));
            }
        } catch (IOException e) {
            throw Throwables.propagate(e);
        } finally {
            pool.shutdown();
            System.out.println("提交Elastic索引数据完成, 耗时: " + TimeLagUtils.formatLagBetween(begin, new Date()));
        }
    }

    private String cluster;

    private String host;

    private int port = -1;

    private String index;

    private String type;

    private int bulkNum = 0;

    private String srcFilePathPattern = null;

    private DefaultConfigable extConf = new DefaultConfigable();

    private static final int defaultPoolSize = 10;

    private FileLineReader lineReader = new FileLineReader() {
        @Override
        public Pair<String, byte[]> readLine(long lineNum, String lineContent) throws Exception {
            Map<String, Object> contMap = JSON.parseObject(lineContent, Map.class);
            return new Pair<String, byte[]>(toStr(contMap.get("id")), lineContent.getBytes("UTF-8"));
        }
    };

    private boolean verifyArgs(String[] args) {
        if (args.length <= 0) {
            System.out.println("未指定参数。");
            return false;
        }

        String extendConfigFile = null;
        for (int i = 0; i < args.length; i += 2) {
            System.out.println("指定参数: " + args[i]);
            if (i + 1 >= args.length) {
                System.out.println("参数数量错误。");
                return false;
            }
            System.out.println("  ------  " + args[i + 1]);

            if (ElasticArgs.FILE_ARG.equalsIgnoreCase(args[i])) {
                srcFilePathPattern = args[i + 1];
            } else if (ElasticArgs.CONF_ARG.equalsIgnoreCase(args[i])) {
                extendConfigFile = args[i + 1];
            } else if (ElasticArgs.CLUSTER_ARG.equalsIgnoreCase(args[i])) {
                cluster = args[i + 1];
            } else if (ElasticArgs.HOST_ARG.equalsIgnoreCase(args[i])) {
                host = args[i + 1];
            } else if (ElasticArgs.PORT_ARG.equalsIgnoreCase(args[i])) {
                port = toInteger(args[i + 1], port);
            } else if (ElasticArgs.INDEX_ARG.equalsIgnoreCase(args[i])) {
                index = args[i + 1];
            } else if (ElasticArgs.TYPE_ARG.equalsIgnoreCase(args[i])) {
                type = args[i + 1];
            } else if (ElasticArgs.BULKNUM_ARG.equalsIgnoreCase(args[i])) {
                bulkNum = toInteger(args[i + 1], bulkNum);
            }
        }

        if (srcFilePathPattern == null) {
            System.out.println("未指定源文件路径。");
            return false;
        }

        if (extendConfigFile != null) {
            Resource extRes = ElasticUtils.getFileResource(extendConfigFile);
            if (extRes == null) return true;
            extConf = extRes.getFilename().endsWith(".properties") ? new PropertiesConfigable(extRes) :
                extRes.getFilename().endsWith(".props") ? new PropsConfigable(extRes) : new DefaultConfigable();
        }

        return true;
    }

}
