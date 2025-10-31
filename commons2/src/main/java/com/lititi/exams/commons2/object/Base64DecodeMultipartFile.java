package com.lititi.exams.commons2.object;

import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.Base64;

/**
 * base64转为multipartFile
 */
public class Base64DecodeMultipartFile implements MultipartFile {
    private final byte[] imgContent;

    private final String header;


    public Base64DecodeMultipartFile(byte[] imgContent, String header) {
        this.imgContent = imgContent;
        this.header = header.split(";")[0];
    }


    @Override
    public String getName() {
        return System.currentTimeMillis() + Math.random() + "." + header.split("/")[1];
    }


    @Override
    public String getOriginalFilename() {
        return System.currentTimeMillis() + (int) Math.random() * 10000 + "." + header.split("/")[1];
    }


    @Override
    public String getContentType() {
        return header.split(":")[1];
    }


    @Override
    public boolean isEmpty() {
        return imgContent == null || imgContent.length == 0;
    }


    @Override
    public long getSize() {
        return imgContent.length;
    }


    @Override
    public byte[] getBytes() throws IOException {
        return imgContent;
    }


    @Override
    public InputStream getInputStream() throws IOException {
        return new ByteArrayInputStream(imgContent);
    }


    @Override
    public void transferTo(File dest) throws IOException, IllegalStateException {
        new FileOutputStream(dest).write(imgContent);
    }

    /**
     * * base64转multipartFile
     * *
     * * @param base64
     * * @return
     */
    public static MultipartFile base64Convert(String base64) {
        String[] baseStrs = base64.split(",");
        byte[] b = new byte[0];
        try {
            b = Base64.getDecoder().decode(baseStrs[1]);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Base64DecodeMultipartFile(b, baseStrs[0]);
    }
}
