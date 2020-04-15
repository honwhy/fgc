package com.github.honwhy;

import com.sun.istack.internal.NotNull;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertTrue;

/**
 * Unit test for simple App.
 */
public class TaskAppTest {

    /**
     * mvn clean package
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
    public void test_taskApp() throws IOException, InterruptedException {
        int megaBytes = 10;
        //megaBytes - coreSize - maxSize - queueSize - requestSize
        Process process = startAppMain(megaBytes, "--coreSize", "4", "--maxSize", "8", "--queueSize", "10000", "--parallel", "20");
        BufferedReader reader =
                new BufferedReader(new InputStreamReader(process.getInputStream()));

        String line;
        while ((line = reader.readLine()) != null) {
            System.out.println(line);
        }
        process.waitFor(100, TimeUnit.SECONDS);
        process.destroy();
    }
    @Test
    public void test_fgc() throws IOException, InterruptedException {
        int megaBytes = 10;
        //megaBytes - coreSize - maxSize - queueSize - requestSize
        Process process = startTest(megaBytes, "--coreSize", "4", "--maxSize", "8", "--queueSize", "10000", "--parallel", "20");
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
    public void test_fgc_jar() throws IOException, InterruptedException {
        int megaBytes = 10;
        //megaBytes - coreSize - maxSize - queueSize - requestSize
        Process process = startJar(megaBytes, "--coreSize", "4", "--maxSize", "8", "--queueSize", "10000", "--parallel", "20");
        BufferedReader reader =
                new BufferedReader(new InputStreamReader(process.getInputStream()));

        String line;
        while ((line = reader.readLine()) != null) {
            System.out.println(line);
        }
        int exitCode = process.waitFor();
        System.out.println(">>" + exitCode);
        process.destroy();
    }

    private static Process startJar(int megaBytes, String ...args) throws IOException {
        String javaHome = System.getProperty("java.home");
        String javaBin = javaHome + File.separator + "bin" + File.separator + "java";
        String classpath = System.getProperty("java.class.path");
        String file = Thread.currentThread().getContextClassLoader().getResource("").getFile();
        String mainJar = new File(file).getParent() + File.separator + "fgc-1.0-SNAPSHOT.jar";
        String dump = new File(file).getParent() + File.separator + "heap.hprof";
        String jvmOptions = "-Xms" + megaBytes + "m -Xmx" + megaBytes + "m -XX:+UseParallelOldGC -XX:+PrintGCDetails -verbose:gc -XX:+HeapDumpBeforeFullGC -XX:HeapDumpPath=" + dump;
        //classpath = classpath + File.pathSeparator + mainJar;
        String className = TaskApp.class.getCanonicalName();
        String[] cmd = {javaBin, jvmOptions, "-jar", mainJar};
        List<String> command = new ArrayList<>(Arrays.asList(cmd));
        command.addAll(Arrays.asList(args));
        System.out.println(command);
        ProcessBuilder builder = new ProcessBuilder(command.toArray(new String[0]));
        //builder.redirectOutput(ProcessBuilder.Redirect.INHERIT);
        return builder.start();
    }

    private static Process startTest(int megaBytes, String ...args) throws IOException {
        String javaHome = System.getProperty("java.home");
        String javaBin = javaHome + File.separator + "bin" + File.separator + "java";
        String classpath = System.getProperty("java.class.path");
        String file = Thread.currentThread().getContextClassLoader().getResource("").getFile();
        //String mainJar = new File(file).getParent() + File.separator + "fgc-1.0-SNAPSHOT.jar";
        String dump = new File(file).getParent() + File.separator + "heap.hprof";
        String jvmOptions = "-Xms" + megaBytes + "m -Xmx" + megaBytes + "m -XX:+UseParallelOldGC -XX:+PrintGCDetails -verbose:gc -XX:+HeapDumpBeforeFullGC -XX:HeapDumpPath=" + dump;
        String className = TaskApp.class.getCanonicalName();
        String[] cmd = {javaBin, jvmOptions, "-cp", classpath, className};
        List<String> command = new ArrayList<>(Arrays.asList(cmd));
        command.addAll(Arrays.asList(args));
        System.out.println(command);
        ProcessBuilder builder = new ProcessBuilder(command.toArray(new String[0]));
        //builder.redirectOutput(ProcessBuilder.Redirect.INHERIT);
        return builder.start();
    }

    private static Process startAppMain(int megaBytes, String ...args) throws IOException {
        String javaHome = System.getProperty("java.home");
        String javaBin = javaHome + File.separator + "bin" + File.separator + "java";
        String classpath = System.getProperty("java.class.path");
        String file = Thread.currentThread().getContextClassLoader().getResource("").getFile();
        //String mainJar = new File(file).getParent() + File.separator + "fgc-1.0-SNAPSHOT.jar";
        String dump = new File(file).getParent() + File.separator + "heap.hprof";
        //String jvmOptions = "-Xms" + megaBytes + "m -Xmx" + megaBytes + "m -XX:+UseParallelOldGC -XX:+PrintGCDetails -verbose:gc -XX:+HeapDumpBeforeFullGC -XX:HeapDumpPath=" + dump;
        String className = TaskApp.class.getCanonicalName();
        String[] cmd = {javaBin, "-cp", classpath, className};
        List<String> command = new ArrayList<>(Arrays.asList(cmd));
        command.addAll(Arrays.asList(args));
        System.out.println(command);
        ProcessBuilder builder = new ProcessBuilder(command.toArray(new String[0]));
        //builder.redirectOutput(ProcessBuilder.Redirect.INHERIT);
        return builder.start();
    }
}
