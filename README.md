# binary-wrapper (launch executables from java)

Java wrapper for commands like [rsync](http://rsync.samba.org/) for Linux and Windows (Mac should be possible also).

For Windows, this library expects a [Cygwin](https://www.cygwin.com/) installation.

Currently supported commands are:

- Rsync
- Ssh
- Scp

## Usage

First one note about host keys: When using Rsync/Ssh/Scp out of your Java application, no interactive host key verification will be done.
Therefore, you once have to connect manually from the shell or cygwin terminal to the desired host
(as the same user which launched the Java application) and do the host key verification.

```bash
ssh -i ~/.ssh/id_rsa user@host
The authenticity of host '[host]...' cant be established.
RSA key fingerprint is SHA256:xxxx...xxx.
This key is not known by any other names.
Are you sure you want to continue connecting (yes/no/[fingerprint])? yes
Warning: Permanently added '[host]' (RSA) to the list of known hosts.
```

### Rsync

#### Rsync with key authentication

```java
var rsync = new Rsync()
    .useSsh("~/.ssh/id_rsa")
    .src("/path/to/source-dir/")
    .dest("user@host:/path/to/dest-dir")
    .recursive()
    .verbose()
    .execute();

rsync.waitForExit();
```

#### Rsync with password authentication (using sshpass)

Please note that this option is not considered to be secure. It's the least
secure option you have. Please read the section "Security Considerations" from
the sshpass man page.

```java
var rsync = new Rsync()
    .useSshPass("password")
    .src("/path/to/source-dir/")
    .dest("user@host:/path/to/dest-dir")
    .recursive()
    .verbose()
    .execute();

rsync.waitForExit();
```

### Ssh

Here an example for remote command execution using Ssh with a custom executor:

```java
var executor = Executors.newSingleThreadExecutor();

var ssh = new Ssh()
    .userAndHost("user@host")
    .keyFile("~/.ssh/id_rsa")
    .command("uname -a && exit")
    .executorService(executor);

System.out.println(ssh.execute(5000).exitCode());
```

## See also

- https://github.com/fracpete/rsync4j
  Maven project for generating a Java wrapper around rsync for Linux, Mac OSX and Windows.