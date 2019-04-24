package com.silaev.packer.di;

/**
 * Each service is supposed to extend AbstractService
 * to employ the dependency inversion principle
 */
public abstract class AbstractService {
    protected final Services services;

    protected AbstractService(Services services) {
        this.services = services;
    }
}
