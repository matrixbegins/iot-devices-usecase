from kafka import KafkaAdminClient


def delete_kafka_topic(topic_list: list):
    try:
        admin = KafkaAdminClient(bootstrap_servers='localhost:9092')

        return admin.delete_topics(topic_list)
    except Exception as err:
        print("\n kafka topic creation error .....", err)
        # traceback.print_exception(None, err, err.__traceback__)
        print(".............. ............ ..... \n")


if __name__ == "__main__":
    # print(delete_kafka_topic(['real_txn_created', 'real_txn_updated']))
    print(delete_kafka_topic(['TXN_EVENTS_STATUS_MERGED']))


