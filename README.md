This is one of my old project. It may not work (google authentication part) any
more.

Many years ago when I hear google voice may not support Obitalk anymore, I was
looking for alternative solution. I need setup a system for my parents so they
can use google voice to make phone call. Setup asterisk PBX should work, but
requires some level of skill. So I decide to write my own.

This project depend on
mobicents <http://www.mobicents.org>
spring-framework <http://www.springsource.org>

The idea is:
1. User dial a number and SIP gateway accept request.
2. Depend on number user dialed, SIP gateway login to user's google voice account
3. SIP gateway POST http request to google voice call the number with callback
   number
4. This callback number (IPKall <http://www.ipkall.com>) will forward the callback
   to SIP gateway.
5. SIP gateway detect callback and connect it to original request.

The project using ant-ivy to manage depdendencies.

To build it:
ant

Build will generate two WAR file, 
SIP gateway: mcs-sip.war: can be deploy to mss-2.1.547-appache-tomcat-7.0.50.zip
Management site: mcs-web.war: can be deploy under tomcat 7 or 8
