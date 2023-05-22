package com.yzlc.common.util.jira;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class JiraConfig {
    private String jiraUrl;
    private String username;
    private String password;
}
