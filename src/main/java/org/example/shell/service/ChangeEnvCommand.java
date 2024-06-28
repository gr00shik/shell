package org.example.shell.service;

import lombok.RequiredArgsConstructor;
import org.example.shell.config.S3Config;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

@RequiredArgsConstructor

@ShellComponent
public class ChangeEnvCommand {

    private final S3Config config;

    @ShellMethod(key = "env")
    public String changeEnv(@ShellOption(defaultValue = "set") String env) {
        config.setCurrentEnv(env);

        return config.getCurrentEnv().equals(env) ? "DONE" : "ERROR";
    }

    @ShellMethod(key = "info")
    public String GetInfoEnv() {
        return config.getCurrentEnv();
    }

}
