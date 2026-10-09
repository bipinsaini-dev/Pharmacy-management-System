package com.pharmacare.service;

import com.pharmacare.dao.UserDAO;
import com.pharmacare.model.User;
import com.pharmacare.util.BusinessException;
import com.pharmacare.util.PasswordUtil;
import com.pharmacare.util.ValidationUtil;

import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;
import java.util.List;

public class AuthService {
    public static final int MIN_PASSWORD = 8;
    private final UserDAO dao = new UserDAO();

    /** Verifies credentials. The returned user never carries the password hash. */
    public User login(String identifier, String password) throws SQLException, BusinessException {
        if (ValidationUtil.isBlank(identifier) || ValidationUtil.isBlank(password)) {
            throw new BusinessException("Please enter your username/email and password.");
        }
        User u = dao.findByLogin(identifier.trim());
        if (u == null || !PasswordUtil.verify(password, u.getPassword())) {
            throw new BusinessException("Invalid username/email or password.");
        }
        if (!u.isActive()) {
            throw new BusinessException("Your account is disabled. Please contact the administrator.");
        }
        u.setPassword(null);
        return u;
    }

    /**
     * Validates user fields (shared by self-registration and admin user management).
     * @param passwordRequired true when creating a user; when false a blank password means "keep current"
     */
    public List<String> validate(User u, String password, String confirm, boolean passwordRequired) throws SQLException {
        List<String> errors = new ArrayList<>();
        if (ValidationUtil.isBlank(u.getFullName()) || u.getFullName().trim().length() < 3) {
            errors.add("Full name is required (at least 3 characters).");
        }
        if (!ValidationUtil.isEmail(u.getEmail())) {
            errors.add("Please enter a valid email address.");
        }
        if (!ValidationUtil.isPhone(u.getPhone())) {
            errors.add("Phone must contain 10 to 13 digits (optional + prefix).");
        }
        if (!ValidationUtil.isUsername(u.getUsername())) {
            errors.add("Username must be 3-30 characters: letters, digits, '_' or '.'.");
        }
        boolean checkPassword = passwordRequired || !ValidationUtil.isBlank(password);
        if (checkPassword) {
            if (password == null || password.length() < MIN_PASSWORD) {
                errors.add("Password must be at least " + MIN_PASSWORD + " characters long.");
            } else if (!password.equals(confirm)) {
                errors.add("Password and confirm password do not match.");
            }
        }
        if (errors.isEmpty()) {
            if (dao.usernameExists(u.getUsername().trim(), u.getId())) errors.add("That username is already taken.");
            if (dao.emailExists(u.getEmail().trim(), u.getId())) errors.add("That email is already registered.");
        }
        return errors;
    }

    /** Public self-registration: always creates an ACTIVE PHARMACIST. */
    public void register(User u, String password) throws SQLException, BusinessException {
        u.setRole("PHARMACIST");
        u.setStatus("ACTIVE");
        create(u, password);
    }

    /** Creates a user with whatever role/status is set on the object (admin use). */
    public void create(User u, String password) throws SQLException, BusinessException {
        u.setFullName(u.getFullName().trim());
        u.setEmail(u.getEmail().trim());
        u.setPhone(u.getPhone().trim());
        u.setUsername(u.getUsername().trim());
        u.setPassword(PasswordUtil.hash(password));
        try {
            dao.create(u);
        } catch (SQLIntegrityConstraintViolationException e) {
            throw new BusinessException("Username or email already exists.");
        }
    }

    public void updateUser(User u, String newPasswordOrBlank) throws SQLException, BusinessException {
        try {
            dao.update(u);
            if (!ValidationUtil.isBlank(newPasswordOrBlank)) {
                dao.updatePassword(u.getId(), PasswordUtil.hash(newPasswordOrBlank));
            }
        } catch (SQLIntegrityConstraintViolationException e) {
            throw new BusinessException("Username or email already exists.");
        }
    }

    public void changePassword(int userId, String current, String next, String confirm)
            throws SQLException, BusinessException {
        User u = dao.findById(userId);
        if (u == null || !PasswordUtil.verify(current, u.getPassword())) {
            throw new BusinessException("Current password is incorrect.");
        }
        checkNewPassword(next, confirm);
        dao.updatePassword(userId, PasswordUtil.hash(next));
    }

    /** Demo-level "forgot password": identity is confirmed by matching username + registered email. */
    public void resetPassword(String username, String email, String next, String confirm)
            throws SQLException, BusinessException {
        User u = dao.findByUsernameAndEmail(ValidationUtil.clean(username), ValidationUtil.clean(email));
        if (u == null) {
            throw new BusinessException("No account matches that username and email.");
        }
        checkNewPassword(next, confirm);
        dao.updatePassword(u.getId(), PasswordUtil.hash(next));
    }

    private void checkNewPassword(String next, String confirm) throws BusinessException {
        if (next == null || next.length() < MIN_PASSWORD) {
            throw new BusinessException("New password must be at least " + MIN_PASSWORD + " characters long.");
        }
        if (!next.equals(confirm)) {
            throw new BusinessException("New password and confirm password do not match.");
        }
    }
}
