import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.springframework.boot") version "2.2.6.RELEASE"
	id("io.spring.dependency-management") version "1.0.9.RELEASE"
	kotlin("jvm") version "1.3.71"
	kotlin("plugin.spring") version "1.3.71"
}

group = "com.example"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_1_8

val imageName = "eu.gcr.io/i-on-dev/example-ci-cd"
val tagName: String by project

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
	testImplementation("org.springframework.boot:spring-boot-starter-test") {
		exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
	}
}

tasks.register<Copy>("extractUberJar") {
	dependsOn("build")
	from(zipTree("$buildDir/libs/${rootProject.name}-$version.jar"))
	into("build/dependency")
}

tasks.register<Exec>("buildDockerImage") {
	dependsOn("extractUberJar")
	commandLine("docker", "build", "-t", "$imageName:$tagName", ".")
}

tasks.register<Exec>("pushDockerImageToGcp") {
	dependsOn("buildDockerImage")
	commandLine("docker", "push", "$imageName:$tagName")
}

tasks.withType<Test> {
	useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs = listOf("-Xjsr305=strict")
		jvmTarget = "1.8"
	}
}
