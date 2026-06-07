package com.XuBoRe.hw4;

import java.io.InputStream;
import java.util.Scanner;

public class Tree {
    private TreeNode root;
    private TreeNode current;

    // 用于 Android UI 历史回退的栈 (完美保留了你的 Extra Credit 逻辑)
    private TreeNode[] historyArray = new TreeNode[100];
    private int historyCount = 0;

    /**
     * 安卓版特制构造函数：直接接收 assets 里的 InputStream
     * 完美融合了你原本在 TreeDriver 里的 buildTreeFromFile 和 readNonEmptyLine 逻辑
     */
    public Tree(InputStream is) {
        Scanner fileScanner = new Scanner(is);

        String rootLabel = readNonEmptyLine(fileScanner);
        String rootPrompt = readNonEmptyLine(fileScanner);
        String rootMessage = readNonEmptyLine(fileScanner);

        this.root = new TreeNode(rootLabel, rootPrompt, rootMessage);

        while (fileScanner.hasNextLine()) {
            String line = readNonEmptyLine(fileScanner);
            if (line == null) break;

            String[] tokens = line.split("\\s+");
            if (tokens.length < 2) continue;

            String parentLabel = tokens[0];
            int numChildren = Integer.parseInt(tokens[1]);

            for (int i = 0; i < numChildren; i++) {
                String childLabel = readNonEmptyLine(fileScanner);
                String childPrompt = readNonEmptyLine(fileScanner);
                String childMessage = readNonEmptyLine(fileScanner);
                addNode(childLabel, childPrompt, childMessage, parentLabel);
            }
        }
        fileScanner.close();

        // 初始化当前节点为根节点
        current = root;
    }

    // ==========================================
    // 以下是你原汁原味的底层逻辑，完全保留！
    // ==========================================

    public boolean addNode(String label, String prompt, String message, String parentLabel) {
        TreeNode parent = getNodeReference(parentLabel);
        if (parent == null) {
            return false;
        }
        TreeNode newNode = new TreeNode(label, prompt, message);
        TreeNode[] children = parent.getChildren();
        for (int i = 0; i < children.length; i++) {
            if (children[i] == null) {
                children[i] = newNode;
                return true;
            }
        }
        return false;
    }

    public TreeNode getNodeReference(String label) {
        return getNodeReferenceRec(root, label);
    }

    private TreeNode getNodeReferenceRec(TreeNode node, String label) {
        if (node == null) {
            return null;
        }
        if (node.getLabel().equals(label)) {
            return node;
        }
        for (int i = 0; i < node.getChildren().length; i++) {
            if (node.getChildren()[i] != null) {
                TreeNode result = getNodeReferenceRec(node.getChildren()[i], label);
                if (result != null) {
                    return result;
                }
            }
        }
        return null;
    }

    private String readNonEmptyLine(Scanner sc) {
        while (sc.hasNextLine()) {
            String line = sc.nextLine().trim();
            if (!line.isEmpty()) {
                return line;
            }
        }
        return null;
    }

    // ==========================================
    // 以下是专为 Android UI 交互新增的状态管理方法
    // ==========================================

    public TreeNode getCurrentNode() {
        return current;
    }

    public int getHistoryCount() {
        return historyCount;
    }

    // 玩家点击选项后，往前走一步
    public void goForward(int childIndex) {
        if (historyCount == historyArray.length) {
            TreeNode[] newHistory = new TreeNode[historyArray.length * 2];
            for (int i = 0; i < historyArray.length; i++) {
                newHistory[i] = historyArray[i];
            }
            historyArray = newHistory;
        }
        historyArray[historyCount] = current;
        historyCount++;
        current = current.getChildren()[childIndex];
    }

    // 后退一步
    public void goBack() {
        if (historyCount > 0) {
            historyCount--;
            current = historyArray[historyCount];
        }
    }

    // 重新开始
    public void restart() {
        current = root;
        historyCount = 0;
    }


    // ==========================================
    // 专为 Android 增加的遍历文本生成方法 (对应菜单 T)
    // ==========================================
    public String preOrderToString() {
        if (root == null) return "Tree is empty.";
        StringBuilder sb = new StringBuilder();
        sb.append("Traversing the tree in preorder:\n\n");
        preOrderRecToString(root, sb);
        return sb.toString();
    }

    private void preOrderRecToString(TreeNode node, StringBuilder sb) {
        if (node == null) return;

        sb.append("Label: ").append(node.getLabel()).append("\n");
        sb.append("Prompt: ").append(node.getPrompt()).append("\n");
        sb.append("Message: ").append(node.getMessage()).append("\n\n");

        for (int i = 0; i < node.getChildren().length; i++) {
            if (node.getChildren()[i] != null) {
                preOrderRecToString(node.getChildren()[i], sb);
            }
        }
    }

}