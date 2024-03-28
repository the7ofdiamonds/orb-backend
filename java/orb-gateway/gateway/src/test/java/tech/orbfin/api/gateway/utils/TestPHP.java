package tech.orbfin.api.gateway.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TestPHP {
    @Test
    public void testUnserializeInteger() {
        PHP php = new PHP();
        Integer result = php.uInteger("i:123;");
        assertEquals(123, result);
    }

    @Test
    public void testUnserializeDouble() {
        PHP php = new PHP();
        Double result = php.uDouble("d:3.14;");
        assertEquals(3.14, result, 0.01);
    }

    @Test
    public void testUnserializeBoolean() {
        PHP php = new PHP();
        boolean resultTrue = php.uBoolean("b:1;");
        boolean resultFalse = php.uBoolean("b:0;");
        assertTrue(resultTrue);
        assertFalse(resultFalse);
    }

    @Test
    public void testUnserializeString() {
        PHP php = new PHP();
        String result = php.uString(5,"s:5:\"Hello\";");
        assertEquals("Hello", result);
    }
}
