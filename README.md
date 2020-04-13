# fgc

demo project to generate fgc

```shell
java  -Xms2m -Xmx2m -XX:+UseParallelOldGC -XX:+PrintGCDetails -verbose:gc -XX:+HeapDumpBeforeFullGC -XX:HeapDumpPath=/path/to/target/heap.hprof  -jar  /path/to/target/fgc-1.0-SNAPSHOT.jar  --coreSize 4  --maxSize 8  --queueSize 100  --requestSize 10000
```