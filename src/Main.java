import com.mysql.cj.x.protobuf.MysqlxPrepare;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;

public class Main {

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
            //Init List
        }
        List<String> currentInventoryData = selectData(connection, "inventory");
        if(currentInventoryData.size() == 0) {
            //Init List
        }
    }

    public static void main(String[] args) throws Exception {

        //======= Get the Connection =======
        Connection conn = getConnection();
        //======= Initialize the Tables =======
        init(conn);
        List<String> results = selectData(conn, "customers");
        results.forEach(System.out::println);


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
