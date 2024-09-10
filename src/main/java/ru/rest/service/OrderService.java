package ru.rest.service;

import ru.rest.entity.Order;
import ru.rest.repository.OrderRepository;

import java.util.List;
import java.util.Optional;

/**
 * Сервисный класс для управления заказами.
 */
public class OrderService {
    private final OrderRepository orderRepository;

    /**
     * Конструктор, создающий новый экземпляр класса OrderService.
     *
     * @param orderRepository репозиторий для управления заказами
     */
    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    /**
     * Создает новый заказ.
     *
     * @param order заказ, который нужно создать
     * @return созданный заказ
     */
    public Order createOrder(Order order) {
        // Валидация данных заказа, если необходимо
        return orderRepository.save(order);
    }

    /**
     * Находит заказ по его идентификатору.
     *
     * @param id идентификатор заказа, который нужно найти
     * @return необязательный объект, содержащий найденный заказ, или пустой необязательный объект, если заказ не найден
     */
    public Optional<Order> findOrderById(Long id) {
        return orderRepository.findById(id);
    }

    /**
     * Находит все заказы.
     *
     * @return список всех заказов
     */
    public List<Order> findAllOrders() {
        return orderRepository.findAll();
    }

    /**
     * Обновляет существующий заказ.
     *
     * @param order обновленный заказ
     * @return обновленный заказ
     */
    public Order updateOrder(Order order) {
        // Валидация обновленных данных заказа, если необходимо
        return orderRepository.save(order);
    }

    /**
     * Удаляет заказ по его идентификатору.
     *
     * @param id идентификатор заказа, который нужно удалить
     */
    public void deleteOrder(Long id) {
        orderRepository.deleteById(id);
    }
}
