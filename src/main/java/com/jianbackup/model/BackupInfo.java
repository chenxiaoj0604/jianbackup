package com.jianbackup.model;

import lombok.Data;

import java.util.List;

/**
 * Created by duanxun on 2018-11-02.
 */
@Data
public class BackupInfo {

    private Ftp BackupSourceFtp;

    private Ftp BackupTargetFtp;

    private List<String> fileNames;

    private String suffix;

    private int saveDays;

    private String appendFileName;

    private List<String> staticFileNames;

}
