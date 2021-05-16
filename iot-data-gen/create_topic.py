
import traceback, os
from kafka import KafkaAdminClient
from kafka.admin import NewTopic


broker = os.getenv('KAFKA_BROKER', 'localhost:9092')

# broker = ["b-1.cluster-x.3jcqhd.c13.kafka.us-east-1.amazonaws.com:9092", "b-2.cluster-x.3jcqhd.c13.kafka.us-east-1.amazonaws.com:9092"]

def create_kafka_topic():
    try:
        print("connecting to broker:: ", broker)
        admin = KafkaAdminClient(bootstrap_servers=broker)

        topic_new_txn = NewTopic(name='real_txn_created',
                        num_partitions=5, replication_factor=1, topic_configs={'retention.ms': 8640000000} )
        # topic_update_txn = NewTopic(name='real_txn_updated',
        #                 num_partitions=1, replication_factor=1, topic_configs={'retention.ms': 8640000000})
        admin.create_topics([topic_new_txn])
    except Exception as err:
        print("\n kafka topic creation error .....", err)
        traceback.print_exception(None, err, err.__traceback__)
        print(".............. ............ ..... \n")



if __name__ == "__main__":
    create_kafka_topic()
    print("\n\n TOPIC Created !!!")


