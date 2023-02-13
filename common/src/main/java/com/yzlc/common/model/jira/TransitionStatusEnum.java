package com.yzlc.common.model.jira;


/**
 * @author
 * @date 2020/4/26 15:38
 */
public enum TransitionStatusEnum {
    ACCEPT_TASK(11, "接受任务"),
    REFUSE_TASK(51, "拒绝任务"),
    START_TASK(21, "开始任务"),
    SOLUTION_TASK(31, "解决任务"),
    AFTER_ACCEPT_HANG_UP_TASK(71, "接受任务后挂起任务"),
    AFTER_START_HANG_UP_TASK(91, "开始任务后挂起任务"),
    RESTART_TASK(81, "重新开始任务"),
    ;

    private Integer code;
    private String desc;

    TransitionStatusEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public Integer getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

}

