package com.macro.mall.tiny.modules.ibkr.component;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Component
public class ProcessManager {

    /**
     * 重启 IBKR Web API
     */
    public void restartIbkrWebApi() {
        try {
            // Step 1: Find the process
            String jarName = "clientportal.gw.jar"; // Replace with the JAR name or keyword to search for
            String pid = findProcess(jarName);

            if (pid != null) {
                log.info("Found IBKR Web API process with PID:{}", pid);
                // Step 2: Kill the process
                killProcess(pid);
                log.info("IBKR Web API process {} killed.", pid);
            } else {
                log.info("No IBKR Web API process found for {}", jarName);
            }

            // Step 3: Restart the process
            String startCommand = "nohup /apps/ibkr/clientportal.gw/bin/run.sh /apps/ibkr/clientportal.gw/root/conf.yaml &"; // Replace with your start command
            restartProcess(startCommand);
            log.info("IBKR Web API process restarted with command:{}", startCommand);
        } catch (IOException | InterruptedException e) {
            log.error("IBKR Web API process restart error", e);
        }
    }

    // Find the process ID (PID) for a given JAR or keyword
    private static String findProcess(String jarName) throws IOException {
        String command = "ps -ef | grep " + jarName + " | grep -v grep";
        Process process = Runtime.getRuntime().exec(new String[]{"/bin/sh", "-c", command});

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // Example output: "user 12345 ... java -jar your-app.jar"
                Pattern pattern = Pattern.compile("\\w+\\s+(\\d+)\\s+.*" + jarName + ".*");
                Matcher matcher = pattern.matcher(line);
                if (matcher.find()) {
                    return matcher.group(1); // Return the PID
                }
            }
        }
        return null; // No process found
    }

    // Kill the process with the given PID
    private static void killProcess(String pid) throws IOException, InterruptedException {
        String command = "kill -9 " + pid;
        Process process = Runtime.getRuntime().exec(new String[]{"/bin/sh", "-c", command});
        process.waitFor(); // Wait for the kill command to complete
    }

    // Restart the process with the given command
    private static void restartProcess(String startCommand) throws IOException {
        Runtime.getRuntime().exec(new String[]{"/bin/sh", "-c", startCommand});
    }
}
