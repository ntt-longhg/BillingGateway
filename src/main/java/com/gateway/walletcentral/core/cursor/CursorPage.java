package com.gateway.walletcentral.core.cursor;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Cursor-based pagination response")
public class CursorPage<T> {

    @Schema(description = "List of items in this page")
    private List<T> items;

    @Schema(description = "Cursor for the next page, null if no more pages")
    private String nextCursor;

    @Schema(description = "Whether there are more items", example = "true")
    private boolean hasNext;

    @Schema(description = "Number of items requested per page", example = "20")
    private int size;

    @Schema(description = "Number of items returned in this page", example = "20")
    private int count;

    public static <T> CursorPage<T> of(List<T> items, String nextCursor, boolean hasNext, int size) {
        return CursorPage.<T>builder()
                .items(items)
                .nextCursor(nextCursor)
                .hasNext(hasNext)
                .size(size)
                .count(items.size())
                .build();
    }
}
