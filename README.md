# URL Shortener Service

## Описание проекта

Этот сервис позволяет пользователю сокращать длинные URL, отслеживать количество переходов по каждой ссылке, устанавливать лимит переходов и автоматически удалять устаревшие ссылки. Система работает с уникальными идентификаторами пользователей, для которых генерируются индивидуальные короткие ссылки.

### Основные возможности:
- Сокращение длинных URL.
- Установка лимита переходов по каждой ссылке.
- Автоматическое удаление ссылок, срок действия которых истек.
- Оповещение пользователей о недоступности ссылок.
- Управление через консоль.

## Как пользоваться сервисом

1. **Запуск программы**:
   - Для начала работы запустите программу через командную строку, используя вашу среду разработки.
   
2. **Работа с сервисом**:
   После запуска вы увидите консольный интерфейс, предлагающий следующие действия:
   - **Создать короткую ссылку**: Введите длинный URL и лимит переходов.
   - **Перейти по короткой ссылке**: Введите короткую ссылку, чтобы увидеть оригинальный URL или получить уведомление о недоступности.
   - **Выход**: Завершите работу с сервисом.

3. **Пример**:
Welcome to the URL Shortener Service! Your unique UUID: f50d7a9b-519a-4a2d-9d55-22990f5c8c85 Choose an action:

Shorten URL
Access a short URL
Exit
markdown
Копировать

## Поддерживаемые команды

1. **Shorten URL**:
- Введите URL, который хотите сократить.
- Укажите лимит переходов.

Пример:
Enter the long URL: https://www.example.com Enter click limit: 5 Shortened URL: clck.ru/abc123

markdown
Копировать

2. **Access a short URL**:
- Введите короткую ссылку для перехода. Важно помнить, что перед вводом ссылки нужно удалить префикс `clck.ru/`.

Пример:
Enter the short URL: abc123

markdown
Копировать

3. **Exit**:
- Завершить программу.