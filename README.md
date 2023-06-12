# ktConfig

> **Warning**
> This library is under development, so you can't use it.

Spigot configuration library using Kotlin data classes.

```kotlin
class Main : JavaPlugin() { 
    @Comment("config.yml", "This is header comments")
    data class SimpleConfig(
        val message: String = "You can use default values"
    )

    override fun onEnable() {
        val config = this.ktConfigFile("config.yml", SimpleConfig())
        logger.info(config.message)
    }
}
```

```yaml
# config.yml
# This is header comments

message: You can use default values
```

## 🔗 Links

- [Website](https://ktConfig.s7a.dev)
- [API Document](https://gh.s7a.dev/ktConfig)

## 🔑 License

```
MIT License

Copyright (c) 2022 sya-ri

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```
