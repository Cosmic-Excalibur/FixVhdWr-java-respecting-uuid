# FixVhdWr-java-respecting-uuid

Fixed-size VHD writer.

Modified from [https://gitee.com/wu-liubing/FixVhdWr-java](https://gitee.com/wu-liubing/FixVhdWr-java) respecting UUID's of already-existing .vhd files and the magic `0xAA55` of 1st sectors.

Writing .bin files in `rawPath` to a .vhd in `vhdPath` with `maxSector`.
```
Usage: fixvhdwr <rawPath> <vhdPath> <maxSector>
```

## Run!!🏃‍♂️

U need a Java☕, of course.

Download the release and unzip `fixvhdwr.jar`, `fixvhdwr.bat` to a folder📁, then
```batch
java -jar `fixvhdwr.jar` rawPath vhdPath maxSector
```
or
```batch
.\fixvhdwr.bat rawPath vhdPath maxSector
```
You could add this folder📁 your system environment variables, and run
```batch
fixvhdwr rawPath vhdPath maxSector
```

Assume u got a `176`-bytes raw .bin file, you may see
```
Write start, rawFile is 176 bytes.
Write finish, vhdFile is 1024 bytes.
DONE.
```
or
```
Write start, rawFile is 176 bytes.
Reuse already existing VHD uuid aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa
Write finish, vhdFile is 1024 bytes.
DONE.
```
if you are overwriting a .vhd with uuid `aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa`.

There's no need to pad the raw .bin file to 512 bytes (given that the length of raw .bin file is < 512 bytes) and supply the footer magic `0xAA55`.

I suppose this should be convenient while working an Oracle VirtualBox vm using a .vhd as mbr :3






