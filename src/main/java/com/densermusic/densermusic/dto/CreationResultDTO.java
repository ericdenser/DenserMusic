package com.densermusic.densermusic.dto;

public record CreationResultDTO<T>(T entity, boolean created) {
}
