# Running Data Collecting App

## User Flow

### Handheld App

1. Select `Activity`, `Gender`, `Height`

2. Click `activate` Button

   - If activated, You can click `Start` button in watch app

3. After collecting data, click `Save` button. If you want to reset collected data, click `Reset` button.

### Watch App

1. If handheld app is activated, you can click `Start` button.

   - Watch App collects inertial data and send data to handheld app through message client

2. If you pause, you can stop sending data by clicking the stop button. Click start button to measure data collection again.

## Saved Data Format

### **Output Directory Structure**

```bash
android
└── media
    └── com.example.running-data-collecting-app
        └── [activity]
            └── [timestamp]
                └── accelerometer.csv
                └── gyroscope.csv
```

### **Csv File Format**

- **accelerometer**

  ```
  gender,{male|female},height,[Int] // first line, meta data
  count,timestamp,x,y,z // second line, header
  [Int],[milli secs],[float],[float],[float] // data
  ```

- **gyroscope**

  ```
  gender,{male|female},height,[Int] // first line, meta data
  count,timestamp,x,y,z // second line, header
  [Int],[milli secs],[float],[float],[float] // data
  ```
