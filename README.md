# snakegame
Eddig ezek működnek:
A SnakeGameApplication-ben van a main.
- Az inicializál egy DBserver nevű osztályt. Ez egyelőre csak egy basic MySQL adatbázissal veszi fel a kapcsolatot. Még nincs semmi lekérdezése, semmi.
- Ezután meghívódik a SpringApplication.run(). Ez futtatja a Spring és Thymeleaf templatek segítségével létrehozott "servert".
  Ez vezényli le a http requesteket. Ha minden jól indult el error nélkül, akkor ha felmentek egy browserben a localhost:8080/login címre,
  akkor egy login oldalra dob titeket a server, ahova beírhattok dolgokat, amik meg tovább irányítanak.

A telepítéshez kell: 
- Nekem a teljes intellij van, ehhez van spring és thymeleaf support is, szóval ez kell.
- A plugin-ek közt aktiválni kell a spring-et és a thymeleaf-et is.
- létrehozol egy új projectet: file/new project
    - bal oldalt Spring Initializer
    - első oldalon minden default, majd next
    - második oldalon:
        - Group: com.webapp2019
        - Artifact: snakegame
        - Type: Maven POM
        - Packiging: Jar
        - Java version: 11 (valszeg mással is müxik)
        - többi mind1
    - dependencies:
        - Developor tools/Spring Boot devTools
        - Web/minden
        - Template Engines/Thymeleaf
        - SQL/ JDBC API
        - többi nem kell
    - itt a név az maradjon snakegame, a location tetszőleges
    - majd finish
- elnavigálsz a folderbe, és klónozod a git repot (ne legyen snakegame/snakegame)
- Projekt beupdateli magát, és feldobja, h importáld a mavent(jobb alul) ezt tedd meg
- Végül: jobb felül a project structure menü
    - Modules/Dependencies
    - jobb oldalt +/jar file
    - itt kell bebrowsolni a mysql connectort
- Ez után a SnakeGameApplication a futtatható cucc

- Ami hasznos lenne, és lehet írni:
    - JavaScriptben egy canvas, ami kirajzolja a snake pályát, és ez benne egy html-ben. Sztem ezt csinálja valaki.
    - Kéne valami leírás a gamestatera, amit majd egy socketen keresztül jason-ként, vagy stringként küldhetnénk.
  
