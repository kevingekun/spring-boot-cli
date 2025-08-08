package com.macro.mall.tiny.modules.ibkr.component;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.File;
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
            String startCommand = "nohup bin/run.sh root/conf.yaml &"; // Replace with your start command
            restartProcess(startCommand);
            log.info("IBKR Web API process restarted with command:{}", startCommand);
        } catch (IOException | InterruptedException e) {
            log.error("IBKR Web API process restart error", e);
        }
    }

    // 查找给定 JAR 或关键字的进程 ID (PID)
    private static String findProcess(String jarName) throws IOException {
        String command = "ps -ef | grep " + jarName + " | grep -v grep";
        Process process = Runtime.getRuntime().exec(new String[]{"/bin/sh", "-c", command});

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // 示例输出: "user 12345 ... java -jar your-app.jar"
                Pattern pattern = Pattern.compile("\\w+\\s+(\\d+)\\s+.*" + jarName + ".*");
                Matcher matcher = pattern.matcher(line);
                if (matcher.find()) {
                    return matcher.group(1); // 返回 PID
                }
            }
        }
        return null; // 未找到进程
    }

    // 杀死给定 PID 的进程
    private static void killProcess(String pid) throws IOException, InterruptedException {
        String command = "kill -9 " + pid;
        Process process = Runtime.getRuntime().exec(new String[]{"/bin/sh", "-c", command});

        // 捕获错误输出
        try (BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
            String line;
            while ((line = errorReader.readLine()) != null) {
                System.err.println("杀死进程错误: " + line);
            }
        }
        int exitCode = process.waitFor();
        if (exitCode != 0) {
            System.err.println("杀死进程失败，退出码: " + exitCode);
        }
    }

    // 使用给定命令重启进程
    private static void restartProcess(String startCommand) throws IOException {
        // 切换到 /apps/ibkr 目录并执行启动命令
        String fullCommand = "cd /apps/ibkr/clientportal.gw && " + startCommand;
        ProcessBuilder pb = new ProcessBuilder("/bin/sh", "-c", fullCommand);

        // 设置输出文件以捕获 nohup 输出
        pb.redirectOutput(new File("/apps/logs/restartIbkrWebApi.log"));
        pb.redirectErrorStream(true); // 合并错误流到 restartIbkrWebApi.log

        Process process = pb.start();

        // 异步读取输出（可选，如果需要实时查看）
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                log.info("重启进程输出: {}", line);
            }
        }
    }
}
