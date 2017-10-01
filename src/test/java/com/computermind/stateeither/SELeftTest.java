package com.computermind.stateeither;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SELeftTest {
    @Test
    void isLeft() {
        assertTrue(SE.left(null).isLeft(), "left value must be true on isLeft");
    }

    @Test
    void isRight() {
        assertTrue(!SE.left(null).isRight(), "left value must be false on isRight");
    }

    @Test
    void left() {
        assertEquals("foo", SE.left("foo").left());
    }

    @Test
    void right() {
        assertThrows(IllegalStateException.class, SE.left(null)::right);
    }

    @Test
    void then() {
        assertEquals("foo", SE.left("foo").then((s, x) -> null).left());
    }

    @Test
    void either() {
        assertEquals("foo", SE.left(1L).either(r -> "foo", r -> "bar"));
    }

    @Test
    void map() {
        assertEquals("foo", SE.left("foo").map(r -> null).left());
    }
}