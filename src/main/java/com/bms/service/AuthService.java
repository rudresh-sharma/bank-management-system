package com.bms.service;

import com.bms.dao.UserDao;
import com.bms.exception.BankingException;
import com.bms.model.User;
import com.bms.util.ConnectionFactory;
import com.bms.util.SecurityUtil;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

public class AuthService {
    private final UserDao userDao;

    public AuthService() {
        this.userDao = new UserDao();
    }

    public User registerUser(String fullName, String email, String password) throws SQLException, BankingException {
        validateRegistrationInput(fullName, email, password);

        try (Connection connection = ConnectionFactory.getConnection()) {
            Optional<User> existingUser = userDao.findByEmail(connection, email);
            if (existingUser.isPresent()) {
                throw new BankingException("An account with this email already exists.");
            }

            User user = new User();
            user.setFullName(fullName.trim());
            user.setEmail(email.trim().toLowerCase());
            user.setPasswordHash(SecurityUtil.hashValue(password));
            return userDao.save(connection, user);
        }
    }

    public User login(String email, String password) throws SQLException, BankingException {
        if (email == null || email.isBlank() || password == null || password.isBlank()) {
            throw new BankingException("Email and password are required.");
        }

        try (Connection connection = ConnectionFactory.getConnection()) {
            User user = userDao.findByEmail(connection, email.trim().toLowerCase())
                    .orElseThrow(() -> new BankingException("Invalid email or password."));

            if (!SecurityUtil.matches(password, user.getPasswordHash())) {
                throw new BankingException("Invalid email or password.");
            }

            return user;
        }
    }

    private void validateRegistrationInput(String fullName, String email, String password) throws BankingException {
        if (fullName == null || fullName.isBlank()) {
            throw new BankingException("Full name is required.");
        }
        if (email == null || email.isBlank() || !email.contains("@")) {
            throw new BankingException("A valid email is required.");
        }
        if (password == null || password.length() < 6) {
            throw new BankingException("Password must be at least 6 characters long.");
        }
    }
}
