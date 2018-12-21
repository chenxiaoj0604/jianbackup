package com.jianbackup.controller;

import com.jianbackup.model.BackupInfo;
import com.jianbackup.model.BackupInfos;
import com.jianbackup.service.BackupService;
import com.jianbackup.utils.DateUtils;
import com.jianbackup.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Date;

/**
 * Created by duanxun on 2018-11-21.
 */
@Slf4j
@Controller
@RequestMapping("/backup")
public class BackupController {

    @Autowired
    private BackupInfos backupInfos;

    @Autowired
    private BackupService backupService;

    /**
     * 吉安HIS数据库备份定时任务
     */
    @Scheduled(cron = "${backuptiming.backupJIANHISDataBase}")
    public void backupJIANHISDataBaseJob() {
        log.info("************吉安HIS数据库备份定时任务");
        for(BackupInfo backupInfo : backupInfos.getJIANHISInfos()){
            backupInfo.setAppendFileName("_" + DateUtils.getDate("yyyyMMdd"));
            R r = backupService.backup(backupInfo);
            backupService.resultDeal(r,backupInfo);
        }
    }

    /**
     * 吉安HIS数据库备份
     */
        @GetMapping("/backupJIANHISDataBase")
    public void backupJIANHISDataBase() {
        log.info("************吉安HIS数据库备份");
        for(BackupInfo backupInfo : backupInfos.getJIANHISInfos()){
            backupInfo.setAppendFileName("_" + DateUtils.getDate("yyyyMMdd"));
            R r = backupService.backup(backupInfo);
            log.info(r.toString());
            backupService.resultDeal(r,backupInfo);
        }
    }


    /**
     * 吉安电子病历备份定时任务
     */
    @Scheduled(cron = "${backuptiming.backupJIANEMRDataBase}")
    public void backupJIANEMRDataBaseJob() {
        log.info("************吉安电子病历备份");
        for(BackupInfo backupInfo : backupInfos.getJIANEMRInfos()){
            backupInfo.setSuffix(".dmp");
            backupInfo.getFileNames().set(0,backupInfo.getStaticFileNames().get(0) + DateUtils.getWeekOfDate(new Date()));
            backupInfo.setAppendFileName("_ja_" + DateUtils.getDate("yyyyMMdd"));
            R r = backupService.backup(backupInfo);
            log.info(r.toString());
            backupService.resultDeal(r,backupInfo);

            backupInfo.setSuffix(".log");
            R logR = backupService.backup(backupInfo);
            log.info(logR.toString());
            backupService.resultDeal(logR,backupInfo);
        }
    }

    /**
     * 吉安电子病历备份
     */
    @GetMapping("/backupJIANEMRDataBase")
    public void backupJIANEMRDataBase() {
        log.info("************吉安电子病历备份");
        for(BackupInfo backupInfo : backupInfos.getJIANEMRInfos()){
            backupInfo.setSuffix(".dmp");
            log.info(DateUtils.getWeekOfDate(new Date()));
            backupInfo.getFileNames().set(0,backupInfo.getStaticFileNames().get(0) + DateUtils.getWeekOfDate(new Date()));
            backupInfo.setAppendFileName("_ja_" + DateUtils.getDate("yyyyMMdd"));
            R r = backupService.backup(backupInfo);
            log.info(r.toString());
            backupService.resultDeal(r,backupInfo);

            backupInfo.setSuffix(".log");
            R logR = backupService.backup(backupInfo);
            log.info(logR.toString());
            backupService.resultDeal(logR,backupInfo);
        }
    }
}
