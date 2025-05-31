package com.carsales.unitTests;

import com.carsales.Admin;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class AdminTest {
    @Test
    void testSetAndGetUsername() {
        Admin admin = new Admin();
        admin.setUsername("admin1");
        assertEquals("admin1", admin.getUsername());
    }

    @Test
    void testSetAndGetPassword() {
        Admin admin = new Admin();
        admin.setPassword("pass");
        assertEquals("pass", admin.getPassword());
    }

    @Test
    void testSetAndGetDealerId() {
        Admin admin = new Admin();
        admin.setDealer_id(5);
        assertEquals(5, admin.getDealer_id());
    }

    @Test
    void testDefaultValues() {
        Admin admin = new Admin();
        assertNull(admin.getUsername());
        assertNull(admin.getPassword());
        assertEquals(0, admin.getDealer_id());
    }

    @Test
    void testChangePassword() {
        Admin admin = new Admin();
        admin.setPassword("a");
        admin.setPassword("b");
        assertEquals("b", admin.getPassword());
    }

    @Test
    void testSetNegativeDealerId() {
        Admin admin = new Admin();
        admin.setDealer_id(-1);
        assertEquals(-1, admin.getDealer_id());
    }
}