# backtup
Robust CLI tool built with Clikt that assists with backing up your file system

## Installation
### Building from source
1. Clone this repository and navigate into it

    ```bash
   $ git clone https://github.com/woojiahao/backtup
   $ cd backtup
    ```
   
2. Build the project

    ```bash
   $ ./gradlew installDist
    ```
   
3. Move the build file into an accessible location (I move it into my dotfiles folder)

    ```bash
   $ cp build/install/backtup/bin/backtup ~/dotfiles/scripts
    ```
   
4. Set permissions for the file

    ```bash
   $ chmod +x ~/dotfiles/scripts
    ```
   
5. Execute the tool
    
    ```bash
   $ ./backtup
    ```
   
6. (Optional) Create an alias for the tool in you `.bashrc` or `.zshrc`

    ```bash
   $ alias backup="~/dotfiles/scripts/backtup"
    ```
   
### Using script
The script is hosted to my `dotfiles` which can be found [here](https://github.com/woojiahao/dotfiles)
## TODO
- [ ] Read up on Gradle's application plugin
- [ ] Read up on Gradle's distribution plugin
  - [ ] installDist
