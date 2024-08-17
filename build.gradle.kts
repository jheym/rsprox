import aws.sdk.kotlin.runtime.auth.credentials.EnvironmentCredentialsProvider
import aws.sdk.kotlin.services.s3.S3Client
import aws.sdk.kotlin.services.s3.model.PutObjectRequest
import aws.smithy.kotlin.runtime.content.asByteStream
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import kotlinx.coroutines.runBlocking
import org.jetbrains.kotlin.daemon.common.toHexString
import org.jetbrains.kotlin.gradle.plugin.KotlinPluginWrapper
import java.security.MessageDigest
import kotlin.io.path.fileSize


plugins {
    alias(libs.plugins.kotlin.jvm)
}

allprojects {
    group = "net.rsprox"
    version = "1.0"

    repositories {
        mavenCentral()
        maven("https://repo.openrs2.org/repository/openrs2-snapshots/")
        maven("https://maven.runelab.io/snapshots/")
    }

    tasks.withType<AbstractArchiveTask>().configureEach {
        isPreserveFileTimestamps = false
        isReproducibleFileOrder = true
    }

    plugins.withType<KotlinPluginWrapper> {
        dependencies {
            testImplementation(kotlin("test"))
        }

        tasks.test {
            useJUnitPlatform()
        }

        kotlin {
            jvmToolchain(11)
            explicitApi()
        }
    }
}

subprojects {
    apply(plugin = "org.jetbrains.kotlin.jvm")
}

dependencies {
    runtimeOnly(projects.gui.proxyTool)
    runtimeOnly(projects.proxy)
}

buildscript {
    dependencies {
        classpath(libs.bundles.jackson)
        classpath(libs.aws.sdk.kotlin.s3)
    }
}

data class Dependencies(
    val artifacts: List<Artifact>,
)

data class Artifact(
    val name: String,
    val path: String,
    val size: Long,
    val hash: String,
)

fun sha256Hash(bytes: ByteArray): String {
    val messageDigest = MessageDigest.getInstance("SHA-256")
    messageDigest.update(bytes)
    return messageDigest.digest().toHexString()
}

suspend fun uploadToS3(
    s3Client: S3Client,
    file: File,
    s3Path: String,
): Artifact {
    val request =
        PutObjectRequest {
            bucket = "cdn.rsprox.net"
            key = s3Path
            body = file.asByteStream()
        }
    s3Client.putObject(request)
    println("Uploaded $file to s3://cdn.rsprox.net/dependencies/$s3Path")
    return Artifact(
        file.name,
        "https://cdn.rsprox.net/$s3Path",
        file.toPath().fileSize(),
        sha256Hash(file.readBytes()),
    )
}

tasks.register("uploadJarsToS3") {
    doLast {
        val outputFile = file("dependencies.json")
        val project = project(":gui")
        val artifacts = mutableListOf<Artifact>()

        val projectArtifacts =
            project.configurations.runtimeClasspath
                .get()
                .resolvedConfiguration
                .resolvedArtifacts

        runBlocking {
            S3Client
                .fromEnvironment {
                    region = "eu-west-1"
                    credentialsProvider = EnvironmentCredentialsProvider()
                }.use { s3 ->
                    for (artifact in projectArtifacts) {
                        if (artifact.type != "jar") continue
                        val group =
                            artifact.moduleVersion.id.group
                                .replace('.', '/')
                        val name = artifact.name
                        val version = artifact.moduleVersion.id.version
                        val jarFile = artifact.file

                        val prefix = "dependencies/$group/$name/$version/"
                        val objectName = "$name-$version.jar"

                        artifacts += uploadToS3(s3, jarFile, "$prefix$objectName")
                    }
                }
        }

        println("Uploaded ${artifacts.size} artifacts to S3")
        outputFile.writeText(jacksonObjectMapper().writeValueAsString(Dependencies(artifacts.sortedBy { it.name })))
    }
}

tasks.create<JavaExec>("proxy") {
    environment("APP_VERSION", project.version)
    group = "run"
    classpath = sourceSets["main"].runtimeClasspath
    mainClass.set("net.rsprox.gui.ProxyToolGuiKt")
}

tasks.create<JavaExec>("download") {
    environment("APP_VERSION", project.version)
    group = "run"
    classpath = sourceSets["main"].runtimeClasspath
    mainClass.set("net.rsprox.proxy.cli.ClientDownloadCommandKt")
}

tasks.create<JavaExec>("tostring") {
    environment("APP_VERSION", project.version)
    group = "run"
    classpath = sourceSets["main"].runtimeClasspath
    mainClass.set("net.rsprox.proxy.cli.BinaryToStringCommandKt")
}

tasks.create<JavaExec>("transcribe") {
    environment("APP_VERSION", project.version)
    group = "run"
    classpath = sourceSets["main"].runtimeClasspath
    mainClass.set("net.rsprox.proxy.cli.TranscribeCommandKt")
}

tasks.create<JavaExec>("index") {
    environment("APP_VERSION", project.version)
    group = "run"
    classpath = sourceSets["main"].runtimeClasspath
    mainClass.set("net.rsprox.proxy.cli.IndexerCommandKt")
}

tasks.create<JavaExec>("patch") {
    environment("APP_VERSION", project.version)
    group = "run"
    classpath = sourceSets["main"].runtimeClasspath
    mainClass.set("net.rsprox.proxy.cli.ClientPatcherCommandKt")
}

// fixes some weird error with "Entry classpath.index is a duplicate but no duplicate handling strategy has been set"
// see https://github.com/gradle/gradle/issues/17236
gradle.taskGraph.whenReady {
    val duplicateTasks =
        allTasks
            .filter { it.hasProperty("duplicatesStrategy") }
    for (task in duplicateTasks) {
        task.setProperty("duplicatesStrategy", "EXCLUDE")
    }
}
