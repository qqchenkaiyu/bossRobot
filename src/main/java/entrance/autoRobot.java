package entrance;

import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.RandomUtil;
import domain.Candidate;
import lombok.SneakyThrows;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import utils.BasicAction;

import java.awt.*;
import java.util.Collections;
import java.util.List;

public class autoRobot {
    static WebDriver driver;
    static JavascriptExecutor frame;

    static {
        ChromeOptions chromeOptions = new ChromeOptions();
        //chromeOptions.add_experimental_option("excludeSwitches", ['enable-automation']);
        // chromeOptions.addArguments("disable-infobars");
        chromeOptions.setExperimentalOption("useAutomationExtension", false);
        chromeOptions.setExperimentalOption("excludeSwitches", Collections.singletonList("enable-automation"));

        // chromeOptions.setExperimentalOption("excludeSwitches","enable-automation");
        driver = new ChromeDriver(chromeOptions);
    }

    static boolean chooseNew = true;
    static Integer index = 0;
    static Robot robot;

    @SneakyThrows
    public static void main(String[] args) {
        robot = new Robot();
        //设置Robot产生一个动作后的休眠时间,否则执行过快
        robot.setAutoDelay(1000);
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                System.out.println("这个钩子启动");
                driver.close();
                System.out.println("这个钩子退出");
            }
        });
//    # 打开boss直聘牛人推荐页面 需要手动登录
        loginBoss();
        sleep(2);
        //点击推荐牛人
        clickEle("dl.menu-recommend");
        sleep(2);

        //点击筛选
        switchFrame();

        chooseActivity();
        while (true) {

            try {
                if (LocalDateTimeUtil.now().getHour() == 1) {
                    System.out.println("到" + LocalDateTimeUtil.now().getHour() + "点了 该睡了");
                    sleep(6 * 60 * 60);
                    System.out.println("到 7点了 ");
                }
                System.out.println("开始滑动屏幕");
                for (int i = 0; i < 20; i++) {
                    //robot.mouseWheel(RandomUtil.randomInt(200, 500));
                    //JavascriptExecutor executor = (JavascriptExecutor) frame;
                    frame.executeScript("window.scrollTo(0,window.scrollY+" + RandomUtil.randomInt(2500, 2700) + ")");
                    sleep(RandomUtil.randomInt(1, 2));
                    findStudentAndTalk();
                    if (isEleExist(".nomore")) {
                        System.out.println("到底了");
                        break;
                    }
                }

                System.out.println("刷新一下" + LocalDateTimeUtil.now());
                sleep(60);

                reload();
            } catch (Exception e) {
                e.printStackTrace();
                ((JavascriptExecutor) driver).executeScript("history.go()");
                sleep(3);
                switchFrame();
                chooseActivity();

            }
        }
    }

    private static void switchFrame() {
        try {
            frame = (JavascriptExecutor) driver.switchTo().frame(driver.findElement(By.cssSelector("#recommendContent > iframe")));
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("switch 失败");
        }
    }

    private static void chooseActivity() {
        sleep(2);
        clickEle("div.fl.recommend-filter");
        sleep(2);
        //clickEle("a[ka='recommend-985-1']");
        //clickEle("a[ka='recommend-211-2']");
        // clickEle("a[ka='recommend-3日内活跃-3']");
        clickEle("a[ka='recommend-今日活跃-2']");
        //clickEle("a[ka='recommend-刚刚活跃-1']");
        clickEle("a[ka='recommend-1年以内-2']");
        clickEle("a[ka='recommend-1-3年-3']");
        clickEle("a[ka='recommend-3-5年-4']");
        //clickEle("a[ka='recommend-5-10年-5']");
        clickEle("a[ka='recommend-本科-5']");
        // clickEle("a[ka='recommend-10-20K-4']");

        clickEle("span[ka='dialog_confirm']");
        sleep(3);
    }

    /**
     * 循环 推荐牛人跟新牛人
     */
    private static void reload() {
        index = 0;

        //((JavascriptExecutor) driver).executeScript("history.go()");
        driver.navigate().refresh();
        sleep(3);
        driver.switchTo().frame(driver.findElement(By.cssSelector("#recommendContent > iframe")));
        //driver.navigate().refresh();
        sleep(3);
        chooseNew = !chooseNew;
        if (chooseNew) {
            clickEle("#wrap > div > div > div.tab-wrap > ul > li:nth-child(3)");
            System.out.println("点击新牛人");
        } else {
            clickEle("#wrap > div > div > div.tab-wrap > ul > li:nth-child(1)");
            System.out.println("点击推荐");
        }
        chooseActivity();
    }

    private static boolean isEnd() {
        return false;
    }


    private static void loginBoss() {

        driver.get("https://www.zhipin.com/web/boss/recommend");
        driver.manage().window().maximize();
//    # 这时候应该重定向到首页了
        sleep(2);
        clickEle("a[ka = 'header-login']");
        clickEle(".sign-pwd .scan-switch");
//
//    # 如果页面有登录元素则睡眠
        while (isEleExist("#wrap > div.sign-wrap.sign-wrap-v2  div.form-btn > button")) {
            sleep(5);
        }
    }

    private static void findStudentAndTalk() {
        sleep(2);
        List<WebElement> elements = driver.findElements(By.cssSelector(".candidate-list-content"));
        System.out.println("点击范围： " + index + "--" + (elements.size() - 1));
        for (int i = index; i < elements.size(); i++) {
            WebElement element = elements.get(i);
            Candidate candidate = BasicAction.fromElement(element);
            if (BasicAction.isTarget(candidate)) {
                try {
                    System.out.println("点击了位置" + i + "牛人" + candidate);
                    element.findElement(By.cssSelector("button.btn.btn-greet")).click();
                    sleep(RandomUtil.randomInt(1, 3));
                } catch (Exception e) {
                    System.out.println(element.getText());
                    System.out.println("当前位置" + i);
                    e.printStackTrace();
                    // 有可能挡住
                    frame.executeScript("window.scrollTo(0,window.scrollY-" + RandomUtil.randomInt(500, 1000) + ")");
                    //robot.mouseWheel(-500);
                    System.out.println("点击了位置" + i + "牛人" + candidate);
                    element.findElement(By.cssSelector("button.btn.btn-greet")).click();
                }
            }
        }
        index = elements.size();
    }


    public static void sleep(long sec) {
        try {
            System.out.println("sleep " + sec + " 秒");
            Thread.sleep(sec * 1000);

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void clickEle(String str) {
        WebElement element = driver.findElement(By.cssSelector(str));
        sleep(RandomUtil.randomInt(1, 2));
        element.click();
    }

    public static boolean isEleExist(String str) {
        return driver.findElements(By.cssSelector(str)).size() != 0;
    }

}
