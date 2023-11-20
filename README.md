# group-project-group-14
group-project-group-14 created by GitHub Classroom

So I went through a lot of websites to see what we can do about our connection, I fixed following problem:

## 1. Modified our manifest
I added the following to grant our app accesss to network.
'text
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
'text

## 2. Replaced jdbc driver to a older version
I went through stackoverflow and somehow they said we can't have a driver that is newer or roughly the same version as our MySQL workbench, so I replaced the 8.2.0 with 5.1.49.

## 3. Granted privileges to all users from MySQL.
https://stackoverflow.com/questions/5016505/mysql-grant-all-privileges-on-database

## What's being left:
### IP address
I saw it on https://youtu.be/tbFiVKOYcXk?si=5E-BgqI_Fwe_2gJv, that he is using IP address 0.0.0.0 to override the inbound security rules.

### register.class
After changing the IP address, if still not working, we can try to put the connection within the register class to see if things can be improved.


