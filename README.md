## A blazing fast json parser

This project is <b>10 times</b> faster than org.json in parsing json text, according to the benchmarks included in the test folder.\
Its performance is similar to fastjson, but without making use of object lazy loading!

The goal of the project is to have a fast json parser suitable for android mobiles running old versions of android.


#### O(n) Time complexity
The parser has an O(n) time complexity, meaning it iterates over the json string only once and does all the processing.\
It also does not create unnecessary objects, avoiding garbage creation.


#### Current status
currently the api for manipulating json objects or arrays is quite basic. (get and put only) \
The parser passes all the unit tests, including the processing of 20 Mbs of json text.

Developed by Amin Sarabi.