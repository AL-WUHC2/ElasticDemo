package org.n3r.elastic.thread;

import static org.n3r.config.Config.getInt;
import static org.n3r.config.Config.getStr;
import static org.n3r.core.lang.RStr.toStr;
import static org.n3r.elastic.utils.BufferedIOUtils.createFileReader;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;

import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.n3r.core.lang.Pair;
import org.n3r.elastic.iface.FileLineReader;
import org.n3r.elastic.utils.TimeLagUtils;

import com.google.common.base.Throwables;
import com.google.common.io.Closeables;

public class ElasticThread implements Runnable {

    private TransportClient elasticClient;

    private String elasticCluster = getStr("elasticCluster");

    private String elasticHost = getStr("elasticHost");

    private int elasticPort = getInt("elasticPort");

    private String elasticIndex = getStr("elasticIndex");

    private String elasticType = getStr("elasticType");

    private String srcFilePath;

    private FileLineReader lineReader = new FileLineReader() {
        @Override
        public Pair<String, byte[]> readLine(long lineNum, String lineContent) throws Exception {
            return new Pair<String, byte[]>(toStr(lineNum), lineContent.getBytes("UTF-8"));
        }
    };

    @Override
    public void run() {
        Date begin = new Date();
        System.out.println("处理数据文件: " + srcFilePath + " 开始.");
        elasticClient = new TransportClient(ImmutableSettings.settingsBuilder().put("cluster.name", elasticCluster).build());
        elasticClient.addTransportAddress(new InetSocketTransportAddress(elasticHost, elasticPort));
        System.out.println("处理数据文件: " + srcFilePath + " 创建客户端完成, 耗时: " + TimeLagUtils.formatLagBetween(begin, new Date()));
        begin = new Date();

        System.out.println("处理数据文件: " + srcFilePath + " 索引数据开始.");
        BufferedReader br = null;
        try {
            br = createFileReader(srcFilePath);
            String lineContent = br.readLine();
            long lineNumber = 1;
            while (lineContent != null) {
                Pair<String, byte[]> obj = lineReader.readLine(lineNumber, lineContent);
                elasticClient.prepareIndex(elasticIndex, elasticType, obj.getFirst()).setSource(obj.getSecond()).execute().actionGet();
                lineContent = br.readLine();
                lineNumber++;
            }
        } catch (FileNotFoundException e) {
            throw Throwables.propagate(e);
        } catch (IOException e) {
            throw Throwables.propagate(e);
        } catch (Exception e) {
            throw Throwables.propagate(e);
        } finally {
            Closeables.closeQuietly(br);
        }

        System.out.println("处理数据文件: " + srcFilePath + " 索引数据完成, 耗时: " + TimeLagUtils.formatLagBetween(begin, new Date()));
        elasticClient.close();
        System.out.println("处理数据文件: " + srcFilePath + " 完成.");
    }

    public String getElasticCluster() {
        return elasticCluster;
    }

    public ElasticThread setElasticCluster(String elasticCluster) {
        if (elasticCluster != null) this.elasticCluster = elasticCluster;
        return this;
    }

    public String getElasticHost() {
        return elasticHost;
    }

    public ElasticThread setElasticHost(String elasticHost) {
        if (elasticHost != null) this.elasticHost = elasticHost;
        return this;
    }

    public int getElasticPort() {
        return elasticPort;
    }

    public ElasticThread setElasticPort(int elasticPort) {
        if (elasticPort >= 0) this.elasticPort = elasticPort;
        return this;
    }

    public String getElasticIndex() {
        return elasticIndex;
    }

    public ElasticThread setElasticIndex(String elasticIndex) {
        if (elasticIndex != null) this.elasticIndex = elasticIndex;
        return this;
    }

    public String getElasticType() {
        return elasticType;
    }

    public ElasticThread setElasticType(String elasticType) {
        if (elasticType != null) this.elasticType = elasticType;
        return this;
    }

    public String getSrcFilePath() {
        return srcFilePath;
    }

    public ElasticThread setSrcFilePath(String srcFilePath) {
        if (srcFilePath != null) this.srcFilePath = srcFilePath;
        return this;
    }

    public FileLineReader getLineReader() {
        return lineReader;
    }

    public ElasticThread setLineReader(FileLineReader lineReader) {
        if (lineReader != null) this.lineReader = lineReader;
        return this;
    }

}
