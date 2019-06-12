## GPShell scripts

### Pre-personalization

If Your card happens to be unfused, there is a [blog post](https://curriegrad2004.ca/2017/02/dealing-with-unfused-jcop-java-cards-sold-from-aliexpress-or-ebay/)
where is written how to cope with the issue.<br/>File `initialize.gpsh` contain most of tips from that post.
There is only one modification on line 7<br/>
where TK(Transport Key) is written - provided by vendor.
After running this script the card will go<br/>into development mode. If you accidentaly "fuse" card, there is no way to revert that operation.

### Instrucion
Firstly, plug reader and card inside the reader.<br/>
To run any script You need to run `cmd.exe` and navigate to this directory. <br/>
Then type `GPShell.exe <script_name.txt>` and there should be info about every command and card APDU response.

### Testing

Instructions in scripts are based on applet construction in `Loyalty.java` file.<br/>
Every script has necessary commentary to applet communicational lines.