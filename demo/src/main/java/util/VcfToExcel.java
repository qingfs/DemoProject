package util;

import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;


import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * https://www.cnblogs.com/java888/p/12268287.html
 * @Description: 读取Vcf文件并转换为Excel
 * @Author: Tan
 * @CreateDate: 2020/2/5
 **/
public class VcfToExcel {
    public static void main(String[] args) throws Exception {
        long start=System.currentTimeMillis();
        //读取vcf文件
        BufferedReader bufferedReader=new BufferedReader(new FileReader("F:\\新建文件夹\\00002(1).vcf"));
        //计数器 统计当前有多少记录
        int count=0;
        //创建导出Excel数据集合
        List<List<String>> exportList=new ArrayList<>();
        //添加一行 算标题 第一行
        exportList.add(Arrays.asList("编号","姓名","手机号码"));
        //读取的信息
        String info;
        //循环读取
        while ((info=bufferedReader.readLine())!=null){
            //读取到的这一行以FN开头 代表名字
            if(info.startsWith("FN")){
                //截取名字编码部分
             String name=info.substring(info.indexOf(":")+1);
             //名字编码的下一行为手机号也可能是名字编码的剩余字符
             String phone=bufferedReader.readLine();
             //判断是否是=开头 如果是就是名字编码的剩余部分,也拼接到名字,下一行就是手机号
             if(phone.startsWith("=")){
                 name+=phone.substring(1);
                 phone=bufferedReader.readLine();
             }else if("".equals(phone)){
                 phone=bufferedReader.readLine();
             }
             //截取手机号
             phone=phone.substring(phone.indexOf(":")+1);

             //判断名字编码最后一位是否是= 如果是要删除掉
             if(name.charAt(name.length()-1)==61){
                name=  name.substring(0,name.length()-1);
             }
                //将编号 名字进行解码 和手机号 添加到导出数据
                exportList.add(Arrays.asList(Integer.toString(++count),qpDecoding(name),phone));
            }

        }
        //关闭缓存流
        bufferedReader.close();

        //创建hutool工具类的写Excel对象
        ExcelWriter writer = ExcelUtil.getWriter("F:\\新建文件夹\\11d1.xlsx");

        //写入数据 生成Excel
        writer.write(exportList, true);

        //关闭writer，释放内存
        writer.close();

        System.out.println("生成完毕,耗费时间 "+(System.currentTimeMillis()-start));

    }




    //解码方法  如果抛出数组越界异常 可能是数据不规范 检查解码字符串
    public final static String qpDecoding(String str)
    {
        if (str == null)
        {
            return "";
        }
        try
        {
            StringBuffer sb = new StringBuffer(str);
            for (int i = 0; i < sb.length(); i++)
            {
                if (sb.charAt(i) == '\n' && sb.charAt(i - 1) == '=')
                {
                    // 解码这个地方也要修改一下
                    // sb.deleteCharAt(i);
                    sb.deleteCharAt(i - 1);
                }
            }
            str = sb.toString();
            byte[] bytes = str.getBytes("US-ASCII");
            for (int i = 0; i < bytes.length; i++)
            {
                byte b = bytes[i];
                if (b != 95)
                {
                    bytes[i] = b;
                }
                else
                {
                    bytes[i] = 32;
                }
            }
            if (bytes == null)
            {
                return "";
            }
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            for (int i = 0; i < bytes.length; i++)
            {
                int b = bytes[i];
                if (b == '=')
                {
                    try
                    {
                        int u = Character.digit((char) bytes[++i], 16);
                        int l = Character.digit((char) bytes[++i], 16);
                        if (u == -1 || l == -1)
                        {
                            continue;
                        }
                        buffer.write((char) ((u << 4) + l));
                    }
                    catch (ArrayIndexOutOfBoundsException e)
                    {
                        e.printStackTrace();
                    }
                }
                else
                {
                    buffer.write(b);
                }
            }
            return new String(buffer.toByteArray(), "UTF-8");
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return "";
        }
    }

}