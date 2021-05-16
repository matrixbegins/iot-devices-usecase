import random

def ramp_up_linear(x, intercept = 1):
    y = (0.5 * x) + intercept
    if random.choice(range(0,2)) == 0:
        return y - y * 0.05
    else:
        return y + y * 0.05


def ramp_down_linear(y, percentage=10.00):
    return y - (y * random.uniform(1, percentage))/100


def peak_traffic_gen(y):
    # whatever value we get from we start from 20% more value then generate new values on top of that
    y = y + y * 0.2
    if random.choice(range(0,2)) == 0:
        return y - y * 0.05
    else:
        return y + y * 0.05


def peak_traffic_negative_gen(y):
    # whatever value we get from we start from 20% less value, then generate new values on top of that
    y = y - y * 0.2
    if random.choice(range(0,2)) == 0:
        return y - y * 0.05
    else:
        return y + y * 0.05


def constant_traffic_gen(y):
    if random.choice(range(0,2)) == 0:
        return y - y * 0.04
    else:
        return y + y * 0.04


def exponential_growth(x, initial=10, rate=0.02):
    return initial * (( 1 + rate ) ** x)


def exponential_decay(x, base=10, rate=0.5):
    return x - ( x * rate )


def generate_faulty_signal(y, flat=20):
    sample = random.uniform(0, 0.25)

    if random.choice(range(0,2)) == 0:
        flat = flat - flat * sample
    else:
        return flat + flat * sample

    return y + flat


pattern_func_map = {
    'L': {
        'ramp_up': ramp_up_linear,
        'ramp_down': ramp_down_linear,
    },
    'E': {
        'ramp_up': exponential_growth,
        'ramp_down': exponential_decay,
    },
    'S': {
        'ramp_up': exponential_growth,
        'ramp_down': exponential_decay,
    },
    'C': {
        'ramp_up': constant_traffic_gen,
        'ramp_down': constant_traffic_gen,
    }
}
