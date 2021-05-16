import psycopg2, random, shortuuid
from psycopg2 import Error

from device_inventory import devices

config = {'host': 'localhost', 'database': 'iot_devices_db', 'user': 'ankurpandey', 'password': '', 'port': 5432}

deviceClusterIds = {
    'temperature':  ['Boiler',  'Freezer', 'Exhaust', 'Storage'],
    'pressure' : ['Compressor', 'GAS-INLET', 'Pressure-Gauge', 'Diff-Pressure-Gauge'],
    'radiation': ['Reactor-Core', 'Insulation-vault', 'Raw-storage', 'Coolant']
    }

orgsIds = ['GAS-INC', 'WIND-INC', 'NUCLEAR-INC']

facility_location = {
        'GAS-INC': ['Dallas', 'Phoenix', 'Portland', 'Chicago', 'Metropolis', 'LA'],
        'WIND-INC': ['LA', 'SANF', 'SANJ', 'Portland', 'Phoenix', 'Dallas'],
        'NUCLEAR-INC': ['Nashville', 'Boston', 'Chicago', 'Atlanta', 'LV', 'SANF']
    }

units = {
    'temperature':  ['C', 'F', 'K' ],
    'pressure' : ['PSI', 'PA', 'ATM' ],
    'radiation': ['GY', 'SV']
    }

unit_range = {
    'C':    { 'min': -20 , 'max': 130 },
    'F':    { 'min': -4 , 'max': 250,    },
    'K':    { 'min': 269.00 , 'max': 400.00,    },
    'PSI':  { 'min': 0 , 'max': 100    },
    'ATM':  { 'min': 0 , 'max': 6.8046    },
    'PA':   { 'min': 0 , 'max': 689476    },
    'GY':   { 'min': 0 , 'max': 90    },
    'SV':   { 'min': 0, 'max': 100 }
}

def get_random_cluster(clusterType):
    return f"{clusterType}-{shortuuid.random(5).upper()}"


def get_facility_id():
    return random.choice(facility_location)


INSERT_SQL = """ INSERT INTO devices.device_info (device_id, singal_type, signal_unit,
        device_cluster, org_id, facility_id, signal_min_value, signal_max_value, created_at, updated_at)
        VALUES (%s, %s, %s, %s, %s, %s, %s, %s, NOW(), NOW() ) """


orgs_temp = orgsIds.copy()

org_counter = 0
facility_counter = 0
cluster_counter = 0
orgName = ""
facilityName = ""
clusterName = ""
cluster__type = ""
sql_params = []
for signal, deviceData in devices.items():
    print("processing singal:: ", signal, "\n")
    if signal in ['bloodpressure', 'heartbeat']:
        continue
    for dev in deviceData:
        if org_counter >= 67:
            orgName = ""
            org_counter = 0

        org_counter += 1

        if facility_counter >= 6:
            facilityName = ""
            facility_counter = 0

        facility_counter += 1

        if cluster_counter >= 3:
            clusterName = ""
            cluster_counter = 0
            cluster__type = ""

        cluster_counter += 1

        if orgName == "":
            try:
                orgName = orgs_temp.pop()
            except:
                orgs_temp = orgsIds.copy()
                orgName = orgs_temp.pop()

        if facilityName == "":
            facilityName = random.choice(facility_location.get(orgName))

        if clusterName == "":
            cluster__type = random.choice(deviceClusterIds.get(signal))
            clusterName = get_random_cluster(cluster__type)
            signal_unit = random.choice(units.get(signal))
            signal_limits = unit_range.get(signal_unit)

        # print("did:", dev, "\torg:", orgName, "\t facility:", facilityName,
        # "\tclusterType:", cluster__type, "\tcluster:", clusterName, "\tunit:",signal_unit, "\tlimit:", signal_limits )

        # (device_id, singal_type, signal_unit,
        # device_cluster, org_id, facility_id, signal_min_value, signal_max_value, created_at, updated_at)

        sql_params.append((dev, signal, signal_unit, clusterName, orgName, facilityName, *(signal_limits.values()) ) )


print(sql_params)


try:
    connection = psycopg2.connect(**config)
    connection.autocommit=False
    # Create a cursor to perform database operations
    cursor = connection.cursor()
    # Print PostgreSQL details
    # print("PostgreSQL server information")
    # print(connection.get_dsn_parameters(), "\n")

    result = cursor.executemany(INSERT_SQL, sql_params)
    connection.commit()
    print(cursor.rowcount, "Record inserted successfully into table")

except Error as e:
    print("Error while connecting to PgSQL Database \n", e)
    raise e

finally:
    if connection:
        cursor.close()
        connection.close()
