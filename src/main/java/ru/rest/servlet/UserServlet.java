package ru.rest.servlet;

import ru.rest.DAO.UserDAO;
import ru.rest.entity.User;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Сервлет для управления пользователями.
 */
public class UserServlet extends HttpServlet {
    private UserDAO userDAO;

    public void init() {
        userDAO = new UserDAO();
    }

    /**
     * Обновляет информацию о пользователе.
     *
     * @param request  объект запроса, содержащий данные пользователя.
     * @param response объект ответа для отправки ответа клиенту.
     * @throws ServletException если произошла ошибка во время обработки.
     * @throws IOException      если произошла ошибка ввода-вывода.
     */
    protected void updateUser(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int userId = Integer.parseInt(request.getParameter("id"));
        String name = request.getParameter("name");
        String email = request.getParameter("email");

        User user = new User();
        user.setId(userId);
        user.setName(name);
        user.setEmail(email);

        try {
            userDAO.updateUser(user);
            response.getWriter().println("User updated successfully.");
        } catch (Exception e) {
            response.getWriter().println("Error updating user: " + e.getMessage());
        }
    }

    /**
     * Удаляет пользователя по идентификатору.
     *
     * @param request  объект запроса, содержащий идентификатор пользователя.
     * @param response объект ответа для отправки ответа клиенту.
     * @throws ServletException если произошла ошибка во время обработки.
     * @throws IOException      если произошла ошибка ввода-вывода.
     */
    protected void deleteUser(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int userId = Integer.parseInt(request.getParameter("id"));

        try {
            userDAO.deleteUser(userId);
            response.getWriter().println("User deleted successfully.");
        } catch (Exception e) {
            response.getWriter().println("Error deleting user: " + e.getMessage());
        }
    }
}
