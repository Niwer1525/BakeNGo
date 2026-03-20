package server.tables;

import niwer.queryon.DataBase;
import niwer.queryon.queries.Expression;
import niwer.queryon.queries.interaction.InsertionManager;
import niwer.queryon.queries.interaction.SelectionManager;
import niwer.queryon.tables.Table;
import server.App;
import server.objects.User;

public class TableUser extends Table {

    public TableUser(DataBase db) {
        super(db);

        this.addColumnsFromClass(User.class).execute();
    }

    @Override public String name() { return "users"; }

    /**
     * Adds a new user to the database with the given email, password and admin status.
     * 
     * @param email The email of the user to add
     * @param password The password of the user to add (It will hash it once. But I'll hash the password again on the client side app)
     * @param isAdmin Whether the user to add should have admin privileges
     * 
     * @throws IllegalArgumentException If the email is null or empty or not valid, or if the password is null or empty
     */
    public static void addUser(String email, String password, boolean isAdmin) {
        if(email == null || email.isEmpty()) throw new IllegalArgumentException("Email cannot be null or empty");
        if(!isEmailValid(email)) throw new IllegalArgumentException("Email is not valid");
        if(password == null || password.isEmpty()) throw new IllegalArgumentException("Password cannot be null or empty");
        email = email.toLowerCase().trim(); // Normalize email
        password = App.hashPassword(password); // Hash the password

        InsertionManager.insert(App.DATA_BASE, TableUser.class, "email", "password", "is_admin")
            .row(email, password, isAdmin)
            .execute();
    }

    /**
     * Gets a user from the database by their email.
     * 
     * @param email The email of the user to get
     * @return The user with the given email, or null if no such user exists
     */
    public static User getUserByEmail(String email) {
        if(email == null || email.isEmpty()) throw new IllegalArgumentException("Email cannot be null or empty");
        if(!isEmailValid(email)) throw new IllegalArgumentException("Email is not valid");
        email = email.toLowerCase().trim(); // Normalize email

        if(!doesUserExist(email)) return null;

        return SelectionManager.select(App.DATA_BASE, TableUser.class)
            .where(Expression.of("LOWER(email)").isEqualTo(email))
            .executeSerializable(User.class);
    }

    /**
     * Checks if a user with the given email has admin privileges.
     * 
     * @param email The email of the user to check
     * @return True if the user with the given email has admin privileges, false otherwise
     */
    public static boolean isUserAdmin(String email) {
        final User USER = getUserByEmail(email);
        return USER != null && USER.isAdmin();
    }

    /**
     * Checks if a user with the given email exists in the database.
     * 
     * @param email The email of the user to check
     * @return True if a user with the given email exists in the database, false otherwise
     */
    public static boolean doesUserExist(String email) {
        if(email == null || email.isEmpty()) throw new IllegalArgumentException("Email cannot be null or empty");
        if(!isEmailValid(email)) throw new IllegalArgumentException("Email is not valid");
        email = email.toLowerCase().trim(); // Normalize email

        return SelectionManager.select(App.DATA_BASE, TableUser.class)
            .where(Expression.of("LOWER(email)").isEqualTo(email.toLowerCase().trim()))
            .executeHasResult();
    }

    /**
     * Authenticates a user with the given email and password.
     * 
     * @param email The email of the user to authenticate
     * @param password The password of the user to authenticate (It will hash it once. But I'll hash the password again on the client side app)
     * @return True if the email and password match a user in the database, false otherwise
     */
    public static boolean authenticateUser(String email, String password) {
        if(email == null || email.isEmpty()) throw new IllegalArgumentException("Email cannot be null or empty");
        if(password == null || password.isEmpty()) throw new IllegalArgumentException("Password cannot be null or empty");
        email = email.toLowerCase().trim(); // Normalize email
        password = App.hashPassword(password); // Hash the password

        final User USER = getUserByEmail(email);
        return USER != null && USER.password().equals(password);
    }

    private static boolean isEmailValid(String email) {
        // Simple regex for email validation
        return email != null && email.matches("^[A-Za-z0-9+_.-]+@(.+)$");
    }
}
