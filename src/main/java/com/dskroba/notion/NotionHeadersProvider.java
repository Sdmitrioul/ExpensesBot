package com.dskroba.notion;

public class NotionHeadersProvider {
    private final String token;
    private final String version;

    public NotionHeadersProvider(String token, String version) {
        this.token = token;
        this.version = version;
    }

    public String[] getPostHeader() {
        return getHeader("Content-Type", "application/json");
    }

    private String[] getHeader(String ... additionalHeaders) {
        int size = (additionalHeaders == null ? 0 : additionalHeaders.length) + 6;
        String[] headers = new String[size];
        headers[0] = "Authorization";
        headers[1] = token;
        headers[2] = "Notion-Version";
        headers[3] = version;
        headers[4] = "Accept";
        headers[5] = "application/json";
        if (additionalHeaders == null) {
            return headers;
        }
        int i = 6;
        for (String header : additionalHeaders) {
            headers[i++] = header;
        }
        return headers;
    }
}
