package client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;

public class Client {
    private static HttpClient httpClient = HttpClient.newHttpClient();
    private static final ObjectMapper objectMapper = new ObjectMapper();
    static Scanner scanner = new Scanner(System.in);

    public Client() {
    }

    // For testing
    public static void setHttpClient(HttpClient httpClient) {
        Client.httpClient = httpClient;
    }

    public static void setScanner(Scanner scanner) {
        Client.scanner = scanner;
    }

    public static void main(String[] args) {
        boolean exit = false;

        while (!exit) {
            displayMenu();
            System.out.print("Enter your choice: ");

            int choice;
            if (scanner.hasNextInt()) {
                choice = scanner.nextInt();
                scanner.nextLine();
            } else {
                System.out.println("Invalid input. Please enter a number.");
                scanner.nextLine(); // Consume invalid input
                continue;
            }

            switch (choice) {
                case 1:
                    fetchAirportsByCityId();
                    break;
                case 2:
                    System.out.println("Fetching all aircraft passengers have travelled on...");
                    fetchAircraftByPassengerId();
                    break;
                case 3:
                    System.out.println("Fetching airports where aircraft can take off and land...");
                    fetchAirportsWhereAircraftCanLandAndTakeOff();
                    break;
                case 4:
                    System.out.println("Fetching airports passengers have used...");
                    fetchAirportsByPassengerId();
                    break;
                case 5:
                    performCustomQuery();
                    break;
                case 6:
                    System.out.println("Exiting...");
                    exit = true;
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
                    break;
            }

            if (!exit) {
                System.out.println("Press Enter to continue...");
                scanner.nextLine();
            }
        }

        scanner.close();
    }

    static void displayMenu() {
        System.out.println("=== Client Application Menu ===");
        System.out.println("1. What airports are in what cities?");
        System.out.println("2. List all aircraft passengers have travelled on?");
        System.out.println("3. Which airports can aircraft take off from and land at?");
        System.out.println("4. What airports have passengers used?");
        System.out.println("5. Perform custom query");
        System.out.println("6. Exit");
    }

    static void fetchAirportsByCityId() {
        System.out.print("Enter City ID: ");
        int cityId = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/cities/" + cityId + "/airports"))
                .GET()
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                prettyPrintAirports(response.body());
            } else {
                System.out.println("Error: " + response.statusCode() + " - " + response.body());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static void fetchAircraftByPassengerId() {
        System.out.print("Enter Passenger ID: ");
        int passengerId = scanner.nextInt();
        scanner.nextLine();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/passengers/" + passengerId + "/aircrafts"))
                .GET()
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                prettyPrintAircraft(response.body());
            } else {
                System.out.println("Error: " + response.statusCode() + " - " + response.body());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static void fetchAirportsWhereAircraftCanLandAndTakeOff() {
        System.out.print("Enter Aircraft ID: ");
        int aircraftId = scanner.nextInt();
        scanner.nextLine();  // Consume newline after the number input

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/aircrafts/" + aircraftId + "/airports"))
                .GET()
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                prettyPrintAirports(response.body());
            } else {
                System.out.println("Error: " + response.statusCode() + " - " + response.body());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static void fetchAirportsByPassengerId() {
        System.out.print("Enter Passenger ID: ");
        int passengerId = scanner.nextInt();
        scanner.nextLine();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/passengers/" + passengerId + "/airports"))
                .GET()
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                prettyPrintAirports(response.body());
            } else {
                System.out.println("Error: " + response.statusCode() + " - " + response.body());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static void performCustomQuery() {
        System.out.println("=== Custom Query Options ===");
        System.out.println("1. Fetch Cities");
        System.out.println("2. Fetch Passengers");
        System.out.println("3. Fetch Airports");
        System.out.println("4. Fetch Aircraft");
        System.out.println("Enter your choice: ");
        int choice = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        String endpoint = "";
        switch (choice) {
            case 1:
                endpoint = "http://localhost:8080/cities";
                break;
            case 2:
                endpoint = "http://localhost:8080/passengers";
                break;
            case 3:
                endpoint = "http://localhost:8080/airports";
                break;
            case 4:
                endpoint = "http://localhost:8080/aircraft";
                break;
            default:
                System.out.println("Invalid choice. Returning to main menu.");
                return;
        }

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(endpoint))
                .GET()
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                prettyPrintResponse(response.body(), choice);
            } else {
                System.out.println("Error: " + response.statusCode() + " - " + response.body());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void prettyPrintResponse(String responseBody, int choice) {
        try {
            JsonNode rootNode = objectMapper.readTree(responseBody);
            if (rootNode.isArray()) {
                for (JsonNode node : rootNode) {
                    switch (choice) {
                        case 1:
                            if (node.has("name") && node.has("state")) {
                                System.out.println("City: " + node.get("name").asText());
                                System.out.println("State: " + node.get("state").asText());
                            } else {
                                System.out.println("Missing city information.");
                            }
                            break;
                        case 2:
                            if (node.has("firstName") && node.has("lastName")) {
                                System.out.println("Passenger: " + node.get("firstName").asText() + " " + node.get("lastName").asText());
                            } else {
                                System.out.println("Missing passenger information.");
                            }
                            break;
                        case 3:
                            if (node.has("name") && node.has("code")) {
                                System.out.println("Airport: " + node.get("name").asText() + ", Code: " + node.get("code").asText());
                            } else {
                                System.out.println("Missing airport information.");
                            }
                            break;
                        case 4:
                            if (node.has("type") && node.has("airlineName")) {
                                System.out.println("Aircraft: " + node.get("type").asText() + ", Airline: " + node.get("airlineName").asText());
                            } else {
                                System.out.println("Missing aircraft information.");
                            }
                            break;
                    }
                    if (node.has("airports") && node.get("airports").isArray()) {
                        System.out.println("Airports:");
                        for (JsonNode airportNode : node.get("airports")) {
                            if (airportNode.has("name")) {
                                System.out.println(" - " + airportNode.get("name").asText());
                            } else {
                                System.out.println(" - Missing airport name.");
                            }
                        }
                    }
                    System.out.println();
                }
            } else {
                prettyPrintNode(rootNode);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void prettyPrintNode(JsonNode node) {
        Iterator<Map.Entry<String, JsonNode>> fields = node.fields();
        while (fields.hasNext()) {
            Map.Entry<String, JsonNode> field = fields.next();
            System.out.println(field.getKey() + ": " + field.getValue().asText());
        }
    }

    private static void prettyPrintAirports(String responseBody) {
        try {
            JsonNode airportsNode = objectMapper.readTree(responseBody);
            if (airportsNode.isArray() && !airportsNode.isEmpty()) {
                System.out.println("Airports where the aircraft can take off and land:");
                for (JsonNode airportNode : airportsNode) {
                    String name = airportNode.has("name") ? airportNode.get("name").asText() : "N/A";
                    String code = airportNode.has("code") ? airportNode.get("code").asText() : "N/A";
                    System.out.println("Airport Name: " + name + ", Code: " + code);
                }
            } else {
                System.out.println("No airports found");
            }
        } catch (Exception e) {
            System.out.println("Error processing the response: " + e.getMessage());
        }
    }

    private static void prettyPrintAircraft(String responseBody) {
        try {
            JsonNode aircraftNode = objectMapper.readTree(responseBody);
            if (aircraftNode.isArray()) {
                for (JsonNode aircraft : aircraftNode) {
                    String aircraftType = aircraft.get("type").asText();
                    String airlineName = aircraft.get("airlineName").asText();
                    System.out.println("Aircraft Type: " + aircraftType + ", Airline: " + airlineName);
                }
            } else {
                System.out.println("No aircraft found for the specified passenger.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}