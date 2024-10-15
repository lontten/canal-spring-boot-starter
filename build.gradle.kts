import com.vanniktech.maven.publish.SonatypeHost

plugins {
    java
    id("java-library")
    id("com.vanniktech.maven.publish") version "0.29.0"
}
group = "io.github.lontten"
version = "2.117.0.RELEASE"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}
tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}
tasks.withType<Javadoc> {
    options.encoding = "UTF-8"
}
configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

mavenPublishing {
    publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)
    signAllPublications()

    coordinates(rootProject.group.toString(), rootProject.name, rootProject.version.toString())

    pom {
        name = rootProject.name
        description = "canal-spring-boot-starter"
        inceptionYear = "2024"
        url = "https://github.com/lontten/canal-spring-boot-starter/"
        licenses {
            license {
                name = "The Apache License, Version 2.0"
                url = "http://www.apache.org/licenses/LICENSE-2.0.txt"
                distribution = "http://www.apache.org/licenses/LICENSE-2.0.txt"
            }
        }
        developers {
            developer {
                id = "lontten"
                name = "lontten"
                url = "https://github.com/lontten/"
            }
        }
        scm {
            url = "https://github.com/lontten/canal-spring-boot-starter/"
            connection = "scm:git:git://github.com/lontten/canal-spring-boot-starter.git"
            developerConnection = "scm:git:ssh://github.com:lontten/canal-spring-boot-starter.git"
        }
    }
}



repositories {
    maven { url = uri("https://repo.maven.apache.org/maven2") }

    maven { url = uri("https://jitpack.io") }
    mavenCentral()
    maven { url = uri("https://repo.spring.io/milestone") }
    maven { url = uri("https://repo.spring.io/snapshot") }
}
val lombokVersion = "1.18.34"
dependencies {
    // canal
    api(libs.bundles.canal)

    // springboot
    implementation(libs.bundles.springboot)


    // lombok
    compileOnly("org.projectlombok:lombok:${lombokVersion}")
    annotationProcessor("org.projectlombok:lombok:${lombokVersion}")
    testCompileOnly("org.projectlombok:lombok:${lombokVersion}")
    testAnnotationProcessor("org.projectlombok:lombok:${lombokVersion}")

    // test
    testImplementation(libs.junit.jupiter)
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}