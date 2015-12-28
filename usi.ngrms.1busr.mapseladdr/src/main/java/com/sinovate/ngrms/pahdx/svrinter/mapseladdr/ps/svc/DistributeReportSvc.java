package com.sinovate.ngrms.pahdx.svrinter.mapseladdr.ps.svc;

import org.springframework.stereotype.Service;
import com.sinovate.ngrms.pahdx.svrinter.mapseladdr.das.dao.DistributeReportDao;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.*;
import java.util.Calendar;

/**
 * Created by limaple on 11/27/15.
 * 下发产品和设备网格单元[完全/部分]不一致数据报表
 */
@Service
public class DistributeReportSvc {
    private static final Logger logger = LoggerFactory.getLogger(DistributeReportSvc.class);

    @Autowired
    private DistributeReportDao distributeReportDao;

    private String filenameTemp;

    public void distrCompletelyNotMatch() {
        logger.info("======= 开始下发产品和设备网格单元完全不一致数据报表");
        logger.info("------- creating file");
        Calendar time = Calendar.getInstance();
        int year = time.get(Calendar.YEAR);
        int month = time.get(Calendar.MONTH) + 1;
        int day = time.get(Calendar.DATE);
        String filename = "wgwq" + year + (month > 9 ? month : "0" + month) + (day > 9 ? day : "0" + day) + ".txt";
        String fileFullname = "/home/wtxz/" + filename;

        File file = new File(fileFullname);

        Boolean createSuccess;
        try {
            createSuccess = file.createNewFile();
        } catch (IOException e) {
            logger.error("failed to create file: " + fileFullname);
            return;
        }
        if (!createSuccess) {
            logger.error("file: " + fileFullname + " already exists");
            return;
        }

        logger.info("写文件: " + fileFullname);
        distributeReportDao.writeCompletelyFile(fileFullname);

        logger.info("------- upload to ftp server");
        try {
            FileInputStream in = new FileInputStream(file);
            boolean flag = uploadFile("134.64.160.38", 8082, "ahwangge", "ahwangge@2015", "/chanpin", filename, in);
            logger.info("------- " + flag);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            logger.error("!!!!!!! file upload error");
        }
        logger.info("------- file upload finished");

        if (file.delete()) {
            logger.info("------- file deleted");
        } else {
            logger.error("!!!!!!! file delete ERROR");
        }

        logger.info("======= distributing file END");
    }

    public void distrPartlyNotMatch() {
        logger.info("======= 开始下发产品和设备网格单元部分不一致数据报表");
        logger.info("------- creating file");
        Calendar time = Calendar.getInstance();
        int year = time.get(Calendar.YEAR);
        int month = time.get(Calendar.MONTH) + 1;
        int day = time.get(Calendar.DATE);
        String filename = "wgbf" + year + (month > 9 ? month : "0" + month) + (day > 9 ? day : "0" + day) + ".txt";
        String fileFullname = "/home/wtxz/" + filename;

        File file = new File(fileFullname);

        Boolean createSuccess;
        try {
            createSuccess = file.createNewFile();
        } catch (IOException e) {
            logger.error("failed to create file: " + fileFullname);
            return;
        }
        if (!createSuccess) {
            logger.error("file: " + fileFullname + " already exists");
            return;
        }

        logger.info("写文件: " + fileFullname);
        distributeReportDao.writePartlyFile(fileFullname);


        logger.info("------- upload to ftp server");
        try {
            FileInputStream in = new FileInputStream(file);
            boolean flag = uploadFile("134.64.160.38", 8082, "ahwangge", "ahwangge@2015", "/chanpin", filename, in);
            logger.info("------- " + flag);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            logger.error("!!!!!!! file upload error");
        }
        logger.info("------- file upload finished");

        if (file.delete()) {
            logger.info("------- file deleted");
        } else {
            logger.error("!!!!!!! file delete ERROR");
        }

        logger.info("======= distributing file END");
    }

    /**
     * Description: 向FTP服务器上传文件
     *
     * @param url      FTP服务器hostname
     * @param port     FTP服务器端口
     * @param username FTP登录账号
     * @param password FTP登录密码
     * @param path     FTP服务器保存目录
     * @param filename 上传到FTP服务器上的文件名
     * @param input    输入流
     * @return 成功返回true，否则返回false
     */
    public static boolean uploadFile(String url, int port, String username, String password, String path, String filename, InputStream input) {
        boolean success = false;
        FTPClient ftp = new FTPClient();
        try {
            int reply;
            ftp.connect(url, port);//连接FTP服务器
            //如果采用默认端口，可以使用ftp.connect(url)的方式直接连接FTP服务器
            ftp.login(username, password);//登录
            reply = ftp.getReplyCode();
            if (!FTPReply.isPositiveCompletion(reply)) {
                ftp.disconnect();
                return false;
            }
            ftp.changeWorkingDirectory(path);
            ftp.setBufferSize(1024 * 1024 * 10);
            ftp.enterLocalPassiveMode();
            ftp.storeFile(filename, input);

            input.close();
            ftp.logout();
            success = true;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (ftp.isConnected()) {
                try {
                    ftp.disconnect();
                } catch (IOException ioe) {
                    logger.info("ftp链接断开失败");
                }
            }
        }
        return success;
    }
}
