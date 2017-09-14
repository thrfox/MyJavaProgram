package my.spider;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TiebaSpider {

    //通过URl获取HTML源码,传入一个url地址
    public String getHTML(URL url){
        InputStreamReader isr = null;
            BufferedReader br = null;
            //定义HTML
            String Page = null;

        //尝试取得URL地址
        try {
            //通过URL获得HTML源码,读取到Java中
            isr = new InputStreamReader(url.openStream());
            br = new BufferedReader(isr);
            //创建拼接对象
            StringBuilder sb = new StringBuilder();
            //通过循环源码，拼接到str中
            while (br.readLine() != null){
                sb.append(br.readLine());
            }
            //转换成String
            Page = sb.toString();
        } catch (MalformedURLException e) {
            //输入URL错误信息
            System.err.println("无法取得网页");
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            //关闭资源
            try {
                isr.close();
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //返回通过URL取得的HTML源码
        return Page;
    }

    //通过正则表达式，取得正文部分
    public List<String> getContent(String html){
        //定义取得正文的正则
        //  \"为转义，即"
        //查找:(里的),匹配的是<div id="post_content[任意字符，直到遇到>]>([任意字符，直到遇到</div>，
        // 且每次匹配成功可以通过group()调用])</div>"
        String regex = "<div id=\"post_content.*?>(.*?)</div>";

        //定义正则对象
        Pattern re = Pattern.compile(regex,Pattern.MULTILINE);    //指定多行匹配模式
        //将正则在HTML源码上匹配
        Matcher m = re.matcher(html);

        List<String> content = new ArrayList<>();
        String replaceOk = null;
        //如果匹配找到
        while (m.find()){
            //对取得的正文内容进行处理，去除无用标签，m.group(1)指的是(.*?)匹配的结果
            replaceOk = replaceTag(m.group(1));
            //将处理好的内容装入list
            content.add(replaceOk);
        }
        System.out.println(content.toString());
        return content;
    }

    //工具方法，取得正文后，删除掉正文源码中的img等标签
    public String replaceTag(String str){
        //正则匹配无用标签，img或者a
        String unusedTag = "<img.*?>|<a.*?>|</a>|<div.*?>";
        //正则匹配换行标签,br或者p
        String lineTag = "<br>|<p>";

        //定义替换后的结果
        String ok = str;
        //根据正则表达将无用标签全部替换成空串
        ok = ok.replaceAll(unusedTag,"");
        //换行便签替换换行转义符
        ok = ok.replaceAll(lineTag,"\n");
        //去空格与制表符
        ok = ok.trim();
        //在每层楼结尾，换行；
        ok = ok + "\n";

        return ok;
    }

    public void writeToTxt(List<String> contentList){
        FileWriter fw = null;
        try {
            //写入当前路径
            fw = new FileWriter("爬虫.txt");
            //循环取得list，并写入txt
            for (String content:contentList
                 ) {
                fw.write(content);

            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                fw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws MalformedURLException {
        TiebaSpider spider = new TiebaSpider();
        //传入帖子链接
        URL url = new URL("http://tieba.baidu.com/p/5273996147");
        //解析URL获得HTML
        String HTML = spider.getHTML(url);
        //匹配正文，装入list
        List<String> content = spider.getContent(HTML);
        //list取出，存入txt
        spider.writeToTxt(content);
    }

}
