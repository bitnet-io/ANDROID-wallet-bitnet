java11-switch-on
echo "requires running java11"
echo ' 
yum install nodejs -y
npm -g install randomstring'
randomstring
#rm -rf *apk
rm -rf my-release-key.jks
keytool -genkey -v -keystore my-release-key.jks -alias alias_name -keyalg RSA -keysize 2048 -validity 10000
#cp -rf wallet/build/outputs/apk/prod/release/wallet-prod-release-unsigned.apk .
/home/Android/Sdk/build-tools/29.0.3/apksigner sign --ks my-release-key.jks --min-sdk-version 28 --out RadioCoin-Android-signed-feefix.aab wallet-prod-release.aab 
 
ls -l *aab
date
