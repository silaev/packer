package com.silaev.packer.service.impl;

import com.silaev.packer.di.AbstractService;
import com.silaev.packer.di.Services;
import com.silaev.packer.service.PropertyService;
import lombok.Getter;
import lombok.SneakyThrows;

import java.io.InputStream;
import java.math.BigDecimal;
import java.util.Properties;

/**
 * Provides properties declared in a config.properties file.
 */
@Getter
public class PropertyServiceImpl extends AbstractService implements PropertyService {

    private final BigDecimal maxWeight;
    private final BigDecimal maxCost;
    private final int itemLimit;
    private final int weightScale;

    public PropertyServiceImpl(Services services) {
        super(services);

        Properties properties = getProperties();

        this.maxWeight =
            new BigDecimal(properties.getProperty("maxWeight", "100"));
        this.maxCost =
            new BigDecimal(properties.getProperty("maxCost", "100"));
        this.itemLimit =
            new Integer(properties.getProperty("itemLimit", "15"));
        this.weightScale =
            new Integer(properties.getProperty("weightScale", "2"));
    }

    @SneakyThrows
    private Properties getProperties() {
        final Properties properties = new Properties();
        InputStream inputStream =
            getClass().getResourceAsStream("/config.properties");
        properties.load(inputStream);
        return properties;
    }
}
