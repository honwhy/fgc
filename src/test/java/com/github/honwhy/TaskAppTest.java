package com.github.honwhy;

import static org.junit.Assert.assertTrue;

import com.github.honwhy.TaskApp;
import org.junit.Before;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Unit test for simple App.
 */
public class TaskAppTest {

    /**
     * mvn clean compile
     */
    //@Before
    public void setup() throws IOException, InterruptedException {
        String[] cmd = {"mvn", "clean", "package"};
        List<String> command = new ArrayList<>(Arrays.asList(cmd));
        ProcessBuilder builder = new ProcessBuilder(command.toArray(new String[0]));
        Process process = builder.start();
        BufferedReader reader =
                new BufferedReader(new InputStreamReader(process.getInputStream()));

        String line;
        while ((line = reader.readLine()) != null) {
            System.out.println(line);
        }
        process.waitFor();
        process.destroy();
    }

    @Test
    public void dummy() {
        String file = Thread.currentThread().getContextClassLoader().getResource("").getFile();
        System.out.println(file);
        String starJars = new File(file).getParent() + File.separator + "*.jar";
        System.out.println(starJars);
        assertTrue(true);
    }

    @Test
    public void testFgc() throws IOException, InterruptedException {
        int megaBytes = 2;
        //megaBytes - coreSize - maxSize - queueSize - requestSize
        Process process = start(megaBytes, "--coreSize 4", "--maxSize 8", "--queueSize 100", "--requestSize 10000");
        BufferedReader reader =
                new BufferedReader(new InputStreamReader(process.getInputStream()));

        String line;
        while ((line = reader.readLine()) != null) {
            System.out.println(line);
        }
        process.waitFor();
        process.destroy();
    }

    private static Process start(int megaBytes, String ...args) throws IOException {
        String javaHome = System.getProperty("java.home");
        String javaBin = javaHome + File.separator + "bin" + File.separator + "java";
        String classpath = System.getProperty("java.class.path");
        String file = Thread.currentThread().getContextClassLoader().getResource("").getFile();
        String mainJar = new File(file).getParent() + File.separator + "fgc-1.0-SNAPSHOT.jar";
        String dump = new File(file).getParent() + File.separator + "heap.hprof";
        String jvmOptions = "-Xms" + megaBytes + "m -Xmx" + megaBytes + "m -XX:+UseParallelOldGC -XX:+PrintGCDetails -verbose:gc -XX:+HeapDumpBeforeFullGC -XX:HeapDumpPath=" + dump;
        classpath = classpath + File.pathSeparator + mainJar;
        String className = TaskApp.class.getCanonicalName();
        String[] cmd = {javaBin, jvmOptions, "-jar", mainJar};
        List<String> command = new ArrayList<>(Arrays.asList(cmd));
        command.addAll(Arrays.asList(args));
        System.out.println(command);
        ProcessBuilder builder = new ProcessBuilder(command.toArray(new String[0]));
        //builder.redirectOutput(ProcessBuilder.Redirect.INHERIT);
        return builder.start();
    }
}
