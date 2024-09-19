package ru.rest.controller;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import com.sun.net.httpserver.HttpExchange;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import ru.rest.entity.Order;
import ru.rest.service.OrderService;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals; // Импортируйте из JUnit 4
import static org.mockito.Mockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest(OrderController.class)
public class TestOrderController {

    /**
     * Тестирует метод {@code getOrders} класса {@link OrderController}.
     * Ожидаемый результат:
     * Метод должен вернуть строку JSON с представлением списка заказов.
     * @throws IOException Если происходит ошибка при работе с потоками ввода-вывода.
     */
    @Test
    public void testGetOrders() throws IOException {
        // Создаем мока для HttpExchange
        HttpExchange exchange = Mockito.mock(HttpExchange.class);

        // Создаем резервный OutputStream для проверки ответа
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        when(exchange.getResponseBody()).thenReturn(outputStream);

        // Мокаем статический метод getAllOrders
        PowerMockito.mockStatic(OrderController.class);
        List<Order> mockOrders = Collections.singletonList(new Order("Product A", 1));
        PowerMockito.when(OrderService.getAllOrders()).thenReturn(mockOrders);

        // Вызов метода
        OrderController.getOrders(exchange);

        // Проверка, что метод отправляет корректный JSON-ответ
        String response = outputStream.toString(StandardCharsets.UTF_8);
        assertEquals("[{\"id\":1,\"product\":\"Product A\",\"userId\":1}]", response);
    }

    /**
     * Тестирует метод {@code getOrderById} класса {@link OrderController}.
     *
     * Этот метод проверяет, что {@code getOrderById} корректно обрабатывает
     * запрос по идентификатору заказа и возвращает ожидаемый JSON-ответ
     * или сообщение об ошибке, если заказ не найден.
     *
     * @throws IOException Если возникает ошибка при работе с потоками ввода-вывода.
     */
    @Test
    public void testGetOrderById() throws IOException, URISyntaxException {
        // Создаем мока для HttpExchange
        HttpExchange exchange = Mockito.mock(HttpExchange.class);

        // Создаем резервный OutputStream для проверки ответа
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        when(exchange.getResponseBody()).thenReturn(outputStream);

        // Устанавливаем мока для других методов
        Order mockOrder = new Order("Product A", 1);
        PowerMockito.mockStatic(OrderController.class);

        // Сценарий: заказ найден
        when(exchange.getRequestURI()).thenReturn(new java.net.URI("/orders/1"));
        PowerMockito.when(OrderService.getOrderById(1)).thenReturn(mockOrder);

        // Вызов метода
        OrderController.getOrderById(exchange);

        // Проверка, что метод отправляет корректный JSON-ответ
        String response = outputStream.toString(StandardCharsets.UTF_8);
        assertEquals("{\"id\":1,\"product\":\"Product A\",\"userId\":1}", response);

        // Сценарий: заказ не найден
        outputStream.reset(); // Очистить поток для нового сценария
        when(exchange.getRequestURI()).thenReturn(new java.net.URI("/orders/2"));
        PowerMockito.when(OrderService.getOrderById(2)).thenReturn(null);

        // Вызов метода
        OrderController.getOrderById(exchange);

        // Проверка, что отправляется статус 404 и сообщение об ошибке
        assertEquals(404, exchange.getResponseHeaders().getFirst("Response-Code")); // Проверка кода ответа
        String errorResponse = outputStream.toString(StandardCharsets.UTF_8);
        assertEquals("Order not found", errorResponse);
    }

    /**
     * Тестирует метод {@code createOrder} класса {@link OrderController}.
     *
     * Этот метод проверяет, что {@code createOrder} корректно обрабатывает
     * запрос на создание нового заказа и возвращает ожидаемый JSON-ответ
     * или сообщение об ошибке в случае недействительных данных.
     *
     * @throws IOException Если возникает ошибка при работе с потоками ввода-вывода.
     */
    @Test
    public void testCreateOrder() throws IOException {
        // Создаем мока для HttpExchange
        HttpExchange exchange = Mockito.mock(HttpExchange.class);

        // Создаем резервный OutputStream для проверки ответа
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        when(exchange.getResponseBody()).thenReturn(outputStream);

        // Устанавливаем мока для других методов
        Order mockOrder = new Order("Product A", 1);
        PowerMockito.mockStatic(OrderController.class);

        // Сценарий: заказ успешно создан
        when(OrderController.readOrderFromRequest(exchange)).thenReturn(mockOrder);
        when(OrderService.insertOrderIntoDatabase(mockOrder)).thenReturn(1);

        // Вызов метода
        OrderController.createOrder(exchange);

        // Проверка, что метод отправляет корректный JSON-ответ
        String response = outputStream.toString(StandardCharsets.UTF_8);
        assertEquals("{\"id\":1,\"product\":\"Product A\",\"userId\":1}", response);

        // Сценарий: недействительные данные заказа
        outputStream.reset(); // Очистить поток для нового сценария
        when(OrderController.readOrderFromRequest(exchange)).thenReturn(null);

        // Вызов метода
        OrderController.createOrder(exchange);

        // Проверка, что отправляется статус 400 и сообщение об ошибке
        String errorResponse = outputStream.toString(StandardCharsets.UTF_8);
        assertEquals("Invalid order data", errorResponse);
        assertEquals(400, exchange.getResponseHeaders().getFirst("Response-Code")); // Проверка кода ответа
    }

    /**
     * Тестирует метод {@code updateOrder} класса {@link OrderController}.
     *
     * Этот метод проверяет, что {@code updateOrder} корректно обрабатывает
     * запрос на обновление заказа и возвращает ожидаемый JSON-ответ
     * или сообщение об ошибке в случае несуществующего заказа или недействительных данных.
     *
     * @throws IOException Если возникает ошибка при работе с потоками ввода-вывода.
     */
    @Test
    public void testUpdateOrder() throws IOException, URISyntaxException {
        // Создаем мока для HttpExchange
        HttpExchange exchange = Mockito.mock(HttpExchange.class);

        // Создаем резервный OutputStream для проверки ответа
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        when(exchange.getResponseBody()).thenReturn(outputStream);

        // Устанавливаем мока для других методов
        Order updatedOrder = new Order("Product A", 1);
        PowerMockito.mockStatic(OrderController.class);

        // Сценарий: заказ успешно обновлен
        when(exchange.getRequestURI()).thenReturn(new java.net.URI("/orders/1"));
        when(OrderController.readOrderFromRequest(exchange)).thenReturn(updatedOrder);
        when(OrderService.updateOrderInDatabase(updatedOrder)).thenReturn(true);

        // Вызов метода
        OrderController.updateOrder(exchange);

        // Проверка, что метод отправляет корректный JSON-ответ
        updatedOrder.setId(1); // Устанавливаем ID для отправки
        String response = outputStream.toString(StandardCharsets.UTF_8);
        assertEquals("{\"id\":1,\"product\":\"Product A\",\"userId\":1}", response);

        // Сценарий: заказ не найден (обновление несуществующего заказа)
        outputStream.reset(); // Очистить поток для нового сценария
        when(OrderService.updateOrderInDatabase(updatedOrder)).thenReturn(false);

        // Вызов метода
        OrderController.updateOrder(exchange);

        // Проверка, что отправляется статус 404 и сообщение об ошибке
        String notFoundResponse = outputStream.toString(StandardCharsets.UTF_8);
        assertEquals("Order not found", notFoundResponse);
        assertEquals(404, exchange.getResponseHeaders().getFirst("Response-Code")); // Проверка кода ответа

        // Сценарий: недействительные данные заказа
        outputStream.reset(); // Очистить поток для нового сценария
        when(OrderController.readOrderFromRequest(exchange)).thenReturn(null);

        // Вызов метода
        OrderController.updateOrder(exchange);

        // Проверка, что отправляется статус 400 и сообщение об ошибке
        String invalidDataResponse = outputStream.toString(StandardCharsets.UTF_8);
        assertEquals("Invalid order data", invalidDataResponse);
        assertEquals(400, exchange.getResponseHeaders().getFirst("Response-Code")); // Проверка кода ответа
    }

    /**
     * Тестирует метод {@code deleteOrder} класса {@link OrderController}.
     *
     * Этот метод проверяет, что {@code deleteOrder} корректно обрабатывает
     * запрос на удаление заказа и возвращает статус 204 в случае успешного удаления
     * или сообщение об ошибке в случае несуществующего заказа.
     *
     * @throws IOException Если возникает ошибка при работе с потоками ввода-вывода.
     */
    @Test
    public void testDeleteOrder() throws IOException, URISyntaxException {
        // Создаем мока для HttpExchange
        HttpExchange exchange = Mockito.mock(HttpExchange.class);

        // Устанавливаем ожидаемый URI
        when(exchange.getRequestURI()).thenReturn(new java.net.URI("/orders/1"));

        PowerMockito.mockStatic(OrderController.class);

        // Сценарий: заказ успешно удален
        when(OrderService.deleteOrderFromDatabase(1)).thenReturn(true);

        // Вызов метода
        OrderController.deleteOrder(exchange);

        // Проверка, что отправляется статус 204
        verify(exchange).sendResponseHeaders(204, 0);

        // Сценарий: заказ не найден
        when(OrderService.deleteOrderFromDatabase(1)).thenReturn(false);

        // Вызов метода
        OrderController.deleteOrder(exchange);

        // Проверка, что отправляется статус 404 и сообщение об ошибке
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        when(exchange.getResponseBody()).thenReturn(outputStream);

        // Вызов метода
        OrderController.deleteOrder(exchange);

        String errorResponse = outputStream.toString(StandardCharsets.UTF_8);
        assertEquals("Order not found", errorResponse);
        assertEquals(404, exchange.getResponseHeaders().getFirst("Response-Code")); // Проверка кода ответа
    }

    /**
     * Тестирует метод {@code getOrdersByUserId} класса {@link OrderController}.
     *
     * Этот метод проверяет, что {@code getOrdersByUserId} корректно обрабатывает
     * запрос на получение списка заказов по идентификатору пользователя и
     * отправляет ожидаемый JSON-ответ.
     *
     * @throws IOException Если возникает ошибка при работе с потоками ввода-вывода.
     */
    @Test
    public void testGetOrdersByUserId() throws IOException, URISyntaxException {
        // Создаем мока для HttpExchange
        HttpExchange exchange = Mockito.mock(HttpExchange.class);

        // Создаем резервный OutputStream для проверки ответа
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        when(exchange.getResponseBody()).thenReturn(outputStream);

        // Устанавливаем ожидаемый URI
        when(exchange.getRequestURI()).thenReturn(new java.net.URI("/orders/user/1"));

        // Сценарий: заказы найдены для пользователя
        Order order = new Order("Product A", 1);
        List<Order> orders = Collections.singletonList(order);
        when(OrderService.getOrdersByUserId(1)).thenReturn(orders);

        // Вызов метода
        OrderService.getOrdersByUserId(exchange.getResponseCode()); // Вызов метода контроллера

        // Проверка, что метод отправляет корректный JSON-ответ
        String response = outputStream.toString(StandardCharsets.UTF_8);
        assertEquals("[{\"id\":1,\"product\":\"Product A\",\"userId\":1}]", response);

        // Сценарий: у пользователя нет заказов
        outputStream.reset(); // Очистка потока для нового сценария
        when(OrderService.getOrdersByUserId(1)).thenReturn(Collections.emptyList());

        // Вызов метода
        OrderService.getOrdersByUserId(exchange.getResponseCode()); // Вызов метода контроллера для пустого ответа

        // Проверка, что метод все равно отправляет пустой JSON-ответ
        String emptyResponse = outputStream.toString(StandardCharsets.UTF_8);
        assertEquals("[]", emptyResponse);
    }

    /**
     * Тестирует метод {@link OrderController#readOrderFromRequest(HttpExchange)}.
     * <p>
     * Этот тест проверяет, что метод корректно десериализует объект заказа из тела
     * HTTP-запроса. Метод создает мок-объект {@link HttpExchange}, который возвращает
     * заранее заданный JSON, представляющий заказ. После вызова тестируемого метода,
     * проверяется корректность заполнения полей объекта {@link Order}.
     * </p>
     *
     * @throws IOException если возникла ошибка при чтении данных из запроса
     */
    @Test
    public void testReadOrderFromRequest() throws IOException {
        // Создание мок-объекта HttpExchange
        HttpExchange exchange = Mockito.mock(HttpExchange.class);

        // Пример заказа в JSON-формате
        String jsonOrder = "{\"id\":1,\"product\":\"Product A\",\"userId\":1}";

        // Настройка возвращаемого потока данных
        InputStream inputStream = new ByteArrayInputStream(jsonOrder.getBytes());
        when(exchange.getRequestBody()).thenReturn(inputStream);

        // Вызов метода readOrderFromRequest
        Order order = OrderController.readOrderFromRequest(exchange);

        // Проверка, что данные заказа соответствуют ожидаемым
        assertEquals(1, order.getId());
        assertEquals("Product A", order.getProduct());
        assertEquals(1, order.getUserId());
    }
}