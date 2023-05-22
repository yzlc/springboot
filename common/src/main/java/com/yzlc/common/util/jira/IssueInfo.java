package com.yzlc.common.util.jira;


import com.atlassian.jira.rest.client.api.domain.input.ComplexIssueInputFieldValue;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author
 * @date 2020/4/26 13:25
 */
@Data
public class IssueInfo {

    /**
     * 任务标题
     */
    private String summary;
    private String projectKey;
    private String issueType;

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
    private String assigneeName;

    /**
     * 报告人
     */
    private String reporter;

    private Iterable<String> affectedVersionsNames;

    private List<CustomFieldOption> customFieldOptions = new ArrayList<>();
    private List<CustomField> customFields = new ArrayList<>();

    public void addCustomFieldOption(String id, String value) {
        Map<String,Object> map = new HashMap<>();
        map.put("value",value);
        customFieldOptions.add(new CustomFieldOption(id, new ComplexIssueInputFieldValue(map)));
    }


    public void addCustomField(String id, String value) {
        customFields.add(new CustomField(id, value));
    }
}

