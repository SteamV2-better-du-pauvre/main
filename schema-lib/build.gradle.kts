import java.io.File
import java.net.URLClassLoader

plugins {
    java
    kotlin("jvm") version "1.9.20"
}

group = "com.monprojet"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

// Définition d'une configuration pour charger les outils Avro [cite: 4]
val avroTools: Configuration by configurations.creating

dependencies {
    implementation("org.apache.avro:avro:1.11.3")
    avroTools("org.apache.avro:avro-compiler:1.11.3")
}

tasks.register("generateAvro") {
    group = "build"
    val inputDir = file("src/main/avro")
    val outputDir = file("build/generated-main-avro-java")

    inputs.dir(inputDir)
    outputs.dir(outputDir)

    doLast {
        outputDir.deleteRecursively()
        outputDir.mkdirs()

        // Chargement dynamique des outils Avro
        val cp = avroTools.files.map { it.toURI().toURL() }.toTypedArray()
        val loader = URLClassLoader(cp, ClassLoader.getSystemClassLoader())

        // Chargement des classes nécessaires
        val schemaClass = loader.loadClass("org.apache.avro.Schema")
        val compilerClass = loader.loadClass("org.apache.avro.compiler.specific.SpecificCompiler")
        val schemaParserClass = loader.loadClass("org.apache.avro.Schema\$Parser")

        inputDir.listFiles { f -> f.extension == "avsc" }?.forEach { schemaFile ->
            // 1. Parser le fichier .avsc [cite: 6]
            val parser = schemaParserClass.getDeclaredConstructor().newInstance()
            val parseMethod = schemaParserClass.getMethod("parse", File::class.java)
            val schema = parseMethod.invoke(parser, schemaFile)

            // 2. Initialiser le compilateur (Correction de la NoSuchMethodException)
            // On utilise explicitement schemaClass (org.apache.avro.Schema) pour le constructeur
            val compiler = compilerClass.getConstructor(schemaClass).newInstance(schema)

            // 3. Lancer la génération du code Java [cite: 7]
            val compileMethod = compilerClass.getMethod("compileToDestination", File::class.java, File::class.java)
            compileMethod.invoke(compiler, null, outputDir)

            println("✅ Schéma Avro compilé avec succès : ${schemaFile.name}")
        }
    }
}

sourceSets {
    main {
        java {
            srcDirs("build/generated-main-avro-java")
        }
    }
}

tasks.named("compileKotlin") {
    dependsOn("generateAvro")
}

kotlin {
    jvmToolchain(21)
}