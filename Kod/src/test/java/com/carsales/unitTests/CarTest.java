package com.carsales.unitTests;

import org.junit.jupiter.api.Test;

import com.carsales.Car;

import static org.junit.jupiter.api.Assertions.*;

class CarTest {
    @Test
    void testSetAndGetId() {
        Car car = new Car();
        car.setId(1);
        assertEquals(1, car.getId());
    }

    @Test
    void testSetAndGetBrand() {
        Car car = new Car();
        car.setBrand("Toyota");
        assertEquals("Toyota", car.getBrand());
    }

    @Test
    void testSetAndGetModel() {
        Car car = new Car();
        car.setModel("Corolla");
        assertEquals("Corolla", car.getModel());
    }

    @Test
    void testSetAndGetYear() {
        Car car = new Car();
        car.setYear(2020);
        assertEquals(2020, car.getYear());
    }

    @Test
    void testSetAndGetPackageType() {
        Car car = new Car();
        car.setPackage_type("Premium");
        assertEquals("Premium", car.getPackage_type());
    }

    @Test
    void testSetAndGetPrice() {
        Car car = new Car();
        car.setPrice(350000.0);
        assertEquals(350000.0, car.getPrice());
    }

    @Test
    void testSetAndGetStatus() {
        Car car = new Car();
        car.setStatus("Stokta");
        assertEquals("Stokta", car.getStatus());
    }

    @Test
    void testSetAndGetDealer() {
        Car car = new Car();
        car.setDealer(2);
        assertEquals(2, car.getDealer());
    }

    @Test
    void testDefaultValues() {
        Car car = new Car();
        assertEquals(0, car.getId());
        assertNull(car.getBrand());
        assertNull(car.getModel());
        assertEquals(0, car.getYear());
        assertNull(car.getPackage_type());
        assertEquals(0.0, car.getPrice());
        assertNull(car.getStatus());
        assertEquals(0, car.getDealer());
    }

    @Test
    void testChangeBrand() {
        Car car = new Car();
        car.setBrand("Toyota");
        car.setBrand("Honda");
        assertEquals("Honda", car.getBrand());
    }

    @Test
    void testSetNegativePrice() {
        Car car = new Car();
        car.setPrice(-1000.0);
        assertEquals(-1000.0, car.getPrice());
    }
}