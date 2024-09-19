package ru.rest.service;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.rest.entity.Order;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Тестовый класс для проверки функциональности службы заказов (OrderService).
 * Этот класс содержит тесты, которые проверяют основные операции работы с заказами,
 * такие как получение всех заказов, получение заказа по ID, вставка, обновление и удаление заказов.
 */
public class TestOrderServiceClass {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/Task2RestApi";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "root";
    private Connection connection;

    /**
     * Устанавливает соединение с базой данных перед выполнением тестов.
     *
     * @throws SQLException если не удалось установить соединение с базой данных
     */
    @Before
    public void setUp() throws SQLException {
        // Устанавливаем соединение с базой данных
        connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        // Можно добавить код для настройки тестовых данных, если необходимо.
    }

    /**
     * Тестирует получение всех заказов из базы данных.
     * Проверяет, что список заказов не равен null и что в таблице заказов содержится ожидаемое количество заказов.
     */
    @Test
    public void testGetAllOrders() {
        List<Order> orders = OrderService.getAllOrders();
        assertNotNull(orders);
        // Предполагаем, что в таблице orders 10 заказов
        assertEquals(10, orders.size());
    }

    /**
     * Тестирует получение заказа по его идентификатору.
     * Проверяет, что заказ с указанным идентификатором существует и его данные соответствуют ожидаемым.
     */
    @Test
    public void testGetOrderById() {
        Order order = OrderService.getOrderById(1); // Предположим, что заказ с id 1 существует
        assertNotNull(order);
        assertEquals("Product A", order.getProduct()); // Ожидаемое название товара
        assertEquals(1, order.getUserId()); // Пользователь с идентификатором 1
    }

    /**
     * Тестирует вставку нового заказа в базу данных.
     * Проверяет, что идентификатор нового заказа больше 0 и что заказ действительно добавлен в базу данных.
     */
    @Test
    public void testInsertOrderIntoDatabase() {
        Order newOrder = new Order("New Product", 1); // Новый заказ для пользователя с id 1
        int newOrderId = OrderService.insertOrderIntoDatabase(newOrder);
        assertTrue(newOrderId > 0); // Проверяем, что идентификатор нового заказа больше 0

        // Проверяем, что заказ действительно добавлен
        Order insertedOrder = OrderService.getOrderById(newOrderId);
        assertNotNull(insertedOrder);
        assertEquals("New Product", insertedOrder.getProduct());
        assertEquals(1, insertedOrder.getUserId());
    }

    /**
     * Тестирует обновление существующего заказа в базе данных.
     * Проверяет, что заказ успешно обновлен, а данные заказа соответствуют ожидаемым.
     */
    @Test
    public void testUpdateOrderInDatabase() {
        Order orderToUpdate = new Order("Updated Product", 1); // Обновляем заказ
        orderToUpdate.setId(1); // Указываем id существующего заказа

        boolean isUpdated = OrderService.updateOrderInDatabase(orderToUpdate);
        assertTrue(isUpdated); // Проверяем, что обновление прошло успешно

        // Проверяем, что данные обновились
        Order updatedOrder = OrderService.getOrderById(1);
        assertEquals("Updated Product", updatedOrder.getProduct());
    }

    /**
     * Тестирует удаление заказа из базы данных.
     * Проверяет, что заказ успешно удален и что он больше не существует в базе данных.
     */
    @Test
    public void testDeleteOrderFromDatabase() {
        boolean isDeleted = OrderService.deleteOrderFromDatabase(10); // Удаляем заказ с id 10
        assertTrue(isDeleted); // Проверяем, что удаление прошло успешно

        // Проверяем, что заказ действительно удален
        Order deletedOrder = OrderService.getOrderById(10);
        assertNull(deletedOrder);
    }

    /**
     * Тестирует получение заказов по идентификатору пользователя.
     * Проверяет, что пользователь с указанным ID имеет заказы и что список заказов не равен null.
     */
    @Test
    public void testGetOrdersByUserId() {
        int userId = 1; // Предполагаем, что существует пользователь с id 1
        List<Order> orders = OrderService.getOrdersByUserId(userId);
        assertNotNull(orders);
        // Проверяем, что у пользователя есть заказы
        assertTrue(orders.size() > 0); // Ожидаем хотя бы один заказ
    }

    /**
     * Закрывает соединение с базой данных после выполнения тестов.
     *
     * @throws SQLException если возникла ошибка при закрытии соединения
     */
    @After
    public void tearDown() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close(); // Закрываем соединение после теста
        }
    }
}