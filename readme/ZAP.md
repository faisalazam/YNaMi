


Install ZAP

https://www.zaproxy.org/download/

Frequently Asked Questions

https://www.zaproxy.org/faq/#cat-general-questions

Then copy all the files from the `/Applications/OWASP\ ZAP.app/Contents/Java` installation directory to the
`penetration/pk/lucidxpo/ynami/zap` project directory.

Then the ZAP can be started from terminal by running the `penetration/pk/lucidxpo/ynami/zap/zap.sh` shell script.

It's GUI interface can also be started from the Applications depending upon the OS and installation.



https://www.zaproxy.org/faq/how-can-you-start-zap/

Generally, most user’s tend to use the Mac OS build, which is a ordinary Mac OS app that can be started as any
other app: Double-Click on the app to start it.

If you have installed ZAP in ‘/Applications’ then you can run it from the command line using
‘/Applications/OWASP\ ZAP.app/Contents/Java/zap.sh’

If you have installed ZAP in another directory then change the initial ‘/Applications’ part accordingly.

If you have downloaded the Linux package, which can also be run on Mac OS, you can use the ‘zap.sh’ script, as per linux.

This script has been copied from the ZAP's installation directory,
i.e. "/Applications/OWASP\ ZAP.app/Contents/Java/zap.sh".
and it is from ZAP's version 2.13.0.

And the zap-2.13.0.jar file can be copied from "/Applications/OWASP\ ZAP.app/Contents/Java/zap-2.13.0.jar"



```
docker pull ghcr.io/zaproxy/zaproxy:stable
or
docker pull softwaresecurityproject/zap-stable
```