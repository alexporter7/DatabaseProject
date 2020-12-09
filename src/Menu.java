import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Menu {

    public static final List<String> MAIN_MENU_OPTIONS =
            Arrays.asList("quit", "view", "insert", "remove");
    public static final List<String> VIEW_MENU_OPTIONS =
            Arrays.asList("row", "all", "quit");
    public static final List<String> INSERT_MENU_OPTIONS =
            Arrays.asList("row", "quit");
    public static final List<String> REMOVE_MENU_OPTIONS =
            Arrays.asList("row", "quit");

    public static String getLimitedResponse(MENU_OPTIONS OPTIONS) {
        Scanner userInput = new Scanner(System.in);
        while(true) {
            System.out.printf("%s: ", OPTIONS.getMode());
            String response = userInput.nextLine();
            List<String> options = getArrayFromEnum(OPTIONS);
            assert options != null;
            if(options.contains(response)) {
                return response;
            }
            System.out.printf("[%s] is not a valid option.%n", response);
        }
    }

    public static List<String> getArrayFromEnum(MENU_OPTIONS OPTION) {
        switch(OPTION) {
            case MAIN_MENU:
                return MAIN_MENU_OPTIONS;
            case VIEW_MENU:
                return VIEW_MENU_OPTIONS;
            case INSERT_MENU:
                return INSERT_MENU_OPTIONS;
            case REMOVE_MENU:
                return REMOVE_MENU_OPTIONS;
        }
        return null;
    }

    public enum MENU_OPTIONS {
        MAIN_MENU("main_menu"),
        VIEW_MENU("view_menu"),
        INSERT_MENU("insert_menu"),
        REMOVE_MENU("remove_menu");

        private final String mode;
        MENU_OPTIONS(String mode) {
            this.mode = mode;
        }

        public String getMode() {
            return this.mode;
        }
    }

}
