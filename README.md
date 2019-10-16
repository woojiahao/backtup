# backtup
Robust CLI tool built with Clikt that assists with backing up your file system

## Installation
On Linux, run the following command to set up the project.

```bash
$ wget https://github.com/woojiahao/backtup/blob/master/install & chmod +x install & ./install
```

Optionally, you can add the tool as an alias so that you don't need to change directories.

```bash
$ echo 'alias backup="~/backtup/build/install/backtup/bin/backtup"' >> .zshrc
```

## TODO
- [ ] Read up on Gradle's application plugin
- [ ] Read up on Gradle's distribution plugin
  - [ ] installDist
- [ ] Edit component
  - [ ] path
  - [ ] name
  - [ ] items
- [ ] Delete component
- [ ] Show path in "backtup ls"
- [ ] Investigate `default.validate` pattern
