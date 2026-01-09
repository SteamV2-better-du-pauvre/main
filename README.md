# KAFKA
Pour lancer docker : 
```
docker compose up -d
```
Puis 
```
 docker exec --workdir /opt/kafka/bin/ -it broker-1 sh

```
Pour crée : 
```
./kafka-topics.sh --bootstrap-server broker-1:19092,broker-2:19092,broker-3:19092 --create --topic test-topic
```
Pour lire : 
```
./kafka-console-consumer.sh --bootstrap-server broker-1:19092,broker-2:19092,broker-3:19092 --topic test-topic --from-beginning
```
Pour écire : 
```
./kafka-console-producer.sh --bootstrap-server broker-1:19092,broker-2:19092,broker-3:19092 --topic test-topic
```
