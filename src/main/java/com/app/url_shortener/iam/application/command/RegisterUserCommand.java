package com.app.url_shortener.iam.application.command;

public record RegisterUserCommand(
    String name,
    String email,
    String password) {
}
