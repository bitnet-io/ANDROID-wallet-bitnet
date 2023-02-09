java11-switch-on
echo "requires running java11"
echo ' 
yum install nodejs -y
npm -g install randomstring'
randomstring
echo ""
echo "the randomstring is a suggestion as a strong password to create the keystore if you use it, do not misplace the password for this keystore jks file"
echo ""
echo "creating google play store signing key save the randomstring or password that you choose to make this jks keystore to resign apps after this"

#rm -rf *apk
#rm -rf my-release-key.jks
keytool -genkey -v -keystore my-release-key.jks -alias google_upload -keyalg RSA -keysize 2048 -validity 10000


#keytool -export -rfc -keystore my-release-key.jks -alias google_upload -file never_delete_password-used-with-jks-upload_certificate.pem




#cp -rf wallet/build/outputs/apk/prod/release/wallet-prod-release-unsigned.apk .
#/home/Android/Sdk/build-tools/29.0.3/apksigner sign --ks my-release-key.jks --min-sdk-version 30 --out RadioCoin-Android-release.aab wallet-prod-release.aab 
#ls -l *aab
#date
