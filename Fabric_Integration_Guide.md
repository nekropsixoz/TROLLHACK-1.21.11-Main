# Интеграция TrafficConeRenderer в Fabric мод Minecraft 1.21.11

## 1. Структура проекта Fabric мода

```
your-mod/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── yourname/
│   │   │           └── trafficconemod/
│   │   │               ├── TrafficConeMod.java           # Основной класс мода
│   │   │               ├── TrafficConeClient.java        # Клиентский класс
│   │   │               └── TrafficConeRenderer.java      # Ваш файл рендера
│   │   └── resources/
│   │       ├── fabric.mod.json                           # Метаданные мода
│   │       └── assets/
│   └── test/
├── build.gradle
├── gradle.properties
└── gradlew / gradlew.bat
```

## 2. Размещение TrafficConeRenderer.java

Положите ваш файл `TrafficConeRenderer.java` в:
```
src/main/java/com/yourname/trafficconemod/TrafficConeRenderer.java
```

## 3. Основной класс мода (TrafficConeMod.java)

```java
package com.yourname.trafficconemod;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TrafficConeMod implements ModInitializer {
    public static final String MOD_ID = "trafficconemod";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        LOGGER.info("Traffic Cone Mod initialized!");
    }
}
```

## 4. Клиентский класс (TrafficConeClient.java)

```java
package com.yourname.trafficconemod;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;

public class TrafficConeClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        // Регистрируем рендер после рендера всех сущностей
        WorldRenderEvents.AFTER_ENTITIES.register(context -> {
            MinecraftClient client = MinecraftClient.getInstance();
            
            if (client.player != null && client.world != null) {
                // Получаем матрицы и провайдер вершин
                MatrixStack matrices = context.matrixStack();
                VertexConsumerProvider vertexConsumers = context.vertexConsumers();
                
                // Получаем позицию камеры для смещения рендера
                Vec3d cameraPos = client.gameRenderer.getCamera().getPos();
                
                // Сохраняем состояние матрицы
                matrices.push();
                
                try {
                    // Смещаем рендер относительно позиции камеры
                    matrices.translate(-cameraPos.x, -cameraPos.y, -cameraPos.z);
                    
                    // Рендерим конус на голове всех игроков
                    for (PlayerEntity player : client.world.getPlayers()) {
                        if (player.isAlive() && !player.isInvisible()) {
                            // Получаем уровень освещения для позиции игрока
                            int light = client.getWorld().getLightLevel(player.getBlockPos());
                            
                            // Рендерим конус
                            TrafficConeRenderer.renderTrafficCone(
                                matrices, 
                                vertexConsumers, 
                                player, 
                                light
                            );
                        }
                    }
                    
                } finally {
                    matrices.pop();
                }
            }
        });
        
        TrafficConeMod.LOGGER.info("Traffic Cone Client initialized!");
    }
}
```

## 5. Файл fabric.mod.json

```json
{
    "schemaVersion": 1,
    "id": "trafficconemod",
    "version": "1.0.0",
    "name": "Traffic Cone Mod",
    "description": "Adds traffic cones on player heads",
    "authors": ["YourName"],
    "contact": {},
    "license": "MIT",
    "icon": "assets/trafficconemod/icon.png",
    "environment": "client",
    "entrypoints": {
        "main": [
            "com.yourname.trafficconemod.TrafficConeMod"
        ],
        "client": [
            "com.yourname.trafficconemod.TrafficConeClient"
        ]
    },
    "depends": {
        "fabricloader": ">=0.16.0",
        "fabric-api": "*",
        "minecraft": "~1.21.1"
    }
}
```

## 6. Файл build.gradle

```gradle
plugins {
    id 'fabric-loom' version '1.7-SNAPSHOT'
    id 'maven-publish'
}

version = project.mod_version
group = project.maven_group

base {
    archivesName = project.archives_base_name
}

repositories {
    maven { url 'https://maven.fabricmc.net/' }
}

loom {
    splitEnvironmentSourceSets()
    mods {
        "trafficconemod" {
            sourceSet sourceSets.main
            sourceSet sourceSets.client
        }
    }
}

dependencies {
    minecraft "com.mojang:minecraft:${project.minecraft_version}"
    mappings "net.fabricmc:yarn:${project.yarn_mappings}:v2"
    modImplementation "net.fabricmc:fabric-loader:${project.loader_version}"

    // Fabric API
    modImplementation "net.fabricmc.fabric-api:fabric-api:${project.fabric_version}"
}

processResources {
    inputs.property "version", project.version

    filesMatching("fabric.mod.json") {
        expand "version": project.version
    }
}

tasks.withType(JavaCompile).configureEach {
    it.options.release = 21
}

java {
    withSourcesJar()

    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

jar {
    from("LICENSE") {
        rename { "${it}_${project.base.archivesName.get()}" }
    }
}

publishing {
    publications {
        create("mavenJava", MavenPublication) {
            artifact(remapJar) {
                builtBy remapJar
            }
            artifact(sourcesJar) {
                builtBy remapSourcesJar
            }
        }
    }

    repositories {
        // Добавьте свои репозитории если нужно
    }
}
```

## 7. Файл gradle.properties

```properties
org.gradle.jvmargs=-Xmx2G
fabric.loom.multiProjectOptimisation=true

# Mod Properties
mod_version = 1.0.0
maven_group = com.yourname
archives_base_name = trafficconemod

# Dependencies
minecraft_version=1.21.1
yarn_mappings=1.21.1+build.9
loader_version=0.16.7
fabric_version=0.102.0+1.21.1
```

## 8. Сборка мода через Gradle

Выполните в терминале в корне проекта:

```bash
# Для Windows
gradlew build

# Для Linux/Mac
./gradlew build
```

После сборки мод будет находиться в:
```
build/libs/trafficconemod-1.0.0.jar
```

## 9. Установка и проверка

1. **Установка мода:**
   - Скопируйте `trafficconemod-1.0.0.jar` в `.minecraft/mods/`
   - Убедитесь что установлен Fabric Loader для Minecraft 1.21.1

2. **Запуск игры:**
   - Запустите Minecraft с Fabric Loader
   - Создайте новый мир или зайдите в существующий

3. **Проверка работы:**
   - Войдите в мир в режиме выживания или творчества
   - На голове вашего игрока должен появиться оранжевый конус
   - Конус должен быть виден также на других игроках в мультиплеере

4. **Отладка:**
   - Проверьте логи в `.minecraft/logs/latest.log`
   - Ищите сообщения "Traffic Cone Mod initialized!" и "Traffic Cone Client initialized!"
   - Если конус не отображается, проверьте что мод загружен в меню "Mods"

## 10. Возможные проблемы и решения

**Проблема:** Конус не отображается
**Решение:** Проверьте что Fabric API установлен и версия Minecraft совпадает

**Проблема:** Ошибки компиляции
**Решение:** Убедитесь что все импорты правильные для версии 1.21.1

**Проблема:** Конус отображается в неправильном месте
**Решение:** Отрегулируйте смещение в TrafficConeRenderer.java

## 11. Дополнительные улучшения

Для добавления переключателя конуса можно добавить команду:

```java
// В TrafficConeClient.java
@Override
public void onInitializeClient() {
    // Регистрация команды
    CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
        dispatcher.register(literal("trafficcone")
            .executes(context -> {
                // Переключение состояния конуса
                return 1;
            })
        );
    });
    
    // Ваш код рендера...
}
```

Теперь ваш мод с дорожным конусом готов к использованию!
