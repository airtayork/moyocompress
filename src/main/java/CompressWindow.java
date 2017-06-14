import com.tinify.Source;
import com.tinify.Tinify;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 *
 * Created by airtayork on 6/14/17.
 */
public class CompressWindow extends JFrame {

    String dirStr;

    JPanel panel;
    JTextField tinyPngKeyTextField;
    JLabel uncompressedDirLabel;
    JLabel compressedDirLabel;
    JLabel progressLabel;

    public CompressWindow(){

        this.setBounds(400, 200, 800, 400);
        this.setVisible(true);
        this.setTitle("Moyo image compress tool");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        panel = new JPanel();
        panel.setLayout(null);

        Container container = this.getContentPane();
        container.add(panel);

        JLabel keyLabel = new JLabel();
        keyLabel.setBounds(150, 30, 40, 30);
        keyLabel.setText("Key:");
        panel.add(keyLabel);

        tinyPngKeyTextField = new JTextField();
        tinyPngKeyTextField.setBounds(190, 30, 500, 30);
        tinyPngKeyTextField.setText("PtcBtLZ-2RbefRm_rpIxB5Dn2c3Hmqek");
        panel.add(tinyPngKeyTextField);

        JButton openBtn = new JButton("选择文件");
        openBtn.setBounds(150, 80, 100, 50);
        panel.add(openBtn);
        openBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                JFileChooser jfc=new JFileChooser();
                jfc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES );
                jfc.showDialog(new JLabel(), "选择");
                File file=jfc.getSelectedFile();
                if(file.isDirectory()){
                    System.out.println("文件夹:"+file.getAbsolutePath());
                }else if(file.isFile()){
                    System.out.println("文件:"+file.getAbsolutePath());
                }
                System.out.println(jfc.getSelectedFile().getName());

                dirStr = file.getAbsolutePath();

                new Thread() {
                    @Override
                    public void run() {
                        uncompressedDirLabel.setText("未压缩图片路径：" + dirStr);
                    }
                }.start();
            }
        });

        uncompressedDirLabel = new JLabel();
        uncompressedDirLabel.setBounds(150, 130, 650, 20);
        uncompressedDirLabel.setText("未压缩图片路径：");
        panel.add(uncompressedDirLabel);

        compressedDirLabel = new JLabel();
        compressedDirLabel.setBounds(150, 150, 650, 20);
        compressedDirLabel.setText("压缩后图片路径：");
        panel.add(compressedDirLabel);

        final JButton compressBtn = new JButton("开始压缩");
        compressBtn.setBounds(150, 200, 100, 50);
        panel.add(compressBtn);
        compressBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                Tinify.setKey(tinyPngKeyTextField.getText().trim());

                new Thread() {
                    @Override
                    public void run() {
                        compress();
                    }
                }.start();
            }
        });

        progressLabel = new JLabel();
        progressLabel.setBounds(150, 280, 100, 50);
        progressLabel.setText("当前进度：0/0");
        panel.add(progressLabel);

        panel.updateUI();
    }

    private void compress() {
        Source source = null;
        try {

            List<File> files = listFiles(new File(dirStr));

            final int totalSize = files.size();
            progressLabel.setText("当前进度：" + 0 + "/" + totalSize);
            progressLabel.updateUI();
            panel.updateUI();
            CompressWindow.this.invalidate();

            for (int i = 0; i < files.size(); i ++) {
                File f = files.get(i);
                String path = f.getAbsolutePath();
                String parentPath = f.getParentFile().getAbsolutePath();

                final String newFolderPath = parentPath + File.separator + "moyo_compressed";
                File theDir = new File(newFolderPath);

                new Thread() {
                    @Override
                    public void run() {
                        compressedDirLabel.setText("压缩后图片路径：" + newFolderPath);
                    }
                }.start();

                // if the directory does not exist, create it
                if (!theDir.exists()) {
                    System.out.println("creating directory: " + theDir.getName());
                    boolean result = false;

                    try{
                        theDir.mkdir();
                        result = true;
                    }
                    catch(SecurityException se){
                        //handle it
                    }
                    if(result) {
                        System.out.println("DIR created");
                    }
                }

                String newName = f.getName();
                String newPath = newFolderPath + File.separator + newName;

                source = Tinify.fromFile(path);
                source.toFile(newPath);

                final String finishedCount = String.valueOf(i + 1);

                progressLabel.setText("当前进度：" + finishedCount + "/" + totalSize);
                progressLabel.updateUI();
                panel.updateUI();
                CompressWindow.this.invalidate();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private List<File> listFiles(File root){
        List<File> files = new ArrayList<File>();
        listFiles(files, root);
        return files;
    }

    private void listFiles(List<File> files, File dir){
        File[] listFiles = dir.listFiles();
        for(File f: listFiles){
            if(f.isFile()){
                files.add(f);
            }else if(f.isDirectory()){
                listFiles(files, f);
            }
        }
    }

}
