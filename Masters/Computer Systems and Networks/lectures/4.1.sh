#!/bin/bash

# Input: Simulated sensor data stream
cat <<EOF > sensor_data_stream.txt
001, 2024-09-01 12:00:00, 25.3
002, 2024-09-01 12:01:00, 18.7
003, 2024-09-01 12:02:00, 21.0
EOF

# Filter Stage: Filter readings with values > 20
filter_stage() {
    awk -F, '$3+0 > 20 {print $0}' sensor_data_stream.txt
}

# Transformation Stage: Convert readings to JSON format
transformation_stage() {
    jq -R -s -c '
    split("\n")[:-1] | 
    map(split(", ") | {sensor_id: .[0], timestamp: .[1], value: (.[2]|tonumber)})
    '
}

# Storage Stage: Save JSON output to a file
storage_stage() {
    local output_file=$1
    cat > "$output_file"
}

# Build the pipeline
filter_stage | transformation_stage | storage_stage processed_readings.json

echo "Processed data saved to processed_readings.json"