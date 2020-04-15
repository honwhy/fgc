# fgc

demo project to generate fgc

```shell
-Xms2m
-Xmx10m
-XX:+UseParallelOldGC
-XX:+PrintGCDetails
-verbose:gc
-XX:+HeapDumpBeforeFullGC
-XX:HeapDumpPath=/path/to/fgc/target/heap.hprof
```