package com.carsales.unitTests;

import com.carsales.Dealer;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class DealerTest {
    @Test
    void testSetAndGetId() {
        Dealer dealer = new Dealer();
        dealer.setId(10);
        assertEquals(10, dealer.getId());
    }

    @Test
    void testSetAndGetName() {
        Dealer dealer = new Dealer();
        dealer.setName("Anadolu Bayi");
        assertEquals("Anadolu Bayi", dealer.getName());
    }

    @Test
    void testDefaultValues() {
        Dealer dealer = new Dealer();
        assertEquals(0, dealer.getId());
        assertNull(dealer.getName());
    }

    @Test
    void testChangeName() {
        Dealer dealer = new Dealer();
        dealer.setName("A");
        dealer.setName("B");
        assertEquals("B", dealer.getName());
    }

    @Test
    void testSetNegativeId() {
        Dealer dealer = new Dealer();
        dealer.setId(-1);
        assertEquals(-1, dealer.getId());
    }
}