# FixVhdWr-java-respecting-uuid

Fixed-size VHD writer.

Modified from [https://gitee.com/wu-liubing/FixVhdWr-java](https://gitee.com/wu-liubing/FixVhdWr-java) respecting UUID's of already-existing .vhd files and the magic `0xAA55` of sector 0.

Inserting .bin files in a .vhd with `maxSectors` sectors.
```
Usage: fixvhdwr <mbrRawPath> <vhdPath> <maxSectors> [rawPath1] [sectorIndex1] [rawPath2] [sectorIndex2] ...
```

## Run!!🏃‍♂️

U need a Java☕ to run this, of course.

Download the release and unzip `fixvhdwr.jar`, `fixvhdwr.bat` to a folder📁, then run
```batch
java -jar `fixvhdwr.jar` rawPath vhdPath maxSectors ...
```
or
```batch
.\fixvhdwr.bat rawPath vhdPath maxSectors ...
```
You could add this folder📁 to your system environment variables, and run
```batch
fixvhdwr rawPath vhdPath maxSectors ...
```

Assume u got a `176`-bytes raw .bin file as an mbr, you might see
```
mbrRawFile is 176 bytes.
Task finished, vhdFile is 1024 bytes.
```
or
```
mbrRawFile is 176 bytes.
Reuse already existing VHD uuid aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa
Task finished, vhdFile is 1024 bytes.
DONE.
```
if you are overwriting a .vhd with uuid `aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa`.

As an example in Chapter 8 of the original book [https://github.com/liracle/codeOfAssembly/tree/master/booktool/c08](https://github.com/liracle/codeOfAssembly/tree/master/booktool/c08), run
```batch
fixvhdwr .\c08_mbr.bin .\c08.vhd 102 .\c08.bin 100
```
and you shall see
```
mbrRawFile is 512 bytes.
Writing .\c08.bin to sector 100
Task finished, vhdFile is 52736 bytes.
```
which completes the creation of needed .vhd files.

Ps. There's no need to pad your mbr to 512 bytes (if it's < 512 bytes) or supply the magic footer `0xAA55`.


## Changelog📜

- **2025-03-26:** Forked from [https://gitee.com/wu-liubing/FixVhdWr-java](https://gitee.com/wu-liubing/FixVhdWr-java). Added uuid & magic footer features.
- **2025-03-31:** Added multi .bin file support.



