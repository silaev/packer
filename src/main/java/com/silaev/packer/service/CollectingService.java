package com.silaev.packer.service;

import com.silaev.packer.model.Item;
import com.silaev.packer.model.ItemPackage;

import java.util.List;

public interface CollectingService {
    List<Item> getOptimalItems(ItemPackage itemPackage);
}
