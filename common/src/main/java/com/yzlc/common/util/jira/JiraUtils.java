package com.yzlc.common.util.jira;

import com.atlassian.jira.rest.client.api.IssueRestClient;
import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.domain.BasicIssue;
import com.atlassian.jira.rest.client.api.domain.Issue;
import com.atlassian.jira.rest.client.api.domain.SearchResult;
import com.atlassian.jira.rest.client.api.domain.input.IssueInputBuilder;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;
import io.atlassian.util.concurrent.Promise;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.URI;
import java.util.concurrent.ExecutionException;

/**
 * @author
 */
@Slf4j
public class JiraUtils {

    private JiraRestClient jiraRestClient;

    public JiraUtils(JiraConfig config) {
        URI uri = URI.create(config.getJiraUrl());
        jiraRestClient = new AsynchronousJiraRestClientFactory().createWithBasicHttpAuthentication(uri, config.getUsername(), config.getPassword());
    }

    public void closeConnection() throws IOException {
        jiraRestClient.close();
    }

    public Issue getIssueByKey(String issueKey) {
        Promise<Issue> issuePromise = jiraRestClient.getIssueClient().getIssue(issueKey);
        try {
            return issuePromise.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return null;
        }
    }

    public SearchResult searchIssues(String jqlQuery, int maxResults) {
        Promise<SearchResult> searchResultPromise = jiraRestClient.getSearchClient().searchJql(jqlQuery, maxResults, 0, null);
        try {
            return searchResultPromise.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return null;
        }
    }

    public BasicIssue createIssue(IssueInfo issueInfo) {
        IssueRestClient issueRestClient = jiraRestClient.getIssueClient();
        IssueInputBuilder issueBuilder = new IssueInputBuilder(issueInfo.getProjectKey(), Long.parseLong(issueInfo.getIssueType()))
                .setSummary(issueInfo.getSummary())
                .setAffectedVersionsNames(issueInfo.getAffectedVersionsNames())
                .setDescription(issueInfo.getDescription())
                .setAssigneeName(issueInfo.getAssigneeName());
        for (CustomFieldOption customFieldOption : issueInfo.getCustomFieldOptions()) {
            issueBuilder.setFieldValue(customFieldOption.getId(), customFieldOption.getValue());
        }
        for (CustomField customField : issueInfo.getCustomFields()) {
            issueBuilder.setFieldValue(customField.getId(), customField.getValue());
        }
        Promise<BasicIssue> issuePromise = issueRestClient.createIssue(issueBuilder.build());
        try {
            return issuePromise.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void main(String[] args) throws IOException {
        JiraConfig config = new JiraConfig("", "", "");
        JiraUtils jiraUtils = new JiraUtils(config);

        // 示例用法
        String issueKey = "PROJECT-123";
        Issue issue = jiraUtils.getIssueByKey(issueKey);
        if (issue != null) {
            System.out.println("Issue: " + issue.getKey());
            System.out.println("Summary: " + issue.getSummary());
            System.out.println("Description: " + issue.getDescription());
        } else {
            System.out.println("Failed to retrieve issue.");
        }

        String jqlQuery = "project = PROJECT";
        SearchResult searchResult = jiraUtils.searchIssues(jqlQuery, 10);
        if (searchResult != null) {
            System.out.println("Total issues found: " + searchResult.getTotal());
            for (Issue resultIssue : searchResult.getIssues()) {
                System.out.println("Issue: " + resultIssue.getKey());
                System.out.println("Summary: " + resultIssue.getSummary());
                System.out.println("Description: " + resultIssue.getDescription());
                System.out.println("------------------------");
            }
        } else {
            System.out.println("Failed to perform issue search.");
        }

        // 示例用法
        IssueInfo issueInfo = new IssueInfo();
        issueInfo.setProjectKey("");
        issueInfo.setIssueType("");
        issueInfo.setSummary("");
        BasicIssue createdIssue = jiraUtils.createIssue(issueInfo);
        if (createdIssue != null) {
            System.out.println("Issue created successfully. Key: " + createdIssue.getKey());
        } else {
            System.out.println("Failed to create issue.");
        }
        jiraUtils.closeConnection();
    }
}

