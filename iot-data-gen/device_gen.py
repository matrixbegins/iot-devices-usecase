import pprint, random, shortuuid
from device_inventory import devices, signal_pattern_time_durations, signal_thresholds
import hashlib, random, time
from message import Message
from datetime import datetime, timedelta
from utils import ramp_up_linear, ramp_down_linear, peak_traffic_gen, constant_traffic_gen, pattern_func_map

devtypes = ['temperature', 'pressure', 'radiation', 'heartbeat', 'bloodpressure']

output_dict = {
    'temperature': [],
    'pressure': [],
    'radiation': [],
    'heartbeat': [],
    'bloodpressure': [],
}

for t in devtypes:
    for i in range(0,201):
        output_dict[t].append(shortuuid.random(10).upper())

pprint.pprint(output_dict)

# a = random.sample(devices.get('temperature'), 3)
# print(a)

# msg = Message(a[0], 56)

# # print(msg.to_dict())
# print(msg.to_key_value())

# h = hashlib.new('sha512')

# payload = f"{msg.deviceId}|{msg.signal_value}|{msg.timestamp}"
# h.update(bytes(payload.encode('utf-8')))
# print('re calc digest:  ', h.hexdigest())


# def execute_func_for_seconds(seconds_, func, x_start=0,  y_start=0, constant=False, delay=0.5):
#     end_time = datetime.now() + timedelta(seconds=seconds_)

#     y_data = []
#     x = x_start
#     y = y_start     # this will only come in when we want to ramp down
#     while datetime.now() < end_time:
#         y = func(y) if y_start > 0 else func(x)
#         y_data.append(y)
#         x = x if constant else x + 1
#         time.sleep(delay)

#     return y_data


# def generate_signals(signal_pattern, singal_type, metric_unit):

#     # identify time durations for each step
#     ramp_up_time, ramp_down_time, peak_time = signal_pattern_time_durations.get(signal_pattern).values()

#     # identify thresholds/ range to generate values
#     min_val, normal_val, max_val, threshold_val = signal_thresholds.get(singal_type).get(metric_unit).values()

#     output_arr = []
#     end_time = datetime.now() + timedelta(seconds=300)
#     while datetime.now() < end_time:

#         # find ramp_up and ramp_down functions based on traffic pattern
#         ramp_up_function, ramp_down_function  = pattern_func_map.get(signal_pattern).values()
#         constant_flag = True if signal_pattern == 'C' else False

#         # generate normal traffic with normal value
#         normal_arr = execute_func_for_seconds(20, constant_traffic_gen, x_start=normal_val, constant=constant_flag )
#         print("\n normal_arr:: ", normal_arr)
#         output_arr.extend(normal_arr)

#         # now start ramp up from last generated value
#         last_gen_value = normal_arr[-1]
#         ramp_up_arr = execute_func_for_seconds(ramp_up_time, ramp_up_function, x_start=last_gen_value, constant=constant_flag )
#         print("\n ramp_up_arr:: ", ramp_up_arr)
#         output_arr.extend(ramp_up_arr)

#         # now start peak generating value above threshold for peak traffic
#         last_gen_value = min(ramp_up_arr[-1], threshold_val)
#         peak_arr = execute_func_for_seconds(peak_time, peak_traffic_gen, x_start=last_gen_value, constant=constant_flag )
#         print("\n peak_arr:: ", peak_arr)
#         output_arr.extend(peak_arr)

#         # now ramping down traffic
#         last_gen_value = peak_arr[-1]
#         ramp_down_arr = execute_func_for_seconds(ramp_down_time, ramp_down_function, y_start=last_gen_value, constant=constant_flag )
#         print("\n ramp_down_arr:: ", ramp_down_arr)
#         output_arr.extend(ramp_down_arr)

#     return output_arr

# signal_pattern = 'C'
# singal_type = 'temperature'
# metric_unit = 'C'

# data = generate_signals(signal_pattern, singal_type, metric_unit)

# print("\n ================ \n", data)

# signal_pattern = 'E'
# singal_type = 'temperature'
# metric_unit = 'C'

# data = generate_signals(signal_pattern, singal_type, metric_unit)

# print("\n ================ \n", data)


# signal_pattern = 'L'
# singal_type = 'temperature'
# metric_unit = 'F'

# data = generate_signals(signal_pattern, singal_type, metric_unit)

# print("\n ================ \n", data)