plugins {
    java
    id("org.springframework.boot") version "3.5.0"
}

java {
    sourceCompatibility = JavaVersion.VERSION_25
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(platform("org.springframework.boot:spring-boot-dependencies:3.5.0"))
    // Spring Boot starters
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-configuration-processor")
    implementation("org.springframework.boot:spring-boot-starter-validation")

    implementation("com.zaxxer:HikariCP")
    implementation("org.postgresql:postgresql")
    implementation("org.liquibase:liquibase-core")
    implementation("com.github.pengrad:java-telegram-bot-api:7.11.0")
    implementation("com.google.code.gson:gson")
    implementation("io.vavr:vavr:0.10.7")
    implementation("org.apache.poi:poi-ooxml:5.3.0")
    implementation("org.jxls:jxls-jexcel:1.0.9")
    implementation("org.dhatim:fastexcel-reader:0.18.1")
    implementation("org.dhatim:fastexcel:0.18.1")

    // Testing
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}