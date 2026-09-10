package com.gateway.billing.core.cursor;

import java.util.List;
import java.util.UUID;

public final class CursorUtil {

    private CursorUtil() {
    }

    /**
     * Build a CursorPage from a list of items.
     * Assumes items are already sorted and filtered.
     * If items.size() == size + 1, there are more items.
     */
    public static <T, E> CursorPage<T> paginate(
            List<T> items,
            int requestedSize,
            String cursor
    ) {
        boolean hasNext = items.size() > requestedSize;

        if (hasNext) {
            items = items.subList(0, requestedSize);
        }

        String nextCursor = null;
        if (hasNext && !items.isEmpty()) {
            E lastItem = (E) items.getLast();
            nextCursor = extractId(lastItem);
        }

        return CursorPage.of(items, nextCursor, hasNext, requestedSize);
    }

    /**
     * Extract UUID as string from an entity-like object.
     * Used when items is List<Object[]> from native queries.
     */
    public static String extractId(Object item) {
        if (item == null) return null;
        if (item instanceof UUID uuid) return uuid.toString();
        return null;
    }

    /**
     * Parse cursor string to UUID, returns null if invalid.
     */
    public static UUID parseCursor(String cursor) {
        if (cursor == null || cursor.isBlank()) return null;
        try {
            return UUID.fromString(cursor.trim());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
