plugins {
	java
	id("org.springframework.boot") version "2.6.0"
	id("io.spring.dependency-management") version "1.1.5"
}

group = "com.example"
version = "0.0.1-SNAPSHOT"

java {
	sourceCompatibility = JavaVersion.VERSION_17
}

configurations {
	compileOnly {
		extendsFrom(configurations.annotationProcessor.get())
	}
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-security")
	implementation("org.springframework.boot:spring-boot-starter-web")

	compileOnly("org.projectlombok:lombok")
	annotationProcessor("org.projectlombok:lombok")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.springframework.security:spring-security-test")
	implementation("com.oracle.database.jdbc:ojdbc8-production:19.7.0.0")

	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
	implementation ("io.springfox:springfox-boot-starter:3.0.0")
	implementation ("javax.servlet:javax.servlet-api:4.0.1")
	implementation("org.jgrapht:jgrapht-core:1.5.1")


	implementation ("io.jsonwebtoken:jjwt-api:0.11.2")
	runtimeOnly ("io.jsonwebtoken:jjwt-impl:0.11.2")
	runtimeOnly ("io.jsonwebtoken:jjwt-jackson:0.11.2")
	implementation ("org.springframework.boot:spring-boot-starter-oauth2-resource-server")

	implementation ("io.jsonwebtoken:jjwt:0.2")
	implementation ("com.auth0:java-jwt:4.4.0")
	implementation ("com.nimbusds:nimbus-jose-jwt:9.31")


}

tasks.withType<Test> {
	useJUnitPlatform()
}
