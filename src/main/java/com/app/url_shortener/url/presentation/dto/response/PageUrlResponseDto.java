package com.app.url_shortener.url.presentation.dto.response;

import java.util.List;

public record PageUrlResponseDto(List<UrlResponseDto> urls, String nextCursor) {
}
