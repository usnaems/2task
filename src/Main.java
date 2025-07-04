import java.util.*;
import java.util.stream.Collectors;

enum CarType { SEDAN, SUV, ELECTRIC, TRUCK, HATCHBACK }

class Car implements Comparable<Car> {
    private String vin;
    private String model;
    private String manufacturer;
    private int year;
    private int mileage;
    private double price;
    private CarType type;

    public Car(String vin, String model, String manufacturer, int year, int mileage, double price, CarType type) {
        this.vin = vin;
        this.model = model;
        this.manufacturer = manufacturer;
        this.year = year;
        this.mileage = mileage;
        this.price = price;
        this.type = type;
    }

    public String getVin() { return vin; }
    public String getModel() { return model; }
    public String getManufacturer() { return manufacturer; }
    public int getYear() { return year; }
    public int getMileage() { return mileage; }
    public double getPrice() { return price; }
    public CarType getType() { return type; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Car car)) return false;
        return vin.equals(car.vin);
    }

    @Override
    public int hashCode() {
        return Objects.hash(vin);
    }

    @Override
    public int compareTo(Car other) {
        return Integer.compare(other.year, this.year); // Новее = выше
    }

    @Override
    public String toString() {
        return String.format("Car{VIN='%s', Model='%s', Manufacturer='%s', Year=%d, Mileage=%d, Price=%.2f, Type=%s}",
                vin, model, manufacturer, year, mileage, price, type);
    }
}

class CarDealership {
    private Set<String> vins = new HashSet<>();
    private List<Car> cars = new ArrayList<>();

    public void addCar(Car car) {
        if (vins.add(car.getVin())) {
            cars.add(car);
            System.out.println("Машина добавлена.");
        } else {
            System.out.println("Ошибка: VIN уже существует!");
        }
    }

    public List<Car> findByManufacturer(String manufacturer) {
        return cars.stream()
                .filter(c -> c.getManufacturer().equalsIgnoreCase(manufacturer))
                .toList();
    }

    public double averagePriceByType(CarType type) {
        return cars.stream()
                .filter(c -> c.getType() == type)
                .mapToDouble(Car::getPrice)
                .average()
                .orElse(0);
    }

    public List<Car> sortedByYearDesc() {
        return cars.stream().sorted().toList();
    }

    public Map<CarType, Long> countByType() {
        return cars.stream().collect(Collectors.groupingBy(Car::getType, Collectors.counting()));
    }

    public Optional<Car> getOldestCar() {
        return cars.stream().min(Comparator.comparingInt(Car::getYear));
    }

    public Optional<Car> getNewestCar() {
        return cars.stream().max(Comparator.comparingInt(Car::getYear));
    }
}

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        CarDealership dealership = new CarDealership();

        // 1. Массив с годами выпуска
        Random random = new Random();
        int[] years = new int[50];
        for (int i = 0; i < 50; i++) {
            years[i] = 2000 + random.nextInt(26);
        }

        System.out.println("Машины после 2015:");
        Arrays.stream(years).filter(y -> y > 2015).forEach(System.out::println);

        double avgAge = Arrays.stream(years)
                .map(y -> 2025 - y)
                .average().orElse(0);
        System.out.printf("Средний возраст авто: %.2f лет%n", avgAge);

        // 2. Коллекции с моделями
        List<String> models = List.of("Toyota Camry", "BMW X5", "Audi A4", "BMW X5", "Tesla Model S", "Tesla Model X");
        Set<String> sortedUniqueModels = new TreeSet<>(Comparator.reverseOrder());
        for (String model : models) {
            if (model.contains("Tesla")) {
                model = "ELECTRO_CAR";
            }
            sortedUniqueModels.add(model);
        }
        System.out.println("Уникальные модели (в обратном порядке):");
        sortedUniqueModels.forEach(System.out::println);

        // 3. Проверка equals/hashCode
        Set<Car> carSet = new HashSet<>();
        carSet.add(new Car("VIN001", "Camry", "Toyota", 2019, 40000, 20000, CarType.SEDAN));
        carSet.add(new Car("VIN002", "Model S", "Tesla", 2022, 10000, 50000, CarType.ELECTRIC));
        carSet.add(new Car("VIN001", "Camry", "Toyota", 2019, 40000, 20000, CarType.SEDAN)); // дубликат
        System.out.println("Уникальные машины по VIN:");
        carSet.forEach(System.out::println);

        // 4. Stream API
        List<Car> carList = List.of(
                new Car("V1", "A4", "Audi", 2020, 30000, 25000, CarType.SEDAN),
                new Car("V2", "X5", "BMW", 2019, 70000, 27000, CarType.SUV),
                new Car("V3", "Model 3", "Tesla", 2021, 15000, 35000, CarType.ELECTRIC),
                new Car("V4", "Camry", "Toyota", 2018, 49000, 18000, CarType.SEDAN),
                new Car("V5", "Yaris", "Toyota", 2023, 5000, 16000, CarType.HATCHBACK)
        );

        System.out.println("\nТОП-3 машины с пробегом < 50000, по убыванию цены:");
        carList.stream()
                .filter(c -> c.getMileage() < 50000)
                .sorted(Comparator.comparingDouble(Car::getPrice).reversed())
                .limit(3)
                .forEach(System.out::println);

        double avgMileage = carList.stream().mapToInt(Car::getMileage).average().orElse(0);
        System.out.printf("Средний пробег: %.2f км%n", avgMileage);

        Map<String, List<Car>> byManufacturer = carList.stream()
                .collect(Collectors.groupingBy(Car::getManufacturer));
        System.out.println("Машины по производителям:");
        byManufacturer.forEach((man, list) -> {
            System.out.println(man + ": " + list.size() + " шт.");
        });

        // 5. Меню
        while (true) {
            System.out.println("""
                    \nМеню:
                    1. Добавить машину
                    2. Найти машины по производителю
                    3. Средняя цена по типу
                    4. Список машин по году выпуска
                    5. Статистика: количество по типу, старая/новая машина
                    0. Выход
                    Выберите: """);

            int choice = scanner.nextInt();
            scanner.nextLine(); // consume newline

            switch (choice) {
                case 1 -> {
                    System.out.print("VIN: "); String vin = scanner.nextLine();
                    System.out.print("Модель: "); String model = scanner.nextLine();
                    System.out.print("Производитель: "); String man = scanner.nextLine();
                    System.out.print("Год: "); int year = scanner.nextInt();
                    System.out.print("Пробег: "); int km = scanner.nextInt();
                    System.out.print("Цена: "); double price = scanner.nextDouble();
                    System.out.print("Тип (SEDAN, SUV, ELECTRIC, TRUCK, HATCHBACK): ");
                    CarType type = CarType.valueOf(scanner.next().toUpperCase());
                    dealership.addCar(new Car(vin, model, man, year, km, price, type));
                }
                case 2 -> {
                    System.out.print("Введите производителя: ");
                    String man = scanner.nextLine();
                    dealership.findByManufacturer(man).forEach(System.out::println);
                }
                case 3 -> {
                    System.out.print("Тип (SEDAN, SUV, ELECTRIC, TRUCK, HATCHBACK): ");
                    CarType type = CarType.valueOf(scanner.next().toUpperCase());
                    System.out.printf("Средняя цена: %.2f%n", dealership.averagePriceByType(type));
                }
                case 4 -> dealership.sortedByYearDesc().forEach(System.out::println);
                case 5 -> {
                    dealership.countByType().forEach((type, count) -> System.out.println(type + ": " + count));
                    System.out.println("Самая старая: " + dealership.getOldestCar().orElse(null));
                    System.out.println("Самая новая: " + dealership.getNewestCar().orElse(null));
                }
                case 0 -> {
                    System.out.println("Выход.");
                    return;
                }
                default -> System.out.println("Неверный выбор.");
            }
        }
    }
}
