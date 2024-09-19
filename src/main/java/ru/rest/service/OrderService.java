package ru.rest.service;

import ru.rest.entity.Order;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import java.util.ArrayList;

import java.util.List;

public class OrderService {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/Task2RestApi";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "root";

    /**
     * Получает все заказы из базы данных.
     *
     * @return список всех заказов. Если произошла ошибка при доступе к базе данных, возвращается null.
     */
    public static List<Order> getAllOrders() {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String sql = "SELECT id, product, user_id FROM orders";
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery();
            List<Order> orders = new ArrayList<>();
            while (resultSet.next()) {
                orders.add(new Order(
                        resultSet.getInt("id"),
                        resultSet.getString("product"),
                        resultSet.getInt("user_id")
                ));
            }
            return orders;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Получает заказ по его идентификатору.
     *
     * @param orderId идентификатор заказа, который нужно получить.
     * @return объект заказа с указанным идентификатором или null, если заказ не найден или произошла ошибка.
     */
    public static Order getOrderById (int orderId) {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String sql = "SELECT id, product, user_id FROM orders WHERE id = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, orderId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return new Order(
                        resultSet.getInt("id"),
                        resultSet.getString("product"),
                        resultSet.getInt("user_id")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Вставляет новый заказ в базу данных.
     *
     * @param order объект заказа, который нужно вставить.
     * @return идентификатор вставленного заказа или -1 в случае ошибки.
     */
    public static int insertOrderIntoDatabase(Order order) {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String sql = "INSERT INTO orders (product, user_id) VALUES (?, ?)";
            PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, order.getProduct());
            statement.setInt(2, order.getUserId());
            statement.executeUpdate();
            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                } else {
                    throw new SQLException("Creating order failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    /**
     * Обновляет существующий заказ в базе данных.
     *
     * @param order объект заказа с обновленными данными.
     * @return true, если заказ был успешно обновлен; иначе false.
     */
    public static boolean updateOrderInDatabase(Order order) {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String sql = "UPDATE orders SET product = ?, user_id = ? WHERE id = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, order.getProduct());
            statement.setInt(2, order.getUserId());
            statement.setInt(3, order.getId());
            int rowsUpdated = statement.executeUpdate();
            return rowsUpdated > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Удаляет заказ из базы данных.
     *
     * @param orderId идентификатор заказа, который нужно удалить.
     * @return true, если заказ был успешно удален; иначе false.
     */
    public static boolean deleteOrderFromDatabase(int orderId) {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String sql = "DELETE FROM orders WHERE id = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, orderId);
            int rowsDeleted = statement.executeUpdate();
            return rowsDeleted > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Получает список заказов по идентификатору пользователя.
     *
     * @param userId идентификатор пользователя, для которого необходимо получить заказы.
     * @return список заказов для указанного пользователя. Если произошла ошибка при доступе к базе данных, возвращается null.
     */
    public static List<Order> getOrdersByUserId(int userId) {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String sql = "SELECT id, product, user_id FROM orders WHERE user_id = ?";

            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, userId);
            ResultSet resultSet = statement.executeQuery();
            List<Order> orders = new ArrayList<>();
            while (resultSet.next()) {
                orders.add(new Order(
                        resultSet.getInt("id"),
                        resultSet.getString("product"),
                        resultSet.getInt("user_id")
                ));
            }
            return orders;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}
