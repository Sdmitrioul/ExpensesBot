plugins {
    java
    id("org.springframework.boot") version "3.5.0"
    id("io.spring.dependency-management") version "1.1.5"
}

java {
    sourceCompatibility = JavaVersion.VERSION_25
}

repositories {
    mavenCentral()
}

dependencies {
    // Spring Boot starters
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")

    //RocksDB dependency
    implementation("org.rocksdb:rocksdbjni:9.8.4")
    //Telegram bot library
    implementation("com.github.pengrad:java-telegram-bot-api:7.11.0")
    //Google libraries
    implementation("com.google.api-client:google-api-client:2.0.0")
    implementation("com.google.oauth-client:google-oauth-client-jetty:1.34.1")
    implementation("com.google.apis:google-api-services-sheets:v4-rev20220927-2.0.0")

    implementation("com.google.code.gson:gson")
    implementation("io.vavr:vavr:0.10.7")
    implementation("org.apache.poi:poi-ooxml:5.4.0")
    implementation("org.jxls:jxls-jexcel:1.0.9")
    implementation("org.dhatim:fastexcel-reader:0.18.1")
    implementation("org.dhatim:fastexcel:0.18.1")
    //Graphics
    implementation("org.knowm.xchart:xchart:3.2.2")

    // Testing
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}