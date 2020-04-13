package com.github.honwhy;

import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;

import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 *
 * An example of fgc when using thread pool
 * -Xmx10m -Xms10m -XX:+UseParallelOldGC -XX:+PrintGCDetails -verbose:gc
 */
public class TaskApp {
    public static void main(String[] args) throws InterruptedException {
        ArgumentParser parser = buildArgumentParser();
        SizeData sizeData = parse(parser, args);
        System.out.println(sizeData);
        ThreadPoolExecutor executor = ThreadPoolExecutorBuilder.build(sizeData.coreSize, sizeData.maxSize, sizeData.queueSize);
        Thread t1 = new Thread(()->{
            for (int i = 0; i < sizeData.requestSize; i++) {
                executor.submit(new RandomSleepTask());
            }
        });
        t1.start();
        Thread t2 = new Thread(()->{
            for (int i = 0; i < sizeData.requestSize; i++) {
                executor.submit(new RandomSleepTask());
            }
        });
        t2.start();
//        for (int i = 0; i < sizeData.requestSize; i++) {
//            executor.submit(new RandomSleepTask());
//        }
        executor.awaitTermination(120, TimeUnit.SECONDS);
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
        return new SizeData(coreSize, maxSize, queueSize, requestSize);
    }

    static class SizeData {
        Integer coreSize;
        Integer maxSize;
        Integer queueSize;
        Integer requestSize;

        public SizeData(Integer coreSize, Integer maxSize, Integer queueSize, Integer requestSize) {
            this.coreSize = coreSize;
            this.maxSize = maxSize;
            this.queueSize = queueSize;
            this.requestSize = requestSize;
        }

        @Override
        public String toString() {
            return "SizeData{" +
                    "coreSize=" + coreSize +
                    ", maxSize=" + maxSize +
                    ", queueSize=" + queueSize +
                    ", requestSize=" + requestSize +
                    '}';
        }
    }
    private static ArgumentParser buildArgumentParser() {
        ArgumentParser parser = ArgumentParsers.newFor("TakeApp").build()
                .defaultHelp(true)
                .description("An example of fgc when using thread pool.");
        parser.addArgument("--coreSize")
                .setDefault(Runtime.getRuntime().availableProcessors())
                .required(true)
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
                .setDefault(10_000)
                .type(Integer.class)
                .help("request size fgc test");
        return parser;
    }
}
