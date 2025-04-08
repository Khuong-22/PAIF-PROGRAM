//package test;
//
//import java.util.Properties;
//import java.util.Scanner;
//import java.util.Vector;
//
//public class LibraryTest {
//    private static Scanner scanner = new Scanner(System.in);
//
//    public static void main(String[] args) {
//        while (true) {
//            printMenu();
//            int choice = getIntInput("Enter your choice (1-7): ");
//
//            try {
//                switch (choice) {
//                    case 1:
//                        insertNewBook();
//                        break;
//                    case 2:
//                        insertNewPatron();
//                        break;
//                    case 3:
//                        searchBooksByTitle();
//                        break;
//                    case 4:
//                        searchBooksByYear();
//                        break;
//                    case 5:
//                        searchPatronsByAge();
//                        break;
//                    case 6:
//                        searchPatronsByZip();
//                        break;
//                    case 7:
//                        System.out.println("Exiting...");
//                        return;
//                    default:
//                        System.out.println("Invalid choice. Please try again.");
//                }
//            } catch (Exception e) {
//                System.out.println("Error: " + e.getMessage());
//            }
//        }
//    }
//
//    private static void printMenu() {
//        System.out.println("\nLibrary System Test Menu");
//        System.out.println("1. Insert new book");
//        System.out.println("2. Insert new patron");
//        System.out.println("3. Search books by title");
//        System.out.println("4. Search books before year");
//        System.out.println("5. Search patrons younger than date");
//        System.out.println("6. Search patrons by zip code");
//        System.out.println("7. Exit");
//    }
//
//    private static void insertNewBook() {
//        Properties props = new Properties();
//
//        // Get book details from user
//        props.setProperty("bookTitle", getStringInput("Enter book title: "));
//        props.setProperty("author", getStringInput("Enter author: "));
//        props.setProperty("pubYear", getStringInput("Enter publication year (YYYY): "));
//        props.setProperty("status", "Active");  // Default status for new books
//
//        // Create and save the book
//        Book newBook = new Book(props);
//        newBook.update();
//        System.out.println("Book added successfully: " + newBook.getState("UpdateStatusMessage"));
//    }
//
//    private static void insertNewPatron() {
//        Properties props = new Properties();
//
//        // Get patron details from user
//        props.setProperty("name", getStringInput("Enter patron name: "));
//        props.setProperty("address", getStringInput("Enter address: "));
//        props.setProperty("city", getStringInput("Enter city: "));
//        props.setProperty("stateCode", getStringInput("Enter state code (2 letters): "));
//        props.setProperty("zip", getStringInput("Enter zip code: "));
//        props.setProperty("email", getStringInput("Enter email: "));
//        props.setProperty("dateOfBirth", getStringInput("Enter date of birth (YYYY-MM-DD): "));
//        props.setProperty("status", "Active");  // Default status for new patrons
//
//        // Create and save the patron
//        Patron newPatron = new Patron(props);
//        newPatron.update();
//        System.out.println("Patron added successfully: " + newPatron.getState("UpdateStatusMessage"));
//    }
//
//    private static void searchBooksByTitle() {
//        String titlePart = getStringInput("Enter part of book title to search: ");
//
//        BookCollection books = new BookCollection();
//        books.findBooksWithTitleLike(titlePart);
//
//        displayBooks(books.getBooks());
//    }
//
//    private static void searchBooksByYear() {
//        String year = getStringInput("Enter year (YYYY): ");
//
//        BookCollection books = new BookCollection();
//        books.findBooksOlderThanDate(year);
//
//        displayBooks(books.getBooks());
//    }
//
//    private static void searchPatronsByAge() {
//        String date = getStringInput("Enter date (YYYY-MM-DD): ");
//
//        PatronCollection patrons = new PatronCollection();
//        patrons.findPatronsYoungerThan(date);
//
//        displayPatrons(patrons.getPatrons());
//    }
//
//    private static void searchPatronsByZip() {
//        String zip = getStringInput("Enter zip code: ");
//
//        PatronCollection patrons = new PatronCollection();
//        patrons.findPatronsAtZipCode(zip);
//
//        displayPatrons(patrons.getPatrons());
//    }
//
//    private static void displayBooks(Vector<Book> books) {
//        if (books.isEmpty()) {
//            System.out.println("No books found.");
//            return;
//        }
//
//        System.out.println("\nFound Books:");
//        System.out.println("--------------------");
//        for (Book book : books) {
//            System.out.println("ID: " + book.getState("bookId"));
//            System.out.println("Title: " + book.getState("bookTitle"));
//            System.out.println("Author: " + book.getState("author"));
//            System.out.println("Year: " + book.getState("pubYear"));
//            System.out.println("Status: " + book.getState("status"));
//            System.out.println("--------------------");
//        }
//    }
//
//    private static void displayPatrons(Vector<Patron> patrons) {
//        if (patrons.isEmpty()) {
//            System.out.println("No patrons found.");
//            return;
//        }
//
//        System.out.println("\nFound Patrons:");
//        System.out.println("--------------------");
//        for (Patron patron : patrons) {
//            System.out.println("ID: " + patron.getState("patronId"));
//            System.out.println("Name: " + patron.getState("name"));
//            System.out.println("Address: " + patron.getState("address"));
//            System.out.println("City: " + patron.getState("city"));
//            System.out.println("State: " + patron.getState("stateCode"));
//            System.out.println("Zip: " + patron.getState("zip"));
//            System.out.println("Email: " + patron.getState("email"));
//            System.out.println("DOB: " + patron.getState("dateOfBirth"));
//            System.out.println("Status: " + patron.getState("status"));
//            System.out.println("--------------------");
//        }
//    }
//
//    private static String getStringInput(String prompt) {
//        System.out.print(prompt);
//        return scanner.nextLine().trim();
//    }
//
//    private static int getIntInput(String prompt) {
//        while (true) {
//            try {
//                System.out.print(prompt);
//                return Integer.parseInt(scanner.nextLine().trim());
//            } catch (NumberFormatException e) {
//                System.out.println("Please enter a valid number.");
//            }
//        }
//    }
//}
