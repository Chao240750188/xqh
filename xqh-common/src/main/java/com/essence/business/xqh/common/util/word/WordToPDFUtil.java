package com.essence.business.xqh.common.util.word;

import com.aspose.words.Document;
import com.aspose.words.License;
import com.aspose.words.SaveFormat;

import java.io.*;

public class WordToPDFUtil {

    /**
     * 获取license
     *
     * @return
     */
    public  boolean getLicense() {
         InputStream license;
        boolean result = false;
        try {
            //license.xml文件的存放路径
            String licenceFilePath=this.getClass().getResource("/").getPath()+"license.xml";
            license = new FileInputStream(licenceFilePath);// 凭证文件
            License aposeLic = new License();
            aposeLic.setLicense(license);
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 将word转为pdf
     * @param
     */
    public  void wordToPdf(String docPath,String pdfPath) {
        // 验证License
        if (!getLicense()) {
            return;
        }
        InputStream fileInput =null;
        FileOutputStream fileOS = null;
        try {
            fileInput = new FileInputStream(docPath);// 待处理的文件
            File outputFile = new File(pdfPath);// 输出路径

            long old = System.currentTimeMillis();
            Document doc = new Document(fileInput);
            fileOS = new FileOutputStream(outputFile);
            doc.save(fileOS, SaveFormat.PDF);

            long now = System.currentTimeMillis();
            System.out.println("共耗时：" + ((now - old) / 1000.0) + "秒\n\n" + "文件保存在:" + outputFile.getPath());
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            try {
                fileInput.close();
                fileOS.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
