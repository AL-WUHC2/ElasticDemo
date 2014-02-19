package org.n3r.elastic.iface;

import org.n3r.core.lang.Pair;

public interface FileLineReader {

    public Pair<String, byte[]> readLine(long lineNum, String lineContent) throws Exception;

}
