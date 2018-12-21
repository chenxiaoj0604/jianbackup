package com.jianbackup.service.impl;

import com.jianbackup.model.BackupInfo;
import com.jianbackup.service.BackupService;
import com.jianbackup.utils.DateUtils;
import com.jianbackup.utils.FtpTool;
import com.jianbackup.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.SocketException;
import java.util.Date;

import static com.jianbackup.utils.FtpTool.deleteFileByDate;
import static com.jianbackup.utils.FtpTool.getFTPClient;

/**
 * Created by duanxun on 2018-11-21.
 */
@Service
@Slf4j
public class BackupServiceImpl implements BackupService {

    @Override
    public R backup(BackupInfo backupInfo) {
        try {
            deleteFileByDate(backupInfo.getBackupTargetFtp(),backupInfo.getBackupTargetFtp().getDir(),backupInfo.getSaveDays());
        } catch (IOException e) {
            return R.error("删除_" + backupInfo.getBackupSourceFtp().getIp() + "_过期文件时读取错误。");
        }
        for(String fileName : backupInfo.getFileNames()){
            String fullFileName = fileName + backupInfo.getSuffix();
            try {
                /**.net方式*/
                FTPClient ftpClient = getFTPClient(backupInfo.getBackupSourceFtp(),backupInfo.getBackupSourceFtp().getDir());

                //判断文件是否存在,大小是否为0
                FTPFile[] ff = ftpClient.listFiles(new String(fullFileName.getBytes("GBK"),"iso-8859-1"));
                log.info("长度 : " + ff.length);
                if(ff.length == 0){
                    return R.error(backupInfo.getBackupSourceFtp().getIp() + "_" + fullFileName + "_找不到当天的备份文件");
                }if(ff[0].getSize() == 0){
                    return R.error(backupInfo.getBackupSourceFtp().getIp() + "_" + fullFileName + "_文件大小为0");
                }
                Date date = DateUtils.parseDate(ftpClient.getModificationTime(new String(fullFileName.getBytes("GBK"),"iso-8859-1")));
                if(date != null){
                    log.info( "date!!" + date.toString());
                    long pastDays = DateUtils.pastDays(date);
                    log.info("pastdays!!!" + pastDays);
                    if(pastDays > 0){
                        return R.error(backupInfo.getBackupSourceFtp().getIp() + "_" + fullFileName + "_文件不是当天生成");
                    }
                }else {
                    return R.error(backupInfo.getBackupSourceFtp().getIp() + "_" + fullFileName + "_无法获取文件最后修改日期(找不到文件)");
                }
                //开始备份
                log.info("获取文件流");
                InputStream is = ftpClient.retrieveFileStream(new String(fullFileName.getBytes("GBK"),"iso-8859-1"));

                log.info("获取完毕");
                log.info("开始上传流");
                FtpTool.uploadFile(backupInfo.getBackupTargetFtp(),backupInfo.getBackupTargetFtp().getDir(),fileName + backupInfo.getAppendFileName() + backupInfo.getSuffix(), is);
                log.info("上传完毕");
                log.info("结束上传"+fullFileName);
                ftpClient.disconnect();
            }catch (FileNotFoundException e) {
                return R.error(backupInfo.getBackupSourceFtp().getIp() + "_" + "没有找到" + fullFileName + "文件");
            } catch (SocketException e) {
                return R.error(backupInfo.getBackupSourceFtp().getIp() + "_" + "连接FTP失败.");
            } catch (IOException e) {
                return R.error(backupInfo.getBackupSourceFtp().getIp() + "_" + fullFileName + "备份过程中文件读取错误。");
            }
        }
        return R.ok("备份成功:" + backupInfo.getBackupSourceFtp().getIp() + "上的" + backupInfo.getFileNames().size() + "个文件已经成功备份到" + backupInfo.getBackupTargetFtp().getIp() + "上");
    }

    @Override
    public void resultDeal(R r,BackupInfo backupInfo){
        String ip = backupInfo.getBackupSourceFtp().getIp();
        if (String.valueOf(r.get("code")).equals("0")) {
            createFile(ip + "_success.txt",String.valueOf(r.get("msg")),backupInfo.getBackupTargetFtp().getIp());
//                String result = uploadForm(ip + "_success.txt",ip + "备份成功",url);
//                log.info("log.info(result);" + result + "!!");
        } else {
            createFile(ip + "_error.txt",String.valueOf(r.get("msg")),backupInfo.getBackupTargetFtp().getIp());
//                String result = uploadForm(ip + "_error.txt",ip + "备份失败",url);
//                log.info("log.info(result);" + result + "!!");
//                JsonParser parse =new JsonParser();
//                JsonObject json= new JsonParser().parse(result).getAsJsonObject();
//                result = json.get("msg").getAsString();
//                todo
//                sendMsg("数据库备份异常监控","这是一条测试数据" + result);
        }

    }

    private Boolean createFile(String fileName,String msg,String targetIp){
        try{
            File file = new File(fileName);
            if (!file.exists()) {
                file.createNewFile();
            }
            PrintStream ps = new PrintStream(new FileOutputStream(file));
            ps.println("这是一条测试数据:" + msg);// todo 往文件里写入字符串
            ps.println("时间:" + DateUtils.getDate("yyyy-MM-dd HH:mm:ss"));
            ps.println("目标服务器:" + targetIp);
            ps.close();
            return true;
        }catch (IOException e){
            e.printStackTrace();
            log.info(e.getMessage());
            return false;
        }
    }

}
