package com.github.honwhy;

import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

/**
 *
 * An example of fgc when using thread pool
 * -Xmx10m -Xms10m -XX:+UseParallelOldGC -XX:+PrintGCDetails -verbose:gc
 */
public class TaskApp {
    public static void main(String[] args) {
        System.err.println(Arrays.asList(args));
        ArgumentParser parser = buildArgumentParser();
        SizeData sizeData = parse(parser, args);
        System.out.println(sizeData);
        ThreadPoolExecutor executor = ThreadPoolExecutorBuilder.build(sizeData.coreSize, sizeData.maxSize, sizeData.queueSize);
        List<Thread> threadList = new ArrayList<>(sizeData.parallel);
        for (int i = 0; i < sizeData.parallel; i++) {
            Thread t = new Thread(()->{
                for (int j = 0; j < sizeData.requestSize; j++) {
                    executor.submit(new RandomSleepTask());
                }
            });
            t.start();
            threadList.add(t);
        }

        threadList.parallelStream().forEach(t -> {
            try {
                t.join();
            } catch (InterruptedException e) {
                //ignore
            }
        });
    }

    static SizeData parse(ArgumentParser parser, String[] args) {
        Namespace ns = null;
        try {
            ns = parser.parseArgs(args);
        } catch (ArgumentParserException e) {
            parser.printHelp();
            System.exit(1);
        }

        Integer coreSize = ns.getInt("coreSize");
        Integer maxSize = ns.getInt("maxSize");
        Integer queueSize = ns.getInt("queueSize");
        Integer requestSize = ns.getInt("requestSize");
        Integer parallel = ns.getInt("parallel");
        return new SizeData(coreSize, maxSize, queueSize, requestSize, parallel);
    }

    static class SizeData {
        Integer coreSize;
        Integer maxSize;
        Integer queueSize;
        Integer requestSize;
        Integer parallel;

        public SizeData(Integer coreSize, Integer maxSize, Integer queueSize, Integer requestSize, Integer parallel) {
            this.coreSize = coreSize;
            this.maxSize = maxSize;
            this.queueSize = queueSize;
            this.requestSize = requestSize;
            this.parallel = parallel;
        }

        @Override
        public String toString() {
            return "SizeData{" +
                    "coreSize=" + coreSize +
                    ", maxSize=" + maxSize +
                    ", queueSize=" + queueSize +
                    ", requestSize=" + requestSize +
                    ", parallel=" + parallel +
                    '}';
        }
    }
    private static ArgumentParser buildArgumentParser() {
        ArgumentParser parser = ArgumentParsers.newFor("TakeApp").build()
                .defaultHelp(true)
                .description("An example of fgc when using thread pool.");
        parser.addArgument("--coreSize")
                .setDefault(Runtime.getRuntime().availableProcessors())
                .type(Integer.class)
                .help("core size of thread pool");
        parser.addArgument("--maxSize")
                .setDefault(16)
                .type(Integer.class)
                .help("max size of thread pool");
        parser.addArgument("--queueSize")
                .setDefault(100)
                .type(Integer.class)
                .help("queue size of thread pool");
        parser.addArgument("--requestSize")
                .setDefault(1000)
                .type(Integer.class)
                .help("request size fgc test");
        parser.addArgument("--parallel")
                .setDefault(4)
                .type(Integer.class)
                .help("parallel level for test");
        return parser;
    }
}
