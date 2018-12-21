package com.jianbackup.service;

import com.jianbackup.model.BackupInfo;
import com.jianbackup.utils.R;

/**
 * Created by duanxun on 2018-11-21.
 */
public interface BackupService {

    /**
     * 备份
     * @param backupInfo
     * @return
     */
    R backup(BackupInfo backupInfo);

    /**
     * 结果处理
     * @param r
     */
    void resultDeal(R r, BackupInfo backupInfo);

}
