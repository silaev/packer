package com.silaev.packer;

import com.silaev.packer.exception.APIException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PackerTest {

    @Test
    void shouldPack() throws APIException {
        //GIVEN
        String filePath = "src/test/resources/input_files/correct/input.txt";
        String packStringExpected = "4" + System.lineSeparator()
            + Packer.NONE_ITEMS + System.lineSeparator()
            + "2,7" + System.lineSeparator()
            + "8,9" + System.lineSeparator();

        //WHEN
        String packStringActual = Packer.pack(filePath);

        //THEN
        assertEquals(packStringExpected, packStringActual);
    }
}
