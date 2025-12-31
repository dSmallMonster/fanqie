# Gradle Wrapper Information

## Overview

The project now includes the **Gradle Wrapper** (version 8.7) which allows building the project without requiring a pre-installed Gradle distribution.

## Files Included

### Gradle Wrapper Files
- `gradlew` - Unix/Linux/macOS executable script (8.5 KB)
- `gradlew.bat` - Windows batch script (2.9 KB)
- `gradle/wrapper/gradle-wrapper.jar` - Gradle Wrapper JAR (43 KB)
- `gradle/wrapper/gradle-wrapper.properties` - Wrapper configuration

### Configuration
```properties
distributionBase=GRADLE_USER_HOME
distributionPath=wrapper/dists
distributionUrl=https://services.gradle.org/distributions/gradle-8.7-bin.zip
networkTimeout=10000
validateDistributionUrl=true
zipStoreBase=GRADLE_USER_HOME
zipStorePath=wrapper/dists
```

## Usage

### On Linux/macOS
```bash
cd WhiteNoiseApp
./gradlew assembleDebug
```

### On Windows
```cmd
cd WhiteNoiseApp
gradlew.bat assembleDebug
```

### Common Gradle Tasks
```bash
# Build debug APK
./gradlew assembleDebug

# Build release APK
./gradlew assembleRelease

# Clean build
./gradlew clean

# Run tests
./gradlew test

# Check for dependency updates
./gradlew dependencyUpdates

# Show project info
./gradlew projects

# Show available tasks
./gradlew tasks
```

## GitHub Actions

A GitHub Actions workflow file has been included at `.github/workflows/android-build.yml` for automated CI/CD.

### Workflow Features
- ✅ Triggers on push/pull request to main/master branch
- ✅ Sets up JDK 17 (required for Android Gradle Plugin 8.2.0)
- ✅ Caches Gradle dependencies for faster builds
- ✅ Builds debug APK
- ✅ Uploads APK as artifact

### Workflow File
```yaml
name: Android CI

on:
  push:
    branches: [ main, master ]
  pull_request:
    branches: [ main, master ]

jobs:
  build:
    runs-on: ubuntu-latest
    
    steps:
    - name: Checkout code
      uses: actions/checkout@v4
      
    - name: Set up JDK 17
      uses: actions/setup-java@v4
      with:
        java-version: '17'
        distribution: 'temurin'
        cache: gradle
        
    - name: Grant execute permission for gradlew
      run: chmod +x gradlew
      
    - name: Build with Gradle
      run: ./gradlew assembleDebug
      
    - name: Upload APK
      uses: actions/upload-artifact@v4
      with:
        name: app-debug
        path: app/build/outputs/apk/debug/app-debug.apk
```

## Requirements

### Java Version
- **Required**: JDK 17 or higher
- **Reason**: Android Gradle Plugin 8.2.0 requires Java 17

### Setting JAVA_HOME (if needed)
```bash
# Linux/macOS
export JAVA_HOME=/path/to/jdk-17

# Windows
set JAVA_HOME=C:\Path\To\jdk-17
```

## First Build

On the first build, the Gradle Wrapper will automatically:
1. Download Gradle 8.7 distribution (~100 MB)
2. Extract it to `~/.gradle/wrapper/dists/`
3. Use the downloaded distribution for all subsequent builds

### Expected Output
```
Downloading https://services.gradle.org/distributions/gradle-8.7-bin.zip
............10%.............20%.............30%............100%

------------------------------------------------------------
Gradle 8.7
------------------------------------------------------------
```

## Advantages of Gradle Wrapper

✅ **No Gradle installation required** - Wrapper downloads correct version automatically  
✅ **Consistent builds** - Everyone uses the same Gradle version  
✅ **CI/CD friendly** - Works seamlessly with GitHub Actions, Jenkins, etc.  
✅ **Version control** - Wrapper files are committed to repository  
✅ **Cross-platform** - Works on Linux, macOS, and Windows  

## Troubleshooting

### Permission Denied (Linux/macOS)
```bash
chmod +x gradlew
./gradlew assembleDebug
```

### Java Version Error
```
Android Gradle plugin requires Java 17 to run.
You are currently using Java 11.
```

**Solution**: Install JDK 17 and set JAVA_HOME
```bash
# Ubuntu/Debian
sudo apt-get install openjdk-17-jdk
export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64

# macOS (Homebrew)
brew install openjdk@17
export JAVA_HOME=/opt/homebrew/opt/openjdk@17

# Windows
# Download from https://adoptium.net/
# Set JAVA_HOME in System Environment Variables
```

### Network Issues
If download fails, manually download Gradle 8.7:
1. Download: https://services.gradle.org/distributions/gradle-8.7-bin.zip
2. Place in: `~/.gradle/wrapper/dists/gradle-8.7-bin/`
3. Run: `./gradlew --version`

### Clean Gradle Cache
```bash
# Remove Gradle cache
rm -rf ~/.gradle/caches/

# Remove wrapper distributions
rm -rf ~/.gradle/wrapper/dists/

# Re-run build
./gradlew assembleDebug
```

## Verification

Verify Gradle Wrapper is working:
```bash
cd WhiteNoiseApp
./gradlew --version
```

Expected output:
```
Gradle 8.7
Kotlin: 1.9.22
JVM: 17.0.x
```

## Build Output

After successful build:
```
app/build/outputs/apk/debug/app-debug.apk
```

Install on device:
```bash
adb install app/build/outputs/apk/debug/app-debug.apk
```

## GitHub Actions Usage

After pushing to GitHub:
1. Go to **Actions** tab in your repository
2. View the **Android CI** workflow
3. Download the built APK from **Artifacts**

## Summary

The Gradle Wrapper is now fully configured and ready for use:
- ✅ Gradle 8.7 wrapper files included
- ✅ Works on Linux, macOS, and Windows
- ✅ GitHub Actions workflow configured
- ✅ No manual Gradle installation required
- ✅ Consistent builds across all environments

You can now build the project on any machine or CI/CD platform without pre-installing Gradle!
