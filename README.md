## "Специализированные инструменты разработки на языке Java"
## «Защита OTP-кодом»

## Описание проекта
Сервис для защиты операций с помощью временных OTP-кодов.

## Перед началом работы необходимо:
  1. Внести настройки в - application.yaml
  2. Заполнить настройки для sms уведомлений
  3. Заполнить настройки для email уведомлений
  4. Заполнить настройки для уведомлений в telegram (код бота в файле недействителен)
  5. Запустить приложение (Используется система сборки Maven)

## Описание API
Используется Bearer авторизация на всех API кроме register и login.

### API администратора
Для использования данных API необходимо быть авторизованным под УЗ администратора.

#### Вывод зарегестрированных пользователей, кроме пользователя-администратора
GET http://localhost:8080/api/admin/users

#### Обновление конфигурации OTP кода
PUT http://localhost:8080/api/admin/config
codeLength - длина ОТП кода
expirationTime - время жизни в секундах

#### Удаление пользователя по его id
DELETE http://localhost:8080/api/admin/deleteUser/{{id}}

### API авторизации
#### Регистрация нового пользователя
POST http://localhost:8080/api/register
username - логин пользователя
password - пароль
email - адресс электронной почты в формате mail@example.com
telegram - имя пользователя в telegram
phone - номер телефона 
role - роль пользователя. Возможные значения ROLE_USER, ROLE_ADMIN. Админ может быть только один.

#### Аутентификация
POST http://localhost:8080/api/login
username - логин пользователя
password - пароль

### API работы с OTP кодами
####  Генерация ОТП кода
POST http://localhost:8080/api/generate
operationId - id операции
notificationType - способ получения кода. Возможные значения TELEGRAM, EMAIL, SMS, FILE

#### Валидация ОТП кода
POST http://localhost:8080/api/validate
code - ОТП код
operationId - id операции

## Тестирование / примеры вызовов.
Проверить работу API можно при помощи Postman.
### Регистрация
POST http://localhost:8080/api/register
{
    "username": "AntonioB",
    "password": "123456",
    "email": "mail@example.com"
    ,"role": "ROLE_ADMIN"
    , "telegram": "Antonio2"
    , "phone": "89997775566"
}
Пример ответа:
{
    "username": "AntonioB",
    "email": "mail@example.com",
    "phoneNumber": "89997775566",
    "telegram": "Antonio2"
}
Нельзя зарегистрировать пользователя с role ROLE_ADMIN если он уже есть в системе.
Нельзя зарегестировать пользователя с таким же именем
Если не указать role - проставится ROLE_USER

#### Аутентификация
POST http://localhost:8080/api/login
{
    "username": "AntonioB",
    "password": "123456"
}
В ответ выдаёт JWT токен.

#### Обновление конфигурации OTP кода
PUT http://localhost:8080/api/admin/config
{
    "codeLength": "4",
    "expirationTime": "30"
}
Требует авторизации под пользователем с ROLE_ADMIN
Пример ответа:
{
    "id": 1,
    "codeLength": 4,
    "expirationTime": 30
}

#### Удаление пользователя по его id
Требует авторизации под пользователем с ROLE_ADMIN
DELETE http://localhost:8080/api/admin/deleteUser/{{id}}
возвращает true при успешном удалении

#### Вывод зарегестрированных пользователей, кроме пользователя-администратора
GET http://localhost:8080/api/admin/users
Пример ответа:
[
    {
        "id": 5,
        "username": "User",
        "password": "$2a$10$KRMxHxDq9WqoD5zDqFkBJ.VHOcTOeTr76kfJbuWw12JIaG6sa/t8G",
        "email": "mail@example.com",
        "phone": null,
        "telegram": "userA",
        "role": "ROLE_USER"
    },
    {
        "id": 6,
        "username": "AntonioB",
        "password": "$2a$10$awX1lNG4IyC0a3rbcgmLtur5dhOOWr/g1OtPJ.WZ2Ml.PizRr7QWi",
        "email": "mail@example.com",
        "phone": null,
        "telegram": "Antonio2",
        "role": "ROLE_USER"
    },
    {
        "id": 7,
        "username": "Brian,
        "password": "$2a$10$G/UNPnfRNec3xzP8WRVZbe5ZeH3rR95Y/I6asDH2geOVSNiOuaM3a",
        "email": "mail@example.com",
        "phone": "89997775566",
        "telegram": "Brianuser",
        "role": "ROLE_USER"
    }
]

####  Генерация ОТП кода
POST http://localhost:8080/api/generate
Требует авторизации 
{
    "operationId": "3",
    "notificationType": "FILE"
}
Пример ответа:
{
    "code": "7833",
    "operationId": 3,
    "username": "AntonioB",
    "notificationType": "FILE"
}
При notificationType:
1. FILE -> код сохраняется в файл с названием {username}_operaionId.txt
2. TELEGRAM -> пользователь перед первым вызовом должен проинициализировать бота (написать ему). Код отправляется в чат с ботом (токен которого указан в application.yaml) 
3. EMAIL -> отправка на email пользователя
4. SMS -> отправка на номер телефона пользователя (для тестирования можно использовать SMPPSim)


#### Валидация ОТП кода
POST http://localhost:8080/api/validate
{
    "code": "7833",
    "operationId": "3"
}
Пример ответа:
{
    "code": "7833",
    "valid": false
}
При получении "valid": true код переходит в состояние USED и при повторном запросе выдаст "valid": false/
Так же код перейдёт из состояния ACTIVE в EXPIRED по истечению времени жизни
