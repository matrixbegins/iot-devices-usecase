import json, hashlib, random
from time import time

class Message:
    def __init__(self, deviceId, signal_value):
        self.deviceId = deviceId
        self.signal_value = round(signal_value, 4)
        self.timestamp = int(time()*1000000)
        self.msg_digest = self.calc_msg_digest()


    def to_key_value(self):
        return f"did|{self.deviceId}|val|{self.signal_value}|ts|{self.timestamp}|dgst|{self.msg_digest}"


    def to_dict(self):
        return {
            'deviceId': self.deviceId,
            'signalValue': self.signal_value,
            'timestamp': self.timestamp,
            'digest': self.msg_digest
        }


    def calc_msg_digest(self):
        h = hashlib.new('sha512')
        payload = f"{self.deviceId}|{self.signal_value}|{self.timestamp}"
        h.update(bytes(payload.encode('utf-8')))
        digest = h.hexdigest()

        if random.uniform(0, 0.1) < 10e-9:
            digest = digest[:-1]
            print("tampered signal")

        return digest


