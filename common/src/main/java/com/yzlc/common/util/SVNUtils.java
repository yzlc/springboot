package com.yzlc.common.util;


import lombok.extern.slf4j.Slf4j;
import org.tmatesoft.svn.core.*;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.internal.io.dav.DAVRepositoryFactory;
import org.tmatesoft.svn.core.internal.io.fs.FSRepositoryFactory;
import org.tmatesoft.svn.core.internal.io.svn.SVNRepositoryFactoryImpl;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;
import org.tmatesoft.svn.core.wc.SVNWCUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.*;

@Slf4j
public class SVNUtils {
    private static final String SVN_URL = PropertiesUtils.getProperty("svn.url");
    private static final String USERNAME = PropertiesUtils.getProperty("svn.userName");
    private static final String PASSWORD = PropertiesUtils.getProperty("svn.password");
    private static SVNRepository repository = null;

    public static void login(String rootDir) throws SVNException {
        log.info("login " + rootDir);
        setupLibrary();
        repository = SVNRepositoryFactory.create(SVNURL.parseURIEncoded(SVN_URL + "/" + rootDir));
        ISVNAuthenticationManager authManager = SVNWCUtil.createDefaultAuthenticationManager(USERNAME, PASSWORD);
        repository.setAuthenticationManager(authManager);
    }

    public static void logout() {
        log.info("logout");
        if (repository != null) {
            repository.closeSession();
        }
    }

    public static void download(long revision, String targetDir) throws SVNException, IOException {
        log.info("downloading... revision=" + revision + ",targetDir=" + targetDir);
        download("", null, null, revision, targetDir);
    }

    public static void download(String path, List<String> keyFiles, List<String> keyDirs, String targetDir) throws SVNException, IOException {
        log.info("downloading... path=" + path + ",keyFiles=" + keyFiles + ",keyDirs=" + keyDirs + ",targetDir=" + targetDir);
        download("/" + path, new HashSet<>(keyFiles), new HashSet<>(keyDirs), -1, targetDir);
    }

    private static void download(String path, Set<String> keyFiles, Set<String> keyDirs, long revision, String targetDir) throws SVNException, IOException {
        Collection<SVNDirEntry> entries = repository.getDir(path, revision, null, (Collection) null);
        for (SVNDirEntry entry : entries) {
            if (!Objects.equals(revision, -1L) && !Objects.equals(entry.getRevision(), revision)) continue;

            if (entry.getKind() == SVNNodeKind.DIR) {
                if (!Objects.isNull(keyDirs) && keyDirs.stream().noneMatch(entry.getName()::contains)) continue;
                String subPath = path.isEmpty() ? entry.getName() : path + "/" + entry.getName();
                download(subPath, keyFiles, null, revision, targetDir);
            } else if (entry.getKind() == SVNNodeKind.FILE) {
                boolean match = true;
                if (!Objects.isNull(keyFiles)) match = keyFiles.stream().allMatch(entry.getName()::contains);
                if (!match) continue;
                try (OutputStream outputStream = new FileOutputStream(targetDir + File.separator + entry.getName())) {
                    repository.getFile(path + "/" + entry.getName(), -1, null, outputStream);
                }
            }
        }
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

    private static void setupLibrary() {
        DAVRepositoryFactory.setup();
        SVNRepositoryFactoryImpl.setup();
        FSRepositoryFactory.setup();
    }

    public static void main(String[] args) throws SVNException, IOException {
        login("xxdir");
        download("/xx项目", Arrays.asList("a", "b"), new ArrayList<>(), null);
        logout();

        login("zzdir");
        download(1, "");
        log(1);
        logout();
    }
}