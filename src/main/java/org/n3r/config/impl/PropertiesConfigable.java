package org.n3r.config.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.springframework.core.io.Resource;

import com.google.common.io.Closeables;

public class PropertiesConfigable extends DefaultConfigable {

    public PropertiesConfigable(Resource res) {
        super(buildProperties(res));
    }

    private static Properties buildProperties(Resource res) {
        Properties properties = new Properties();
        InputStream inputStream = null;;
        try {
            inputStream = res.getInputStream();
            properties.load(inputStream);
        }
        catch (IOException e) {
            Closeables.closeQuietly(inputStream);
        }

        return properties;
    }

}
