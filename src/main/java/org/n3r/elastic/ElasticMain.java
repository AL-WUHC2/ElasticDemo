package org.n3r.elastic;

import org.apache.commons.lang3.ArrayUtils;

public class ElasticMain {

    public static void main(String[] args) {
        if (args.length <= 0) {
            new ElasticHelper().execute(new String[]{});
            return;
        }

        String[] params = ArrayUtils.subarray(args, 1, args.length);
        if (ElasticArgs.GEN_PARAM.equalsIgnoreCase(args[0])) {
            new ElasticGenerator().execute(params);
        } else if (ElasticArgs.IDX_PARAM.equalsIgnoreCase(args[0])) {
            new ElasticIndexer().execute(params);
        } else {
            new ElasticHelper().execute(params);
        }
    }

}
