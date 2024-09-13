package ru.rest.DAO;

import ru.rest.entity.Order;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO (Data Access Object) класс для управления заказами в базе данных.
 */
public class OrderDAO {
    private final DataSource dataSource;

    /**
     * Создает объект OrderDAO, используя DataSource.
     *
     * @param dataSource Источник данных для подключения к базе данных.
     */
    public OrderDAO(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * Создает новый заказ в базе данных.
     *
     * @param order Заказ для создания.
     * @throws SQLException если произошла ошибка с базой данных.
     */
    public void createOrder(Order order) throws SQLException {
        String sql = "INSERT INTO orders (product, user_id) VALUES (?, ?)";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, order.getProduct());
            stmt.setInt(2, order.getUserId());
            stmt.executeUpdate();
        }
    }

    /**
     * Извлекает все заказы из базы данных.
     *
     * @return Список всех заказов.
     * @throws SQLException если произошла ошибка с базой данных.
     */
    public List<Order> getAllOrders() throws SQLException {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT * FROM orders";
        try (Connection connection = dataSource.getConnection();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Order order = new Order();
                order.setId(rs.getInt("id"));
                order.setProduct(rs.getString("product"));
                order.setUserId(rs.getInt("user_id"));
                orders.add(order);
            }
        }
        return orders;
    }

    /**
     * Обновляет данные заказа в базе данных.
     *
     * @param order Заказ с обновленными данными.
     * @throws SQLException если произошла ошибка с базой данных.
     */
    public void updateOrder(Order order) throws SQLException {
        String sql = "UPDATE orders SET product = ?, user_id = ? WHERE id = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, order.getProduct());
            stmt.setInt(2, order.getUserId());
            stmt.setInt(3, order.getId());
            stmt.executeUpdate();
        }
    }

    /**
     * Удаляет заказ из базы данных по его идентификатору.
     *
     * @param orderId Идентификатор заказа для удаления.
     * @throws SQLException если произошла ошибка с базой данных.
     */
    public void deleteOrder(int orderId) throws SQLException {
        String sql = "DELETE FROM orders WHERE id = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, orderId);
            stmt.executeUpdate();
        }
    }
}
