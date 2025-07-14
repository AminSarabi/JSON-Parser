## A blazing fast json parser

This is an O(n) json parser that creates the least amount of objects and does not require you to write try-catch clauses everywhere.
It works fast and does not trigger the garbage collector, making it suitable for old android devices.

I have been using this parser in my projects for a long time.

- [x] Has createIfAbsent, increment, forEach and other useful methods.
- [x] Works on android too (even older versions)
- [x] Supports converting a JSONObject to a java object and vice versa (Serialization/Deserialization)
- [x] Does not require try-catch.
- [x] Simply returns null instead of yelling at you when the key is not present.
- [x] Has automated tests (follows TDD)
---

### Benchmark

This library is **10 times** faster than org.json in parsing *
*[25 Megabytes](https://github.com/json-iterator/test-data)** of json text:

|     library     | time (ms) |
|:---------------:|:---------:|
| reyminsoft.json |    046    |
|    json.org:    |    530    |

---

### O(n) Time complexity

The parser has an O(n) time complexity, meaning it iterates over the json string only once and does all the processing.\
It also avoids creating unnecessary objects that trigger the garbage collector.

---

### Limitations

1. The library assumes that the json input has valid syntax. if not, an exception will be thrown, but the message might not be in detail. this assumption allows for optimizations like seeing the
   character `f` and interpreting it as `false`, without looping through the remaining characters.
2. The parser converts tiny double values with a **tiny error** by default. it uses a specialized algorithm to convert
   numbers faster. **this can be disabled library-wide**

---

Developed by Amin Sarabi .
