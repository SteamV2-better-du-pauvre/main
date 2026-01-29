import java.io.File
import java.net.URLClassLoader

plugins {
    `java-library`
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
    api("org.apache.avro:avro:1.11.3")
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

        val parserConstructor = schemaParserClass.getDeclaredConstructor()
        val parseMethod = schemaParserClass.getMethod("parse", File::class.java)
        val getTypesMethod = schemaParserClass.getMethod("getTypes")
        val addTypesMethod = schemaParserClass.getMethod("addTypes", Map::class.java)
        
        val files = inputDir.listFiles { f -> f.extension == "avsc" }?.toMutableList() ?: mutableListOf()
        val failedFiles = mutableMapOf<File, Throwable>()
        val validTypes = mutableMapOf<String, Any>()

        while (files.isNotEmpty()) {
            val processed = mutableListOf<File>()
            val iterator = files.iterator()
            
            while (iterator.hasNext()) {
                val schemaFile = iterator.next()
                try {
                    // 1. Créer un nouveau parser pour éviter la pollution ("Can't redefine")
                    val parser = parserConstructor.newInstance()
                    
                    // 2. Injecter les types déjà connus
                    addTypesMethod.invoke(parser, validTypes)

                    // 3. Parser le fichier .avsc
                    val schema = parseMethod.invoke(parser, schemaFile)

                    // 4. Récupérer et sauvegarder les nouveaux types
                    val types = getTypesMethod.invoke(parser) as Map<String, Any>
                    validTypes.putAll(types)

                    // 5. Initialiser le compilateur
                    val compiler = compilerClass.getConstructor(schemaClass).newInstance(schema)

                    // 6. Lancer la génération du code Java
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