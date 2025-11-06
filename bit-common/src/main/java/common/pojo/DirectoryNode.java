package commons.pojo;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class DirectoryNode {
    private String name;
    private String path;
    private List<DirectoryNode> children = new ArrayList<>();

    public DirectoryNode(String name, String path) {
        this.name = name;
        this.path = path;
    }

    public void addChild(DirectoryNode child) {
        children.add(child);
    }

    // Getter 和 Setter 略，可使用 lombok 简化
}
