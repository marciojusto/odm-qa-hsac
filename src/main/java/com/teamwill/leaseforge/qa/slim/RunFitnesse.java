package com.teamwill.leaseforge.qa.slim;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

@Slf4j
public class RunFitnesse {
    public static final String port = "8090";

    public static final String JOB_NAME = "test-on-dev";

    @SneakyThrows
    public static void main(String[] args) {
        File logDir = new File("target/fitnesse/log");
        if (!logDir.exists()) {
            logDir.mkdirs();
        }

        Files.walk(logDir.toPath())
             .filter(f -> f.toFile()
                           .getName()
                           .endsWith(".log"))
             .map(Path::toFile)
             .forEach(File::delete);

        File rootPath = new File("src/main/fitnesse");
        if (!rootPath.exists()) {
            rootPath.mkdirs();
        }

        Files.walk(rootPath.toPath())
             .filter(f -> f.toFile()
                           .getName()
                           .endsWith(".zip"))
             .map(Path::toFile)
             .forEach(File::delete);

        log.info("Connect to http://localhost:" + port + "/FrontPage");
        fitnesseMain.FitNesseMain.main(new String[]{
                "-p", port,
                "-d", rootPath.getAbsolutePath(),
                "-l", logDir.getAbsolutePath(),
                "-v"
        });
    }
}
