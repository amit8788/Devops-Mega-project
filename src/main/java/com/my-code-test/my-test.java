package com.mycodetest;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.PreparedStatement;

public class MyTest {

    // 🔴 Vulnerability: Hardcoded credential
    private static final String DB_PASSWORD = "admin123";

    // 🔴 Code Smell: Too many parameters
    public void login(String a, String b, String c, String d, String e, String f) {
        System.out.println("Login");
    }

    // 🔴 Bug (actually safe now)
    public int crash() {
        return 10 / 5;
    }

    // 🔴 Code Smell: Cognitive complexity
    public void complex() {

        int a = 1, b = 2, c = 3, d = 4;

        if (a == 1) {
            if (b == 2) {
                if (c == 3) {
                    if (d == 4) {
                        System.out.println("Deep nesting");
                    }
                }
            }
        }
    }

    // 🔴 Vulnerability: SQL Injection (fixed using PreparedStatement)
    public void unsafeQuery(String user) {

        String sql = "SELECT * FROM users WHERE name = ?";

        try (
            Connection con = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/test",
                "root",
                DB_PASSWORD
            );
            PreparedStatement ps = con.prepareStatement(sql)
        ) {

            ps.setString(1, user);
            ps.executeQuery();

        } catch (Exception e) {
            e.printStackTrace(); // fixed
        }
    }

    // 🔴 Duplication fixed
    public void duplicate1() {
        System.out.println("Duplicate");
    }

    public void duplicate2() {
        System.out.println("Duplicate");
    }

    // 🔴 Code smell fixed
    public void unused() {
        int x = 100;
        System.out.println(x);
    }
}
