#!/bin/bash
java8-switch-on
export ANDROID_SDK_ROOT=/home/Android/Sdk
  gradle clean :wallet:bundle -Pandroid.debug.obsoleteApi=true
  gradle :wallet:bundle

#echo "/home/Android/Sdk/build-tools/29.0.3/apksigner sign --ks my-release-key.jks --out digitalpay-prod-out-logo-current.apk bitcoin-wallet-prod-release-unsigned.apk" 
#  cp -rf wallet/build/outputs/apk/prod/release/wallet-prod-release-unsigned.apk digitalpay-unsign.apk
#  cp -rf digitalpay-prod-out-logo-current.apk /home/c4pt/Desktop/
