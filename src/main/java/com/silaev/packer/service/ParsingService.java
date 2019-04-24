package com.silaev.packer.service;

import com.silaev.packer.exception.APIException;
import com.silaev.packer.model.ItemPackage;

import java.util.List;

public interface ParsingService {
    List<ItemPackage> parseFile(String filePath) throws APIException;
}
