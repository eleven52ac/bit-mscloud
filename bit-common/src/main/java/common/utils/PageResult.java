package common.utils;

import lombok.Data;
import java.util.List;

@Data
public class PageResult<T> {
    private long current;
    private long size;
    private long total;
    private List<T> records;

    public PageResult(long current, long size, long total, List<T> records) {
        this.current = current;
        this.size = size;
        this.total = total;
        this.records = records;
    }

    public static <T> PageResult<T> fromPage(com.baomidou.mybatisplus.extension.plugins.pagination.Page<T> page) {
        return new PageResult<>(
            page.getCurrent(),
            page.getSize(),
            page.getTotal(),
            page.getRecords()
        );
    }
}
