# fgc

demo project to generate fgc and other things

```shell
-Xms2m
-Xmx10m
-XX:+UseParallelOldGC
-XX:+PrintGCDetails
-verbose:gc
-XX:+HeapDumpBeforeFullGC
-XX:HeapDumpPath=/path/to/fgc/target/heap.hprof
```