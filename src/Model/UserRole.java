package Model;

/**
 * Defines the role of a system user.
 * Controls what actions the user is allowed to perform.
 *
 * GUEST — browsing only, no account required
 * USER  — registered user, can create collections and submit reviews
 * ADMIN — can add and delete games from the master game database
 */
public enum UserRole {
    GUEST,
    USER,
    ADMIN
}