# snakegame
Eddig ezek működnek:
A SnakeGameApplication-ben van a main.
- Az inicializál egy DBserver nevű osztályt. Ez egyelőre csak egy basic MySQL adatbázissal veszi fel a kapcsolatot. Még nincs semmi lekérdezése, semmi.
- Ezután meghívódik a SpringApplication.run(). Ez futtatja a Spring és Thymeleaf templatek segítségével létrehozott "servert".
  Ez vezényli le a http requesteket. Ha minden jól indult el error nélkül, akkor ha felmentek egy browserben a localhost:8080/login címre,
  akkor egy login oldalra dob titeket a server, ahova beírhattok dolgokat, amik meg tovább irányítanak.

A telepítéshez kell: 
- Nekem a teljes intellij van, ehhez van spring és thymeleaf support is, szóval ezt ajánlom.
- A plugin-ek közt aktiválni kell a spring-et és a thymeleaf-et is
- Azután meg valami mágiával biztos betölthető ez a project. Elképesztően sok külső függőség van, szóval tuti nem egyszerű.
- Nekem ez most a melós gépemen van, és igyekszem majd felvarázsolni a laptopomra is. Ha sikerült, akkor megírom, h mi kell hozzá.

- Ami hasznos lenne: JavaScriptben egy canvas, ami kirajzolja a snake pályát, és ez benne egy html-ben. Sztem ezt csinálja valaki.
- Kéne valami leírás a gamestatera, amit majd egy socketen keresztül jason-ként, vagy stringként küldhetnénk.
- Jövő héten üljünk majd össze.
- Keddig nekem nem sok időm lesz ezzel foglalkozni.
  
