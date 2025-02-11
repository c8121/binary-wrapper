# binary-wrapper (launch executables from java)

Java wrapper for commands like [rsync](http://rsync.samba.org/) for Linux and Windows (Mac should be possible also).

For Windows, this library expects a [Cygwin](https://www.cygwin.com/) installation.

Currently supported commands are:

- Rsync
- Ssh

## Usage

### Rsync

First one note about rsync over ssh: When using Rsync out of your Java application, no interactive host key verification will be done.
Therefore, you once have to connect manually from the shell or cygwin terminal to the desired host 
(as the same user which launched the Java application). 

#### Rsync with key authentication

```
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

```
var rsync = new Rsync()
    .useSshPass("password")
    .src("/path/to/source-dir/")
    .dest("user@host:/path/to/dest-dir")
    .recursive()
    .verbose()
    .execute();

rsync.waitForExit();
```

## See also

- https://github.com/fracpete/rsync4j
  Maven project for generating a Java wrapper around rsync for Linux, Mac OSX and Windows.