package domain;

import lombok.Data;

/**
 *  候选人对象
 *
 */
@Data
public class Candidate {
    private String name;
    private Integer age;
    private String school;
    private String work;
    private String activity;
    // 空代表面议
    private Integer salary;
    private String expectWork;

}
