#!/usr/bin/env python
import threading, time, json, random, os, sys
import logging

from kafka import KafkaProducer
from datetime import datetime, timedelta
from config import brokers_config, topic_config
from cluster_message import ClusterMessage
from device_inventory import signal_pattern_time_durations, signal_thresholds
from utils import peak_traffic_gen, constant_traffic_gen, pattern_func_map, generate_faulty_signal

logger = logging.getLogger("ClusterProducer")
logger.setLevel(logging.DEBUG)

class ClusterProducer(threading.Thread):
    def __init__(self, signalType, device, pattern, interval, timelimit, unit, msgformat, faulty=False):
        threading.Thread.__init__(self)
        self.stop_event = threading.Event()

        self.device = device
        self.producer_id = device.get('deviceId')
        self.signal_type = signalType
        self.deviceId = device.get('deviceId')
        self.pattern = pattern.upper()
        self.timelimit = timelimit
        self.msg_delay = interval
        self.metric_unit = unit.upper()
        self.msg_format = msgformat.upper()
        self.kafka_topic = topic_config.get('cluster')
        self.brokers = brokers_config.get(os.getenv('APP_ENV', 'local'))

        self.gen_faulty = faulty
        self.counter = 0

        logger.debug("Producer params: %s", vars(self))

        self.producer = KafkaProducer(bootstrap_servers=self.brokers,
                    value_serializer= bytes if self.msg_format == 'K' else  lambda v: json.dumps(v).encode('utf-8'),
                    client_id= self.producer_id, acks=0, compression_type='gzip')


    def stop(self):
        self.stop_event.set()


    def run(self):

        while not self.stop_event.is_set():
            try:
                self.generate_signals()
                self.producer.flush()

            except Exception as err:
                logger.error(f"[{self.producer_id}]:: \t .... ERROR:: %s", err)
                raise err

        self.producer.flush()
        self.producer.close()
        logger.info(f"[{self.producer_id}]:: messages published: %s", self.counter)


    def generate_signals(self):
        # identify thresholds/ range to generate values
        min_val, normal_val, max_val, threshold_val = signal_thresholds.get(self.signal_type).get(self.metric_unit).values()

        # identify time durations for each step
        ramp_up_time, ramp_down_time, peak_time, normal_time = signal_pattern_time_durations.get(self.pattern).values()

        # find ramp_up and ramp_down functions based on traffic pattern
        ramp_up_function, ramp_down_function  = pattern_func_map.get(self.pattern).values()
        constant_flag = True if self.pattern == 'C' else False

        # generate normal traffic with normal value
        normal_arr = self.execute_func_for_seconds(normal_time, constant_traffic_gen, x_start=normal_val, constant=constant_flag )
        ###### print("\n normal_arr:: ", normal_arr)

        # now start ramp up from last generated value
        last_gen_value = normal_arr[-1]
        ramp_up_arr = self.execute_func_for_seconds(ramp_up_time, ramp_up_function, x_start=last_gen_value, constant=constant_flag )
        ###### print("\n ramp_up_arr:: ", ramp_up_arr)

        # now start peak generating value above threshold for peak traffic
        last_gen_value = min(ramp_up_arr[-1], threshold_val)
        peak_arr = self.execute_func_for_seconds(peak_time, peak_traffic_gen, x_start=last_gen_value, constant=constant_flag )
        ###### print("\n peak_arr:: ", peak_arr)

        # now ramping down traffic
        last_gen_value = peak_arr[-1]
        ramp_down_arr = self.execute_func_for_seconds(ramp_down_time, ramp_down_function, y_start=last_gen_value, constant=constant_flag )
        ###### print("\n ramp_down_arr:: ", ramp_down_arr)


    def execute_func_for_seconds(self, seconds_, func, x_start=0, y_start=0, constant=False):
        end_time = datetime.now() + timedelta(seconds=seconds_)

        y_data = []
        x = x_start
        y = y_start     # this will only come in when we want to ramp down
        while datetime.now() < end_time:
            # for ramp down the dependent variable is different
            y = func(y) if y_start > 0 else func(x)

            if self.gen_faulty:
                y = generate_faulty_signal(y)

            y_data.append(y)

            # to generate constant traffic do not increase the dependent variable
            x = x if constant else x + 1

            # send message tp kafka
            self.publish_message(y)
            if self.msg_delay > 0:
                time.sleep(self.msg_delay)

        return y_data


    def publish_message(self, signalVal):
        msg = ClusterMessage(self.device, signalVal)
        payload = msg.to_dict() if self.msg_format == 'J' else msg.to_key_value()

        self.producer.send(self.kafka_topic, payload)
        self.counter = self.counter + 1
        # logger.info(f"[{self.producer_id}]:: new event published: %s", payload)
