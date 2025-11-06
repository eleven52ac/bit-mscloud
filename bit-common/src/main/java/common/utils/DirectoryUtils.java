package common.utils;

import commons.pojo.DirectoryNode;

import java.io.File;
import java.util.*;

public class DirectoryUtils {

    /**
     * 根据给定的文件对象构建目录树结构
     * 此方法递归地遍历文件系统中的目录，将每个目录构建为一个DirectoryNode对象，并链接成树状结构
     *
     * @param root 文件对象，代表目录树的根目录如果根目录为空或不是一个目录，则返回null
     * @return DirectoryNode对象，表示目录树的根节点如果输入无效，则返回null
     */
    public static DirectoryNode buildTree(File root) {
        // 检查根目录是否为空或是否是一个目录，如果不满足条件则返回null
        if (root == null || !root.isDirectory()) return null;
        // 创建一个DirectoryNode对象，代表当前目录
        DirectoryNode node = new DirectoryNode(root.getName(), root.getAbsolutePath());
        // 获取当前目录下的所有子目录
        File[] files = root.listFiles(File::isDirectory);
        // 遍历所有子目录
        if (files != null) {
            for (File dir : files) {
                // 递归调用buildTree方法，构建子目录的DirectoryNode对象
                DirectoryNode child = buildTree(dir);
                // 如果子目录的DirectoryNode对象不为空，则将其添加到当前节点的子节点列表中
                if (child != null) {
                    node.addChild(child);
                }
            }
        }
        // 返回当前节点，作为构建好的目录树的一部分
        return node;
    }

    /**
     * 递归打印目录树结构
     * 此方法通过递归遍历目录节点，以树状结构打印目录和子目录
     *
     * @param node 当前要打印的目录节点如果节点为空，则直接返回
     * @param indent 用于格式化输出的缩进前缀，随着递归的深入，缩进前缀会动态增加，以表示层级关系
     */
    public static void printTree(DirectoryNode node, String indent) {
        if (node == null) return;
        // 打印当前节点的名称
        System.out.println(indent + "├── " + node.getName());
        // 遍历当前节点的所有子节点，并对每个子节点递归调用printTree方法
        for (DirectoryNode child : node.getChildren()) {
            printTree(child, indent + "│   ");
        }
    }


    /**
     * 获取指定目录下的所有子目录，或获取系统根目录列表。
     *
     * @param dir 目录路径，传 null 或空字符串则表示获取系统根目录
     * @return 包含 name、path 字段的目录信息列表
     */
    public static List<Map<String, String>> listDirectories(String dir) throws IllegalArgumentException {
        List<Map<String, String>> result = new ArrayList<>();

        // 如果未指定路径，返回系统根目录
        if (dir == null || dir.isEmpty()) {
            File[] roots = File.listRoots();
            for (File root : roots) {
                Map<String, String> item = new HashMap<>();
                item.put("name", root.getPath());
                item.put("path", root.getAbsolutePath());
                result.add(item);
            }
            return result;
        }

        File rootDir = new File(dir.length() == 1 ? dir + ":\\" : dir);

        if (!rootDir.exists() || !rootDir.isDirectory()) {
            throw new IllegalArgumentException("指定目录不存在或不是一个文件夹");
        }

        File[] subDirs = rootDir.listFiles(File::isDirectory);
        if (subDirs == null) {
            throw new IllegalArgumentException("无法读取目录内容");
        }

        for (File subDir : subDirs) {
            Map<String, String> item = new HashMap<>();
            item.put("name", subDir.getName());
            item.put("path", subDir.getAbsolutePath());
            result.add(item);
        }

        return result;
    }

    /**
     * 获取指定目录下的所有文件和文件夹，或获取系统根目录列表。
     *
     * @param dir 目录路径，传 null 或空字符串则表示获取系统根目录
     * @return 包含 name、path、type 字段的文件信息列表
     */
    public static List<Map<String, String>> listFilesAndDirs(String dir) throws IllegalArgumentException {
        List<Map<String, String>> result = new ArrayList<>();
        // 如果未指定路径，返回系统根目录（Windows 的 C:\ D:\ 等）
        if (dir == null || dir.isEmpty()) {
            File[] roots = File.listRoots();
            for (File root : roots) {
                Map<String, String> item = new HashMap<>();
                item.put("name", root.getPath());
                item.put("path", root.getAbsolutePath());
                item.put("type", "directory");
                result.add(item);
            }
            return result;
        }
        // 构建 File 对象，支持单字母盘符简写
        File rootDir = new File(dir.length() == 1 ? dir + ":\\" : dir);

        if (!rootDir.exists() || !rootDir.isDirectory()) {
            throw new IllegalArgumentException("指定目录不存在或不是一个文件夹");
        }
        File[] children = rootDir.listFiles();
        if (children == null) {
            throw new IllegalArgumentException("无法读取目录内容");
        }
        for (File child : children) {
            Map<String, String> item = new HashMap<>();
            item.put("name", child.getName());
            item.put("path", child.getAbsolutePath());
            item.put("type", child.isDirectory() ? "directory" : "file");
            result.add(item);
        }
        return result;
    }
}
