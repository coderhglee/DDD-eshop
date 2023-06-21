package com.coderhglee.eshop.utils;

import java.util.UUID;
import com.fasterxml.uuid.Generators;

public class TestUtils {
    public static UUID generateUUID() {
        return Generators.timeBasedGenerator().generate();
    }

    public static String generateUUIDToString() {
        return Generators.timeBasedGenerator().generate().toString();
    }
}
