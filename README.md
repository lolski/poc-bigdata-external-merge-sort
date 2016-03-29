Problem set 1, part 1

Main assumptions:
  - input size is 300 million integer, requires 9.6GB space excluding overheads (int is 32 bit). won't fit in main memory
  - input from file, I/O needed
  
Design decisions:
  - Output is written to a new file instead of overwriting the input.
      The reason is to prevent losing / corrupting the input file in case of failure (e.g., sorting crashed while in the middle of writing)
  - Some form of external sorting since input is assumed to be large
    - Minimize number of I/O accesses (e.g., by minimizing the number of reading / writing pass)
    - Optimize I/O using buffering
    - Minimize memory buffer

Implementation:
  - we decide on using external merge sort

Limitations:
  - Minimize open file at a time
