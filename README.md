# Aston Task 2 REST API

## Описание

Это RESTful API, реализованное на языке Java с использованием сервлетов, для управления данными пользователей и заказов. 
API позволяет выполнять операции CRUD (Create, Read, Update, Delete) для пользователей и их заказов.

## Функциональность

Основные функции API включают:

по пользователям:
- Получение списка всех пользователей.
- Получение информации о конкретном пользователе по ID.
- Добавление новых пользователей.
- Обновление информации о пользователе.
- Удаление пользователя.

по заказам:
- Получение списка заказов, связанных с конкретным пользователем.
- Получение списка всех заказов.
- Получение информации о конкретном заказе по ID.
- Создание нового заказа.
- Обновление информации о заказе.
- Удаление заказа.

## Основные запросы к http серверу

# Для пользователей
- GET http://localhost:8080/users - получение списка всех пользователей
- GET http://localhost:8080/users/1 - получение информации о пользователе с ID 1
- POST http://localhost:8080/users - создание нового пользователя
- PUT http://localhost:8080/users/1 - обновление информации о пользователе с ID 1
- DELETE http://localhost:8080/users/1 - удаление пользователя с ID 1

# Для заказов
- GET http://localhost:8080/orders - получение списка всех заказов
- GET http://localhost:8080/orders/1 - получение информации о заказе с ID 1
- GET http://localhost:8080/orders/1/users - получение списка заказов, связанных с пользователем 1
- POST http://localhost:8080/orders - создание нового заказа
- PUT http://localhost:8080/orders/1 - обновление информации о заказе с ID 1
- DELETE http://localhost:8080/orders/1 - удаление заказа с ID 1


## Технологический стек

- Java (с использованием сервлетов)
- MySQL (для хранения данных)
- Maven (для управления зависимостями)
- Gson (для работы с JSON)