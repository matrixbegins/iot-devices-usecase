brokers_config = {
    'local': 'localhost:9092',
    'prod': ['kafka-0:9092', 'kafka-2:9092', 'kafka-2:9092']
}

topic_config = {
    'J': 'device_events_entry_json',
    'K': 'device_events_entry_key_value',
    'cluster': 'device_cluster_faulty_topic'
}
