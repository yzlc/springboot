package com.yzlc.common.model.jira;


/**
 * @author
 * @date 2020/4/26 13:25
 */
public class IssueInfo {

    /**
     * 任务标题
     */
    private String summary;

    /**
     * 描述
     */
    private String description;

    /**
     * issue key
     */
    private String key;

    /**
     * 优先级  紧急重要、紧急不重要、重要不紧急、不重要不紧急 name属性
     */
    private String priority;

    /**
     * 状态 新建，处理中，已完成
     */
    private String status;

    /**
     * 解决结果
     */
    private String resolution;

    /**
     * 经办人 通过name属性设置经办人
     */
    private String assignee;

    /**
     * 报告人
     */
    private String reporter;

    /**
     * 开始日期
     */
    private String customfield_10200;

    /**
     * 预计工时 整形
     */
    private String customfield_10201;

    /**
     * 完成度
     */
    private String customfield_10203;

    /**
     * 实际工作量
     */
    private String customfield_10202;
}

