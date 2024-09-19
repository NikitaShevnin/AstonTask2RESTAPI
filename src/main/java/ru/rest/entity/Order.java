package ru.rest.entity;

/**
 * Класс {@code Order} представляет заказ пользователя в системе.
 *
 * @author [Ваше Имя]
 * @version 1.0
 */
public class Order {
    private int id;
    private String product;
    private int userId; // Внешний ключ

    /**
     * Создает новый экземпляр {@code Order} с указанными идентификатором, наименованием продукта и идентификатором пользователя.
     *
     * @param product наименование продукта
     * @param userId  идентификатор пользователя, сделавшего заказ
     */
    public Order(String product, int userId) {
        this.id = id;
        this.product = product;
        this.userId = userId;
    }

    /**
     * Возвращает идентификатор заказа.
     *
     * @return идентификатор заказа
     */
    public int getId() {
        return id;
    }

    /**
     * Устанавливает идентификатор заказа.
     *
     * @param id идентификатор заказа
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Возвращает наименование продукта заказа.
     *
     * @return наименование продукта заказа
     */
    public String getProduct() {
        return product;
    }

    /**
     * Устанавливает наименование продукта заказа.
     *
     * @param product наименование продукта заказа
     */
    public void setProduct(String product) {
        this.product = product;
    }

    /**
     * Возвращает идентификатор пользователя, сделавшего заказ.
     *
     * @return идентификатор пользователя
     */
    public int getUserId() {
        return userId;
    }

    /**
     * Устанавливает идентификатор пользователя, сделавшего заказ.
     *
     * @param userId идентификатор пользователя
     */
    public void setUserId(int userId) {
        this.userId = userId;
    }
}
