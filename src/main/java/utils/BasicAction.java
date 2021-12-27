package utils;

import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.RegexPool;
import cn.hutool.core.lang.hash.Hash;
import cn.hutool.core.util.ReUtil;
import domain.Candidate;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.io.File;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;

public class BasicAction {
    static String targetSchool = FileUtil.readString(FileUtil.file("目标院校.txt"), Charset.defaultCharset());

    /**
     * 从页面元素获得候选人详细信息
     *
     * @param element
     * @return
     */
    public static Candidate fromElement(WebElement element) {
        String name = element.findElement(By.cssSelector(".name")).getText().split("\n")[0];
        String activity = "";
        try {
            activity = element.findElement(By.cssSelector(".name")).getText().split("\n")[1];
        } catch (Exception e) {
            System.out.println(name + " 没有活跃程度");
        }

        String school = element.findElement(By.cssSelector(".edu-exp-box")).getText();
        // 可能没有工作经历
        String work = "";
        try {
            work = element.findElement(By.cssSelector(".work-exp-box")).getText();
        } catch (Exception e) {
            //System.out.println(name + " 没有工作经历");
        }
        String age = element.findElement(By.cssSelector(".info-labels")).getText().split(" ")[0];
        String year = element.findElement(By.cssSelector(".info-labels")).getText().split(" ")[1];
        String salary = element.findElement(By.cssSelector(".salary")).getText();
        String expectWork = element.findElement(By.cssSelector(".expect-box")).getText();

        Candidate candidate = new Candidate();
        candidate.setName(name);
        candidate.setYear(year);
        candidate.setSchool(school);
        candidate.setWork(work);
        candidate.setActivity(activity);
        candidate.setExpectWork(expectWork);
        candidate.setSalary(salary.equals("面议") ? null : Integer.valueOf(ReUtil.get("^[0-9]*", salary, 0)));
        // candidate.setAge(Integer.valueOf(age.replace("岁","")));
        return candidate;
    }

    public static void main(String[] args) throws InterruptedException {
        System.out.println(LocalDateTimeUtil.now());
        Thread.sleep(60000);
        System.out.println(LocalDateTimeUtil.now());
    }

    public static boolean isTarget(Candidate candidate) {
        // 院校符合
        String topSchool = ReUtil.get("[\u4e00-\u9fa5]*[学院|大学]", candidate.getSchool(), 0);
        if (topSchool == null) {
            System.out.println("非法数据" + candidate);
            return false;
        }
//        if (candidate.getYear() < 5) {
//           //D2 必须目标
//            if (!targetSchool.contains(topSchool) || candidate.getSchool().contains("大专") || candidate.getSchool().contains("非全日制")) {
//                return false;
//            }
//        } else {
//            //D3 可以非目标
//            if (candidate.getSchool().contains("大专") || candidate.getSchool().contains("学院") || candidate.getSchool().contains("非全日制")) {
//                return false;
//            }
//        }
        if (!(candidate.getYear().equals("20年应届生") || candidate.getYear().equals("21年应届生") || candidate.getYear().equals("19年应届生")
                || candidate.getYear().equals("1年")
                || candidate.getYear().equals("2年")
                || candidate.getYear().equals("3年")
                || candidate.getYear().equals("4年")
                || candidate.getYear().equals("5年")
                || candidate.getYear().equals("6年")
                || candidate.getYear().equals("7年"))) {
            return false;
        }
        if (candidate.getSchool().contains("大专") || candidate.getSchool().contains("非全日制") || !targetSchool.contains(topSchool)) {
            return false;
        }
        //没有中兴经历--同时也别有大厂经历
        if (candidate.getWork().contains("中兴") || candidate.getWork().contains("浩鲸") || candidate.getWork().contains("阿里") || candidate.getWork().contains("蚂蚁") || candidate.getWork().contains("网易") || candidate.getWork().contains("华为")) {
            return false;
        }
        //期望薪资不高--专注搞d2
        if (candidate.getYear().equals("5年")
                || candidate.getYear().equals("6年")
                || candidate.getYear().equals("7年")) {
            if (candidate.getSalary() != null && candidate.getSalary() > 25) {
                return false;
            }
        } else {
            if (candidate.getSalary() != null && candidate.getSalary() > 20) {
                return false;
            }
        }

//        //期望薪资不高--专注搞d2
//        if (candidate.getSalary() != null && candidate.getSalary() > salaryMap.get(candidate.getYear())) {
//            return false;
//        }
        //活跃度
        if (!candidate.getActivity().equals("刚刚活跃") && !candidate.getActivity().equals("今日活跃") && !candidate.getActivity().equals("3日内活跃")) {
            return false;
        }
        // 期望职位为java
        if (!candidate.getExpectWork().equals("求职期望 : 杭州 · Java") && !candidate.getExpectWork().equals("求职期望 : 杭州 · web前端") &&
                !candidate.getExpectWork().equals("求职期望 : 杭州 · 后端开发") &&
                !candidate.getExpectWork().equals("求职期望 : 杭州 · 前端开发") &&
                !candidate.getExpectWork().equals("求职期望 : 杭州 · 移动web前端") &&
                !candidate.getExpectWork().equals("求职期望 : 杭州 · 全栈工程师") &&
                !candidate.getExpectWork().equals("求职期望 : 杭州 · 数据开发") &&
                !candidate.getExpectWork().equals("求职期望 : 杭州 · JavaScript") &&
                !candidate.getExpectWork().equals("求职期望 : 杭州 · 大数据开发工程师")) {

            System.out.println("期望职位不符合" + candidate);
            return false;
        }
        return true;

    }
}
