package com.foodiefinder.datapipeline.util.hash;

import com.foodiefinder.datapipeline.cache.HashGenerator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HashGeneratorTest {

    private HashGenerator hashGenerator = new HashGenerator();


    @Test
    @DisplayName("해시 비교 테스트")
    public void givenStringValue_whenHash_thenReturn(){
        // Given
        String value1 = "asiodasidkpasodkapsodkaposdkaokerpjgpoishgp0aw34uyp309jdrpa9w7";
        String value2 = "asiodasidkpasodkapsodkaposdkaokerpjgpoishgp0aw34uyp309jdrpa9w7";
        String value3 = "asiodasidkpasodkapsodkaposdkaokerpjgpoishgp0aw34uyp309jdrpa9w71";

        // When
        String hash1 = hashGenerator.calculateSHA256(value1);
        String hash2 = hashGenerator.calculateSHA256(value2);
        String hash3 = hashGenerator.calculateSHA256(value3);

        // Then
        System.out.println("hash1 = " + hash1);
        System.out.println("hash2 = " + hash2);
        System.out.println("hash3 = " + hash3);
        assertEquals(hash1, hash2);
        assertNotEquals(hash2,hash3);
    }
}