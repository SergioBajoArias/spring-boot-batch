package com.xeridia.model;

public record Comment(Long id, Long postId, String name, String email, String body) {}
