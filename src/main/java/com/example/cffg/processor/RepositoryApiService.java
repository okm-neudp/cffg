package com.example.cffg.processor;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.springframework.stereotype.Service;

import java.io.File;

@Slf4j
@Service
public class RepositoryApiService {
    private Config config;

    public RepositoryApiService(Config config) {
        this.config = config;
    }

    public void readRepo() {

        try {
            log.info("started");

            File directory = new File(config.getProperty("DEFAULT_TEMP_PATH"));
            deleteFolder(directory);

            CloneCommand cloneCommand = Git.cloneRepository();
            String username = config.getProperty("CUCUMBER_PROJECT_REPOSITORY_USERNAME");
            cloneCommand.setURI(String.format(config.getProperty("CUCUMBER_PROJECT_REPOSITORY_PATH"), username));
            cloneCommand.setCredentialsProvider(
                    new UsernamePasswordCredentialsProvider(username,
                            config.getProperty("CUCUMBER_PROJECT_REPOSITORY_PASSWORD")));
            cloneCommand.setDirectory(directory);
            cloneCommand.setBranch("develop");
            cloneCommand.call();
            log.info("ended");
        } catch (GitAPIException e) {
            log.error(e.getMessage());
            e.printStackTrace();
        }
    }

    private void deleteFolder(File folder) {
        File[] files = folder.listFiles();
        if (files != null) { //some JVMs return null for empty dirs
            for (File f : files) {
                if (f.isDirectory()) {
                    deleteFolder(f);
                } else {
                    f.delete();
                }
            }
        }
        folder.delete();
    }
}
