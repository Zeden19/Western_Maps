# Western Maps
### What is Western Maps?
Western Maps is an application designed to help the user navigate
the many buildings around campus. Utilizing the compiled floor plans
provided by Western University, the application provides the user
with the ability to identify specific buildings and points of
interest within the buildings. Users are provided with many ways
of browsing and discovering points of interest within each building,
as well as creating their own points of interest.

### Dependencies
This application depends on the following libraries (*all included in the build*):
* FlatLaf v3.0:
  * flatlaf-3.0.jar
  * flatlaf-extras-3.0.jar
  * flatlaf-fonts-inter-3.19.jar
* Jackson v2.14.2:
  * jackson-annotations-2.14.2.jar
  * jackson-core-2.14.2.jar
  * jackson-databind-2.14.2.jar
* Java Specification Requests 305 v3.0.2:
  * jsr305-3.0.2.jar
* SVG Salamander v1.1.3:
  * svgSalamander-1.1.3.jar

### Running the Built Program
To run the application from the build:
1. Extract the provided `westernmaps-(version number)-dist.zip`
to the desired application directory.
2. Within the extracted directory, run `westernmaps-(version number).jar`.

### Compiling the Program from Source Code
To compile the application from the source code:
1. Place all source files in the desired project directory.
2. Run `mvn clean compile` in the terminal to compile all source files.
   * After compilation, a `westernmaps-(version number)-dist.zip`
is placed in the `dist` folder of the project directory.
3. Extract the resulting zip file to the desired application directory.
4. Within the extracted directory, run `westernmaps-(versin number)-(release type).jar`.

### How to Use the Program
On all screens of the application, a menu bar is located at top
of the window. Click the Help tab, then click "Help" to access the help
pages, which give explanations on all features of the program. Also under
the Help tab is the "About Western Maps" button, which shows a dialog
with project metadata and developer contact information.


### Accessing Developer Mode
*Developer mode* allows the user to fully edit built-in POIs. To access
developer mode, log into the developer account:
* Username: developerUser
* Password: password1!

Also provided is a built-in regular user account. This is an empty account
without developer mode access. To log in to this account:
* Username: regularUser
* Password: password1!
