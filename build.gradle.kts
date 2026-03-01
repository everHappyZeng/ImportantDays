// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
}

// 添加镜像源配置
buildscript {
    repositories {
        // 阿里云镜像（放在最前面）
        maven { url = uri("https://maven.aliyun.com/repository/public") }
        maven { url = uri("https://maven.aliyun.com/repository/google") }
        maven { url = uri("https://maven.aliyun.com/repository/gradle-plugin") }
        maven { url = uri("https://maven.aliyun.com/repository/central") }

        // 华为云镜像（备用）
        maven { url = uri("https://repo.huaweicloud.com/repository/maven") }

        // 腾讯云镜像（备用）
        maven { url = uri("https://mirrors.cloud.tencent.com/nexus/repository/maven-public") }

        google()
        mavenCentral()
        gradlePluginPortal()
    }

    dependencies {
        // 如果需要特定版本的插件，可以在这里添加
        // classpath("com.android.tools.build:gradle:8.3.2")
        // classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.9.0")
    }
}

// 为所有子项目配置仓库
allprojects {
    repositories {
        // 阿里云镜像（放在最前面）
        maven { url = uri("https://maven.aliyun.com/repository/public") }
        maven { url = uri("https://maven.aliyun.com/repository/google") }

        // 华为云镜像（备用）
        maven { url = uri("https://repo.huaweicloud.com/repository/maven") }

        google()
        mavenCentral()

        // 如果有其他特殊仓库
        maven { url = uri("https://jitpack.io") }
        maven { url = uri("https://oss.sonatype.org/content/repositories/snapshots") }
    }
}