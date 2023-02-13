package com.yzlc.common.service;

import java.io.IOException;
import java.util.Map;

public interface JiraService {
    void createIssue(Map<String, Object> map) throws IOException;
}
