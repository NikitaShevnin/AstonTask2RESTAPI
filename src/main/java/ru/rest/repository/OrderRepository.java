package ru.rest.repository;

import ru.rest.entity.Order;
import java.util.List;
import java.util.Optional;

/**
 * Интерфейс репозитория для управления заказами.
 */
public interface OrderRepository {
    /**
     * Сохраняет новый заказ.
     *
     * @param order заказ, который нужно сохранить
     * @return сохраненный заказ
     */
    Order save(Order order);

    /**
     * Находит заказ по его идентификатору.
     *
     * @param id идентификатор заказа, который нужно найти
     * @return необязательный объект, содержащий найденный заказ, или пустой необязательный объект, если заказ не найден
     */
    Optional<Order> findById(Long id);

    /**
     * Находит все заказы.
     *
     * @return список всех заказов
     */
    List<Order> findAll();

    /**
     * Удаляет заказ по его идентификатору.
     *
     * @param id идентификатор заказа, который нужно удалить
     */
    void deleteById(Long id);
}