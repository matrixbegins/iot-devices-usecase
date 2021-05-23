
import traceback, os
from kafka import KafkaAdminClient
from kafka.admin import NewTopic


broker = os.getenv('KAFKA_BROKER', 'localhost:9092')

# broker = ["b-1.cluster-x.3jcqhd.c13.kafka.us-east-1.amazonaws.com:9092", "b-2.cluster-x.3jcqhd.c13.kafka.us-east-1.amazonaws.com:9092"]

def create_kafka_topic():
    try:
        print("connecting to broker:: ", broker)
        admin = KafkaAdminClient(bootstrap_servers=broker)

        topic_entry_json = NewTopic(name='device_events_entry_json',
                        num_partitions=52, replication_factor=2, topic_configs={'retention.ms': 8640000000} )

        topic_entry_kv = NewTopic(name='device_events_entry_key_value',
                        num_partitions=52, replication_factor=2, topic_configs={'retention.ms': 8640000000})

        topic_tampred = NewTopic(name='tampered_signals_topic',
                        num_partitions=20, replication_factor=2, topic_configs={'retention.ms': 8640000000})

        topic_device_cluster = NewTopic(name='device_cluster_faulty_topic',
                        num_partitions=5, replication_factor=2, topic_configs={'retention.ms': 8640000000})

        topic_faulty = NewTopic(name='faulty_signals_topic',
                        num_partitions=32, replication_factor=2, topic_configs={'retention.ms': 8640000000})


        admin.create_topics([topic_entry_json, topic_entry_kv, topic_tampred, topic_device_cluster, topic_faulty])

    except Exception as err:
        print("\n kafka topic creation error .....", err)
        traceback.print_exception(None, err, err.__traceback__)
        print(".............. ............ ..... \n")



if __name__ == "__main__":
    create_kafka_topic()
    print("\n\n TOPIC Created !!!")

