package org.n3r.elastic;

public class ElasticHelper {

    public void execute(String[] args) {
        System.out.println("Elastic Search测试数据生成工具.");
        System.out.println();
        System.out.println("参数列表: (参数名不区分大小写)");
        System.out.println();
        System.out.println("        gen:  生成测试数据");
        System.out.println("              可指定参数:");
        System.out.println("              -file:      指定生成数据文件路径");
        System.out.println("              -fileCount: 指定生成数据文件数量");
        System.out.println("              -keyCount:  指定关键字总数");
        System.out.println("              -idRepeat:  指定id重复次数, 代表对单条数据的Elastic操作(插入/更新)的次数");
        System.out.println("              -keyRepeat: 指定关键字重复次数, 代表单个关键字对应的Elastic数据的数量");
        System.out.println("              -keyName:   指定生成关键字文件中的表头内容");
        System.out.println();
        System.out.println("        idx:  提交测试数据到ElasticSearch");
        System.out.println("              可指定参数:");
        System.out.println("              -file:      指定源数据文件路径, 可包含通配符指定多个文件");
        System.out.println("              -conf:      指定自定义配置文件路径, 优先级最高");
        System.out.println("              -cluster:   指定Elastic集群名称, 默认为elasticsearch");
        System.out.println("              -host:      指定Elastic节点IP地址, 默认为127.0.0.1");
        System.out.println("              -port:      指定Elastic节点端口, 默认为9300");
        System.out.println("              -index:     指定Elastic节点索引名, 默认为rocket");
        System.out.println("              -type:      指定Elastic节点索引数据类型名, 默认为original");
        System.out.println("              -bulknum:   指定Elastic批量提交索引的数量, 默认为1");
        System.out.println();
        System.out.println("        help: 打印帮助文档");
    }

}
