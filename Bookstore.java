import java.sql.*;
import java.util.*;

public class Bookstore {

    // Database connection parameters
    private static final String DB_URL = "jdbc:postgresql://localhost:5432/Bookstore";
    private static final String USER = "postgres";
    private static final String PASSWORD = "";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        try {
            // Register the PostgreSQL driver
            Class.forName("org.postgresql.Driver");
            try (Connection connection = DriverManager.getConnection(DB_URL, USER, PASSWORD)) {

                // Create a statement
                try (Statement statement = connection.createStatement()) {

                    // Create a table (if not exists)
                    String createTableQuery = "CREATE TABLE IF NOT EXISTS Book_info (BookID serial PRIMARY KEY, Title VARCHAR(255), "
                            + "Available_Books INT, Price float, YearInfo INT, AuthorID INT)";
                    statement.executeUpdate(createTableQuery);

                    // Menu for CRUD operations
                    while (true) {
                        System.out.println("\nWelcome to Josh's Book Store");
                        System.out.println("\nMenu\n");
                        System.out.println("1. Add A Book");
                        System.out.println("2. View The Book(s)");
                        System.out.println("3. Update Book Information");
                        System.out.println("4. Delete a Book");
                        System.out.println("5. Order A Book");
                        System.out.println("6. Display Available Tables");
                        System.out.println("7. Display show Column Detail");
                        System.out.println("8. Display Primary Keys");
                        System.out.println("9. Display Foreign Keys");
                        System.out.println("10. Exit");

                        System.out.println("\nEnter an option: ");

                        int choice = scanner.nextInt();
                        switch (choice) {
                            case 1:
                                addBook(statement, scanner);
                                break;
                            case 2:
                                readRecord(statement);
                                break;
                            case 3:
                                updateBookInfo(statement, scanner);
                                break;
                            case 4:
                                deleteBook(connection, scanner);
                                break;
                            case 5:
                                insertOrder(connection, scanner);
                                break;
                            case 6:
                                displayTableNames(connection);
                                break;
                            case 7:
                                displayColumnDetails(connection, "Book_info");
                                break;
                            case 8:
                                displayPrimaryKeys(connection);
                                break;
                            case 9:
                                displayForeignKeys(connection);
                                break;
                            case 10:
                                System.out.println("Goodbye!");
                                System.exit(0);
                                break;
                            default:
                                System.out.println("Invalid choice!");
                        }
                    }
                }
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        } finally {
            scanner.close();
        }
    }

    // Method to add a new book to the database
    private static void addBook(Statement statement, Scanner scanner) throws SQLException {
        // Get user input for book details
        System.out.print("Enter BookID: ");
        int BookID = scanner.nextInt();
        scanner.nextLine();

        System.out.print("Title: ");
        String title = scanner.nextLine();

        System.out.print("Number of copies: ");
        int availableBooks = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        System.out.print("Price: ");
        float price = scanner.nextFloat();
        scanner.nextLine(); // Consume newline

        System.out.print("Year of Publication: ");
        int yearInfo = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        System.out.print("Author ID: ");
        int authorID = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        // Execute SQL INSERT statement
        String insertQuery = String.format("INSERT INTO Book_info (Title, Available_Books, Price, YearInfo, AuthorID) VALUES " +
                "('%s', %d, %f, %d, %d)", title, availableBooks, price, yearInfo, authorID);
        int rowsAffected = statement.executeUpdate(insertQuery);

        // Print success/failure message
        if (rowsAffected > 0) {
            System.out.println("Book added successfully.");
        } else {
            System.out.println("Failed! Please try again.");
        }
    }

    // Method to read and display book records from the database
    private static void readRecord(Statement statement) throws SQLException {
        // Execute SQL SELECT statement
        String selectQuery = "SELECT * FROM Book_info";
        try (ResultSet resultSet = statement.executeQuery(selectQuery)) {

            // Print the result set
            if (!resultSet.isBeforeFirst()) {
                System.out.println("Error!.");
                System.out.println("There is no book recorded!.");
            } else {
                System.out.println("Book(s) information:");
                while (resultSet.next()) {
                    System.out.printf("BookID: %d, Title: %s, Available_Books: %d, Price: %d, YearInfo: %d, AuthorID: %d%n",
                            resultSet.getInt("BookID"),
                            resultSet.getString("Title"),
                            resultSet.getInt("Available_Books"),
                            resultSet.getInt("Price"),
                            resultSet.getInt("YearInfo"),
                            resultSet.getInt("AuthorID"));
                }
            }
        }
    }

    // Method to update book information in the database
    private static void updateBookInfo(Statement statement, Scanner scanner) throws SQLException {
        System.out.println("\nUpdating book information based on BookID:");

        // Get user input for BookID
        System.out.print("Enter BookID: ");
        int bookID = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        // Check if the book exists
        if (!bookExists(statement, bookID)) {
            System.out.println("Book not found.");
            return;
        }

        // Get user input for updated book information
        System.out.print("Title: ");
        String newTitle = scanner.nextLine();

        System.out.print("Price: ");
        float newPrice = scanner.nextFloat();
        scanner.nextLine(); // Consume newline

        System.out.print("Quantity: ");
        int newQuantity = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        System.out.print("Enter Year: ");
        int newYearInfo = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        // Execute SQL UPDATE statement to update book_info
        String updateBookInfoQuery = String.format("UPDATE book_info SET Title = '%s', Price = %f, Available_Books = %d, YearInfo = %d WHERE BookID = %d",
                newTitle, newPrice, newQuantity, newYearInfo, bookID);
        int rowsAffected = statement.executeUpdate(updateBookInfoQuery);

        // Print success/failure message
        if (rowsAffected > 0) {
            System.out.printf("BookID %d Updated.%n", bookID);
        } else {
            System.out.printf("Failed to update BookID %d. Please check the data.%n", bookID);
        }
    }

    // Method to check if a book exists in the database
    private static boolean bookExists(Statement statement, int bookID) throws SQLException {
        String checkBookQuery = String.format("SELECT * FROM book_info WHERE BookID = %d", bookID);
        try (ResultSet resultSet = statement.executeQuery(checkBookQuery)) {
            return resultSet.isBeforeFirst();
        }
    }

    // Method to delete a book from the database
    private static void deleteBook(Connection connection, Scanner scanner) throws SQLException {
        System.out.println("\nDeleting a book:");

        // Get user input for book ID to delete
        System.out.print("Enter BookID: ");
        int bookIDToDelete = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        try {
            // Start a transaction
            connection.setAutoCommit(false);

            // Delete corresponding records from the "orderitem" table
            String deleteOrderItemsQuery = String.format("DELETE FROM OrderItem WHERE BookID = %d", bookIDToDelete);
            try (Statement deleteOrderItemsStatement = connection.createStatement()) {
                int orderItemsRowsAffected = deleteOrderItemsStatement.executeUpdate(deleteOrderItemsQuery);

                // Proceed with deleting the book if order items deletion is successful
                if (orderItemsRowsAffected >= 0) {
                    // Delete the book from the "Book_info" table
                    String deleteBookQuery = String.format("DELETE FROM Book_info WHERE BookID = %d", bookIDToDelete);
                    try (Statement deleteBookStatement = connection.createStatement()) {
                        int bookRowsAffected = deleteBookStatement.executeUpdate(deleteBookQuery);

                        // Print success/failure message
                        if (bookRowsAffected > 0) {
                            System.out.println("Book and associated order items deleted successfully.");
                            // Commit the transaction if successful
                            connection.commit();
                        } else {
                            System.out.println("Failed to delete the book. Please check the data.");
                        }
                    }
                } else {
                    System.out.println("Failed to delete associated order items. Please check the data.");
                }
            }
        } catch (SQLException e) {
            // Rollback the transaction in case of an exception
            connection.rollback();
            e.printStackTrace();
        } finally {
            // Restore auto-commit mode
            connection.setAutoCommit(true);
        }
    }

    // Method to insert a new order into the database
    private static void insertOrder(Connection connection, Scanner scanner) throws SQLException {
        System.out.println("\nInserting a new order:");

        // Get user input for CustomerID, BookID, and Quantity
        System.out.print("Enter CustomerID: ");
        int customerID = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        System.out.print("Enter BookID: ");
        int bookID = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        System.out.print("Enter Quantity: ");
        int quantity = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        try {
            // Insert order item
            String insertOrderItemQuery = "INSERT INTO OrderItem (CustomerID, BookID, Quantity) VALUES (?, ?, ?)";
            try (PreparedStatement insertOrderItemStatement = connection.prepareStatement(insertOrderItemQuery, PreparedStatement.RETURN_GENERATED_KEYS)) {
                insertOrderItemStatement.setInt(1, customerID);
                insertOrderItemStatement.setInt(2, bookID);
                insertOrderItemStatement.setInt(3, quantity);

                int rowsAffected = insertOrderItemStatement.executeUpdate();

                if (rowsAffected > 0) {
                    System.out.println("Order Placed!.");

                    // Update book_info (no need to retrieve OrderID in this example)
                    String updateBookInfoQuery = "UPDATE book_info SET Available_Books = Available_Books - ? WHERE BookID = ?";
                    try (PreparedStatement updateBookInfoStatement = connection.prepareStatement(updateBookInfoQuery)) {
                        updateBookInfoStatement.setInt(1, quantity);
                        updateBookInfoStatement.setInt(2, bookID);

                        int updateRows = updateBookInfoStatement.executeUpdate();

                        if (updateRows > 0) {
                            //System.out.println("book_info updated successfully.");
                        } else {
                            System.out.println("Failed! Try Again!.");
                        }
                    }
                } else {
                    System.out.println("Failed! Please try again!");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Method to display the names of existing tables in the database
    private static void displayTableNames(Connection connection) throws SQLException {
        DatabaseMetaData metaData = connection.getMetaData();
        ResultSet tables = metaData.getTables(null, null, "%", new String[]{"TABLE"});

        System.out.println("\nCurrent Tables:");
        while (tables.next()) {
            String tableName = tables.getString("TABLE_NAME");
            System.out.println(tableName);
        }
    }

    // Method to display details of columns in a specified table
    private static void displayColumnDetails(Connection connection, String tableName) throws SQLException {
        DatabaseMetaData metaData = connection.getMetaData();
        ResultSet columns = metaData.getColumns(null, null, tableName, "%");

        System.out.println("\nColumn Details: " + tableName);
        while (columns.next()) {
            String columnName = columns.getString("COLUMN_NAME");
            String dataType = columns.getString("TYPE_NAME");
            int columnSize = columns.getInt("COLUMN_SIZE");

            System.out.println("Column: " + columnName + ", Type: " + dataType + ", Size: " + columnSize);
        }
    }

    // Method to display primary keys in the database
    private static void displayPrimaryKeys(Connection connection) throws SQLException {
        DatabaseMetaData metaData = connection.getMetaData();
        ResultSet primaryKeys = metaData.getPrimaryKeys(null, null, "%");

        System.out.println("\nPrimary Keys:");
        while (primaryKeys.next()) {
            String tableName = primaryKeys.getString("TABLE_NAME");
            String columnName = primaryKeys.getString("COLUMN_NAME");

            System.out.println("Table: " + tableName + ", Primary Key: " + columnName);
        }
    }

    // Method to display foreign keys in the database
    private static void displayForeignKeys(Connection connection) throws SQLException {
        DatabaseMetaData metaData = connection.getMetaData();
        ResultSet foreignKeys = metaData.getImportedKeys(null, null, "%");

        System.out.println("\nForeign Keys:");
        while (foreignKeys.next()) {
            String tableName = foreignKeys.getString("FKTABLE_NAME");
            String columnName = foreignKeys.getString("FKCOLUMN_NAME");
            String referencedTable = foreignKeys.getString("PKTABLE_NAME");
            String referencedColumnName = foreignKeys.getString("PKCOLUMN_NAME");

            System.out.println("Table: " + tableName + ", Foreign Key: " + columnName +
                    ", Referenced Table: " + referencedTable + ", Referenced Column: " + referencedColumnName);
        }
    }
}
