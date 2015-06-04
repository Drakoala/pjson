# pjson - Panda JSON v0.1.0
Simple JSON library for encoding and decoding, written in Java (1.8).

Inspired by JSON-Simple, the intentions behind pjson were to reduce the amount of boilerplate code required, and to allow for pretty-printing right off the bat. Encoding and decoding conform to the ECMA JSON specification (roughly speaking), and uses 4 spaces for indents while pretty printing.

Parsing is somewhat unstable and will be polished in the next update. However, initial performance tests suggest that encoding and decoding are faster, and produce less garbage for the JVM, than JSON-Simple by some 40% (measured in milliseconds). Granted, this was only confirmed on one machine. If you'd like to submit test cases, feel free to!



### Code examples:
###### Encoding
```java
JsonObject object = new JsonObject();
object.put("name", "value");
String json = object.toString();
```
```java
HashMap<Object, Object> map = new HashMap<>();
map.put("name", "value");
String json = JsonObject.encode(map);
```
Printing json from either example results in `{"name":"value"}`.
###### Encoding with Pretty Printing
```java
HashMap<Object, Object> map = new HashMap<>();
map.put("name", "value");
map.put("object", new HashMap<Object, Object>() {{
    put("key", "value");
}});
String json = JsonObject.encodePretty(map);
```
Printing json from this example results in 
```json
{
    "name":"value",
    "object":{
        "key":"value"
    }
}
```
Another way of writing the above example is manually with the JsonWriter.
```java
JsonObject object = new JsonObject();
object.put("name", "value");
object.put("object", new HashMap<Object, Object>() {{
    put("key", "value");
}});
JsonWriter writer = new JsonWriter().setPrettyPrinting(true);
object.writeJson(writer);
String json = writer.toString();
```
This example prints the same as the above pretty printing example, and is potentially faster as doing so skips determining the type of the initial value and writes it as an object.
###### Decoding
Consider the above pretty printed JSON, represented as a String named json. To decode it, simply pass it to the decode method, and cast the returned object to a JsonObject. If all is well, the object is populated properly. If not, as with all aspects of the API, an exception is thrown.
```java
JsonObject object = (JsonObject)JsonObject.decode(json);
```
