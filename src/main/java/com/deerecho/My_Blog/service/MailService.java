package com.deerecho.My_Blog.service;

public interface MailService {
    void sendMail(String to, String subject, String content);
}
