package com.carsales.unitTests;

import com.carsales.Customer;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.sql.Timestamp;

class CustomerTest {
    @Test
    void testSetAndGetUsername() {
        Customer customer = new Customer();
        customer.setUsername("ali");
        assertEquals("ali", customer.getUsername());
    }

    @Test
    void testSetAndGetPassword() {
        Customer customer = new Customer();
        customer.setPassword("1234");
        assertEquals("1234", customer.getPassword());
    }

    @Test
    void testSetAndGetName() {
        Customer customer = new Customer();
        customer.setName("Ali Veli");
        assertEquals("Ali Veli", customer.getName());
    }

    @Test
    void testSetAndGetAge() {
        Customer customer = new Customer();
        customer.setAge(30);
        assertEquals(30, customer.getAge());
    }

    @Test
    void testSetAndGetPhone() {
        Customer customer = new Customer();
        customer.setPhone("5551234567");
        assertEquals("5551234567", customer.getPhone());
    }

    @Test
    void testSetAndGetRegistrationDate() {
        Customer customer = new Customer();
        Timestamp now = new Timestamp(System.currentTimeMillis());
        customer.setRegistration_date(now);
        assertEquals(now, customer.getRegistration_date());
    }

    @Test
    void testDefaultValues() {
        Customer customer = new Customer();
        assertNull(customer.getUsername());
        assertNull(customer.getPassword());
        assertNull(customer.getName());
        assertEquals(0, customer.getAge());
        assertNull(customer.getPhone());
        assertNull(customer.getRegistration_date());
    }

    @Test
    void testChangeUsername() {
        Customer customer = new Customer();
        customer.setUsername("ali");
        customer.setUsername("veli");
        assertEquals("veli", customer.getUsername());
    }

    @Test
    void testChangeAge() {
        Customer customer = new Customer();
        customer.setAge(20);
        customer.setAge(40);
        assertEquals(40, customer.getAge());
    }

    @Test
    void testSetEmptyName() {
        Customer customer = new Customer();
        customer.setName("");
        assertEquals("", customer.getName());
    }

    @Test
    void testSetNullPhone() {
        Customer customer = new Customer();
        customer.setPhone(null);
        assertNull(customer.getPhone());
    }
}