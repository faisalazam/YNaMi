Install ZAP

https://www.zaproxy.org/download/

Frequently Asked Questions

https://www.zaproxy.org/faq/#cat-general-questions

Then copy all the files from the `/Applications/OWASP\ ZAP.app/Contents/Java` installation directory to the
`penetration/pk/lucidxpo/ynami/zap` project directory.

Then the ZAP can be started from terminal by running the `penetration/pk/lucidxpo/ynami/zap/zap.sh` shell script.

It's GUI interface can also be started from the Applications depending upon the OS and installation.

https://www.zaproxy.org/faq/how-can-you-start-zap/

https://www.zaproxy.org/docs/desktop/releases/

Generally, most user’s tend to use the Mac OS build, which is a ordinary Mac OS app that can be started as any
other app: Double-Click on the app to start it.

If you have installed ZAP in ‘/Applications’ then you can run it from the command line using
‘/Applications/OWASP\ ZAP.app/Contents/Java/zap.sh’

If you have installed ZAP in another directory then change the initial ‘/Applications’ part accordingly.

If you have downloaded the Linux package, which can also be run on Mac OS, you can use the ‘zap.sh’ script, as per
linux.

This script has been copied from the ZAP's installation directory,
i.e. "/Applications/OWASP\ ZAP.app/Contents/Java/zap.sh".
and it is from ZAP's version 2.16.1.

And the zap-2.16.1.jar file can be copied from "/Applications/OWASP\ ZAP.app/Contents/Java/zap-2.16.1.jar"

## Easy way to upgrade is:

1- Download the `Linux Package` from: https://www.zaproxy.org/docs/desktop/releases/
2- You should have `ZAP_<VERSION>_Linux.tar` now (e.g. ZAP_2.13.0_Linux.tar)
3- Extract the `ZAP_<VERSION>_Linux.tar` file
4- Delete the `src/test/java/penetration/pk/lucidxpo/ynami/zap/*`
5- Move the extracted contents to the `src/test/java/penetration/pk/lucidxpo/ynami/zap/*` folder
6- Search for <VERSION> in the whole project and replace with <NEW_VERSION> where makes sense
7- That's it, ready to go
8- But, after upgrading, if you have failing penetration tests due to errors like below:
`org.zaproxy.clientapi.core.ClientApiException: Does Not Exist (does_not_exist): IDs: [40012, 40014, 40016, 40017]`
Then, the likely cause would be the new `src/test/java/penetration/pk/lucidxpo/ynami/zap/plugin` dir is missing
some files which are not available in new version, e.g.: `ascanrulesAlpha-alpha-48.zap`, `ascanrulesBeta-beta-58.zap`
So, basically work out which *.zap contains the `does_not_exist` id, and then download and
copy it to the src/test/java/penetration/pk/lucidxpo/ynami/zap/plugin dir. e.g.:

```
curl -L -o src/test/java/penetration/pk/lucidxpo/ynami/zap/plugin/ascanrules-release-70.zap https://github.com/zaproxy/zap-extensions/releases/download/ascanrules-v70/ascanrules-release-70.zap
curl -L -o src/test/java/penetration/pk/lucidxpo/ynami/zap/plugin/ascanrulesAlpha-alpha-19.zap https://github.com/zaproxy/zap-extensions/releases/download/ascanrulesAlpha-v48/ascanrulesAlpha-alpha-19.zap
curl -L -o src/test/java/penetration/pk/lucidxpo/ynami/zap/plugin/ascanrulesBeta-beta-21.zap https://github.com/zaproxy/zap-extensions/releases/download/ascanrulesBeta-v56/ascanrulesBeta-beta-21.zap
```

Or, go to https://www.zaproxy.org/addons/ and download the relevant zap addon file from there

```
docker pull ghcr.io/zaproxy/zaproxy:stable
or
docker pull softwaresecurityproject/zap-stable
```

# BDD Security

https://github.com/iriusrisk/bdd-security/tree/master