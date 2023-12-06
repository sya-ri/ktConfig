# Installation

## Stable

N/A

## Snapshot

> You can use libraries that are still under development. Please note, subject to change.
> 
{style="warning"}

<tabs>

<tab title="build.gradle.kts">

```Kotlin
repositories {
    maven(url = "https://s01.oss.sonatype.org/content/repositories/snapshots/")
}

dependencies {
    implementation("dev.s7a:ktConfig:1.0.0-SNAPSHOT")
}
```

</tab>

<tab title="build.gradle">

```Groovy
repositories {
    maven {
        url "https://s01.oss.sonatype.org/content/repositories/snapshots/"
    }
}

dependencies {
    implementation "dev.s7a:ktConfig:1.0.0-SNAPSHOT"
}
```

</tab>

</tabs>
