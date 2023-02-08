# ftp-client-server

Implementation of a file transfer protocol

## Environment Setup

```
git clone https://github.com/anmbajaj/ftp-client-server.git
javac Server.java
javac Client.java
```
### Open ~/.zshrc and add the aliases
```
vi ~/.zshrc
alias ftpclient='java -cp "<path-to-project-folder>" Client localhost'
alias ftpserver='java -cp "<path-to-project-folder>" Server'
```
## Steps to run the Server
### The server runs on port 8080 by default

```
ftpserver
```
## Steps to run the Client

```
ftpclient 8080
```

