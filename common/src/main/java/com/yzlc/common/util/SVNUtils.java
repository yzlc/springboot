package com.yzlc.common.util;


import lombok.extern.slf4j.Slf4j;
import org.tmatesoft.svn.core.*;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.internal.io.dav.DAVRepositoryFactory;
import org.tmatesoft.svn.core.internal.io.fs.FSRepositoryFactory;
import org.tmatesoft.svn.core.internal.io.svn.SVNRepositoryFactoryImpl;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNRevision;
import org.tmatesoft.svn.core.wc.SVNUpdateClient;
import org.tmatesoft.svn.core.wc.SVNWCUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.*;

@Slf4j
public class SVNUtils {
    private static SVNRepository repository = null;

    public SVNUtils(String rootDir, String svnUrl, String username, String password) throws SVNException {
        log.info("login " + rootDir);
        setupLibrary();
        repository = SVNRepositoryFactory.create(SVNURL.parseURIEncoded(svnUrl + "/" + rootDir));
        ISVNAuthenticationManager authManager = SVNWCUtil.createDefaultAuthenticationManager(username, password);
        repository.setAuthenticationManager(authManager);
    }

    public void logout() {
        log.info("logout");
        if (repository != null) {
            repository.closeSession();
        }
    }

    public void download(long revision, String targetDir) throws SVNException, IOException {
        log.info("downloading... revision=" + revision + ",targetDir=" + targetDir);
        download("", null, null, revision, targetDir, false);
    }

    public List<String> search(String path, List<String> keyFiles, List<String> keyDirs) throws SVNException, IOException {
        log.info("search... path=" + path + ",keyFiles=" + keyFiles + ",keyDirs=" + keyDirs);
        return download("/" + path, new HashSet<>(keyFiles), new HashSet<>(keyDirs), -1, null, true);
    }

    public List<String> download(String path, List<String> keyFiles, List<String> keyDirs, String targetDir) throws SVNException, IOException {
        log.info("downloading... path=" + path + ",keyFiles=" + keyFiles + ",keyDirs=" + keyDirs + ",targetDir=" + targetDir);
        return download("/" + path, new HashSet<>(keyFiles), new HashSet<>(keyDirs), -1, targetDir, false);
    }

    private List<String> download(String path, Set<String> keyFiles, Set<String> keyDirs, long revision, String targetDir, boolean search) throws SVNException, IOException {
        Collection<SVNDirEntry> entries = repository.getDir(path, revision, null, (Collection) null);
        List<String> list = new ArrayList<>();
        for (SVNDirEntry entry : entries) {
            if (!Objects.equals(revision, -1L) && !Objects.equals(entry.getRevision(), revision)) continue;

            if (entry.getKind() == SVNNodeKind.DIR) {
                if (!Objects.isNull(keyDirs) && !keyDirs.isEmpty() && keyDirs.stream().noneMatch(entry.getName()::contains))
                    continue;
                String subPath = path.isEmpty() ? entry.getName() : path + "/" + entry.getName();
                List<String> download = download(subPath, keyFiles, null, revision, targetDir, search);
                if (!download.isEmpty()) list.addAll(download);
            } else if (entry.getKind() == SVNNodeKind.FILE) {
                boolean match = true;
                if (!Objects.isNull(keyFiles)) match = keyFiles.stream().allMatch(entry.getName()::contains);
                if (!match) continue;
                log.info(path + "/" + entry.getName());
                list.add(path + "/" + entry.getName());
                FileUtil.create(targetDir + "/" + path);
                if (search) continue;
                try (OutputStream outputStream = new FileOutputStream(targetDir + File.separator + path + File.separator + entry.getName())) {
                    repository.getFile(path + "/" + entry.getName(), -1, null, outputStream);
                }
            }
        }
        return list;
    }

    public static String log(long revision) throws SVNException {
        log.info("log revision=" + revision);
        Collection<SVNLogEntry> logEntries = repository.log(new String[]{""}, null, revision, revision, true, true);
        for (SVNLogEntry logEntry : logEntries) {
            if (logEntry.getChangedPaths().size() > 0) {
                StringBuilder sb = new StringBuilder();
                for (SVNLogEntryPath entryPath : logEntry.getChangedPaths().values()) {
                    sb.append(entryPath.getType()).append(" ").append(entryPath.getPath()).append("\n");
                }
                log.info(sb.toString());
                return sb.toString();
            }
        }
        return null;
    }

    private void setupLibrary() {
        DAVRepositoryFactory.setup();
        SVNRepositoryFactoryImpl.setup();
        FSRepositoryFactory.setup();
    }

    // 拉取代码到指定目录
    public void checkout(String destinationDir) throws SVNException {
        log.info("checkout destinationDir=" + destinationDir);
        SVNUpdateClient updateClient = SVNClientManager.newInstance().getUpdateClient();
        updateClient.setIgnoreExternals(false);
        updateClient.doCheckout(repository.getLocation(), new File(destinationDir), SVNRevision.HEAD, SVNRevision.HEAD, SVNDepth.INFINITY, false);
    }

    // 更新本地工作副本
    public void update(String workingCopyPath) throws SVNException {
        log.info("update destinationDir=" + workingCopyPath);
        SVNUpdateClient updateClient = SVNClientManager.newInstance().getUpdateClient();
        updateClient.setIgnoreExternals(false);
        updateClient.doUpdate(new File(workingCopyPath), SVNRevision.HEAD, SVNDepth.INFINITY, false, false);
    }

    public static void main(String[] args) throws SVNException, IOException {
        SVNUtils svnUtils = new SVNUtils("src", "https://ip:port/svn",
                "username", "password");
        List<String> list = svnUtils.search("a/b", Arrays.asList(".jar"), Collections.emptyList());
        svnUtils.download("a/b", Arrays.asList("pom.xml"), Collections.emptyList(), "test");
        svnUtils.logout();
    }
}
