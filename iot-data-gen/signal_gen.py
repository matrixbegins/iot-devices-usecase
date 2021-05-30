import click, random, time, logging

from producer import Producer
from cluster_producer import ClusterProducer
from device_inventory import devices as device_master_list

logging.basicConfig(
    filename='signal-producer.log', filemode='w',
  format="%(asctime)s :%(name)s : %(levelname)s : %(message)s"
)

@click.group()
def cli():
    pass

@click.command('temp', short_help='Generates temperature metric')
@click.option('-p', '--pattern', envvar='ZEUS_IOT_PATTERN',
                    default='L',
                    prompt='Signal pattern:\n - [L]inear \n - [E]xponential \n - [C]onstant \n - [S]pike \n >> ',
                    type=click.Choice(['L', 'E', 'C', 'S'], case_sensitive=False),
                    help='Pattern of the singal to change linear, exponential, const, spike')
@click.option('-i', '--interval', envvar='ZEUS_IOT_INTERVAL',
                    default=0.5,
                    prompt="Time Interval(in seconds) enter 0 for no delay: ",
                    help='The interval between generating consequent signals in seconds.')
@click.option('-l', '--timelimit', envvar='ZEUS_IOT_TIME_LIMIT',
                    prompt="How Long to maintain the peak pattern(in seconds): ",
                    default=30,
                    help='How Long to maintain the peak pattern for singals(in seconds) in case of Linear, exp, spike traffic pattern')
@click.option('-u', '--unit', default='c', envvar='ZEUS_IOT_METRIC_UNIT',
                    prompt="Enter Unit of temperature to generate signal: \n - [C]elsius \n - [F]ahrenheit \n - [K]elvin \n >> ",
                    type=click.Choice(['C', 'F', 'K'], case_sensitive=False),
                    help='Measurement unit')
@click.option('-n', '--devices', default=1, envvar='ZEUS_IOT_NO_DEVICES',
                    type=click.IntRange(0, 100, clamp=True),
                    prompt="Enter number of devices: ", help='Number of devices to generate signals')
@click.option('-f', '--msgformat', default='J', envvar='ZEUS_IOT_MSG_FORMAT',
                    prompt="Enter message format: \n - [J]SON \n - [K]ey Value \n >> ",
                    type=click.Choice(['J', 'K'], case_sensitive=False),
                    help='The message format of signal')
@click.option('-t', '--runtime', default=20, envvar='ZEUS_IOT_RUNTIME',
                    prompt="Enter total runtime of the task: ")
def temperature_generator(pattern, interval, timelimit, unit, devices, msgformat, runtime):
    print("pattern:: ", pattern)
    print("interval:: ", interval)
    print("timelimit:: ", timelimit)
    print("unit:: ", unit)
    print("devices:: ", devices)
    print("format:: ", msgformat)
    print("runtime:: ", runtime)

    # devices = 3
    # get random list of devices from the device inventory
    device_list = random.sample(device_master_list.get('temperature'), devices)

    producer_list = []
    for device in device_list:
        producer_list.append(Producer('temperature', device, pattern, interval, timelimit, unit, msgformat))


    for t in producer_list:
        t.start()
        time.sleep(0.01)    # because we want to make a gap in start of the execution of two threads.

    time.sleep(runtime)

    # Stop threads
    for task in producer_list:
        task.stop()

    for task in producer_list:
        task.join()



@click.command('pressure', short_help='Generates pressure metric')
@click.option('-p', '--pattern', envvar='ZEUS_IOT_PATTERN',
                    default='L',
                    prompt='Signal pattern:\n - [L]inear \n - [E]xponential \n - [C]onstant \n - [S]pike \n >> ',
                    type=click.Choice(['L', 'E', 'C', 'S'], case_sensitive=False),
                    help='Pattern of the singal to change linear, exponential, const, spike')
@click.option('-i', '--interval', envvar='ZEUS_IOT_INTERVAL',
                    default=0.5,
                    prompt="Time Interval(in seconds) enter 0 for no delay: ",
                    help='The interval between generating consequent signals in seconds.')
@click.option('-l', '--timelimit', envvar='ZEUS_IOT_TIME_LIMIT',
                    default=30,
                    prompt="How Long to maintain the peak pattern(in seconds): ",
                    help='How Long to maintain the peak pattern for singals(in seconds) in case of Linear, exp, spike traffic pattern')
@click.option('-u', '--unit', envvar='ZEUS_IOT_METRIC_UNIT',
                    default='PSI',
                    prompt="Enter Unit of pressure to generate signal: \n - [Pa]scal \n - [PSI] \n - [ATM] \n >> ",
                    type=click.Choice(['PA', 'PSI', 'ATM'], case_sensitive=False),
                    help='Measurement unit')
@click.option('-n', '--devices', envvar='ZEUS_IOT_NO_DEVICES',
                    default=1, prompt="Enter number of devices: ",
                    type=click.IntRange(1, 100, clamp=True),
                    help='Number of devices to generate signals')
@click.option('-f', '--msgformat', default='J', envvar='ZEUS_IOT_MSG_FORMAT',
                    prompt="Enter message format: \n - [J]SON \n - [K]ey Value \n >> ",
                    type=click.Choice(['J', 'K'], case_sensitive=False),
                    help='The message format of signal')
@click.option('-t', '--runtime', default=20, envvar='ZEUS_IOT_RUNTIME',
                    prompt="Enter total runtime of the task: ")
def pressure_generator(pattern, interval, timelimit, unit, devices, msgformat, runtime):
    print("pattern:: ", pattern)
    print("interval:: ", interval)
    print("timelimit:: ", timelimit)
    print("unit:: ", unit)
    print("devices:: ", devices)
    print("format:: ", msgformat)
    print("runtime:: ", runtime)
    # get random list of devices from the device inventory
    device_list = random.sample(device_master_list.get('pressure'), devices)

    producer_list = []
    for device in device_list:
        producer_list.append(Producer('pressure', device, pattern, interval, timelimit, unit, msgformat))


    for t in producer_list:
        t.start()
        time.sleep(0.01)    # because we want to make a gap in start of the execution of two threads.

    time.sleep(runtime)

    # Stop threads
    for task in producer_list:
        task.stop()

    for task in producer_list:
        task.join()



@click.command('cluster', short_help='Generates signals of devices from a same cluster.')
@click.option('-t', '--runtime', default=20, envvar='ZEUS_IOT_RUNTIME',
                    prompt="Enter total runtime of the task: ")
def generate_cluster_load(runtime):
    pattern = 'C'   # constant
    interval = 0.001
    timelimit = 40

    signal_type = 'temperature'
    unit = 'C'
    devices = 3
    msgformat = 'J' # json

    print("pattern:: ", pattern)
    print("interval:: ", interval)
    print("timelimit:: ", timelimit)
    print("unit:: ", unit)
    print("devices:: ", devices)
    print("format:: ", msgformat)
    print("runtime:: ", runtime)

    device_list = [
        {
            'deviceId': 'E6HUM23Z4X',
            'deviceCluster': 'Boiler-4ZQH2',
            'signalUnit': unit,
            'orgId': 'NUCLEAR-INC',
            'facilityId': 'Boston',
            'signalType': signal_type
        },
        {
            'deviceId': 'JWWMFAJQ8B',
            'deviceCluster': 'Boiler-4ZQH2',
            'signalUnit': unit,
            'orgId': 'NUCLEAR-INC',
            'facilityId': 'Boston',
            'signalType': signal_type
        },
        {
            'deviceId': '4QFXB9MEZA',
            'deviceCluster': 'Boiler-4ZQH2',
            'signalUnit': unit,
            'orgId': 'NUCLEAR-INC',
            'facilityId': 'Boston',
            'signalType': signal_type
        }
    ]


    producer_list = []
    for idx, device in enumerate(device_list):
        faulty = False if idx != (len(device_list) -1) else True
        producer_list.append(ClusterProducer('temperature', device, pattern, interval, timelimit, unit, msgformat, faulty))


    for t in producer_list:
        t.start()
        time.sleep(0.01)    # because we want to make a gap in start of the execution of two threads.

    time.sleep(runtime)

    # Stop threads
    for task in producer_list:
        task.stop()

    for task in producer_list:
        task.join()


@click.command('radiation', short_help='Generates radiation metric')
@click.option('-p', '--pattern', envvar='ZEUS_IOT_PATTERN',
                    default='L',
                    prompt='Signal pattern:\n - [L]inear \n - [E]xponential \n - [C]onstant \n - [S]pike \n >> ',
                    type=click.Choice(['L', 'E', 'C', 'S'], case_sensitive=False),
                    help='Pattern of the singal to change linear, exponential, const, spike')
@click.option('-i', '--interval', envvar='ZEUS_IOT_INTERVAL',
                    default=0.5,
                    prompt="Time Interval(in seconds) enter 0 for no delay: ",
                    help='The interval between generating consequent signals in seconds.')
@click.option('-l', '--timelimit', envvar='ZEUS_IOT_TIME_LIMIT',
                    default=30,
                    prompt="How Long to maintain the peak pattern(in seconds): ",
                    help='How Long to maintain the peak pattern for singals(in seconds) in case of Linear, exp, spike traffic pattern')
@click.option('-u', '--unit', envvar='ZEUS_IOT_METRIC_UNIT',
                    default='SV',
                    prompt="Enter Unit of radiation to generate signal: \n - [GY] \n - [SV]  \n >> ",
                    type=click.Choice(['GY', 'SV',], case_sensitive=False),
                    help='Measurement unit')
@click.option('-n', '--devices', envvar='ZEUS_IOT_NO_DEVICES',
                    default=1, prompt="Enter number of devices: ",
                    type=click.IntRange(1, 100, clamp=True),
                    help='Number of devices to generate signals')
@click.option('-f', '--msgformat', default='J', envvar='ZEUS_IOT_MSG_FORMAT',
                    prompt="Enter message format: \n - [J]SON \n - [K]ey Value \n >> ",
                    type=click.Choice(['J', 'K'], case_sensitive=False),
                    help='The message format of signal')
@click.option('-t', '--runtime', default=20, envvar='ZEUS_IOT_RUNTIME',
                    prompt="Enter total runtime of the task: ")
def radiation_generator(pattern, interval, timelimit, unit, devices, msgformat, runtime):
    print("pattern:: ", pattern)
    print("interval:: ", interval)
    print("timelimit:: ", timelimit)
    print("unit:: ", unit)
    print("devices:: ", devices)
    print("format:: ", msgformat)
    print("runtime:: ", runtime)

    # get random list of devices from the device inventory
    device_list = random.sample(device_master_list.get('radiation'), devices)

    producer_list = []
    for device in device_list:
        producer_list.append(Producer('radiation', device, pattern, interval, timelimit, unit, msgformat))


    for t in producer_list:
        t.start()
        time.sleep(0.01)    # because we want to make a gap in start of the execution of two threads.

    time.sleep(runtime)

    # Stop threads
    for task in producer_list:
        task.stop()

    for task in producer_list:
        task.join()




cli.add_command(temperature_generator)
cli.add_command(pressure_generator)
cli.add_command(generate_cluster_load)
cli.add_command(radiation_generator)


def main():
    return cli()


if __name__ == '__main__':
    main()
