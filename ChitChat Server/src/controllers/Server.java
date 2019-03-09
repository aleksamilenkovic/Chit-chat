package controllers;


import java.io.IOException;
import java.net.ServerSocket;
import java.sql.*;
import java.util.ArrayList;

public class Server {
    private static ServerSocket server;
    private static final int  port = 16114;
    public static ArrayList<User> onlineUsers;
    private static Connection connection;
    public static void main(String[] args) throws IOException {
        server = new ServerSocket(16114);
        try{
            Class.forName("com.mysql.jdbc.Driver");
            connection= DriverManager.getConnection(
                    "jdbc:mysql://localhost/chitchatdatabase", "root", "");
            onlineUsers = new ArrayList<>();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try{
            while(true){
                    new UserHandler(server.accept()).start();
            }
        }catch (Exception e){
            e.printStackTrace();
            server.close();
        }
    }


    public static void insertIntoDatabase(String user, String pass){
        try{
            Statement stmt = (Statement)connection.createStatement();
            stmt.execute("INSERT INTO `user`(`Username`, `Password`) VALUES ('"+user+"','"+pass+"')");
            stmt.close();
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    public static boolean isRegistered(String user){
        try{
            Statement stmt = (Statement)connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT Username FROM user");

            while (rs.next())
                if(user.equals(rs.getString(1)))
                    return true;

            stmt.close();
        }catch(SQLException e){
            e.printStackTrace();
        }

        return false;
    }
    public static boolean isRegistered(String user, String pass){
        try{
            Statement stmt = (Statement)connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT Password FROM user WHERE Username='"+user+"' ");
            while (rs.next())
                if(pass.equals(rs.getString(1)))
                    return true;

            stmt.close();
        }catch(SQLException e){
            e.printStackTrace();
        }

        return false;
    }
}
