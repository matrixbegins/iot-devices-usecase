from collections import defaultdict
import psycopg2, os
from psycopg2 import Error

from slack_sdk import WebClient
from slack_sdk.errors import SlackApiError

client = WebClient(token=os.environ.get("SLACK_BOT_TOKEN", "xoxb"))

config = {'host': 'localhost', 'database': 'iot_devices_db', 'user': 'ankurpandey', 'password': '', 'port': 5432}

channel_names = ['all_alerts_all_orgs', 'all_alerts_wind_inc', 'all_alerts_gas_inc', 'all_alerts_nuclear_inc']

try:
    connection = psycopg2.connect(**config)

    cursor = connection.cursor()
    cursor.execute("select org_id, facility_id, count(1) from devices.device_info group by 1,2 order by 1,2")

    records = cursor.fetchall()

    # print(records)

    for data in records:
        chName = f"alerts_{data[0]}_facility_{data[1]}"
        chName = chName.lower().replace("-", "_")
        channel_names.append(chName)


except Error as er:
    print(er)

# print(channel_names)


try:
    # Call the conversations.create method using the WebClient
    # conversations_create requires the channels:manage bot scope
    for channel in channel_names:
        result = client.conversations_create(
            # The name of the conversation
            name=channel,
            is_private=False
        )
        # Log the result which includes information like the ID of the conversation
        print(result)

except SlackApiError as e:
    print("Error creating conversation: {}".format(e))
