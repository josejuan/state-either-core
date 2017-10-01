package com.computermind.stateeither;

import org.junit.jupiter.api.Test;

import static com.computermind.stateeither.Right.success;
import static org.junit.jupiter.api.Assertions.*;

class SERightTest {
    @Test
    void isLeft() {
        assertTrue(!SE.right(null).isLeft(), "right value must be false on isLeft");
    }

    @Test
    void isRight() {
        assertTrue(SE.right(null).isRight(), "right value must be true on isRight");
    }

    @Test
    void left() {
        assertThrows(IllegalStateException.class, SE.right(null)::left);
    }

    @Test
    void right() {
        assertEquals("foo", SE.right("foo").right());
    }

    @Test
    void then() {
        assertEquals("foo", SE.right("f").then((s, x) -> success(x + "oo")).right());
    }

    @Test
    void either() {
        assertEquals("foo", SE.right(1L).either(r -> "bar", r -> "foo"));
    }

    @Test
    void map() {
        assertEquals("foo", SE.right(1L).map(r -> "foo").right());
    }
}