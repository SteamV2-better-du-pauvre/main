package com.monprojet

import com.monprojet.avro.User
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerRecord
import java.util.*

fun main() {
    val props = Properties().apply {
        put("bootstrap.servers", "localhost:9092")
        put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
        put("value.serializer", "io.confluent.kafka.serializers.KafkaAvroSerializer")
        put("schema.registry.url", "http://localhost:8081")
    }

    val producer = KafkaProducer<String, User>(props)
    val nomsPossibles = listOf("Alice", "Bob", "Charlie", "David", "Eve", "Frank")

    println("--- DÉMARRAGE DU PRODUCER AUTOMATIQUE ---")

    while (true) {
        val randomId = UUID.randomUUID().toString()
        val randomNom = nomsPossibles.random()
        val randomAge = (10..60).random() // Génère des mineurs et des majeurs

        val user = User.newBuilder()
            .setId(randomId)
            .setNom(randomNom)
            .setAge(randomAge)
            .build()

        val record = ProducerRecord("mon-topic-avro", user.getId().toString(), user)

        producer.send(record) { _, _ ->
            println("Envoi : $randomNom ($randomAge ans)")
        }

        Thread.sleep(3000) // Attend 3 secondes
    }
}