
# password to sign with upload_cert.der bwrlEJiW08jAAonH4M5tixkaKeFvZmV5
#keytool -importcert -file upload_cert.der -keystore my-release-key.jks -alias google_upload
#
#keytool -importcert -file upload_cert.der -keystore my-release-key.jks -alias google_upload

#/home/Android/Sdk/build-tools/29.0.3/apksigner sign --ks my-release-key.jks --min-sdk-version 30 --out RadioCoin-Android-release.aab wallet-prod-release.aab 

/home/Android/Sdk/build-tools/30.0.3/apksigner sign --ks keystore.jks --min-sdk-version 30 --out RadioCoin-Android-release.aab wallet-prod-release.aab 
