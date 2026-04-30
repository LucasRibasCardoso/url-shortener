package com.app.url_shortener.auth.application.command;

public record RegisterUserCommand(
    String name,
    String email,
    String password) {
}
