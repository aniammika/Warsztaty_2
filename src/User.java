import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class User {

    private int id;
    private String username;
    private String email;
    private String password;


    public User(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.setPassword(password);
    }

    public User() {
        this.id = 0;
        this.username = null;
        this.password = null;
        this.email = null;
    }

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = BCrypt.hashpw(password, BCrypt.gensalt());
    }

    public void saveToDB(Connection conn, int id) throws SQLException {
        if (id == 0) {
            String sql = "INSERT INTO users(username, email, password) VALUES (?, ?, ?)";
            String generatedColumns[] = {"ID"};
            PreparedStatement preparedStatement;
            preparedStatement = conn.prepareStatement(sql, generatedColumns);
            preparedStatement.setString(1, getUsername());
            preparedStatement.setString(2, getEmail());
            preparedStatement.setString(3, getPassword());
            preparedStatement.executeUpdate();
            ResultSet rs = preparedStatement.getGeneratedKeys();
            if (rs.next()) {
                this.id = rs.getInt(1);
            }
            System.out.println("User has been created with id number " + this.id + "\n");
        } else {
            String sql = "UPDATE users SET username=?, email=?, password=? where id = ?";
            PreparedStatement preparedStatement;
            preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1, this.username);
            preparedStatement.setString(2, this.email);
            preparedStatement.setString(3, this.password);
            preparedStatement.setInt(4, id);
            preparedStatement.executeUpdate();
            System.out.println("User " + id + " has been changed. \n");
        }
    }

    static public User loadUserById(Connection conn, int id) throws SQLException {
        String sql = "SELECT * FROM users where id=?";
        PreparedStatement preparedStatement = conn.prepareStatement(sql);
        preparedStatement.setInt(1, id);
        ResultSet resultSet = preparedStatement.executeQuery();
        if (resultSet.next()) {
            User loadedUser = new User();r
            loadedUser.id = resultSet.getInt("id");
            loadedUser.setUsername(resultSet.getString("username"));
            loadedUser.setPassword(resultSet.getString("password"));
            loadedUser.setEmail(resultSet.getString("email"));
            return loadedUser;
        } else {
            System.out.println("User no " + id + " does not exist! \n");
            return null;
        }
    }

    static public ArrayList<User> loadAllUsers(Connection conn) throws SQLException {
        ArrayList<User> usersList = new ArrayList<>();
        String sql = "SELECT * FROM users";
        PreparedStatement preparedStatement;
        preparedStatement = conn.prepareStatement(sql);
        ResultSet rs = preparedStatement.executeQuery();
        while (rs.next()) {
            User loadedUser = new User();
            loadedUser.id = rs.getInt("id");
            loadedUser.username = rs.getString("username");
            loadedUser.password = rs.getString("password");
            loadedUser.email = rs.getString("email");
            usersList.add(loadedUser);
        }
        return usersList;
    }

    public void removeUserById (Connection conn, int id) throws SQLException {
        String sql = "DELETE FROM users WHERE id=?";
        PreparedStatement preparedStatement = conn.prepareStatement(sql);
        preparedStatement.setInt(1, id);
        preparedStatement.executeUpdate();
        this.id = 0;
        System.out.println("User number " + id + " has been removed.");
    }

    @Override
    public String toString() {
        return "User Id: " + this.id + "\n" +
                "User Name: " + this.username + "\n" +
                "Email Address: " + this.email + "\n" +
                "Password: " + this.password + "\n";
    }
}
