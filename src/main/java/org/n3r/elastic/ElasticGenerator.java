package org.n3r.elastic;

import static org.n3r.core.lang.RStr.alignLeft;
import static org.n3r.core.lang.RStr.alignRight;
import static org.n3r.core.lang.RStr.toInteger;
import static org.n3r.core.lang.RStr.toStr;
import static org.n3r.core.text.RRand.randNum;
import static org.n3r.elastic.utils.BufferedIOUtils.createFileReader;
import static org.n3r.elastic.utils.BufferedIOUtils.createFileWriter;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.n3r.elastic.bean.ElasticBean;
import org.n3r.elastic.utils.ElasticUtils;
import org.n3r.elastic.utils.TimeLagUtils;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Throwables;
import com.google.common.io.Closeables;

public class ElasticGenerator {

    public void execute(String[] args) {
        if (!verifyArgs(args)) return;

        BufferedWriter bw = null;
        BufferedWriter keyBw = null;
        BufferedReader keyBr = null;
        try {
            System.out.println("Elastic数据文件生成开始.");
            Date begin = new Date();
            // write key file.
            String keyFilePath = generateFilePath + "-key" + keyFileType;
            keyBw = createFileWriter(keyFilePath, false);
            keyBw.append(keyName + lineSeparator);
            int keyBit = ElasticUtils.decimalBits(keyCount);
            for (int key = 0; key < keyCount; key++) {
                keyBw.append(randNum(6) + alignRight(toStr(key), keyBit, '0') + lineSeparator);
            }
            Closeables.closeQuietly(keyBw);

            String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
            for (int file = 0; file < fileCount; file++) {
                String filePath = generateFilePath + (fileCount == 1 ? "" : "-" + file) + generateFileType;

                // write generate file.
                bw = createFileWriter(filePath, false);
                // id重复的次数代表一次大量插入或更新次数
                int idBit = ElasticUtils.decimalBits(idRepeat) + 1;
                for (int idr = 0; idr < idRepeat; idr++) {
                    String state = alignRight(toStr(idr), idBit, '0'); // 每次重复id状态都更新
                    // key重复的次数代表同一个关键字对应id的最大数量
                    keyBit = ElasticUtils.decimalBits(keyRepeat) + 1;
                    for (int keyr = 0; keyr < keyRepeat; keyr++) {
                        String idPrefix = alignLeft(toStr(keyr), keyBit, '0');

                        keyBr = createFileReader(keyFilePath);
                        keyBr.readLine(); // keyName line
                        String key = keyBr.readLine();
                        while (key != null) {
                            // ID = 关键字重复标识 + 时间戳 + 关键字
                            ElasticBean random = ElasticUtils.randomElasticBean(idPrefix + timeStamp + key, key, state);
                            bw.append(JSON.toJSONString(random) + lineSeparator);
                            key = keyBr.readLine();
                        }
                        Closeables.closeQuietly(keyBr);
                    }
                }
                Closeables.closeQuietly(bw);
            }
            System.out.println("Elastic数据文件生成完成, 耗时: " + TimeLagUtils.formatLagBetween(begin, new Date()));
        } catch (IOException e) {
            throw Throwables.propagate(e);
        } finally {
            Closeables.closeQuietly(bw);
            Closeables.closeQuietly(keyBw);
        }
    }

    private int fileCount = 1;

    private int keyCount = 100;

    private int idRepeat = 1;

    private int keyRepeat = 1;

    private String keyName = "key";

    private String generateFilePath;

    private final static String lineSeparator = System.getProperty("line.separator");

    private final static String keyFileType = ".csv";

    private final static String generateFileType = ".esdata";

    private boolean verifyArgs(String[] args) {
        if (args.length <= 0) {
            System.out.println("未指定参数。");
            return false;
        }

        for (int i = 0; i < args.length; i += 2) {
            System.out.println("指定参数: " + args[i]);
            if (i + 1 >= args.length) {
                System.out.println("参数数量错误。");
                return false;
            }
            System.out.println("  ------  " + args[i + 1]);

            if (ElasticArgs.FILE_ARG.equalsIgnoreCase(args[i])) {
                generateFilePath = args[i + 1];
            } else if (ElasticArgs.FILE_COUNT_ARG.equalsIgnoreCase(args[i])) {
                fileCount = toInteger(args[i + 1], fileCount);
            } else if (ElasticArgs.KEY_COUNT_ARG.equalsIgnoreCase(args[i])) {
                keyCount = toInteger(args[i + 1], keyCount);
            } else if (ElasticArgs.ID_REPEAT_ARG.equalsIgnoreCase(args[i])) {
                idRepeat = toInteger(args[i + 1], idRepeat);
            } else if (ElasticArgs.KEY_REPEAT_ARG.equalsIgnoreCase(args[i])) {
                keyRepeat = toInteger(args[i + 1], keyRepeat);
            } else if (ElasticArgs.KEY_NAME_ARG.equalsIgnoreCase(args[i])) {
                keyName = args[i + 1];
            }
        }

        if (generateFilePath == null) {
            System.out.println("未指定生成文件路径。");
            return false;
        }

        return true;
    }

}
