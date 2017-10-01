package com.computermind.stateeither;

import org.junit.jupiter.api.Test;

import static com.computermind.stateeither.Left.failure;
import static org.junit.jupiter.api.Assertions.assertEquals;

class LeftTest {

    @Test
    void either() {
        assertEquals(true, failure(null).either(l -> true, r -> false));
    }

}