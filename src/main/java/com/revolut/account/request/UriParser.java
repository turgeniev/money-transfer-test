package com.revolut.account.request;

class UriParser {

    private final String prefix;

    UriParser(String prefix) {
        this.prefix = prefix;
    }

    boolean prefixMatches(String uri) {
        final String normalizedUri = uri.trim();
        return normalizedUri.startsWith(prefix + "/") || normalizedUri.equals(prefix);
    }

    String[] parse(String uri, int minCount, int maxCount) {
        String suffix = uri.trim().substring(prefix.length());
        if (suffix.startsWith("/")) {
            suffix = suffix.substring(1);
        }
        if (suffix.endsWith("/")) {
            suffix = suffix.substring(0, suffix.length() - 1);
        }
        final String[] parts = suffix.split("/");
        if (partsNotInRange(parts, minCount, maxCount)){
            throw new UnsupportedOperationException("Not supported uri: " + uri);
        }
        return parts;
    }

    private static boolean partsNotInRange(String[] parts, int minCount, int maxCount) {
        if (parts == null) {
            return true;
        }
        if (parts.length == 1 && parts[0].isEmpty()) {
            return true;
        }
        return parts.length < minCount || parts.length > maxCount;
    }

}