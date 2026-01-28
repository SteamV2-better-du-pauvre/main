import java.io.File
import java.net.URLClassLoader

plugins {
    java
    kotlin("jvm") version "2.3.0"
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
    implementation("org.slf4j:slf4j-simple:2.0.9")
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

        val parser = schemaParserClass.getDeclaredConstructor().newInstance()
        val parseMethod = schemaParserClass.getMethod("parse", File::class.java)
        val files = inputDir.listFiles { f -> f.extension == "avsc" }?.toMutableList() ?: mutableListOf()
        val failedFiles = mutableMapOf<File, Throwable>()

        while (files.isNotEmpty()) {
            val processed = mutableListOf<File>()
            val iterator = files.iterator()
            
            while (iterator.hasNext()) {
                val schemaFile = iterator.next()
                try {
                    // 1. Parser le fichier .avsc
                    val schema = parseMethod.invoke(parser, schemaFile)

                    // 2. Initialiser le compilateur
                    val compiler = compilerClass.getConstructor(schemaClass).newInstance(schema)

                    // 3. Lancer la génération du code Java
                    val compileMethod = compilerClass.getMethod("compileToDestination", File::class.java, File::class.java)
                    compileMethod.invoke(compiler, null, outputDir)

                    println("✅ Schéma Avro compilé avec succès : ${schemaFile.name}")
                    processed.add(schemaFile)
                    iterator.remove()
                    failedFiles.remove(schemaFile)
                } catch (e: Exception) {
                    val cause = if (e is java.lang.reflect.InvocationTargetException) e.targetException else e
                    failedFiles[schemaFile] = cause
                }
            }

            if (processed.isEmpty() && files.isNotEmpty()) {
                println("⚠️ Impossible de résoudre les dépendances pour : ${files.map { it.name }}")
                failedFiles.forEach { (f, e) -> 
                    println("Erreur pour ${f.name} : ${e.message}")
                }
                throw GradleException("Erreur de compilation Avro : dépendances circulaires ou types manquants.")
            }
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