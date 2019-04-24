package com.silaev.packer.di;

import com.silaev.packer.service.CollectingService;
import com.silaev.packer.service.ParsingService;
import com.silaev.packer.service.PropertyService;
import com.silaev.packer.service.impl.CollectingServiceImpl;
import com.silaev.packer.service.impl.ParsingServiceImpl;
import com.silaev.packer.service.impl.PropertyServiceImpl;
import lombok.Getter;

/**
 * Builds a set of services.
 */
@Getter
public class ServiceFactory implements Services {
    private final ParsingService parsingService;
    private final CollectingService collectingService;
    private final PropertyService propertyService;

    private ServiceFactory() {
        propertyService = new PropertyServiceImpl(this);
        parsingService = new ParsingServiceImpl(this, propertyService);
        collectingService = new CollectingServiceImpl(this, propertyService);
    }

    public static Services createServices() {
        return new ServiceFactory();
    }
}
