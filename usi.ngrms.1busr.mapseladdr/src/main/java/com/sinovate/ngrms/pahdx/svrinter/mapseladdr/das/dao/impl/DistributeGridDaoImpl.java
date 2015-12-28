package com.sinovate.ngrms.pahdx.svrinter.mapseladdr.das.dao.impl;

import com.sinovate.ngrms.pahdx.svrinter.mapseladdr.das.dao.DistributeGridDao;
import com.sun.org.apache.xpath.internal.operations.Bool;
import org.apache.commons.io.IOUtils;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;

/**
 * Created by limaple on 10/23/15.
 */
@Repository
public class DistributeGridDaoImpl implements DistributeGridDao {

    private static final Logger logger = LoggerFactory.getLogger(DistributeGridDaoImpl.class);

    @Autowired
    @Qualifier("jdbcTemplate_inv")
    private JdbcTemplate jdbcTemplate;

    private String path = "/home/wtxz/";
    private String filenameTemp;
    private String filename;

    @Override
    public void distribute() {
        logger.info("======= distributing grid file START");
        logger.info("------- creating file");
        Calendar time = Calendar.getInstance();
        int year = time.get(Calendar.YEAR);
        int month = time.get(Calendar.MONTH) + 1;
        int day = time.get(Calendar.DATE);
        filename = "wg" + year + (month > 9 ? month : "0" + month) + (day > 9 ? day : "0" + day) + ".txt";

        Boolean notcreated = false;
        try {
            notcreated = creatTxtFile(filename);
        } catch (IOException e) {
            logger.error("!!!!!!! error creating grid file");
        }

        if (!notcreated) {
            logger.warn("!!!!!!! file exists");
        } else {
            StringBuffer sb = new StringBuffer();
            sb.append("area_eid;area_name;subarea_eid;subarea_name;yyb_name;wangge_dalei;wangge_xiaolei;wangge_id;wangge_name;wangge_zuobiao\n");
            jdbcTemplate.query("select * from wangge_unit g1 where rowid = (select max(rowid) from wangge_unit g2 where g1.wangge_id = g2.wangge_id) order by wangge_id", rs -> {
                sb.append(rs.getInt("area_eid")).append(";")
                        .append(rs.getString("area_name")).append(";")
                        .append(rs.getInt("subarea_eid")).append(";")
                        .append(rs.getString("subarea_name")).append(";")
                        .append(rs.getString("yyb_name")).append(";")
                        .append(rs.getString("wangge_dalei")).append(";")
                        .append(rs.getString("wangge_xiaolei")).append(";")
                        .append(rs.getString("wangge_id")).append(";")
                        .append(rs.getString("wangge_name")).append(";")
                        .append(rs.getString("wangge_zuobiao")).append("\r\n");

            });
            try {
                writeTxtFile(sb.toString());
                logger.info("------- file writen");
            } catch (IOException e) {
                logger.error("!!!!!!!error writing grid file");
            }

            logger.info("------- upload to ftp server");
            try {
                FileInputStream in=new FileInputStream(new File(filenameTemp));
                boolean flag = uploadFile("134.64.160.38", 8082, "ahwangge", "ahwangge@2015", "/", filename, in);
                logger.info("------- " + flag);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                logger.error("!!!!!!! file upload error");
            }
            logger.info("------- file upload finished");
            try {
                File f = new File(filenameTemp);
                if(f.delete()) {
                    logger.info("------- file deleted");
                }
            } catch (Exception e) {
                logger.error("!!!!!!! file delete ERROR");
            }

            logger.info("======= distributing grid file END");
        }
    }

    /**
     * 创建文件
     *
     * @throws IOException
     */
    public boolean creatTxtFile(String name) throws IOException {
        boolean flag = false;
        filenameTemp = path + name;
        File filename = new File(filenameTemp);
        if (!filename.exists()) {
            filename.createNewFile();
            flag = true;
        }
        return flag;
    }

    /**
     * 写文件
     *
     * @param newStr 新内容
     * @throws IOException
     */
    public boolean writeTxtFile(String newStr) throws IOException {
        // 先读取原有文件内容，然后进行写入操作
        boolean flag = false;
        String temp = "";

        FileInputStream fis = null;
        InputStreamReader isr = null;
        BufferedReader br = null;

        FileOutputStream fos = null;
        PrintWriter pw = null;
        try {
            // 文件路径
            File file = new File(filenameTemp);
            // 将文件读入输入流
            fis = new FileInputStream(file);
            isr = new InputStreamReader(fis);
            br = new BufferedReader(isr);
            StringBuffer buf = new StringBuffer();

            // 保存该文件原有的内容
            for (int j = 1; (temp = br.readLine()) != null; j++) {
                buf = buf.append(temp);
                // System.getProperty("line.separator")
                // 行与行之间的分隔符 相当于“\n”
                buf = buf.append(System.getProperty("line.separator"));
            }
            buf.append(newStr);

            fos = new FileOutputStream(file);
            pw = new PrintWriter(fos);
            pw.write(buf.toString().toCharArray());
            pw.flush();
            flag = true;
        } catch (IOException e1) {
            logger.error("文件写入错误");
        } finally {
            if (pw != null) {
                pw.close();
            }
            if (fos != null) {
                fos.close();
            }
            if (br != null) {
                br.close();
            }
            if (isr != null) {
                isr.close();
            }
            if (fis != null) {
                fis.close();
            }
        }
        return flag;
    }

    /*public void uploadFile() {
        FTPClient ftpClient = new FTPClient();
        FileInputStream fis = null;

        try {
            ftpClient.connect("134.64.105.155");
            ftpClient.login("ftp_zymx", "Hello@163");
            ftpClient.setControlEncoding("UTF-8");

            File srcFile = new File(filenameTemp);
            logger.info(filenameTemp);
            fis = new FileInputStream(srcFile);
            //设置上传目录
            ftpClient.changeWorkingDirectory("/");
            //设置文件类型（二进制）
            ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
            logger.info(filename);
            ftpClient.storeFile(filename, fis);
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("FTP客户端出错！");
        } finally {
            IOUtils.closeQuietly(fis);
            try {
                ftpClient.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
                logger.error("关闭FTP连接发生异常！");
            }
        }
    }*/
    /**
     * Description: 向FTP服务器上传文件
     * @Version1.0 Jul 27, 2008 4:31:09 PM by 崔红保（cuihongbao@d-heaven.com）创建
     * @param url FTP服务器hostname
     * @param port FTP服务器端口
     * @param username FTP登录账号
     * @param password FTP登录密码
     * @param path FTP服务器保存目录
     * @param filename 上传到FTP服务器上的文件名
     * @param input 输入流
     * @return 成功返回true，否则返回false
     */
    public static boolean uploadFile(String url,int port,String username, String password, String path, String filename, InputStream input) {
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
                return success;
            }
            ftp.changeWorkingDirectory(path);
            ftp.setBufferSize(1024*1024*10);
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
                }
            }
        }
        return success;
    }
}
