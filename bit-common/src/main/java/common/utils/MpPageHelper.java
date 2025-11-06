//package commons.utils;
//
//import com.baomidou.mybatisplus.core.metadata.OrderItem;
//import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import java.util.*;
//import java.util.function.Supplier;
//
///**
// * MyBatis-Plus 分页工具类（PageHelper 风格链式写法）
// *
// * <p>使用示例：</p>
// * <pre>
// * // 最简用法（无排序）
// * PageResult<User> result = MpPageHelper
// *         .startPage(1, 10)
// *         .doSelectPageResult(() -> userMapper.selectList(null));
// *
// * // 指定单列排序（列名为数据库字段）
// * PageResult<User> result2 = MpPageHelper
// *         .startPage(1, 10, "create_time", false)
// *         .doSelectPageResult(() -> userMapper.selectList(null));
// *
// * // 多列排序（传入 OrderItem 列表）
// * List&lt;OrderItem&gt; orders = Arrays.asList(OrderItem.desc("create_time"), OrderItem.asc("id"));
// * PageResult<User> result3 = MpPageHelper
// *         .startPage(1, 10, orders)
// *         .doSelectPageResult(() -> userMapper.selectList(null));
// *
// * // 链式追加排序（可选，便于在 Service 侧灵活拼装）
// * PageResult<User> result4 = MpPageHelper
// *         .startPage(1, 10)
// *         .orderByDesc("create_time")
// *         .orderByAsc("id")
// *         .doSelectPageResult(() -> userMapper.selectList(null));
// * </pre>
// *
// * <p><b>注意</b>：</p>
// * <ul>
// *   <li>需在 MyBatis-Plus 配置 {@code PaginationInnerInterceptor}。</li>
// *   <li>排序字段必须为数据库列名（非 Java 属性名），且仅允许字母、数字、下划线组成。</li>
// *   <li>请始终通过 {@code doSelectPage(...)} 或 {@code doSelectPageResult(...)} 收尾，以确保 ThreadLocal 清理。</li>
// * </ul>
// *
// * @author
// * @since 2025-10-26
// */
//public class MpPageHelper {
//
//    private static final Logger log = LoggerFactory.getLogger(MpPageHelper.class);
//
//    /** 保存当前线程分页对象 */
//    private static final ThreadLocal<Page<?>> PAGE_THREAD_LOCAL = new ThreadLocal<>();
//
//    /** 默认最大每页大小，防止滥用 */
//    private static final long DEFAULT_MAX_PAGE_SIZE = 1000L;
//
//    /* ==========================
//     * 对外入口：startPage（无排序）
//     * ========================== */
//
//    /**
//     * 启动分页（无排序）。
//     */
//    public static <T> PageChain<T> startPage(long pageNum, long pageSize) {
//        validatePageParams(pageNum, pageSize);
//        Page<T> page = Page.of(pageNum, pageSize);
//        PAGE_THREAD_LOCAL.set(page);
//        warnIfUsedOutsideChain();
//        return new PageChain<>();
//    }
//
//    /* ==========================
//     * 对外入口：startPage（单列排序）
//     * ========================== */
//
//    /**
//     * 启动分页并指定排序字段（默认升序）。
//     */
//    public static <T> PageChain<T> startPage(long pageNum, long pageSize, String orderByColumn) {
//        return startPage(pageNum, pageSize, orderByColumn, true);
//    }
//
//    /**
//     * 启动分页并指定排序字段及排序方向。
//     */
//    public static <T> PageChain<T> startPage(long pageNum, long pageSize, String orderByColumn, boolean isAsc) {
//        validatePageParams(pageNum, pageSize);
//        validateOrderByColumn(orderByColumn);
//        Page<T> page = Page.of(pageNum, pageSize);
//        page.addOrder(isAsc ? OrderItem.asc(orderByColumn) : OrderItem.desc(orderByColumn));
//        PAGE_THREAD_LOCAL.set(page);
//        warnIfUsedOutsideChain();
//        return new PageChain<>();
//    }
//
//    /* ==========================
//     * 对外入口：startPage（多列排序）
//     * ========================== */
//
//    /**
//     * 启动分页并指定多个排序项。
//     */
//    public static <T> PageChain<T> startPage(long pageNum, long pageSize, List<OrderItem> orders) {
//        validatePageParams(pageNum, pageSize);
//        if (orders != null && !orders.isEmpty()) {
//            for (OrderItem item : orders) {
//                if (item != null && item.getColumn() != null) {
//                    validateOrderByColumn(item.getColumn());
//                }
//            }
//        }
//        Page<T> page = Page.of(pageNum, pageSize);
//        if (orders != null && !orders.isEmpty()) {
//            page.addOrder(orders);
//        }
//        PAGE_THREAD_LOCAL.set(page);
//        warnIfUsedOutsideChain();
//        return new PageChain<>();
//    }
//
//    /* ==========================
//     * 链式执行器
//     * ========================== */
//
//    /**
//     * 链式执行器：承载二次操作（排序追加 / 执行查询 / 返回结果）。
//     *
//     * @param <T> 实体类型
//     */
//    public static class PageChain<T> {
//
//        /**
//         * 追加升序排序，支持可变参数。
//         */
//        public PageChain<T> orderByAsc(String... columns) {
//            if (columns != null) {
//                for (String col : columns) {
//                    if (col != null) {
//                        validateOrderByColumn(col);
//                        currentPage().addOrder(OrderItem.asc(col));
//                    }
//                }
//            }
//            return this;
//        }
//
//        /**
//         * 追加降序排序，支持可变参数。
//         */
//        public PageChain<T> orderByDesc(String... columns) {
//            if (columns != null) {
//                for (String col : columns) {
//                    if (col != null) {
//                        validateOrderByColumn(col);
//                        currentPage().addOrder(OrderItem.desc(col));
//                    }
//                }
//            }
//            return this;
//        }
//
//        /**
//         * 一次性追加多个排序项。
//         */
//        public PageChain<T> orderBy(List<OrderItem> orders) {
//            if (orders != null && !orders.isEmpty()) {
//                for (OrderItem item : orders) {
//                    if (item != null && item.getColumn() != null) {
//                        validateOrderByColumn(item.getColumn());
//                    }
//                }
//                currentPage().addOrder(orders);
//            }
//            return this;
//        }
//
//        /**
//         * 执行分页查询并返回 MyBatis-Plus 的 {@link Page} 对象。
//         * <p>在执行 supplier.get() 期间，MyBatis-Plus 分页插件会自动识别 ThreadLocal 中的分页参数。</p>
//         * <p>执行完毕后自动清理 ThreadLocal。</p>
//         */
//        public Page<T> doSelectPage(Supplier<?> supplier) {
//            Objects.requireNonNull(supplier, "supplier 不能为空");
//            try {
//                supplier.get(); // 必须触发 MyBatis 查询
//                Page<T> page = getPage();
//                if (page == null) {
//                    throw new IllegalStateException(
//                            "未获取到分页上下文 Page，请确认：\n" +
//                                    "1) 已正确配置 MyBatis-Plus 的 PaginationInnerInterceptor；\n" +
//                                    "2) supplier 内部已调用 Mapper 方法并触发了数据库查询；\n" +
//                                    "3) 未在异步线程中调用。"
//                    );
//                }
//                return page;
//            } finally {
//                clearPage();
//            }
//        }
//
//        /**
//         * 执行分页查询并返回封装后的 {@link PageResult}。
//         */
//        public PageResult<T> doSelectPageResult(Supplier<?> supplier) {
//            Page<T> page = doSelectPage(supplier);
//            return PageResult.fromPage(page);
//        }
//
//        /* 获取当前线程 Page<T> */
//        @SuppressWarnings("unchecked")
//        private Page<T> currentPage() {
//            Page<T> page = (Page<T>) PAGE_THREAD_LOCAL.get();
//            if (page == null) {
//                throw new IllegalStateException("未发现分页上下文，请先调用 MpPageHelper.startPage(...)。");
//            }
//            return page;
//        }
//
//        /**
//         * 解析前端传入的 orderBy 字符串并追加排序。
//         * 示例： "createTime desc,id asc" 或 "create_time desc,id asc"
//         * 默认：允许 camelCase -> 下划线转换；不启用白名单；不使用别名映射。
//         */
//        public PageChain<T> orderBy(String orderBy) {
//            return orderBy(orderBy, null, null, true);
//        }
//
//        /**
//         * 带白名单的 orderBy 解析（推荐）。仅 allowColumns 中的字段会被接受。
//         * 示例 allowColumns: Set.of("id", "create_time", "name")
//         * 默认：允许 camelCase -> 下划线；不使用别名映射。
//         */
//        public PageChain<T> orderBy(String orderBy, Set<String> allowColumns) {
//            return orderBy(orderBy, allowColumns, null, true);
//        }
//
//        /**
//         * 完全可控的 orderBy 解析。
//         *
//         * @param orderBy             例如 "createTime desc,id asc"
//         * @param allowColumns        允许的列名白名单（传 null 则不启用白名单）
//         * @param aliasMap            别名映射（如 "createTime" -> "create_time" 或 "personName" -> "p.name"），仅对 key 命中时生效
//         * @param camelToUnderscore   是否将未命中 aliasMap 的字段从驼峰转下划线
//         */
//        public PageChain<T> orderBy(String orderBy,
//                                    Set<String> allowColumns,
//                                    Map<String, String> aliasMap,
//                                    boolean camelToUnderscore) {
//            if (orderBy == null || orderBy.trim().isEmpty()) {
//                return this;
//            }
//            String[] parts = orderBy.split(",");
//            for (String raw : parts) {
//                if (raw == null) continue;
//                String seg = raw.trim();
//                if (seg.isEmpty()) continue;
//
//                // 拆分 "col [asc|desc]"
//                String[] tokens = seg.split("\\s+");
//                String rawCol = tokens[0].trim();
//
//                // 列名归一化：alias > camelToUnderscore > 原样
//                String normalizedCol = normalizeColumn(rawCol, aliasMap, camelToUnderscore);
//
//                // 安全校验：只允许 [a-zA-Z0-9_]+ ；若 aliasMap 映射到复杂表达式，请确保它是可信的
//                // 如果你需要允许 aliasMap 映射到表达式（如 "p.name"），可放宽此处校验，但务必只允许在 aliasMap 命中时通过。
//                boolean fromAlias = (aliasMap != null && aliasMap.containsKey(rawCol));
//                if (!fromAlias && !normalizedCol.matches("^[a-zA-Z0-9_]+$")) {
//                    throw new IllegalArgumentException("非法排序字段: " + rawCol);
//                }
//
//                // 白名单校验（仅对非 alias 的普通列名做；若要对 alias 也校验可自行调整）
//                if (!fromAlias && allowColumns != null && !allowColumns.isEmpty()
//                        && !allowColumns.contains(normalizedCol)) {
//                    // 忽略未在白名单内的列（也可选择抛异常）
//                    continue;
//                }
//
//                String dir = (tokens.length >= 2) ? tokens[1].trim().toUpperCase(Locale.ROOT) : "ASC";
//                if (!"ASC".equals(dir) && !"DESC".equals(dir)) {
//                    dir = "ASC"; // 非法方向回退为 ASC（也可选择抛异常）
//                }
//
//                if ("ASC".equals(dir)) {
//                    currentPage().addOrder(OrderItem.asc(normalizedCol));
//                } else {
//                    currentPage().addOrder(OrderItem.desc(normalizedCol));
//                }
//            }
//            return this;
//        }
//
//        /* ========== 下方是内部小工具：字段归一化 ========== */
//
//        private String normalizeColumn(String rawCol, Map<String, String> aliasMap, boolean camelToUnderscore) {
//            if (aliasMap != null) {
//                String mapped = aliasMap.get(rawCol);
//                if (mapped != null && !mapped.isBlank()) {
//                    return mapped.trim();
//                }
//            }
//            String col = rawCol.trim();
//            if (camelToUnderscore) {
//                col = camelToUnderline(col);
//            }
//            return col;
//        }
//
//        private String camelToUnderline(String s) {
//            if (s == null || s.isEmpty()) return s;
//            StringBuilder sb = new StringBuilder(s.length() + 8);
//            for (int i = 0; i < s.length(); i++) {
//                char c = s.charAt(i);
//                if (Character.isUpperCase(c)) {
//                    sb.append('_').append(Character.toLowerCase(c));
//                } else {
//                    sb.append(c);
//                }
//            }
//            return sb.toString();
//        }
//
//    }
//
//    /* ==========================
//     * 内部工具方法
//     * ========================== */
//
//    private static void validatePageParams(long pageNum, long pageSize) {
//        if (pageNum <= 0) {
//            throw new IllegalArgumentException("页码必须大于 0，当前值: " + pageNum);
//        }
//        if (pageSize <= 0 || pageSize > DEFAULT_MAX_PAGE_SIZE) {
//            throw new IllegalArgumentException(
//                    String.format("每页大小必须在 1 ~ %d 之间，当前值: %d", DEFAULT_MAX_PAGE_SIZE, pageSize)
//            );
//        }
//    }
//
//    /** 排序字段只允许字母、数字、下划线，防止 SQL 注入。 */
//    private static void validateOrderByColumn(String column) {
//        if (column == null || column.trim().isEmpty()) {
//            throw new IllegalArgumentException("排序字段不能为空");
//        }
//        if (!column.matches("^[a-zA-Z0-9_]+$")) {
//            throw new IllegalArgumentException("非法的排序字段: '" + column + "'，仅支持字母、数字、下划线");
//        }
//    }
//
//    @SuppressWarnings("unchecked")
//    private static <T> Page<T> getPage() {
//        return (Page<T>) PAGE_THREAD_LOCAL.get();
//    }
//
//    /** 手动清除分页上下文（通常不需要，链式方法会自动清理）。 */
//    public static void clearPage() {
//        PAGE_THREAD_LOCAL.remove();
//    }
//
//    /**
//     * 警告：如果直接调用 startPage 而未通过链式 doSelectPage/doSelectPageResult 收尾，可能导致 ThreadLocal 泄漏。
//     * 这里在开发环境打日志提示（可通过日志级别控制）。
//     */
//    private static void warnIfUsedOutsideChain() {
//        if (log.isWarnEnabled()) {
//            // 检查调用栈中是否包含 PageChain#doSelectPage 或 #doSelectPageResult（同一线程内）
//            // 这里仅作弱提示，真实判断较难（因为链式调用在之后才发生）
//            log.warn("MpPageHelper.startPage() 已设置分页上下文，请确保随后调用 doSelectPage()/doSelectPageResult() 以清理 ThreadLocal。");
//        }
//    }
//
//
//}
