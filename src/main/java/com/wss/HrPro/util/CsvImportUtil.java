package com.wss.HrPro.util;

import lombok.Data;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Data
public class CsvImportUtil {


    //上传文件的路径
    private final static URL PATH = Thread.currentThread().getContextClassLoader().getResource("");




    /**
     * @return File  一般文件类型
     * @Description 上传文件的文件类型
     * @Param multipartFile
     **/
    public static File uploadFile(MultipartFile multipartFile) {
        // 获 取上传 路径
        String path = PATH.getPath() + multipartFile.getOriginalFilename();
        try {
            // 通过将给定的路径名字符串转换为抽象路径名来创建新的 File实例
            File file = new File(path);
            // 此抽象路径名表示的文件或目录是否存在
            if (!file.getParentFile().exists()) {
                // 创建由此抽象路径名命名的目录，包括任何必需但不存在的父目录
                file.getParentFile().mkdirs();
            }
            // 转换为一般file 文件
            multipartFile.transferTo(file);

            return file;
        } catch (IOException e) {

            e.printStackTrace();
            return null;
        }

    }

    /**
     * @return List<List<String>>
     * @Description 读取CSV文件的内容（不含表头）
     * @Param filePath 文件存储路径，colNum 列数
     **/
    public static List<List<String>> readCSV(String filePath, int colNum) {
        BufferedReader bufferedReader = null;
        InputStreamReader inputStreamReader = null;
        FileInputStream fileInputStream = null;

        try {
            fileInputStream = new FileInputStream(filePath);
            inputStreamReader = new InputStreamReader(fileInputStream);
            bufferedReader = new BufferedReader(inputStreamReader);

            CSVParser parser = CSVFormat.DEFAULT.parse(bufferedReader);


            //  表内容集合，外层 List为行的集合，内层 List为字段集合
            List<List<String>> values = new ArrayList<>();


            int rowIndex = 0;
            // 读取文件每行内容

            for (CSVRecord record : parser.getRecords()) {
                //  跳过表头
                if (rowIndex == 0) {
                    rowIndex++;
                    continue;
                }
                // 判断下角标是否越界
                if(colNum>record.size()){
                    // 返回空集合
                    return values;
                }
                //  每行的内容
                List<String> value = new ArrayList<>();
                for (int i = 0; i < colNum; i++) {
                    value.add(record.get(i));
                }
                values.add(value);
                rowIndex++;
            }
            return values;
        } catch (IOException e) {
            e.printStackTrace();

        }finally {
            //关闭流
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (inputStreamReader != null) {
                try {
                    inputStreamReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    public static List<CSVRecord> read(final String csvFile, final String[] fileHeader, boolean skipHeader)
            throws IOException {
        CSVFormat format;

        if (skipHeader) {
            // 这里显式地配置一下CSV文件的Header，然后设置跳过Header（要不然读的时候会把头也当成一条记录）
            format = CSVFormat.DEFAULT.withHeader(fileHeader).withFirstRecordAsHeader().withIgnoreHeaderCase()
                    .withTrim();
            // format = CSVFormat.DEFAULT.withHeader(fileHeader)
            // .withIgnoreHeaderCase().withTrim().withSkipHeaderRecord();
        } else {
            format = CSVFormat.DEFAULT.withHeader(fileHeader).withIgnoreHeaderCase().withTrim();
        }
        Reader reader = Files.newBufferedReader(Paths.get(csvFile));

        CSVParser csvParser = new CSVParser(reader, format);
        List<CSVRecord> records = csvParser.getRecords();
        Iterator<CSVRecord> iterator = records.iterator();
        while(iterator.hasNext()){
            CSVRecord record = iterator.next();
            if (record.get(0).startsWith("#"))
                iterator.remove();
        }

        return records;
    }


    public static List<CSVRecord> read(final String csvFile, final String[] fileHeader)
            throws IOException{
        return read(csvFile, fileHeader, true);
    }

    public static List<CSVRecord> read(final String csvFile)
            throws IOException{
        return read(csvFile, null, true);
    }
}
