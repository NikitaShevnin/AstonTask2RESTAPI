package ru.rest.DAO;

import ru.rest.entity.User;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO (Data Access Object) класс для управления пользователями в базе данных.
 */
public class UserDAO {
    private final DataSource dataSource;

    /**
     * Создает объект UserDAO, используя DataSource.
     *
     * @param dataSource Источник данных для подключения к базе данных.
     */
    public UserDAO(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * Создает нового пользователя в базе данных.
     *
     * @param user Пользователь для создания.
     * @throws SQLException если произошла ошибка с базой данных.
     */
    public void createUser(User user) throws SQLException {
        String sql = "INSERT INTO users (name, email) VALUES (?, ?)";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, user.getName());
            stmt.setString(2, user.getEmail());
            stmt.executeUpdate();
        }
    }

    /**
     * Извлекает всех пользователей из базы данных.
     *
     * @return Список всех пользователей.
     * @throws SQLException если произошла ошибка с базой данных.
     */
    public List<User> getAllUsers() throws SQLException {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users";
        try (Connection connection = dataSource.getConnection();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setName(rs.getString("name"));
                user.setEmail(rs.getString("email"));
                users.add(user);
            }
        }
        return users;
    }

    /**
     * Обновляет данные пользователя в базе данных.
     *
     * @param user Пользователь с обновленными данными.
     * @throws SQLException если произошла ошибка с базой данных.
     */
    public void updateUser(User user) throws SQLException {
        String sql = "UPDATE users SET name = ?, email = ? WHERE id = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, user.getName());
            stmt.setString(2, user.getEmail());
            stmt.setInt(3, user.getId());
            stmt.executeUpdate();
        }
    }

    /**
     * Удаляет пользователя из базы данных по его идентификатору.
     *
     * @param userId Идентификатор пользователя для удаления.
     * @throws SQLException если произошла ошибка с базой данных.
     */
    public void deleteUser(int userId) throws SQLException {
        String sql = "DELETE FROM users WHERE id = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.executeUpdate();
        }
    }
}
