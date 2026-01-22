package com.monprojet

import com.monprojet.avro.User
import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.KafkaStreams
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.StreamsConfig
import org.apache.kafka.streams.kstream.KStream
import java.time.Duration
import java.util.*
import kotlin.concurrent.thread

fun main() {
    val bootstrapServers = "localhost:9092"
    val schemaRegistryUrl = "http://localhost:8081"

    // --- 1. LANCEMENT DU STREAM (TRANSFORMATION) ---
    thread {
        val streamProps = Properties().apply {
            put(StreamsConfig.APPLICATION_ID_CONFIG, "stream-processor-v1")
            put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers)
            put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().javaClass)
            put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, SpecificAvroSerde::class.java)
            put("schema.registry.url", schemaRegistryUrl)
        }

        val builder = StreamsBuilder()
        val userStream: KStream<String, User> = builder.stream("mon-topic-avro")

        userStream
            .filter { _, user -> user.getAge() >= 18 }
            .mapValues { user ->
                user.setNom("ADULTE: ${user.getNom().uppercase()}")
                user
            }
            .to("users-majeurs")

        val streams = KafkaStreams(builder.build(), streamProps)
        println("[STREAM] Démarrage du filtrage...")
        streams.start()
    }

    // --- 2. LANCEMENT DU CONSUMER (AFFICHAGE) ---
    thread {
        val consumerProps = Properties().apply {
            put("bootstrap.servers", bootstrapServers)
            put("group.id", "display-group")
            put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
            put("value.deserializer", "io.confluent.kafka.serializers.KafkaAvroDeserializer")
            put("schema.registry.url", schemaRegistryUrl)
            put("specific.avro.reader", "true")
            put("auto.offset.reset", "earliest")
        }

        val consumer = KafkaConsumer<String, User>(consumerProps)
        consumer.subscribe(listOf("users-majeurs"))

        println("[CONSUMER] En attente des résultats sur 'users-majeurs'...")

        while (true) {
            val records = consumer.poll(Duration.ofMillis(500))
            records.forEach { record ->
                val user = record.value()
                println("--- MESSAGE REÇU ---")
                println("Nom modifié : ${user.getNom()}")
                println("Âge vérifié : ${user.getAge()}")
            }
        }
    }
}