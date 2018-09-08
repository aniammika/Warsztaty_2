import java.sql.SQLException;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        boolean keepWorking = true;
        int id = 0;

        DBConnection conn = new DBConnection(
                "jdbc:mysql://localhost:3306/programming_school?useSSL=false&characterEncoding=utf8", "root", "coderslab");


        while (keepWorking) {

            System.out.println("CHOOSE OPERATION: \n"
                    + "add = add user \n"
                    + "edit = edit user \n"
                    + "remove = remove user \n"
                    + "show user = show user by id \n"
                    + "show all = show all users \n"
                    + "quit = exit program");
            Scanner scanner = new Scanner(System.in);
            String operation = scanner.nextLine();

            if (operation.equals("quit")) {
                try {
                    conn.closeConnection();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                System.out.println("Good bye!");
                keepWorking = false;
            } else {

                // DODAWANIE UŻYTKOWNIKA
                if (operation.equals("add")) {
                    Scanner scanName = new Scanner(System.in);
                    System.out.println("Please enter user name:");
                    String userName = scanName.nextLine();
                    Scanner scanEmail = new Scanner(System.in);
                    System.out.println("Please enter email:");
                    String userEmail = scanEmail.nextLine();
                    Scanner scanPassword = new Scanner(System.in);
                    System.out.println("Please enter password:");
                    String userPassword = scanPassword.nextLine();
                    try {
                        User user = new User(userName, userEmail, userPassword);
                        user.saveToDB(conn.getConnection(), 0);
                    } catch (com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException e) {
                        System.out.println("Email address aleady exists! User not created");
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }

                    // EDYCJA UŻYTKOWNIKA
                } else if (operation.equals("edit")) {
                    boolean correctValue = false;

                    while (!correctValue) {

                        try {
                            Scanner scanId = new Scanner(System.in);
                            System.out.println("Please enter User Id number:");
                            id = scanId.nextInt();
                            correctValue = true;
                        } catch (InputMismatchException e) {
                            System.out.println("Incorrect User Id format!");
                        }
                    }
                    try {
                        User userToEdit = User.loadUserById(conn.getConnection(), id);
                        if (userToEdit == null) {
                        }
                        else {
                            System.out.println(userToEdit);
                            Scanner scanName = new Scanner(System.in);
                            System.out.println("Please enter new User Name:");
                            String userName = scanName.nextLine();
                            Scanner scanEmail = new Scanner(System.in);
                            System.out.println("Please enter new Email Address:");
                            String userEmail = scanEmail.nextLine();
                            Scanner scanPassword = new Scanner(System.in);
                            System.out.println("Please enter new Password:");
                            String userPassword = scanPassword.nextLine();
                            User userToOverwrite = new User(userName, userEmail, userPassword);
                            userToOverwrite.saveToDB(conn.getConnection(), userToEdit.getId());
                        }
                    }
                     catch (com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException e) {
                        System.out.println("Email address aleady exists! User not updated");
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                } else if (operation.equals("remove")) {
                    boolean correctValue = false;

                    while (!correctValue) {

                        try {
                            Scanner scanId = new Scanner(System.in);
                            System.out.println("Please enter User Id to remove:");
                            id = scanId.nextInt();
                            correctValue = true;
                        } catch (InputMismatchException e) {
                            System.out.println("Incorrect User Id format!");
                        }
                    }
                    try {
                        User userToRemove = User.loadUserById(conn.getConnection(), id);
                        if (userToRemove != null) {
                            User.removeUserById(conn.getConnection(), id);
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }


                    //POBIERANIE UŻYTKOWNIKA Z BAZY PO ID
                } else if (operation.equals("show user")) {
                    boolean correctValue = false;

                    while (!correctValue) {

                        try {
                            Scanner scanId = new Scanner(System.in);
                            System.out.println("Please enter User Id to edition:");
                            id = scanId.nextInt();
                            correctValue = true;
                        } catch (InputMismatchException e) {
                            System.out.println("Incorrect User Id format!");
                        }
                    }
                    try {
                        System.out.println(User.loadUserById(conn.getConnection(), id));
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }

                } else if (operation.equals("show all")) {
                    try {
                        List <User> userList = User.loadAllUsers(conn.getConnection());
                        for (User currentUser: userList
                             ) {
                            System.out.println(currentUser);
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}

