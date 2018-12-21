package com.jianbackup.utils;

import com.jianbackup.model.Ftp;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import java.io.*;
import java.net.SocketException;
import java.util.Date;

/**
 * Created by duanxun on 2018-11-07.
 */
@Slf4j
public class FtpTool {

    public static FTPClient getFTPClient(Ftp ftp, String ftpPath) throws IOException {
        FTPClient ftpClient = new FTPClient();
            ftpClient = new FTPClient();
            ftpClient.connect(ftp.getIp(), ftp.getPort());// 连接FTP服务器
            ftpClient.login(ftp.getUsername(), ftp.getPassword());// 登陆FTP服务器
            ftpClient.setControlEncoding("UTF-8"); // 中文支持
            ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
//            ftpClient.enterLocalPassiveMode();
            if(ftpPath != null){
                ftpClient.changeWorkingDirectory(ftpPath);
            }
            if (!FTPReply.isPositiveCompletion(ftpClient.getReplyCode())) {
                log.info("未连接到FTP，用户名或密码错误。");
                ftpClient.disconnect();
            } else {
                log.info("FTP连接成功。");
            }
        return ftpClient;
    }

    /**
     * 从FTP服务器下载文件
     * @param ftp FTP IP地址 用户名 用户名密码 端口
     * @param ftpPath FTP服务器中文件所在路径 格式： ftptest/aa
     * @param localPath 下载到本地的位置 格式：H:/download
     * @param fileName 文件名称
     */
    public static void downloadFtpFile(Ftp ftp,String ftpPath, String localPath,
                                       String fileName) {

        FTPClient ftpClient = null;

        try {
            ftpClient = getFTPClient(ftp,ftpPath);

            File localFile = new File(localPath + File.separatorChar + fileName);
            OutputStream os = new FileOutputStream(localFile);
            ftpClient.retrieveFile(fileName, os);
            os.close();
            ftpClient.logout();

        } catch (FileNotFoundException e) {
            System.out.println("没有找到" + ftpPath + "文件");
            e.printStackTrace();
        } catch (SocketException e) {
            System.out.println("连接FTP失败.");
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("文件读取错误。");
            e.printStackTrace();
        }

    }

    /**
     * Description: 向FTP服务器上传文件
     * @param ftp FTP IP地址 用户名 用户名密码 端口
     * @param ftpPath  FTP服务器中文件所在路径 格式： ftptest/aa
     * @param fileName ftp文件名称
     * @param input 文件流
     * @return 成功返回true，否则返回false
     */
    public static boolean uploadFile(Ftp ftp, String ftpPath,
                                     String fileName,InputStream input) throws IOException {
        boolean success = false;
        FTPClient ftpClient = null;
        try {
            int reply;
            ftpClient = getFTPClient(ftp,ftpPath);

            reply = ftpClient.getReplyCode();
            if (!FTPReply.isPositiveCompletion(reply)) {
                ftpClient.disconnect();
                return success;
            }


            ftpClient.storeFile(new String(fileName.getBytes("GBK"),"iso-8859-1"), input);

            input.close();
            ftpClient.logout();
            success = true;
        } finally {
            if (ftpClient.isConnected()) {
                try {
                    ftpClient.disconnect();
                } catch (IOException ioe) {
                }
            }
        }
        return success;
    }

    /**
     * 根据时间删除备份数据
     */
    public static void deleteFileByDate(Ftp ftp, String ftpPath, int day) throws IOException {
        FTPClient ftpClient = getFTPClient(ftp,ftpPath);
        FTPFile[] files = ftpClient.listFiles();
        for (int i = 0; i < files.length; i++){
            Date date = files[i].getTimestamp().getTime();
            if(DateUtils.pastDays(date) > day){
                log.info("删除文件" + files[i].getName());
                ftpClient.deleteFile(files[i].getName());
            }
        }
    }
}
