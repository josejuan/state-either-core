package com.computermind.stateeither;

import org.junit.jupiter.api.Test;

import static com.computermind.stateeither.Right.success;
import static org.junit.jupiter.api.Assertions.assertEquals;

class RightTest {

    @Test
    void either() {
        assertEquals(true, success(null).either(l -> false, r -> true));
    }

}