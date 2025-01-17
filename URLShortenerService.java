import java.util.*;
import java.util.concurrent.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.awt.Desktop;
import java.net.URI;
import java.io.IOException;
import java.util.Scanner;

public class URLShortenerService {

    private static final long EXPIRY_TIME_IN_SECONDS = 86400L; // 24 часа
    private Map<String, URLData> urlDatabase = new HashMap<>(); // База данных ссылок
    private Map<String, String> userUUIDs = new HashMap<>(); // База данных UUID пользователей
    private Map<String, Set<String>> userLinks = new HashMap<>(); // База данных ссылок пользователя

    // Класс для хранения информации о короткой ссылке
    private static class URLData {
        String originalURL;
        String userUUID;
        int clickLimit;
        int clicks;
        long expiryTime;

        public URLData(String originalURL, String userUUID, int clickLimit) {
            this.originalURL = originalURL;
            this.userUUID = userUUID;
            this.clickLimit = clickLimit;
            this.clicks = 0;
            this.expiryTime = Instant.now().getEpochSecond() + EXPIRY_TIME_IN_SECONDS; // Устанавливаем время жизни на 24 часа
        }
    }

    // Генерация уникального UUID для пользователя
    public String generateUUID() {
        String uuid = UUID.randomUUID().toString();
        userUUIDs.put(uuid, uuid);
        userLinks.put(uuid, new HashSet<>());
        return uuid;
    }

    // Генерация короткой ссылки
    public String shortenURL(String originalURL, String userUUID, int clickLimit) throws NoSuchAlgorithmException {
        // Проверка UUID
        if (!userUUIDs.containsKey(userUUID)) {
            throw new IllegalArgumentException("Invalid user UUID.");
        }

        // Генерация короткой ссылки с использованием хэширования + UUID для уникальности
        String shortURL = generateShortURLHash(originalURL, userUUID);

        // Сохраняем информацию о ссылке в базе данных
        URLData urlData = new URLData(originalURL, userUUID, clickLimit);
        urlDatabase.put(shortURL, urlData);
        userLinks.get(userUUID).add(shortURL);

        return "clck.ru/" + shortURL;
    }

    // Генерация короткой ссылки на основе хэширования + UUID пользователя
    private String generateShortURLHash(String originalURL, String userUUID) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        String toHash = originalURL + userUUID; // Для уникальности добавляем UUID пользователя
        byte[] hashBytes = md.digest(toHash.getBytes());
        StringBuilder shortURL = new StringBuilder();

        for (int i = 0; i < 6; i++) {
            shortURL.append(Integer.toHexString(hashBytes[i] & 0xFF));
        }

        return shortURL.toString();
    }

    // Переход по короткой ссылке
    public String getOriginalURL(String shortURL, String userUUID) {
        URLData urlData = urlDatabase.get(shortURL);

        if (urlData == null) {
            return "Link not found.";
        }

        // Проверка прав доступа пользователя
        if (!urlData.userUUID.equals(userUUID)) {
            return "You do not have permission to access this link.";
        }

        // Проверка на истечение срока действия ссылки
        if (Instant.now().getEpochSecond() > urlData.expiryTime) {
            urlDatabase.remove(shortURL); // Удаляем просроченную ссылку
            return "This link has expired.";
        }

        // Проверка на достижение лимита переходов
        if (urlData.clicks >= urlData.clickLimit) {
            return "This link has exceeded the click limit.";
        }

        // Увеличиваем количество переходов
        urlData.clicks++;
        openInBrowser(urlData.originalURL); // Открываем ссылку в браузере
        return urlData.originalURL;
    }

    // Метод для открытия ссылки в браузере
    private void openInBrowser(String originalURL) {
        try {
            Desktop.getDesktop().browse(new URI(originalURL));
        } catch (IOException | java.net.URISyntaxException e) {
            e.printStackTrace();
        }
    }

    // Основной метод, для управления сервисом через консоль
    public static void main(String[] args) throws NoSuchAlgorithmException {
        URLShortenerService service = new URLShortenerService();
        Scanner scanner = new Scanner(System.in);

        System.out.println("Welcome to the URL Shortener Service!");

        // Генерация UUID пользователя
        String userUUID = service.generateUUID();
        System.out.println("Your unique UUID: " + userUUID);

        while (true) {
            System.out.println("\nChoose an action:");
            System.out.println("1. Shorten URL");
            System.out.println("2. Access a short URL");
            System.out.println("3. Exit");

            String choice = scanner.nextLine();
            if (choice.equals("1")) {
                // Сокращение URL
                System.out.print("Enter the long URL: ");
                String originalURL = scanner.nextLine();
                System.out.print("Enter click limit: ");
                int clickLimit = Integer.parseInt(scanner.nextLine());

                try {
                    String shortURL = service.shortenURL(originalURL, userUUID, clickLimit);
                    System.out.println("Shortened URL: " + shortURL);
                } catch (IllegalArgumentException e) {
                    System.out.println(e.getMessage());
                }
            } else if (choice.equals("2")) {
                // Переход по короткой ссылке
                System.out.print("Enter the short URL: ");
                String shortURL = scanner.nextLine();
                String result = service.getOriginalURL(shortURL.substring(8), userUUID); // Убираем "clck.ru/"
                System.out.println(result);
            } else if (choice.equals("3")) {
                // Выход из программы
                System.out.println("Goodbye!");
                break;
            } else {
                System.out.println("Invalid option, please try again.");
            }
        }
        scanner.close();
    }
}
