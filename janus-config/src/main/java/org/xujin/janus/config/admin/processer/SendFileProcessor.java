package org.xujin.janus.config.admin.processer;

import org.xujin.janus.client.cmo.SendFileCmd;
import org.xujin.janus.client.cmo.SendFileDTO;
import org.xujin.janus.config.ConfigRepo;
import org.xujin.janus.config.app.DynamicClassConfig;
import org.xujin.janus.config.observer.ClassFileChangedObserver;
import org.xujin.janus.damon.exchange.JanusCmdMsg;

import java.io.File;
import java.io.FilenameFilter;
import java.io.PrintWriter;

/**
 * admin 推送过来新文件处理类;如果文件名重复，则替换旧文件
 * 分为Filter文件和Predicate文件，处理逻辑基本一样，文件存放路径不一样
 *
 * @author: gan
 * @date: 2020/5/23
 */
public class SendFileProcessor extends AbstractProcessor {


    public static final String FILTER = "filter";
    public static final String PREDICATE = "predicate";

    @Override
    public JanusCmdMsg doExecute(Object payload) {
        if (payload == null) {
            throw new RuntimeException("payload cannot be null");
        }
        SendFileCmd sendFileCmd = (SendFileCmd) payload;
        if (sendFileCmd == null || sendFileCmd.getSendFileDTOS().isEmpty()) {
            return successResponse();
        }
        sendFileCmd.getSendFileDTOS().stream().forEach(e -> receiveFile(e));
        ClassFileChangedObserver.notifyListeners();
        return successResponse();
    }

    private void receiveFile(SendFileDTO sendFileCmd) {
        if (sendFileCmd.getFileName() == null || sendFileCmd.getFileContent() == null || sendFileCmd.getFileType() == null) {
            throw new RuntimeException("fileName、fileContent、fileType cannot be null");
        }
        DynamicClassConfig dynamicClassConfig = ConfigRepo.getServerConfig().getDynamicClass();
        //check file type
        String filePath;
        if (FILTER.equalsIgnoreCase(sendFileCmd.getFileType())) {
            filePath = dynamicClassConfig.getFiltersPath();
        } else if (PREDICATE.equalsIgnoreCase(sendFileCmd.getFileType())) {
            filePath = dynamicClassConfig.getPredicatesPath();
        } else {
            throw new RuntimeException("unSupported filter type");
        }

        //check file name
        if (!sendFileCmd.getFileName().endsWith(dynamicClassConfig.getJavaExtendName())
                && !sendFileCmd.getFileName().endsWith(dynamicClassConfig.getGroovyExtendName())) {
            throw new RuntimeException("unSupported filter extension name");
        }
        createOrReplaceFile(filePath, sendFileCmd.getFileName(), sendFileCmd.getFileContent());
    }

    private void createOrReplaceFile(String path, String filterName, String content) {
        java.io.File directory = new java.io.File(path);
        if (!directory.exists()) {
            if (!directory.mkdirs()) {
                throw new RuntimeException(path + " create fail");
            }
        }
        if (!directory.setReadable(true)) {
            throw new RuntimeException(path + " cannot read");
        }
        if (!directory.setWritable(true)) {
            throw new RuntimeException(path + " cannot write");
        }
        if (!directory.setExecutable(true)) {
            throw new RuntimeException(path + " cannot execute");
        }
        java.io.File[] existFiles = directory.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.equalsIgnoreCase(filterName);
            }
        });
        //如果文件已经存在，删除
        if (existFiles.length > 0) {
            for (java.io.File file : existFiles) {
                file.delete();
            }
        }

        java.io.File newFilterFile = new java.io.File(path + "/" + filterName);
        try {
            newFilterFile.createNewFile();
            try (PrintWriter out = new PrintWriter(newFilterFile)) {
                out.println(content);
            }
        } catch (Exception ex) {
            throw new RuntimeException(newFilterFile + "create error", ex);
        }
    }
}
