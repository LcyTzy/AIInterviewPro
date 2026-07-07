package com.zhenyu;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class Temp {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String pass = encoder.encode("admin123");
        System.out.println(pass);
    }
}
