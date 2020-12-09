import com.mysql.cj.x.protobuf.MysqlxPrepare;

import java.sql.*;
import java.util.*;

public class Main {

    public static boolean hasInit = false;

    public static Connection conn;

    //======= Define our Table Names =======
    public static final List<String> TABLE_NAMES = Arrays.asList("customers", "item_sale", "inventory");

    //======= Define the Columns in the Table =======
    public static final List<String> CUSTOMER_COLUMNS = Arrays.asList("id", "first", "last");
    public static final List<String> ITEM_SALE_COLUMNS = Arrays.asList("id", "customer_id", "item_id", "quantity");
    public static final List<String> INVENTORY_COLUMNS = Arrays.asList("id", "item_name", "price", "stock");

    public static void init(Connection connection) throws Exception {
        //Specify the table data for each table
        Hashtable<String, String> tableDict = new Hashtable<>();
        tableDict.put("customers", "id int NOT NULL AUTO_INCREMENT, first varchar(255), last varchar(255), PRIMARY KEY(id)");
        tableDict.put("item_sale", "id int NOT NULL AUTO_INCREMENT, customer_id int NOT NULL, item_id int NOT NULL, quantity int NOT NULL, PRIMARY KEY(id)");
        tableDict.put("inventory", "id int NOT NULL AUTO_INCREMENT, item_name varchar(255) NOT NULL, price int NOT NULL, stock int NOT NULL, PRIMARY KEY(id)");

        //For each table, attempt to create the table if it DOES NOT exist
        TABLE_NAMES.forEach(table -> {
            try {
                //Create the table if it doesn't exist
                createTable(connection,
                            table,
                            tableDict.get(table)
                );
            } catch (Exception e) { e.printStackTrace(); }
        });
        //Check if the table is empty, if NOT then insert test data
        List<String> currentCustomerData = selectData(connection, "customers");
        if(currentCustomerData.size() == 0) {
            List<String> customerColumns = Arrays.asList("first", "last");
            List<List<String>> customerData = Arrays.asList(
                    Arrays.asList("John", "Doe"),
                    Arrays.asList("Jane", "Doe"),
                    Arrays.asList("Bob", "Builder")
            );
            insertRow(connection, "customers", customerColumns, customerData);
        }
        List<String> currentItemSaleData = selectData(connection, "item_sale");
        if(currentItemSaleData.size() == 0) {
            List<String> itemSaleColumns = Arrays.asList("customer_id", "item_id", "quantity");
            List<List<String>> itemSaleData = Arrays.asList(
                    Arrays.asList("0", "1", "2"),
                    Arrays.asList("0", "2", "1"),
                    Arrays.asList("0", "1", "4"),
                    Arrays.asList("0", "3", "1"),
                    Arrays.asList("1", "2", "4"),
                    Arrays.asList("2", "1", "7")
            );
            insertRow(connection, "item_sale", itemSaleColumns, itemSaleData);
        }
        List<String> currentInventoryData = selectData(connection, "inventory");
        if(currentInventoryData.size() == 0) {
            List<String> inventoryColumns = Arrays.asList("item_name", "price", "stock");
            List<List<String>> inventoryData = Arrays.asList(
                    Arrays.asList("Item 1", "10", "46"),
                    Arrays.asList("Item 2", "15", "24"),
                    Arrays.asList("Item 3", "5", "31"),
                    Arrays.asList("Item 4", "20", "23")
            );
            insertRow(connection, "inventory", inventoryColumns, inventoryData);
        }
    }

    public static void main(String[] args) throws Exception {

        if(!hasInit) {
            //======= Get the Connection =======
            System.out.println("Attempting to grab database connection");
            conn = getConnection();
            System.out.println("Connection successful");
            //======= Initialize the Tables =======
            System.out.println("Attempting to initialize database");
            init(conn);
            System.out.println("Initialization successful");
            hasInit = true;
        }

        //Print syntax
        System.out.println("\n================ Menu Options ===============\n");
        System.out.println("main_menu: view, insert, remove, quit\n" +
                            "view_menu: all, row, quit\n" +
                            "insert_menu: row, quit\n" +
                            "remove_menu: all, row, quit\n");
        //Prompt for the main menu
        String response = Menu.getLimitedResponse(Menu.MENU_OPTIONS.MAIN_MENU);
        switch(response) {
            case "quit":
                System.exit(0);
                break;
            case "view":
                viewMenu();
                break;
            case "insert":
                insertMenu();
                break;
            case "remove":
                removeMenu();
                break;
        }
        //Loop back into main menu (except in case of quit
        main(null);

        //FOR TESTING PURPOSES
//        List<String> results = selectData(conn, "customers");
//        results.forEach(System.out::println);
    }

    public static void viewMenu() throws Exception {
        Scanner userInput = new Scanner(System.in);
        String response = Menu.getLimitedResponse(Menu.MENU_OPTIONS.VIEW_MENU);
        switch(response) {
            case "row":
                System.out.print("ID: ");
                String id = userInput.nextLine();
                System.out.print("Table Name: ");
                String tableNameId = userInput.nextLine();
                selectData(conn, tableNameId, id).forEach(System.out::println);
                break;
            case "all":
                System.out.print("Table Name: ");
                String tableName = userInput.nextLine();
                selectData(conn, tableName).forEach(System.out::println);
                break;
        }
        main(null);
    }

    public static void insertMenu() throws Exception {

        main(null);
    }

    public static void removeMenu() throws Exception {

        main(null);
    }

    public static List<String> selectData(Connection conn, String tableName, String id) throws Exception{
        List<String> results = new ArrayList<>();

        try {
            //Get the row where the id matches
            PreparedStatement statement = conn.prepareStatement(
                    String.format("SELECT * FROM %s WHERE id=%s", tableName, id)
            );
            //Get the ResultSet and add it to our results List
            ResultSet result = statement.executeQuery();
            while( result.next() ) {
                switch (tableName) {
                    case "customers":
                        results.add(String.format("ID: %s%nFirst Name: %s%nLast Name: %s",
                                result.getString(1), result.getString(2), result.getString(3)));
                        break;
                    case "item_sale":
                        results.add(String.format("ID: %s%nCustomer ID: %s%nItem ID: %s%nQuantity: %s",
                                result.getString(1), result.getString(2),
                                result.getString(3), result.getString(4)));
                        break;
                    case "inventory":
                        results.add(String.format("ID: %s%nItem Name: %s%nPrice: %s%nStock: %s",
                                result.getString(1), result.getString(2),
                                result.getString(3), result.getString(4)));
                        break;
                }
            }
        } catch (Exception e) { e.printStackTrace(); }

        return results;
    }

    public static List<String> selectData(Connection conn, String tableName) throws Exception{
        List<String> results = new ArrayList<>();

        try {
            //Get the row where the id matches
            PreparedStatement statement = conn.prepareStatement(
                    String.format("SELECT * FROM %s", tableName)
            );
            //Get the ResultSet and add it to our results List
            ResultSet result = statement.executeQuery();
            while( result.next() ) {
                switch (tableName) {
                    case "customers":
                        results.add(String.format("ID: %s%nFirst Name: %s%nLast Name: %s",
                                result.getString(1), result.getString(2), result.getString(3)));
                        break;
                    case "item_sale":
                        results.add(String.format("ID: %s%nCustomer ID: %s%nItem ID: %s%nQuantity: %s",
                                result.getString(1), result.getString(2),
                                result.getString(3), result.getString(4)));
                        break;
                    case "inventory":
                        results.add(String.format("ID: %s%nItem Name: %s%nPrice: %s%nStock: %s",
                                result.getString(1), result.getString(2),
                                result.getString(3), result.getString(4)));
                        break;
                }
            }
        } catch (Exception e) { e.printStackTrace(); }

        return results;
    }

    public static void insertRow(Connection conn, String tableName, List<String> columns, List<List<String>> values) {

        try {
            //Go through each data row and create an input query, then execute
            for (List<String> dataRow : values) {
                //Create the "base query" for the insert statement
                StringBuilder baseQuery = new StringBuilder(String.format("INSERT INTO %s (", tableName));
                for (int i = 0; i < columns.size(); i++) {
                    //If we're still mid list, add the next column, otherwise, add the ")" for the end
                    if (i + 1 == columns.size()) baseQuery.append(columns.get(i)).append(")");
                    else baseQuery.append(columns.get(i)).append(",");
                }

                baseQuery.append(" VALUES (");

                for (int i = 0; i < dataRow.size(); i++) {
                    //If we're still mid list, add the next value, otherwise add the ")" for the end
                    if (i + 1 == dataRow.size()) baseQuery.append('"').append(dataRow.get(i)).append('"').append(")");
                    else baseQuery.append('"').append(dataRow.get(i)).append('"').append(",");
                }

                //Create a PreparedStatement then execute
                PreparedStatement insertStatement = conn.prepareStatement(baseQuery.toString());
                insertStatement.executeUpdate();
                System.out.println(baseQuery.toString());
            }
        } catch (Exception e) { e.printStackTrace(); }

    }

    public static void createTable(Connection conn, String tableName, String tableData) throws Exception {
        try {

            //======= Create a Prepared Statement =======
            PreparedStatement createTable = conn.prepareStatement(
                    "CREATE TABLE IF NOT EXISTS "
                            + tableName + "("
                            + tableData + ")"
            );

            //======= Execute the Statement =======
            createTable.executeUpdate();
            System.out.printf("Created table [%s]%n", tableName);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Connection getConnection() throws Exception {

        try {

            //======= Setup Driver and Connection Details =======
            String driver = "com.mysql.jdbc.Driver";
            String url = "jdbc:mysql://localhost:3306/projectdb";
            String username = "root";
            String password = "1234";
            Class.forName(driver);

            //======= Try and setup the connection =======
            Connection conn = DriverManager.getConnection(url, username, password);
            System.out.println("Connected to [" + url + "] successfully");
            //======= Return the Connection =======
            return conn;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;

    }

}
