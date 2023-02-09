# BITCOIN WALLET
```
For generic skin emulator with default apis (without google apis):

List All System Images Available for Download: sdkmanager --list | grep system-images

Download Image: sdkmanager --install "system-images;android-29;default;x86"

Create Emulator: echo "no" | avdmanager --verbose create avd --force --name "generic_10" --package "system-images;android-29;default;x86" --tag "default" --abi "x86"

I recommend adding these lines to: ~/.android/avd/generic_10.avd/config.ini

skin.name=1080x1920        # proper screen size for emulator
hw.lcd.density=480
hw.keyboard=yes            # enables keys from your laptop to be sent to the emulator

If you cannot do this, you can still pass -skin 1080x1920 as an argument when starting the emulator. 
Run Emulator: emulator @generic_10 &
```

to resize tmp to 8G
```
sudo mount -o remount,size=8G,noatime /tmp;
```
* requires maven, ant, java8, gradle6.2, android sdk in path
* sh build.sh


Welcome to _Bitcoin Wallet_, a standalone Bitcoin payment app for your Android device!

This project contains several sub-projects:

 * __wallet__:
     The Android app itself. This is probably what you're searching for.
 * __market__:
     App description and promo material for the Google Play app store.
 * __integration-android__:
     A tiny library for integrating Bitcoin payments into your own Android app
     (e.g. donations, in-app purchases).
 * __sample-integration-android__:
     A minimal example app to demonstrate integration of Bitcoin payments into
     your Android app.


### PREREQUISITES FOR BUILDING

You'll need git, a Java 8 SDK (or later) and Gradle 4.4 (or later) for this. We'll assume Ubuntu 18.04 LTS (Bionic Beaver)
for the package installs, which comes with OpenJDK 8 and Gradle 4.4.1 out of the box.

    # first time only
    sudo apt install git gradle openjdk-8-jdk

Create a directory for the Android SDK (e.g. `android-sdk`) and point the `ANDROID_HOME` variable to it.

Download the [Android SDK Tools](https://developer.android.com/studio/index.html#command-tools)
and unpack it to `$ANDROID_HOME/`.

Finally, the last preparative step is acquiring the source code. Again in your workspace, use:

    # first time only
    git clone -b master https://github.com/bitcoin-wallet/bitcoin-wallet.git bitcoin-wallet
    cd bitcoin-wallet


### BUILDING

You can build all sub-projects in all flavors at once using Gradle:

    # each time
    gradle clean build

For details about building the wallet see the [specific README](wallet/README.md).


### BINARIES

You can install the app from the app store of your choice:

 * __Testnet__:
   <a href="https://f-droid.org/app/org.apache.cordova.radiocoin.wallet_test">F-Droid</a> |
   <a href='https://play.google.com/store/apps/details?id=org.apache.cordova.radiocoin.wallet_test'>Google Play</a>
 * __Mainnet__:
   <a href="https://f-droid.org/app/org.apache.cordova.radiocoin.wallet">F-Droid</a> |
   <a href='https://play.google.com/store/apps/details?id=org.apache.cordova.radiocoin.wallet'>Google Play</a>
