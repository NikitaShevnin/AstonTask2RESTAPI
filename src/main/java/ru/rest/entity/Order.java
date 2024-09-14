package ru.rest.entity;

public class Order {
    private int id;
    private String product;
    private int userId; // Foreign key

    // Конструктор без параметров
    public Order(int id, String product, int userId) {
        this.id = id;
        this.product = product;
        this.userId = userId;
    }
    // Конструктор с параметрами
    public Order() {
        this.id = id;
        this.product = product;
        this.userId = userId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}
