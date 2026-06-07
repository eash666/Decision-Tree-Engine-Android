package com.XuBoRe.hw4;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {

    private Tree tree;

    // UI 容器
    private LinearLayout layoutMainMenu;
    private LinearLayout layoutHelpSession;

    // 问答界面的组件
    private TextView tvMessage;
    private LinearLayout layoutChoices;
    private Button btnBack;
    private Button btnReturnMenu;

    // 文件选择器
    private ActivityResultLauncher<String> filePickerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // ★ 新增核心功能：在界面加载前，检查并自动初始化自带的决策树
        initializeDefaultTreeIfNeeded();

        layoutMainMenu = findViewById(R.id.layout_main_menu);
        layoutHelpSession = findViewById(R.id.layout_help_session);
        tvMessage = findViewById(R.id.tv_message);
        layoutChoices = findViewById(R.id.layout_choices);
        btnBack = findViewById(R.id.btn_back);
        btnReturnMenu = findViewById(R.id.btn_return_menu);

        Button btnL = findViewById(R.id.btn_menu_L);
        Button btnH = findViewById(R.id.btn_menu_H);
        Button btnT = findViewById(R.id.btn_menu_T);
        TextView btnQ = findViewById(R.id.btn_menu_Q);

        // ================= 菜单 L: Load Tree =================
        filePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        promptToSaveTree(uri);
                    }
                }
        );

        btnL.setOnClickListener(v -> filePickerLauncher.launch("text/plain"));

        // ================= 菜单 H: Help Session =================
        btnH.setOnClickListener(v -> {
            showTreeSelectionDialog(() -> {
                tree.restart();
                layoutMainMenu.setVisibility(View.GONE);
                layoutHelpSession.setVisibility(View.VISIBLE);
                updateUI();
            });
        });

        // ================= 菜单 T: Traverse =================
        btnT.setOnClickListener(v -> {
            showTreeSelectionDialog(() -> {
                new AlertDialog.Builder(this)
                        .setTitle("Preorder Traversal")
                        .setMessage(tree.preOrderToString())
                        .setPositiveButton("OK", null)
                        .show();
            });
        });

        btnQ.setOnClickListener(v -> finishAffinity());

        // 导航按钮
        btnBack.setOnClickListener(v -> {
            tree.goBack();
            updateUI();
        });

        btnReturnMenu.setOnClickListener(v -> {
            layoutHelpSession.setVisibility(View.GONE);
            layoutMainMenu.setVisibility(View.VISIBLE);
        });
    }

    // ★ 新增方法：首次运行全自动释放“修洗衣机”决策树
    private void initializeDefaultTreeIfNeeded() {
        String defaultFileName = "Washing Machine Guide.txt";
        File file = new File(getFilesDir(), defaultFileName);

        // 如果文件不存在，说明 App 是第一次被安装启动
        if (!file.exists()) {
            try {
                // 自动读取自带 assets 目录下的 exampleText.txt
                InputStream is = getAssets().open("exampleText.txt");
                FileOutputStream fos = openFileOutput(defaultFileName, MODE_PRIVATE);

                byte[] buffer = new byte[1024];
                int length;
                while ((length = is.read(buffer)) > 0) {
                    fos.write(buffer, 0, length);
                }

                fos.close();
                is.close();
            } catch (Exception e) {
                e.printStackTrace(); // 如果读取失败，在日志中打印错误
            }
        }
    }

    // 后续的 promptToSaveTree, saveUriToInternalStorage, showTreeSelectionDialog 和 updateUI 方法保持原样即可...
    private void promptToSaveTree(Uri uri) {
        EditText input = new EditText(this);
        input.setHint("e.g. Washing Machine");

        new AlertDialog.Builder(this)
                .setTitle("Name This Decision Tree")
                .setMessage("Save this file to your app's memory as:")
                .setView(input)
                .setPositiveButton("Save", (dialog, which) -> {
                    String treeName = input.getText().toString().trim();
                    if (treeName.isEmpty()) treeName = "Unnamed_Tree_" + System.currentTimeMillis();
                    saveUriToInternalStorage(uri, treeName + ".txt");
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void saveUriToInternalStorage(Uri uri, String fileName) {
        try {
            InputStream is = getContentResolver().openInputStream(uri);
            FileOutputStream fos = openFileOutput(fileName, MODE_PRIVATE);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = is.read(buffer)) > 0) {
                fos.write(buffer, 0, length);
            }
            fos.close();
            if (is != null) is.close();
            Toast.makeText(this, "Tree Saved! You can now select it.", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(this, "Failed to save file.", Toast.LENGTH_SHORT).show();
        }
    }

    private void showTreeSelectionDialog(Runnable onTreeSelectedAction) {
        File[] files = getFilesDir().listFiles((dir, name) -> name.endsWith(".txt"));

        if (files == null || files.length == 0) {
            Toast.makeText(this, "No trees saved. Please LOAD one first.", Toast.LENGTH_SHORT).show();
            return;
        }

        String[] displayNames = new String[files.length];
        for (int i = 0; i < files.length; i++) {
            displayNames[i] = files[i].getName().replace(".txt", "");
        }

        new AlertDialog.Builder(this)
                .setTitle("Select a Decision Tree")
                .setItems(displayNames, (dialog, which) -> {
                    try {
                        FileInputStream fis = openFileInput(files[which].getName());
                        tree = new Tree(fis);
                        fis.close();
                        onTreeSelectedAction.run();
                    } catch (Exception e) {
                        Toast.makeText(this, "Error loading the selected tree.", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void updateUI() {
        if (tree == null) return;
        TreeNode current = tree.getCurrentNode();
        if (current == null) return;

        tvMessage.setText(current.getMessage());
        layoutChoices.removeAllViews();

        btnBack.setVisibility(tree.getHistoryCount() > 0 ? View.VISIBLE : View.GONE);

        if (current.isLeaf()) {
            tvMessage.setText(current.getMessage() + "\n\n✅ Session Complete");
        } else {
            TreeNode[] children = current.getChildren();
            for (int i = 0; i < children.length; i++) {
                if (children[i] != null) {
                    androidx.appcompat.widget.AppCompatButton btn = new androidx.appcompat.widget.AppCompatButton(this);
                    btn.setText((i + 1) + ".  " + children[i].getPrompt());

                    btn.setBackgroundResource(R.drawable.bg_ios_choice);
                    btn.setTextColor(0xFF000000);
                    btn.setAllCaps(false);
                    btn.setTextSize(16);
                    btn.setGravity(android.view.Gravity.CENTER_VERTICAL | android.view.Gravity.START);
                    btn.setPadding(50, 0, 50, 0);

                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            160
                    );
                    params.setMargins(0, 0, 0, 24);
                    btn.setLayoutParams(params);

                    final int choiceIndex = i;
                    btn.setOnClickListener(v -> {
                        tree.goForward(choiceIndex);
                        updateUI();
                    });
                    layoutChoices.addView(btn);
                }
            }
        }
    }
}