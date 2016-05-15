# External Merge Sort #

Common sorting algorithm assumes that the items to be sorted are small enough and can fit in the main memory. However, in big data there are often situation where the items are so large that they won't fit. This is a practice of implementing the *external merge sort* algorithm which utilizes disk in order to avoid running out of memory.

## Running The Program ##

### Requirements ###
1. SBT 0.13.8
2. Java(TM) SE Runtime Environment 1.8.0_74
3. Scala 2.10.6

### Input Files ###

1. The input file is files/in.txt. It contains large numbers generated randomly.
2. After a run, the output will be under files/out.txt. This file must be deleted before a subsequent run.

### Steps ###
```
git clone https://github.com/lolski/practice-bigdata-external-merge-sort.git
cd practice-bigdata-external-merge-sort
rm -f files/out.txt  # DELETE 'out.txt' before executing a run
sbt run
```