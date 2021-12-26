package utils;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.RegexPool;
import cn.hutool.core.util.ReUtil;
import domain.Candidate;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.io.File;
import java.nio.charset.Charset;
import java.util.List;

public class BasicAction {
    static String targetSchool = FileUtil.readString(FileUtil.file("C:\\Users\\Administrator\\Desktop\\目标院校.txt"), Charset.defaultCharset());

    /**
     * 从页面元素获得候选人详细信息
     *
     * @param element
     * @return
     */
    public static Candidate fromElement(WebElement element) {
        String name = element.findElement(By.cssSelector(".name")).getText().split("\n")[0];
        String activity = element.findElement(By.cssSelector(".name")).getText().split("\n")[1];
        String school = element.findElement(By.cssSelector(".edu-exp-box")).getText();
        String work = element.findElement(By.cssSelector(".work-exp-box")).getText();
        String age = element.findElement(By.cssSelector(".info-labels")).getText().split(" ")[0];
        String salary = element.findElement(By.cssSelector(".salary")).getText();
        String expectWork = element.findElement(By.cssSelector(".expect-box")).getText();

        Candidate candidate = new Candidate();
        candidate.setName(name);
        candidate.setSchool(school);
        candidate.setWork(work);
        candidate.setActivity(activity);
        candidate.setExpectWork(expectWork);
       candidate.setSalary(salary.equals("面议")?null:Integer.valueOf(ReUtil.get("^[0-9]*",salary,0)));
        // candidate.setAge(Integer.valueOf(age.replace("岁","")));
        return candidate;
    }

    public static void main(String[] args) {
        String str = FileUtil.readString(FileUtil.file("C:\\Users\\Administrator\\Desktop\\新建文本文档.txt"), Charset.defaultCharset());
        System.out.println(str.split("\n")[0]);
//        List<String> strings = FileUtil.readLines(FileUtil.file("C:\\Users\\Administrator\\Desktop\\新建文本文档.txt"), Charset.defaultCharset());
//        System.out.println(str);
//        System.out.println("name : " + strings.get(2));
//        System.out.println("age : " + strings.get(4));
//        System.out.println("school : " + strings.get(2));
//        System.out.println("work : " + strings.get(2));
    }

    public static boolean isTarget(Candidate candidate) {
        // 院校符合
        try {
            String topSchool = ReUtil.get("[\u4e00-\u9fa5]*[学院|大学]", candidate.getSchool(), 0);
            if(candidate.getSchool()==null || topSchool==null){
                System.out.println("数据异常："+candidate);
                return false;
            }
            if(candidate.getSchool().contains("大专") || !targetSchool.contains(topSchool)){
                return false;
            }
            //没有中兴经历--同时也别有大厂经历
            if(candidate.getWork().contains("中兴") || candidate.getWork().contains("浩鲸")|| candidate.getWork().contains("阿里")|| candidate.getWork().contains("蚂蚁")|| candidate.getWork().contains("网易")|| candidate.getWork().contains("华为")){
                return false;
            }
            //期望薪资不高--专注搞d2
            if(candidate.getSalary()!=null &&candidate.getSalary()>20){
                return false;
            }
            // 期望职位为java
            if(!candidate.getExpectWork().equals("求职期望 : 杭州 · Java")){
                return false;
            }
        }catch (Exception e){
                e.printStackTrace();
            System.out.println("空指针："+candidate);
        }

        return true;

    }
}
