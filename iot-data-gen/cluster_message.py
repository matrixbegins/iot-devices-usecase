import json, hashlib
from time import time

class ClusterMessage:
    def __init__(self, device, signal_value):
        self.deviceId = device.get('deviceId')
        self.deviceCluster = device.get('deviceCluster')
        self.signalUnit = device.get('signalUnit')
        self.orgId = device.get('orgId')
        self.facilityId = device.get('facilityId')
        self.signalType = device.get('signalType')

        self.signal_value = round(signal_value, 4)
        self.timestamp = int(time()*1000000)
        self.msg_digest = self.calc_msg_digest()


    def to_key_value(self):
        return f"did|{self.deviceId}|val|{self.signal_value}|cls|{self.deviceCluster}|unit|{self.signalUnit}|org|{self.orgId}|fclt|{self.facilityId}|styp|{self.signalType}|ts|{self.timestamp}|dgst|{self.msg_digest}"


    def to_dict(self):
        return {
            'deviceId': self.deviceId,
            'signalValue': self.signal_value,
            'deviceCluster': self.deviceCluster,
            'signalUnit': self.signalUnit,
            'orgId': self.orgId,
            'facilityId': self.facilityId,
            'signalType': self.signalType,
            'timestamp': self.timestamp,
            'digest': self.msg_digest
        }

    def calc_msg_digest(self):
        h = hashlib.new('sha512')
        payload = f"{self.deviceId}|{self.signal_value}|{self.timestamp}"
        h.update(bytes(payload.encode('utf-8')))
        return h.hexdigest()
