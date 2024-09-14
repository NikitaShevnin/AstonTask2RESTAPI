package ru.rest.servlet;

import ru.rest.DAO.OrderDAO;
import ru.rest.entity.Order;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Сервлет для управления заказами.
 */
public class OrderServlet extends HttpServlet {
    private OrderDAO orderDAO;

    public void init() throws ServletException {
        try {
            InitialContext ctx = new InitialContext();
            DataSource dataSource = (DataSource) ctx.lookup("java:comp/env/jdbc/yourDataSource");
            orderDAO = new OrderDAO(dataSource);
        } catch (NamingException e) {
            throw new ServletException("Cannot initialize OrderDAO", e);
        }
    }

    /**
     * Обновляет информацию о заказе.
     *
     * @param request  объект запроса, содержащий данные заказа.
     * @param response объект ответа для отправки ответа клиенту.
     * @throws ServletException если произошла ошибка во время обработки.
     * @throws IOException      если произошла ошибка ввода-вывода.
     */
    protected void updateOrder(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int orderId = Integer.parseInt(request.getParameter("id"));

        String product = request.getParameter("product");
        int userId = Integer.parseInt(request.getParameter("user_id"));

        Order order = new Order();
        order.setId(orderId);
        order.setProduct(product);
        order.setUserId(userId);

        try {
            orderDAO.updateOrder(order);
            response.getWriter().println("Order updated successfully.");
        } catch (Exception e) {
            response.getWriter().println("Error updating order: " + e.getMessage());
        }
    }

    /**
     * Удаляет заказ по идентификатору.
     *
     * @param request  объект запроса, содержащий идентификатор заказа.
     * @param response объект ответа для отправки ответа клиенту.
     * @throws ServletException если произошла ошибка во время обработки.
     * @throws IOException      если произошла ошибка ввода-вывода.
     */
    protected void deleteOrder(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int orderId = Integer.parseInt(request.getParameter("id"));

        try {
            orderDAO.deleteOrder(orderId);
            response.getWriter().println("Order deleted successfully.");
        } catch (Exception e) {
            response.getWriter().println("Error deleting order: " + e.getMessage());
        }
    }
}
