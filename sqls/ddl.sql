-- create database
CREATE database iot_devices_db;

-- create schema
CREATE schema devices;

-- NOW create tables

-- new table device_types
CREATE TABLE devices.device_types (
    id serial Primary Key,
    type character varying  NULL,
    description text  NULL,
    threshold smallint  NULL,
    created_at timestamp  DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at timestamp  DEFAULT CURRENT_TIMESTAMP NOT NULL,
CONSTRAINT device_type_unique_idx
    UNIQUE (type));


-- new table device_info
CREATE TABLE devices.device_info (
    id serial Primary Key,
    device_id varchar(50) NOT NULL,
    signal_type varchar(50) NOT NULL,
    signal_unit varchar(50) NOT NULL,
    device_cluster varchar(50) NOT NULL,
    org_id varchar(50) NOT NULL,
    facility_id varchar(50) NOT NULL,
    signal_min_value NUMERIC(10,4) NOT NULL,
    signal_max_value NUMERIC(10,4) NOT NULL,
    created_at timestamp  DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at timestamp  DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT device_info_unique_idx_device_id
    UNIQUE (device_id)
);
CREATE INDEX dvc_info_idx_cluster ON devices.device_info USING BTREE (device_cluster);
CREATE INDEX dvc_info_idx_org_id ON devices.device_info USING BTREE (org_id);
CREATE INDEX dvc_info_idx_facility_id ON devices.device_info USING BTREE (facility_id);


-- new table signal_type_metric_template
CREATE TABLE devices.signal_type_metric_template (
    device_id varchar(50) NOT NULL,
    signal_type varchar(50) NOT NULL,
    signal_unit varchar(50) NOT NULL,
    signal_value NUMERIC(10,4) NOT NULL,
    device_cluster varchar(50) NOT NULL,
    org_id varchar(50) NOT NULL,
    facility_id varchar(50) NOT NULL,
    device_timestamp timestamp NOT NULL,
    reported_year SMALLINT NOT NULL,
	reported_month SMALLINT NOT NULL,
	reported_day SMALLINT NOT NULL,
	reported_hour SMALLINT NOT NULL,
	reported_minute SMALLINT NOT NULL,
	reported_sec SMALLINT NOT NULL,
    created_at timestamp  DEFAULT CURRENT_TIMESTAMP NOT NULL,
    PRIMARY KEY (device_id, created_at)
);

-- CREATE TABLE devices.<signal_type>_metric_tracker AS SELECT * FROM devices.signal_type_metric_template;

-- CREATE INDEX <tablename>_idx_cluster ON devices.<tablename> USING BTREE (device_cluster);
-- CREATE INDEX <tablename>_idx_org_id ON devices.<tablename> USING BTREE (org_id);
-- CREATE INDEX <tablename>_idx_facility_id ON devices.<tablename> USING BTREE (facility_id);
-- CREATE INDEX <tablename>_idx_reported_year ON devices.<tablename> USING BTREE (reported_year);
-- CREATE INDEX <tablename>_idx_reported_month ON devices.<tablename> USING BTREE (reported_month);
-- CREATE INDEX <tablename>_idx_reported_day ON devices.<tablename> USING BTREE (reported_day);
-- CREATE INDEX <tablename>_idx_reported_hour ON devices.<tablename> USING BTREE (reported_hour);
-- CREATE INDEX <tablename>_idx_reported_minute ON devices.<tablename> USING BTREE (reported_minute);
-- CREATE INDEX <tablename>_idx_reported_sec ON devices.<tablename> USING BTREE (reported_sec);


-- new table faulty_signals
CREATE TABLE devices.faulty_signals (
    device_id varchar(50) NOT NULL,
    signal_type varchar(50) NOT NULL,
    signal_value NUMERIC(10,4) NOT NULL,
    device_timestamp timestamp NOT NULL,
    reported_year SMALLINT NOT NULL,
	reported_month SMALLINT NOT NULL,
	reported_day SMALLINT NOT NULL,
	reported_hour SMALLINT NOT NULL,
	reported_minute SMALLINT NOT NULL,
	reported_sec SMALLINT NOT NULL,
    created_at timestamp  DEFAULT CURRENT_TIMESTAMP NOT NULL,
    PRIMARY KEY (device_id, created_at)
);

CREATE INDEX faulty_signals_idx_reported_year ON devices.faulty_signals USING BTREE (reported_year);
CREATE INDEX faulty_signals_idx_reported_month ON devices.faulty_signals USING BTREE (reported_month);
CREATE INDEX faulty_signals_idx_reported_day ON devices.faulty_signals USING BTREE (reported_day);
CREATE INDEX faulty_signals_idx_reported_hour ON devices.faulty_signals USING BTREE (reported_hour);
CREATE INDEX faulty_signals_idx_reported_minute ON devices.faulty_signals USING BTREE (reported_minute);
CREATE INDEX faulty_signals_idx_reported_sec ON devices.faulty_signals USING BTREE (reported_sec);
CREATE INDEX faulty_signals_idx_signal_type ON devices.faulty_signals USING BTREE (signal_type);


-- new table compromised_signals
CREATE TABLE devices.compromised_signals (
    device_id varchar(50) NOT NULL,
    signal_type varchar(50) NOT NULL,
    signal_value NUMERIC(10,4) NOT NULL,
    message_digest varchar(250) NOT NULL,
    device_timestamp timestamp NOT NULL,
    reported_year SMALLINT NOT NULL,
	reported_month SMALLINT NOT NULL,
	reported_day SMALLINT NOT NULL,
	reported_hour SMALLINT NOT NULL,
	reported_minute SMALLINT NOT NULL,
	reported_sec SMALLINT NOT NULL,
    created_at timestamp  DEFAULT CURRENT_TIMESTAMP NOT NULL,
    PRIMARY KEY (device_id, created_at)
);

CREATE INDEX compromised_signals_idx_reported_year ON devices.compromised_signals USING BTREE (reported_year);
CREATE INDEX compromised_signals_idx_reported_month ON devices.compromised_signals USING BTREE (reported_month);
CREATE INDEX compromised_signals_idx_reported_day ON devices.compromised_signals USING BTREE (reported_day);
CREATE INDEX compromised_signals_idx_reported_hour ON devices.compromised_signals USING BTREE (reported_hour);
CREATE INDEX compromised_signals_idx_reported_minute ON devices.compromised_signals USING BTREE (reported_minute);
CREATE INDEX compromised_signals_idx_reported_sec ON devices.compromised_signals USING BTREE (reported_sec);
CREATE INDEX compromised_signals_idx_signal_type ON devices.compromised_signals USING BTREE (signal_type);


-- new table device_cluster_metric_tracker
CREATE TABLE IF NOT EXISTS devices.device_cluster_metric_tracker (
    device_id varchar(50) NOT NULL,
    signal_type varchar(50) NOT NULL,
    signal_unit varchar(50) NOT NULL,
    signal_value NUMERIC(10,4) NOT NULL,
    device_cluster varchar(50) NOT NULL,
    org_id varchar(50) NOT NULL,
    facility_id varchar(50) NOT NULL,
    device_timestamp timestamp NOT NULL,
    reported_year SMALLINT NOT NULL,
	reported_month SMALLINT NOT NULL,
	reported_day SMALLINT NOT NULL,
	reported_hour SMALLINT NOT NULL,
	reported_minute SMALLINT NOT NULL,
	reported_sec SMALLINT NOT NULL,
    created_at timestamp  DEFAULT CURRENT_TIMESTAMP NOT NULL,
    PRIMARY KEY (device_id, created_at)
);

CREATE INDEX device_cluster_metric_tracker_idx_cluster ON devices.device_cluster_metric_tracker USING BTREE (device_cluster);
CREATE INDEX device_cluster_metric_tracker_idx_org_id ON devices.device_cluster_metric_tracker USING BTREE (org_id);
CREATE INDEX device_cluster_metric_tracker_idx_facility_id ON devices.device_cluster_metric_tracker USING BTREE (facility_id);
CREATE INDEX device_cluster_metric_tracker_idx_reported_year ON devices.device_cluster_metric_tracker USING BTREE (reported_year);
CREATE INDEX device_cluster_metric_tracker_idx_reported_month ON devices.device_cluster_metric_tracker USING BTREE (reported_month);
CREATE INDEX device_cluster_metric_tracker_idx_reported_day ON devices.device_cluster_metric_tracker USING BTREE (reported_day);
CREATE INDEX device_cluster_metric_tracker_idx_reported_hour ON devices.device_cluster_metric_tracker USING BTREE (reported_hour);
CREATE INDEX device_cluster_metric_tracker_idx_reported_minute ON devices.device_cluster_metric_tracker USING BTREE (reported_minute);
CREATE INDEX device_cluster_metric_tracker_idx_reported_sec ON devices.device_cluster_metric_tracker USING BTREE (reported_sec);


CREATE EXTENSION IF NOT EXISTS timescaledb CASCADE;

SELECT public.create_hypertable('devices.compromised_signals', 'created_at' );
SELECT public.create_hypertable('devices.faulty_signals', 'created_at');
SELECT public.create_hypertable('devices.device_cluster_metric_tracker', 'created_at');



