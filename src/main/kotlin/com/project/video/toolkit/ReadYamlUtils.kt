package com.project.video.toolkit

import org.yaml.snakeyaml.Yaml

class ReadYamlUtils {

    companion object {
        fun readConfig(): Map<String, Any> {
            return Yaml().loadAs(
                this::class.java.classLoader.getResourceAsStream("config.yml"),
                Map::class.java
            )
        }

        fun readConfigProperty(key: String): Any? {
            val yaml = readConfig()
            return yaml[key]
        }
    }

}