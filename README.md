
# Thaumcraft 6 Source Code

## Introduction
Thaumcraft 6 is a rich and immersive mod for Minecraft that adds magical elements to the game. This source code repository contains the deobfuscated and decompiled code, ready for compilation and further development.

## Getting Started

### Prerequisites
- Java Development Kit (JDK) 8 or newer
- Minecraft Forge (compatible version with Thaumcraft 6)

### Setting Up the Development Environment
1. Clone the repository to your local machine:
   ```
   git clone https://github.com/TheDarkTower314/Thaumcraft-6-Source-Code.git
   ```
2. Navigate to the cloned directory and run the Gradle wrapper to set up the workspace and dependencies:
   ```
   cd Thaumcraft-6-Source-Code
   ./gradlew setupDecompWorkspace
   ```
3. To build the mod, use:
   ```
   ./gradlew build
   ```
   The build artifacts will be located in the `build/libs` directory.

### Running the Mod in Development
- After setting up the workspace, you can run the mod directly within a development environment using:
  ```
  ./gradlew runClient
  ```

## Contribution Guidelines
If you are looking to contribute to the Thaumcraft 6 mod, please follow these steps:
1. Fork the repository.
2. Create your feature branch (`git checkout -b feature/AmazingFeature`).
3. Commit your changes (`git commit -am 'Add some AmazingFeature'`).
4. Push to the branch (`git push origin feature/AmazingFeature`).
5. Open a Pull Request.

Note: Please make sure to update tests as appropriate and adhere to the existing coding conventions and standards.


## Acknowledgements
- Thanks to the original authors and contributors of Thaumcraft.


