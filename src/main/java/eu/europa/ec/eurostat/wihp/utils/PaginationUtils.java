package eu.europa.ec.eurostat.wihp.utils;

import static java.util.stream.Collectors.toUnmodifiableList;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

public class PaginationUtils {

    private PaginationUtils() {
        throw new IllegalStateException("Utility class");
    }

    public static <T> Page<T> createPage(final Collection<T> collection, final Pageable pageable) {
        long maxOffset = pageable.getOffset() + pageable.getPageSize();
        long pageSize = Math.min(maxOffset, collection.size());
        List<T> resultList = collection.size() > pageable.getOffset()
            ? collection.stream().skip(pageable.getOffset()).limit(pageSize).collect(toUnmodifiableList())
            : Collections.emptyList();
        return new PageImpl<>(resultList, pageable, collection.size());
    }
}
