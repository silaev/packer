package com.silaev.packer.di;

import com.silaev.packer.service.CollectingService;
import com.silaev.packer.service.ParsingService;
import com.silaev.packer.service.PropertyService;

/**
 * Provides access to all of the services so that circular dependencies
 * between them can be resolved.
 */
public interface Services {
    ParsingService getParsingService();

    CollectingService getCollectingService();

    PropertyService getPropertyService();
}
