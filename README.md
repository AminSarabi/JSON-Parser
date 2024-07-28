## A blazing fast json parser

This library is an O(n) json parser that creates the least amount of objects.
It works fast and does not trigger the garbage collector, making it suitable for old android devices.

---

### Benchmark
This library is **10 times** faster than org.json in parsing **[25 Megabytes](https://github.com/json-iterator/test-data)** of json text:

|     library     | time (ms) |
|:---------------:|:---:|
| reyminsoft.json | 046 |
|    json.org:    | 530 |

---

### O(n) Time complexity

The parser has an O(n) time complexity, meaning it iterates over the json string only once and does all the processing.\
It also avoids creating unnecessary objects that trigger the garbage collector.

---

### Limitations

1. The library assumes that the json input has valid syntax. this assumption allows for optimizations like seeing the character `f` and interpreting it as `false`, without looping through the remaining characters. \
in case of an invalid json input, the thrown exception may not have a descriptive and in detail explanation. 
2. currently the api for manipulating json objects or arrays is quite basic. (get and put only)
3. The parser passes all the unit tests, including the processing of 25 Mbs of json text. But the code has not been used in the wild.

---

Developed by Amin Sarabi .
