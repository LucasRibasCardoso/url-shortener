package com.app.url_shortener.url.application.result;

import java.util.List;

public record PageUrlResult(List<UrlDetailsResult> urls, String nextCursor) {
}
