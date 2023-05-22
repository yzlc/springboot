package com.yzlc.common.util.jira;

import com.atlassian.jira.rest.client.api.domain.input.ComplexIssueInputFieldValue;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CustomFieldOption {
    private String id;
    private ComplexIssueInputFieldValue value;
}
