package com.yzlc.common.service;

import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.domain.*;
import com.atlassian.jira.rest.client.api.domain.input.*;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.yzlc.common.model.jira.TransitionStatusEnum;
import com.yzlc.common.util.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author yzlc
 */
@Service
@Slf4j
public class JiraServiceImpl implements JiraService {
    @Value("${jira.url}")
    private String url;
    @Value("${jira.user}")
    String user;
    @Value("${jira.pwd}")
    String pwd;
    @Value("${jira.projectKey}")
    private String projectKey;

    /**
     * 通过issueKey获取issue
     *
     * @param issueKey
     * @return
     */
    public Issue getIssue(String issueKey) throws JsonProcessingException {
        JiraRestClient restClient = loginJira(url, user, pwd);
        Issue issue = restClient.getIssueClient().getIssue(issueKey).claim();
        log.info(JsonUtil.obj2json(issue));
        return issue;
    }


    /**
     * 创建issue：description ,summary,projectKey,issueType,是必须要有的
     *
     * @param map
     */
    @Override
    public void createIssue(Map<String, Object> map) throws IOException {
        JiraRestClient restClient = loginJira(url, user, pwd);
        IssueInputBuilder builder = new IssueInputBuilder();
        builder.setDescription(map.get(IssueFieldId.DESCRIPTION_FIELD.id).toString());
        builder.setSummary(map.get(IssueFieldId.SUMMARY_FIELD.id).toString());
        builder.setProjectKey(projectKey);
        builder.setFieldInput(new FieldInput(IssueFieldId.ISSUE_TYPE_FIELD, ComplexIssueInputFieldValue.with("name", "任务")));
        builder.setFieldInput(new FieldInput(IssueFieldId.PRIORITY_FIELD, ComplexIssueInputFieldValue.with("name", "不重要不紧急")));
//如果还需要一些其他信息，可以按照上面的build继续构造，想要知道IssueFieldId对应哪些数据，
//我们可以通过getIssue()方法获取一个issue有哪些数据，然后对照着构建。

        IssueInput issueInput = builder.build();
        try {
            BasicIssue issue = restClient.getIssueClient().createIssue(issueInput).claim();
            log.info(JsonUtil.obj2json(issue));
        } catch (Exception e) {
            log.error("", e);
        } finally {
            restClient.close();
        }
    }

    /**
     * 创建子任务
     *
     * @param description
     * @param summary
     * @param parentIssueKey
     */
    public void createSubTask(String description, String summary, String parentIssueKey) throws JsonProcessingException {
        JiraRestClient restClient = loginJira(url, user, pwd);
        IssueInputBuilder builder = new IssueInputBuilder();
        builder.setDescription(description);
        builder.setSummary(summary);
        builder.setProjectKey(projectKey);
        builder.setFieldInput(new FieldInput(IssueFieldId.ISSUE_TYPE_FIELD, ComplexIssueInputFieldValue.with("name", "子任务")));
        builder.setFieldInput(new FieldInput(IssueFieldId.PRIORITY_FIELD, ComplexIssueInputFieldValue.with("name", "不重要不紧急")));
        //创建子任务需要指定父任务的issuekey
        builder.setFieldInput(new FieldInput("parent", ComplexIssueInputFieldValue.with("key", parentIssueKey)));

        IssueInput issueInput = builder.build();
        BasicIssue issue = restClient.getIssueClient().createIssue(issueInput).claim();
        log.info(JsonUtil.obj2json(issue));

    }

    /**
     * 更新issue
     *
     * @param issueKey,下面需要更新的数据现在代码里是写死了，真正用的时候以参数的形式传进去
     */
    public void updateIssue(String issueKey) {
        JiraRestClient restClient = loginJira(url, user, pwd);

        IssueInputBuilder builder = new IssueInputBuilder();
        //设置项目key然后才能修改
        builder.setProjectKey(projectKey);
//        builder.setDescription("通过java更新issue描述");
        //优先级
        builder.setFieldInput(new FieldInput("priority", ComplexIssueInputFieldValue.with("name", "不重要不紧急")));
        //经办人
//        builder.setFieldInput(new FieldInput("assignee", ComplexIssueInputFieldValue.with("name", "liuwei01")));
        restClient.getIssueClient().updateIssue(issueKey, builder.build()).claim();
    }

    /**
     * 改变工作状态:如果接受任务--开始任务--完成任务
     * 接受任务必须要哪些参数都可以通过jira页面查看，他要你必填哪些信息。和我们通过接口调用是一致的
     *
     * @param issueKey
     */
    public void changeStatus(String issueKey) throws JsonProcessingException {
        JiraRestClient restClient = loginJira(url, user, pwd);
        Issue issue = getIssue(issueKey);
        //查询进度转变
        Iterable<Transition> transitions = restClient.getIssueClient().getTransitions(issue).claim();
        log.info(JsonUtil.obj2json(transitions));

        List<FieldInput> fieldInputList = new ArrayList<>();
        //接受任务
//        fieldInputList.add(new FieldInput("customfield_10200", "2020-04-26"));
//        fieldInputList.add(new FieldInput("customfield_10201", 4));
        //实际工作量
        fieldInputList.add(new FieldInput("customfield_10202", 4));
        //完成度
//        fieldInputList.add(new FieldInput("customfield_10203", "-1"));
//这里需要注意的是issue返回的信息里面有很多customfield_10203这样类型的字段，
//具体含义需要我们去尝试、猜测，我总结了部分字段的含义，搞了个枚举，在后面贴出

        TransitionInput transitionInput = new TransitionInput(TransitionStatusEnum.SOLUTION_TASK.getCode(), fieldInputList);
        restClient.getIssueClient().transition(issue, transitionInput).claim();
    }

    /**
     * 增加注释 评论
     *
     * @param issueKey
     */
    public void addCommon(String issueKey) throws JsonProcessingException {
        JiraRestClient restClient = loginJira(url, user, pwd);
        Comment comment = Comment.valueOf("one more time");
        Issue issue = getIssue(issueKey);
        URI uri = issue.getCommentsUri();
        log.info(uri.toString());
        restClient.getIssueClient().addComment(uri, comment);
        try {
            restClient.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * 获取项目信息
     *
     * @param projectKey
     */
    public void getProject(String projectKey) throws JsonProcessingException {
        JiraRestClient restClient = loginJira(url, user, pwd);
        Project project = restClient.getProjectClient().getProject(projectKey).claim();
        log.info(JsonUtil.obj2json(project));
    }

    /**
     * 创建项目 这个在 getProjectClient（）里面居然没有创建方法。就只能使用发送http请求的形式了。
     *
     * @param description    描述
     * @param name           项目名
     * @param key            项目的key。自己设置
     * @param user           用户名 搞一个有创建项目权限的账号
     * @param pwd            密码
     * @param projectTypeKey 有效值：software, service_desk, business
     */
    public void createProject(String description, String name, String key,
                              String user, String pwd, String projectTypeKey) throws IOException {
        Map<String, Object> map = new HashMap<>();
        map.put("description", description);
        map.put("name", name);
        map.put("key", key);
        map.put("projectTypeKey", projectTypeKey);
        map.put("lead", "chenjing");
        try {
            //下面这个Unirest也是参考官方文档使用的，其实内部封装是apache httpclient，简化了使用
            HttpResponse<JsonNode> response = Unirest.post(url + "/rest/api/2/project")
                    .basicAuth(user, pwd)
                    .header("Accept", "application/json")
                    .header("Content-Type", "application/json")
                    .body(JsonUtil.obj2json(map))
                    .asJson();
            System.out.println(response.getBody());
        } catch (UnirestException e) {
            e.printStackTrace();
        }
    }

    /**
     * 通过projectKey删除 项目
     *
     * @param projectKey
     * @param user
     * @param pwd
     * @throws UnirestException
     */
    public void deleteProject(String projectKey, String user, String pwd) throws UnirestException {
        HttpResponse<String> response = Unirest.delete(url + "/rest/api/2/project/" + projectKey)
                .basicAuth(user, pwd)
                .asString();
        log.info(response.getBody());
    }


    /**
     * 上传附件
     */
  /*  public  void addAttachment(String issueKey,String filePath) throws FileNotFoundException {
        Issue issue = getIssue(issueKey);
        JiraRestClient restClient = loginJira(url, user, pwd);
        File file = new File(filePath);
        FileInputStream inputStream = new FileInputStream(file);
        restClient.getIssueClient().addAttachment(issue.getAttachmentsUri(),inputStream, file.getName());
    }*/
    public String getIssueAllTypes() throws UnirestException {
        HttpResponse<JsonNode> response = Unirest.get(url + "/rest/api/2/issuetype")
                .basicAuth(user, pwd)
                .header("Accept", "application/json")
                .asJson();
        System.out.println(response.getBody());

        return response.getBody().toString();
    }

    /**
     * 删除issue，需要权限
     *
     * @param issueKey
     */
    public void deleteIssue(String issueKey) {
        JiraRestClient restClient = loginJira(url, user, pwd);
        restClient.getIssueClient().deleteIssue(issueKey, true);
    }

    private JiraRestClient loginJira(String url, String userName, String pwd) {
        AsynchronousJiraRestClientFactory asynchronousJiraRestClientFactory = new AsynchronousJiraRestClientFactory();
        JiraRestClient jiraRestClient = asynchronousJiraRestClientFactory.createWithBasicHttpAuthentication(URI.create(url), userName, pwd);
        return jiraRestClient;
    }
}