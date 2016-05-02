package com.yang.rungang.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.widget.ImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 文件工具类
 * Created by 洋 on 2016/4/25.
 */
public class FileUtil {

    /**
     * 获取拍照存储图片路径
     * @param bitmap
     * @return
     */
    public static String saveBitmapToFile(Bitmap bitmap){
        String picPath=null;
        FileOutputStream fileOutputStream = null;
        try {
            // 获取 SD 卡根目录
            String saveDir = Environment.getExternalStorageDirectory() + "/headbitmap";
            // 新建目录
            File dir = new File(saveDir);
            if (! dir.exists()) dir.mkdir();
            // 生成文件名
            SimpleDateFormat t = new SimpleDateFormat("yyyyMMddssSSS");
            String filename = "Head" + (t.format(new Date())) + ".jpg";
            // 新建文件
            File file = new File(saveDir, filename);
            // 打开文件输出流
            fileOutputStream = new FileOutputStream(file);
            // 生成图片文件
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
            // 相片的完整路径
            picPath = file.getPath();

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
            if (fileOutputStream != null) {
                try {
                        fileOutputStream.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        return  picPath;
    }

    /**
     * 根据路径，从文件中获取图片
     * @param path
     * @return
     */
    public static Bitmap getBitmapFromFile(String path) {

        Bitmap bitmap = null;

        File file = new File(path);

        if(file.exists()) {
            bitmap = BitmapFactory.decodeFile(path);
        }

        return bitmap;
    }

}
