## FCT (FHIRCore Tooling Application)
This document provides a step-by-step guide to set up and run the KMP Kotlin Multi-Platform desktop application.

### Prerequisites
Before you begin, ensure you have the following installed:

- Kotlin
- IntelliJ IDEA or Android Studio
- Android Debug Bridge (ADB)

### Setup Instructions
#### Verify ADB Configuration
1. Open your terminal or command prompt.
2. Type the following command to check if ADB is installed and properly configured:


```sh
adb --version
```
3. You should see the version information. If you encounter an error, ensure that ADB is included in your system's PATH variable.

### Connect Your Device
1. Connect your Android device to your computer via USB.
2. Ensure that USB debugging is enabled on your device. You can enable it by navigating to **Settings > Developer options > USB debugging.**
3. Verify that your device is properly connected and visible:

```sh
adb devices
```

4. You should see your device listed in the output. If it’s not listed, check your USB connection and permissions.

### Run the Application

1. To run the application, use the following command in your terminal:

```sh
./gradlew :composeApp:run
```
2. Alternatively, you can set up this command in your IDE's configurations for easier access.

### Select a Package from the Package Manager
1. Launch the application.
2. Open the **Package Manager** window within the application.
3. From the list of available packages, select the desired package you want to work with.