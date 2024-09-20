package ru.rest.controller;

import com.sun.net.httpserver.HttpExchange;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import ru.rest.entity.Order;
import ru.rest.service.OrderService;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class TestOrderController {

    @Mock
    private OrderService orderService;

    @InjectMocks
    private OrderController orderController;

    private HttpExchange exchange;
    private ByteArrayOutputStream outputStream;

    @Before
    public void setUp() {
        exchange = mock(HttpExchange.class);
        outputStream = new ByteArrayOutputStream();
        when(exchange.getResponseBody()).thenReturn(outputStream);
    }

    @Test
    public void testGetOrders() throws IOException {
        List<Order> mockOrders = Collections.singletonList(new Order("Product A", 1));
        when(orderService.getAllOrders()).thenReturn(mockOrders);

        orderController.getOrders(exchange);

        String expectedResponse = "[{\"id\":1,\"product\":\"Product A\",\"userId\":1}]";
        assertEquals(expectedResponse, outputStream.toString(StandardCharsets.UTF_8));
    }

    @Test
    public void testGetOrderById_Success() throws IOException, URISyntaxException {
        Order mockOrder = new Order("Product A", 1);
        when(exchange.getRequestURI()).thenReturn(new URI("/orders/1"));
        when(orderService.getOrderById(1)).thenReturn(mockOrder);

        orderController.getOrderById(exchange);

        String expectedResponse = "{\"id\":1,\"product\":\"Product A\",\"userId\":1}";
        assertEquals(expectedResponse, outputStream.toString(StandardCharsets.UTF_8));
    }

    @Test
    public void testGetOrderById_NotFound() throws IOException, URISyntaxException {
        when(exchange.getRequestURI()).thenReturn(new URI("/orders/2"));
        when(orderService.getOrderById(2)).thenReturn(null);

        orderController.getOrderById(exchange);

        assertEquals(404, exchange.getResponseCode()); // Код статуса
        assertEquals("Order not found", outputStream.toString(StandardCharsets.UTF_8));
    }

    @Test
    public void testCreateOrder_Success() throws IOException {
        String jsonOrder = "{\"product\":\"Product A\",\"userId\":1}";
        InputStream inputStream = new ByteArrayInputStream(jsonOrder.getBytes());
        when(exchange.getRequestBody()).thenReturn(inputStream);

        Order newOrder = new Order("Product A", 1);
        when(orderService.insertOrderIntoDatabase(newOrder)).thenReturn(1);

        orderController.createOrder(exchange);

        String expectedResponse = "{\"id\":1,\"product\":\"Product A\",\"userId\":1}";
        assertEquals(expectedResponse, outputStream.toString(StandardCharsets.UTF_8));
    }

    @Test
    public void testCreateOrder_InvalidData() throws IOException {
        when(exchange.getRequestBody()).thenReturn(new ByteArrayInputStream("invalid data".getBytes()));

        orderController.createOrder(exchange);

        assertEquals(400, exchange.getResponseCode()); // Код статуса
        assertEquals("Invalid order data", outputStream.toString(StandardCharsets.UTF_8));
    }

    @Test
    public void testUpdateOrder_Success() throws IOException, URISyntaxException {
        // Подготовка JSON-данных для обновления заказа
        String jsonOrder = "{\"product\":\"Product A\",\"userId\":1}";
        when(exchange.getRequestBody()).thenReturn(new ByteArrayInputStream(jsonOrder.getBytes()));

        // Установка URI запроса
        when(exchange.getRequestURI()).thenReturn(new URI("/orders/1"));

        // Создание объекта заказа для обновления
        Order updatedOrder = new Order("Product A", 1);
        updatedOrder.setId(1); // Установка ID для обновленного заказа

        // Мокаем `readOrderFromRequest` чтобы вернуть обновленный заказ
        when(orderService.updateOrderInDatabase(any(Order.class))).thenReturn(true);

        // Вызов метода контроллера
        orderController.updateOrder(exchange);

        // Проверяем ответ
        String expectedResponse = "{\"id\":1,\"product\":\"Product A\",\"userId\":1}";
        assertEquals(expectedResponse, outputStream.toString(StandardCharsets.UTF_8));
    }

    // И дополнительно протестируем readOrderFromRequest
    @Test
    public void testCreateOrder_ReadOrderFromRequest() throws IOException {
        // Подготовка входного потока с JSON-данными
        String jsonOrder = "{\"product\":\"Product A\", \"userId\":1}";
        InputStream inputStream = new ByteArrayInputStream(jsonOrder.getBytes());
        when(exchange.getRequestBody()).thenReturn(inputStream);

        // Вызов методу readOrderFromRequest
        Order order = OrderController.readOrderFromRequest(exchange);

        // Проверка, что данные заказа соответствуют ожидаемым
        assertEquals("Product A", order.getProduct());
        assertEquals(1, order.getUserId());
    }

    @Test
    public void testUpdateOrder_NotFound() throws IOException, URISyntaxException {
        String jsonOrder = "{\"product\":\"Product A\",\"userId\":1}";
        when(exchange.getRequestBody()).thenReturn(new ByteArrayInputStream(jsonOrder.getBytes()));
        when(exchange.getRequestURI()).thenReturn(new URI("/orders/1"));

        Order updatedOrder = new Order("Product A", 1);
        when(orderService.updateOrderInDatabase(updatedOrder)).thenReturn(false);

        orderController.updateOrder(exchange);

        assertEquals(404, exchange.getResponseCode()); // Код статуса
        assertEquals("Order not found", outputStream.toString(StandardCharsets.UTF_8));
    }

    @Test
    public void testDeleteOrder_Success() throws IOException, URISyntaxException {
        when(exchange.getRequestURI()).thenReturn(new URI("/orders/1"));
        when(orderService.deleteOrderFromDatabase(1)).thenReturn(true);

        orderController.deleteOrder(exchange);

        verify(exchange).sendResponseHeaders(204, 0); // Проверка на код 204
    }

    @Test
    public void testDeleteOrder_NotFound() throws IOException, URISyntaxException {
        when(exchange.getRequestURI()).thenReturn(new URI("/orders/1"));
        when(orderService.deleteOrderFromDatabase(1)).thenReturn(false);

        orderController.deleteOrder(exchange);

        assertEquals(404, exchange.getResponseCode()); // Код статуса
        assertEquals("Order not found", outputStream.toString(StandardCharsets.UTF_8));
    }

    @Test
    public void testGetOrdersByUserId() throws IOException, URISyntaxException {
        when(exchange.getRequestURI()).thenReturn(new URI("/orders/user/1"));

        Order order = new Order("Product A", 1);
        List<Order> orders = Collections.singletonList(order);
        when(orderService.getOrdersByUserId(1)).thenReturn(orders);

        orderController.getOrdersByUserId(exchange);

        String expectedResponse = "[{\"id\":1,\"product\":\"Product A\",\"userId\":1}]";
        assertEquals(expectedResponse, outputStream.toString(StandardCharsets.UTF_8));
    }
}