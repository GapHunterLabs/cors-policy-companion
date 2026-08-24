package com.acmecorp.api;

public class ApiController {

    // Invalid: wildcard origin + credentials -- browsers reject this.
    @CrossOrigin(origins = "*", allowCredentials = "true")
    public Account getAccount() {
        return accountService.current();
    }

    // Correct: specific origin, credentials allowed.
    @CrossOrigin(origins = "https://app.acmecorp.com", allowCredentials = "true")
    public Order getOrder() {
        return orderService.current();
    }
}
